package com.gocharm.lohaskh;

import java.util.ArrayList;
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
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

@SuppressLint("ValidFragment")
public class VistaFragment extends Fragment{
	private static final String TAG = "vistaFrag";
	private int pageN;
	private ListView mShowList;
	private LinearLayout error;
	private ImageView nothing, loading;
	private String catID;
	
	private LohasAdapter adapter;
	private ArrayList<HashMap<String,String>> dataArray = new ArrayList<HashMap<String,String>>();
	private ProgressDialog pd;
	
	private DBHelper db;
    private AsyncTask<String, Integer, String> mTask = null;
    
    public static VistaFragment newInstance(int index) {
    	VistaFragment f = new VistaFragment();
	    Bundle args = new Bundle();
	    args.putInt("args", index);
	    f.setArguments(args);
	    return f;
	}
    
    public VistaFragment(){}

    @Override
    public void onResume() {
    	super.onResume();
    }
    
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	pageN = getArguments().getInt("args");
    }
    
    @Override
    public void onDestroy() {
    	if(mTask != null) 
    		mTask.cancel(true);
    	super.onDestroy();
    	db.close();
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		//imageLoader = ImageLoader.getInstance();
    	db  = new DBHelper(getActivity().getApplicationContext());
    	catID = getActivity().getSharedPreferences(Util.PREF_NAME, 0).getString("vista_cat_id_" + pageN, "1");//Util.vistaCatMap.get(pageN).getName();
    	View view = inflater.inflate(R.layout.fragment_art, container, false);
    	mShowList = (ListView) view.findViewById(R.id.artList);
    	nothing = (ImageView) view.findViewById(R.id.nothing);
    	nothing.setVisibility(View.INVISIBLE);
    	loading = (ImageView) view.findViewById(R.id.loading);
    	loading.setVisibility(View.VISIBLE);
    	error = (LinearLayout) view.findViewById(R.id.error);
    	error.setVisibility(View.INVISIBLE);
    	
    	getList(catID);
    	
    	return view;
    }
    
    private void getList(String catID) {
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
    	mapParam.put("tag", catID);
    	Log.i(TAG,"catID: " + catID);
    	mTask = ReqUtil.send("twVista/vista/list/15", mapParam, new COIMCallListener() {
    		@Override
			public void onInvalidToken() {
				new AlertDialog.Builder(getActivity()).setTitle("warning").setMessage("invalid token").show();
			}
    		
			@Override
			public void onSuccess(JSONObject result) {
				Log.i(TAG,"res: " + result);
				if(0 == Assist.getErrCode(result)) {
					JSONArray arr= new JSONArray();
					arr = Assist.getList(result);
					
					if(arr.length() > 0) {
						dataArray.clear();
						for(int i = 0; i < arr.length(); i++) {
							HashMap<String, String> item = new HashMap<String, String>();
							JSONObject obj;
							try {
								obj = (JSONObject) arr.get(i);
								String auxString = obj.getString("aux");
								JSONObject aux = new JSONObject(auxString);
								item.put("title", obj.getString("title"));
								item.put("geID", obj.getString("geID"));
								item.put("openTime", aux.getString("openTime"));
								item.put("tel", aux.getString("tel"));
								item.put("addr", obj.getString("addr"));
								item.put("ngID", obj.getString("ngID"));
								item.put("imgURL", obj.getString("imgURL"));
								item.put("lat", obj.getString("latitude"));
								item.put("lng", obj.getString("longitude"));
								dataArray.add(item);
							
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
								ImageView icon = (ImageView)view.findViewById(R.id.listIcon);
								icon.setImageDrawable(getResources().getDrawable(R.drawable.nopic_vista));
								if(!dataArray.get(position).get("imgURL").equals("")) {
									ImageLoader.getInstance().displayImage(dataArray.get(position).get("imgURL"), icon, Util.vistaOptions);//, new Util.AnimateFirstDisplayListener());
								}
								
								
								if(db.hasID(dataArray.get(position).get("ngID"), DBHelper.TABLE_VISTA)) {
									collectBut.setImageDrawable(getResources().getDrawable(R.drawable.favor_remove));
								}
								else {
									collectBut.setImageDrawable(getResources().getDrawable(R.drawable.favor_add));
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
												pd = ProgressDialog.show(getActivity(), "讀取中", "取得景點資訊中…");
												Map<String, Object> mapParam = new HashMap<String, Object>();
												mapParam.put("icon", "1");
												ReqUtil.send("twVista/vista.page/view/" + dataArray.get(position).get("geID") + "." + dataArray.get(position).get("ngID"), null, new COIMCallListener() {
													@Override
													public void onInvalidToken() {
														new AlertDialog.Builder(getActivity()).setTitle("warning").setMessage("invalid token").show();
														
													}
													
													@Override
													public void onSuccess(JSONObject result) {
														Log.i(TAG, "res: " + result);
														pd.dismiss();
														if(0 == Assist.getErrCode(result)) {
															
															JSONObject res = Assist.getValue(result);
															JSONObject extraObj = new JSONObject(dataArray.get(position));
															try {
																extraObj.put("descTx", res.getString("body"));
															} catch (JSONException e) {
																e.printStackTrace();
															}
															String extra = extraObj.toString();
															Intent intent = new Intent(getActivity(), VistaDetailActivity.class);
															intent.putExtra("vistaInfo", extra);
															startActivity(intent);
														}
														else {
															Log.i(TAG, "err: " + Assist.getMessage(result));
														}
													}
													
													@Override
													public void onFail(HttpResponse response, Exception ex) {
														pd.dismiss();
														Log.i(TAG, "ex: " + ex.getLocalizedMessage());
													}
												});
												
												break;
											case R.id.collectBut:
												//Map<String, String> map = new HashMap<String, String>();
												//map.put("addr", dataArray.get(position).get("addr"));
												//map.put("ngID", dataArray.get(position).get("ngID"));
												//map.put("title", dataArray.get(position).get("title"));
												//map.put("imgURL", dataArray.get(position).get("imgURL"));
												//map.put("tel", dataArray.get(position).get("tel"));
												//map.put("lat", dataArray.get(position).get("lat"));
												//map.put("lng", dataArray.get(position).get("lng"));
												//map.put("openTime", dataArray.get(position).get("openTime"));
												String ngID = dataArray.get(position).get("ngID");
												if(db.hasID(ngID, DBHelper.TABLE_VISTA)){
													db.delete(ngID, DBHelper.TABLE_VISTA);
													collectBut.setImageDrawable(getResources().getDrawable(R.drawable.favor_add));
												}
												else {
													db.insert(dataArray.get(position), DBHelper.TABLE_VISTA);
													collectBut.setImageDrawable(getResources().getDrawable(R.drawable.favor_remove));
												}
												
												break;
										}
									}
								};
								
								collectBut.setOnClickListener(listener);
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
								new int[]{R.id.rowTitle,R.id.rowSubtitle});
						mShowList.setAdapter(adapter);
						
						mShowList.setVisibility(View.INVISIBLE);
						nothing.setVisibility(View.VISIBLE);
						loading.setVisibility(View.INVISIBLE);
						error.setVisibility(View.INVISIBLE);
						
					}
				}
				else {
					Log.i(TAG, "err: " + Assist.getMessage(result));
				}
			}
			
			@Override
			public void onFail(HttpResponse response, Exception ex) {
				Log.i(TAG, "ex: " + ex.getLocalizedMessage() + ", mshowList: " + mShowList);
				dataArray.clear();
				adapter = new LohasAdapter(
						getActivity().getApplicationContext(), 
						dataArray, R.layout.row_main_list, 
						new String[]{"title", "addr"}, 
						new int[]{R.id.rowTitle,R.id.rowSubtitle});
				mShowList.setAdapter(adapter);
				
				mShowList.setVisibility(View.INVISIBLE);
				nothing.setVisibility(View.INVISIBLE);
				loading.setVisibility(View.INVISIBLE);
				error.setVisibility(View.VISIBLE);
			}
		});
    }
}
