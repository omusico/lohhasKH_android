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
public class TicketFragment extends Fragment {
	private static final String TAG = "iticketFrag";
	private int pageN;
	private ListView mShowList;
	public ImageView nothing;
	public ImageView loading;
	public LinearLayout error;
	private LohasAdapter adapter;
	private ArrayList<HashMap<String,String>> dataArray = new ArrayList<HashMap<String,String>>();
	private ProgressDialog pd;
	
	private DBHelper db;
    private JSONArray prodList;
    private JSONObject extraObj;
    private String siID;
    private String catID;
    private AsyncTask<String, Integer, String> mTask = null;
    public TicketFragment(){}

    public static TicketFragment newInstance(int index) {
    	TicketFragment f = new TicketFragment();
	    Bundle args = new Bundle();
	    args.putInt("args", index);
	    f.setArguments(args);
		
	    return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	pageN = getArguments().getInt("args");
    }
    @Override
    public void onDestroy() {
    	Log.i(TAG, "destroy fragment");
    	if(mTask != null)
    		mTask.cancel(true);
    	db.close();
    	super.onDestroy();
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
    	db  = new DBHelper(getActivity().getApplicationContext());
    	catID = getActivity().getSharedPreferences(Util.PREF_NAME, 0).getString("ticket_cat_id_"+pageN, "1");//Util.ticketCatMap.get(pageN).getName();
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
    	
    	
    	
    	return view;
    }
    
    @Override
    public void onStart() {
    	super.onStart();
    	getList(catID);
    }
    
    private void queryProducts(final int index){
		try {
			ReqUtil.send("iticket/product/info/" + prodList.getJSONObject(index).getString("pdID") , null, new COIMCallListener() {
				@Override
				public void onInvalidToken() {
					new AlertDialog.Builder(getActivity()).setTitle("warning").setMessage("invalid token").show();
					
				}
				@Override
				public void onSuccess(JSONObject result) {
					Log.i(TAG, "prod info result:\n" + result);
					if(0 == Assist.getErrCode(result)) {
						
						try {
							JSONObject res = (JSONObject) result.get("value");
							JSONObject promo = res.getJSONObject("promotion");
							prodList.getJSONObject(index).put("price", res.getString("price"));
							if(promo.length() == 0)
								prodList.getJSONObject(index).put("offer", "null");
							else 
								prodList.getJSONObject(index).put("offer", promo.getString("offer"));
							if((index+1) < prodList.length()) {
								queryProducts(index+1);
							}
							else {
								extraObj.put("prodList", prodList);
								Log.i(TAG, "extra: " + extraObj.toString());
								pd.dismiss();
								Intent intent = new Intent(getActivity(), TicketDetailActivity.class);
								intent.putExtra("ticketInfo", extraObj.toString());
								startActivity(intent);
							}
						} catch (JSONException e) {
							if((index+1) < prodList.length()) {
								queryProducts(index+1);
							}
							else {
								pd.dismiss();
							}
							e.printStackTrace();
						}
					}
					else {
						Log.i(TAG, "err: " + Assist.getMessage(result));
					}
				}

				@Override
				public void onFail(HttpResponse response, Exception ex) {
					Log.i(TAG, "ex: " + ex.getLocalizedMessage());
				}
			});
		} catch (Exception e) {
		}
    }
    
    private void getList(String catID) {
    	Log.i(TAG, "activity: " + getActivity());
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
    	
    	mTask = ReqUtil.send("iticket/store/search/" + catID, mapParam, new COIMCallListener() {
    		@Override
			public void onInvalidToken() {
				new AlertDialog.Builder(getActivity()).setTitle("warning").setMessage("invalid token").show();
				
			}
			@Override
			public void onSuccess(JSONObject result) {
				Log.i(TAG, "result: " + result);
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
								item.put("title", obj.getString("storeName"));
								item.put("addr", obj.getString("addr"));
								item.put("tel", obj.getString("phone"));
								item.put("siID", obj.getString("siID"));
								item.put("lat", obj.getString("latitude"));
								item.put("lng", obj.getString("longitude"));
								item.put("imgURL", obj.getString("iconURI"));
								dataArray.add(item);
							} catch (JSONException e) {
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
								icon.setImageDrawable(getResources().getDrawable(R.drawable.nopic_ticket));
								if(!dataArray.get(position).get("imgURL").equals("")) {
									ImageLoader.getInstance().displayImage(dataArray.get(position).get("imgURL"), icon, Util.ticketOptions);//, new Util.AnimateFirstDisplayListener());
								}
								
								if(db.hasID(dataArray.get(position).get("siID"), DBHelper.TABLE_TICKET)) {
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
												siID = dataArray.get(position).get("siID");
												pd = ProgressDialog.show(getActivity(), "讀取中", "取得餐廳資訊…");
												Map<String, Object> mapParam = new HashMap<String, Object>();
												mapParam.put("detail", "1");
												ReqUtil.send("iticket/store/info/" + siID, mapParam, new COIMCallListener() {
													@Override
													public void onInvalidToken() {
														new AlertDialog.Builder(getActivity()).setTitle("warning").setMessage("invalid token").show();
													}
													@Override
													public void onSuccess(JSONObject result) {
														Log.i(TAG, "res: " + result);
														if(0 == Assist.getErrCode(result)) {
															
															try {
																JSONObject res = (JSONObject) result.get("value");
																extraObj = new JSONObject(dataArray.get(position));
																
																extraObj.put("openTime", res.getString("openTime"));
																extraObj.put("closeTime", res.getString("closeTime"));
																extraObj.put("restFrom", res.getString("restFrom"));
																extraObj.put("restTo", res.getString("restTo"));
																extraObj.put("name", res.getString("name"));
																extraObj.put("summary", res.getJSONObject("doc").getString("summary"));
																extraObj.put("siID", res.getString("siID"));
															
																ReqUtil.send("iticket/product/list/" + siID , null, new COIMCallListener() {
																	@Override
																	public void onInvalidToken() {
																		new AlertDialog.Builder(getActivity()).setTitle("warning").setMessage("invalid token").show();
																	}
																	
																	@Override
																	public void onSuccess(JSONObject result) {
																		if(0 == Assist.getErrCode(result)) {
																			try {
																				prodList = ((JSONObject) result.get("value")).getJSONArray("list");
																				queryProducts(0);
																				
																			} catch (JSONException e) {
																				pd.dismiss();
																			}
																		}
																		else {
																			pd.dismiss();
																			Log.i(TAG, "err: " + Assist.getMessage(result));
																		}
																	}
																	
																	@Override
																	public void onFail(HttpResponse response, Exception ex) {
																		Log.i(TAG,"ex: " + ex.getLocalizedMessage());
																		pd.dismiss();
																	}
																});
															} catch (JSONException e) {
															}
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
												if(db.hasID(dataArray.get(position).get("siID"), DBHelper.TABLE_TICKET)){
													db.delete(dataArray.get(position).get("siID"), DBHelper.TABLE_TICKET);
													collectBut.setImageDrawable(getResources().getDrawable(R.drawable.favor_add));
												}
												else{ 
													db.insert(dataArray.get(position), DBHelper.TABLE_TICKET);
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
								new String[]{"title","addr"}, 
								new int[]{R.id.rowTitle, R.id.rowSubtitle});
						mShowList.setAdapter(adapter);
						
						mShowList.setVisibility(View.INVISIBLE);
						loading.setVisibility(View.INVISIBLE);
						error.setVisibility(View.INVISIBLE);
						nothing.setVisibility(View.VISIBLE);
					}
				}
				else {
					Log.i(TAG, "err: " + Assist.getMessage(result));
					dataArray.clear();
					adapter = new LohasAdapter(
							getActivity().getApplicationContext(), 
							dataArray, R.layout.row_main_list, 
							new String[]{"title"}, 
							new int[]{R.id.rowTitle});
					mShowList.setAdapter(adapter);
					mShowList.setVisibility(View.INVISIBLE);
					nothing.setVisibility(View.INVISIBLE);
					loading.setVisibility(View.INVISIBLE);
					error.setVisibility(View.VISIBLE);
				}
			}
			
			@Override
			public void onFail(HttpResponse response, Exception ex) {
				Log.i(TAG, "ex: " + ex.getLocalizedMessage() + ", mshowList: " + mShowList);
				dataArray.clear();
				adapter = new LohasAdapter(
						getActivity().getApplicationContext(), 
						dataArray, R.layout.row_main_list, 
						new String[]{"title"}, 
						new int[]{R.id.rowTitle});
				mShowList.setAdapter(adapter);
				mShowList.setVisibility(View.INVISIBLE);
				nothing.setVisibility(View.INVISIBLE);
				loading.setVisibility(View.INVISIBLE);
				error.setVisibility(View.VISIBLE);
			}
		});
    }
}
