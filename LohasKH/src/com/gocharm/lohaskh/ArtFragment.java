package com.gocharm.lohaskh;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.coimotion.csdk.common.COIMCallListener;
import com.coimotion.csdk.util.Assist;
import com.coimotion.csdk.util.ReqUtil;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

@SuppressLint("ValidFragment")
public class ArtFragment extends Fragment{
	private static final String TAG = "artFrag";
	private int pageN;
	private AsyncTask<String, Integer, String> mTask = null;
	public ListView mShowList;
	public ImageView loading;
	public ImageView nothing;
	public LinearLayout error;
	
	private LohasAdapter adapter;
	private ArrayList<HashMap<String,String>> dataArray = new ArrayList<HashMap<String,String>>();
	private ProgressDialog pd;
	private String catID;
	private DBHelper db;
	
    public static ArtFragment newInstance(int index) {
    	ArtFragment f = new ArtFragment();
	    Bundle args = new Bundle();
	    args.putInt("args", index);
	    f.setArguments(args);
		
	    return f;
	 }
    
    public ArtFragment(){}
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	pageN = getArguments().getInt("args");
    }
    
    @Override
    public void onDestroy() {
    	Log.i(TAG, "art destroy");
    	if(mTask != null)
    		mTask.cancel(true);
    	super.onDestroy();
   		db.close();
    }
    
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		SharedPreferences pref = getActivity().getSharedPreferences(Util.PREF_NAME, 0);
    	db  = new DBHelper(getActivity().getApplicationContext());
    	catID = pref.getString("art_cat_id_"+pageN, "1");//Util.artCatMap.get(pageN).getName();
    	View view = inflater.inflate(R.layout.fragment_art, container, false);
    	mShowList = (ListView) view.findViewById(R.id.artList);
    	mShowList.setVisibility(View.INVISIBLE);
    	nothing = (ImageView) view.findViewById(R.id.nothing);
    	nothing.setVisibility(View.INVISIBLE);
    	loading = (ImageView)view.findViewById(R.id.loading);
    	loading.setVisibility(View.VISIBLE);
    	error = (LinearLayout)view.findViewById(R.id.error);
    	error.setVisibility(View.INVISIBLE);
    	error.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.i(TAG, "reload");
				getList(catID);
			}
		});
    	
    	getList(catID);
    	
    	return view;
    }
    
    @SuppressLint("SimpleDateFormat")
	private void getList(String catID){
    	if(!Util.isNetworkAvailable(getActivity().getApplication())) {
			dataArray.clear();
			adapter = new LohasAdapter(
					getActivity().getApplicationContext(), 
					dataArray, R.layout.row_main_list, 
					new String[]{"title", "addr"}, 
					new int[]{R.id.rowTitle, R.id.rowSubtitle});
			
			mShowList.setVisibility(View.INVISIBLE);
			nothing.setVisibility(View.INVISIBLE);
			loading.setVisibility(View.INVISIBLE);
			error.setVisibility(View.VISIBLE);
			return;
		}
    	
    	Map<String, Object> mapParam = new HashMap<String, Object>();
    	mapParam.put("cat", catID);
    	Date now = new Date(System.currentTimeMillis());
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		Calendar cal = Calendar.getInstance();
	    cal.setTime(now);
	    cal.add(Calendar.DAY_OF_MONTH, 30);
	    mapParam.put("fromTm", "" + formatter.format(now));
		mapParam.put("toTm", "" + formatter.format((Date) cal.getTime()));
    	
    	mTask = ReqUtil.send("twShow/show/byCity/15", mapParam, new COIMCallListener() {
			
			@Override
			public void onSuccess(JSONObject result) {
				if(mTask.isCancelled()) {
					return;
				}
				if(Assist.getErrCode(result) == 0) {
					JSONArray arr= new JSONArray();
					try {
						arr = result.getJSONObject("value").getJSONArray("list");
					} catch (JSONException e) {
						e.printStackTrace();
					}
					if(arr.length() > 0) {
						String title = "";
						dataArray.clear();
						for(int i = 0; i < arr.length(); i++) {
							HashMap<String, String> item = new HashMap<String, String>();
							JSONObject obj;
							try {
								obj = (JSONObject) arr.get(i);
								if (!title.equals(obj.getString("title"))) {
									title = obj.getString("title");
									item.put("title", obj.getString("title"));
									item.put("addr", obj.getString("placeName"));
									item.put("spID", obj.getString("spID"));
									item.put("imgURL", obj.getString("imgURL"));
									
									String offiURL =  obj.getString("offiURL");
									String saleURL =  obj.getString("saleURL");
									if (offiURL != "" ) {
										item.put("share", offiURL);
									}
									else if(saleURL != ""){
										item.put("share", saleURL);
									}
									else{
										item.put("share", "");
									}
									
									dataArray.add(item);
								}
							} catch (JSONException e) {
								e.printStackTrace();
							}					
						}
						adapter = new LohasAdapter(
								getActivity().getApplicationContext(), 
								dataArray, R.layout.row_main_list, 
								new String[]{"title", "addr"}, 
								new int[]{R.id.rowTitle, R.id.rowSubtitle}){
							@Override
							public View getView(final int position, View convertView, ViewGroup parent) {
								View view = super.getView(position, convertView, parent);
								final ImageButton collectBut = (ImageButton) view.findViewById(R.id.collectBut);
								ImageButton shareBut = (ImageButton) view.findViewById(R.id.shareBut);
								if (!dataArray.get(position).get("share").equals("")) 
								{
									shareBut.setVisibility(View.VISIBLE);
								}
								
								ImageView icon = (ImageView)view.findViewById(R.id.listIcon);
								icon.setImageDrawable(getResources().getDrawable(R.drawable.nopic_arts));
								if(!dataArray.get(position).get("imgURL").equals("")) {
									ImageLoader.getInstance().displayImage(dataArray.get(position).get("imgURL"), icon, Util.artOptions, new Util.AnimateFirstDisplayListener());
								}
								
								if(!db.hasID(dataArray.get(position).get("spID"), DBHelper.TABLE_ART)) {
									collectBut.setImageDrawable(getResources().getDrawable(R.drawable.favor_add));
								}
								else {
									collectBut.setImageDrawable(getResources().getDrawable(R.drawable.favor_remove));
								}
								OnClickListener listener = new OnClickListener() {
									
									@Override
									public void onClick(View v) {
										switch (v.getId()) {
											case R.id.listIcon:
												if( !Util.isNetworkAvailable(getActivity().getApplication())) {
													Util.noNetworkAlert(getActivity());
													return;
												}
												
												pd = ProgressDialog.show(getActivity(), "讀取中", "取得活動資料中…");
												ReqUtil.send("twShow/show/info/" + dataArray.get(position).get("spID"), null, new COIMCallListener() {
													
													@Override
													public void onSuccess(JSONObject result) {
														pd.dismiss();
														if(Assist.getErrCode(result) == 0) {
															try {
																JSONObject object = result.getJSONObject("value");
																object.put("spID", dataArray.get(position).get("spID"));
																object.put("addr", dataArray.get(position).get("addr"));
																String extra = object.toString();
																
																Intent intent = new Intent(getActivity(), ArtDetailActivity.class);
																intent.putExtra("artInfo", extra);
																startActivity(intent);
															} catch (JSONException e) {
																e.printStackTrace();
															}
														}
														else {
															Log.i(TAG, "ex: " + Assist.getMessage(result));
														}
													}
													
													@Override
													public void onFail(HttpResponse response, Exception ex) {
														pd.dismiss();
														Log.i(TAG, "ex: " + ex.getLocalizedMessage());
													}
	
													@Override
													public void onInvalidToken() {
														new AlertDialog.Builder(getActivity()).setTitle("warning").setMessage("invalid token").show();													
													}
												});
												
												break;
											case R.id.shareBut:
												Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND); 
											    sharingIntent.setType("text/plain");
											    String shareBody = dataArray.get(position).get("share");
											    sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, dataArray.get(position).get("title"));
											    sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
												startActivity(Intent.createChooser(sharingIntent, "Share via"));
												break;
											case R.id.collectBut:
												//Map<String, String> mapParam = dataArray.get(position);
												
												if (!db.hasID(dataArray.get(position).get("spID"), DBHelper.TABLE_ART)) {
													db.insert(dataArray.get(position), DBHelper.TABLE_ART);
													collectBut.setImageDrawable(getResources().getDrawable(R.drawable.favor_remove));
												}
												else {
													db.delete(dataArray.get(position).get("spID"), DBHelper.TABLE_ART);
													collectBut.setImageDrawable(getResources().getDrawable(R.drawable.favor_add));
												}
												break;
										}
									}
								};
								
								collectBut.setOnClickListener(listener);
								shareBut.setOnClickListener(listener);
								icon.setOnClickListener(listener);
			        			return view;
							}
						};
						mShowList.setAdapter(adapter);
						mShowList.setVisibility(View.VISIBLE);
						nothing.setVisibility(View.INVISIBLE);
						loading.setVisibility(View.INVISIBLE);
						error.setVisibility(View.INVISIBLE);
					}
					else {
						dataArray.clear();
						adapter = new LohasAdapter(
								getActivity().getApplicationContext(), 
								dataArray, R.layout.row_main_list, 
								new String[]{"title", "addr"}, 
								new int[]{R.id.rowTitle, R.id.rowSubtitle});
						mShowList.setAdapter(adapter);
						//loadingAdapter.clear();
						
						mShowList.setVisibility(View.INVISIBLE);
						nothing.setVisibility(View.VISIBLE);
						loading.setVisibility(View.INVISIBLE);
						error.setVisibility(View.INVISIBLE);
						
					}
				}
				else {
					dataArray.clear();
					adapter = new LohasAdapter(
							getActivity().getApplicationContext(), 
							dataArray, R.layout.row_main_list, 
							new String[]{"title", "addr"}, 
							new int[]{R.id.rowTitle, R.id.rowSubtitle});
					
					mShowList.setVisibility(View.INVISIBLE);
					nothing.setVisibility(View.INVISIBLE);
					loading.setVisibility(View.INVISIBLE);
					error.setVisibility(View.VISIBLE);
				}
			}
			
			@Override
			public void onFail(HttpResponse response, Exception ex) {
				Log.i(TAG, "ex: " + ex.getLocalizedMessage());
				dataArray.clear();
				adapter = new LohasAdapter(
						getActivity().getApplicationContext(), 
						dataArray, R.layout.row_main_list, 
						new String[]{"title", "addr"}, 
						new int[]{R.id.rowTitle, R.id.rowSubtitle});
				
				mShowList.setVisibility(View.INVISIBLE);
				nothing.setVisibility(View.INVISIBLE);
				loading.setVisibility(View.INVISIBLE);
				error.setVisibility(View.VISIBLE);
			}

			@Override
			public void onInvalidToken() {
				new AlertDialog.Builder(getActivity()).setTitle("warning").setMessage("invalid token").show();
			}

			@Override
			public void onProgress(Integer progress) {
			}
		});
    }
}
