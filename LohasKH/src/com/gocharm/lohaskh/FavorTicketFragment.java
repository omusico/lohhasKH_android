package com.gocharm.lohaskh;

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

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class FavorTicketFragment extends Fragment{
	private static final String TAG = "favTicketFrag";
	private ImageView nothing;
	private ImageView loading;
	private LinearLayout error;
	private DBHelper db;
	private ListView mList;
	private CursorAdapter adapter;
	private JSONArray prodList;
	private Cursor cursor;
	
	@Override
	public void onDestroyView() {
		super.onDestroyView();
		cursor.close();
		db.close();
	}
	
	@SuppressWarnings("deprecation")
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	db  = new DBHelper(getActivity().getApplicationContext());
    	View view = inflater.inflate(R.layout.fragment_art, container, false);
    	nothing = (ImageView) view.findViewById(R.id.nothing);
    	mList = (ListView) view.findViewById(R.id.artList);
    	loading = (ImageView) view.findViewById(R.id.loading);
    	error = (LinearLayout) view.findViewById(R.id.error);
    	//extraObj = new JSONObject();
    	loading.setVisibility(View.VISIBLE);
    	nothing.setVisibility(View.INVISIBLE);
    	error.setVisibility(View.INVISIBLE);
    	mList.setVisibility(View.INVISIBLE);
    	
    	
    	cursor = db.select(DBHelper.TABLE_TICKET);
    	Log.i(TAG, "n data: " + cursor.getCount());
		if(cursor.getCount() > 0) {
			adapter = new CursorAdapter(getActivity().getApplicationContext(), cursor) {
				
				@Override
				public View newView(Context context, Cursor c, ViewGroup vg) {
					View view = LayoutInflater.from(context).inflate(R.layout.row_main_list, vg, false);
					return view;
				}
				
				@Override
				public void bindView(View v, Context context, Cursor c) {
					final JSONObject extraObj = new JSONObject();
					final String siID = c.getString(c.getColumnIndex(DBHelper.FIELD_SIID));
					final String title = c.getString(c.getColumnIndex(DBHelper.FIELD_TITLE));
					final String addr = c.getString(c.getColumnIndex(DBHelper.FIELD_ADDR));
					final String tel = c.getString(c.getColumnIndex(DBHelper.FIELD_TEL));
					final String lat = c.getString(c.getColumnIndex(DBHelper.FIELD_LAT));
					final String lng = c.getString(c.getColumnIndex(DBHelper.FIELD_LNG));
					final String imgURL = c.getString(c.getColumnIndex(DBHelper.FIELD_IMAGEURL));
					try {
						extraObj.put("siID", siID);
						extraObj.put("name", title);
						extraObj.put("addr", addr);
						extraObj.put("tel", tel);
						extraObj.put("lat",lat);
						extraObj.put("lng", lng);
						extraObj.put("imgURL", imgURL);
						Log.i(TAG, "imgURL in extra: "+ imgURL);
					} catch (JSONException e1) {
					}
					
					ImageView icon = (ImageView)v.findViewById(R.id.listIcon);
					TextView titleText = (TextView)v.findViewById(R.id.rowTitle);
					TextView subtitleText = (TextView)v.findViewById(R.id.rowSubtitle);
					ImageButton favorBur = (ImageButton)v.findViewById(R.id.collectBut);
					icon.setImageDrawable(getResources().getDrawable(R.drawable.nopic_ticket));
					if(!imgURL.equals("")) {
						ImageLoader.getInstance().displayImage(imgURL, icon, Util.ticketOptions);
					}
					favorBur.setImageDrawable(getResources().getDrawable(R.drawable.favor_remove));
					favorBur.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View v) {
							if(db.hasID(siID, DBHelper.TABLE_TICKET)) {
								db.delete(siID, DBHelper.TABLE_TICKET);
								Cursor newCursor = db.select(DBHelper.TABLE_TICKET);
								Log.i(TAG, "new Cursor: " + newCursor.getCount());
								if(newCursor.getCount() > 0)
									adapter.changeCursor(newCursor);
								else {
									mList.setVisibility(View.INVISIBLE);
									nothing.setVisibility(View.VISIBLE);
								}
							}
						}
					});
					titleText.setText(title);
					subtitleText.setText(addr);
					
					icon.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View v) {
							Map<String, Object> mapParam = new HashMap<String, Object>();
							mapParam.put("detail", "1");
							ReqUtil.send("iticket/store/info/" + siID, mapParam, new COIMCallListener() {
								@Override
								public void onInvalidToken() {
									new AlertDialog.Builder(getActivity()).setTitle("warning").setMessage("invalid token").show();
								}
								
								@Override
								public void onSuccess(JSONObject result) {
									if(Assist.getErrCode(result) == 0) {
										
										try {
											JSONObject res = result.getJSONObject("value");
											extraObj.put("openTime", res.getString("openTime"));
											extraObj.put("closeTime", res.getString("closeTime"));
											extraObj.put("restFrom", res.getString("restFrom"));
											extraObj.put("restTo", res.getString("restTo"));
											extraObj.put("name", res.getString("name"));
											extraObj.put("summary", res.getJSONObject("doc").getString("summary"));
											
										
											ReqUtil.send("iticket/store/listProd/" + siID , null, new COIMCallListener() {
												@Override
												public void onInvalidToken() {
													new AlertDialog.Builder(getActivity()).setTitle("warning").setMessage("invalid token").show();
												}
												
												@Override
												public void onSuccess(JSONObject result) {
													Log.i(TAG, "res: " + result);
													if(Assist.getErrCode(result) == 0) {
														try {
															prodList = ((JSONObject) result.get("value")).getJSONArray("list");
															queryProducts(0,extraObj);
														} catch (JSONException e) {
														}
													}
													else {
														Log.i(TAG, "err: " + Assist.getMessage(result));
													}
												}
												
												@Override
												public void onFail(HttpResponse response, Exception ex) {
												}
											});
										} catch (JSONException e) {
										}
									}
									else {
										
									}Log.i(TAG, "err: " + Assist.getMessage(result));
								}
								
								@Override
								public void onProgress(Integer progress) {
									
								}
								
								@Override
								public void onFail(HttpResponse response, Exception ex) {
									Log.i(TAG, "ex: " + ex.getLocalizedMessage());
								}
							});
						}
					});
				}
			};
			mList.setAdapter(adapter);
			
			mList.setVisibility(View.VISIBLE);
	    	nothing.setVisibility(View.INVISIBLE);
	    	error.setVisibility(View.INVISIBLE);
	    	loading.setVisibility(View.INVISIBLE);
		}
		else {
			mList.setVisibility(View.INVISIBLE);
	    	nothing.setVisibility(View.VISIBLE);
	    	error.setVisibility(View.INVISIBLE);
	    	loading.setVisibility(View.INVISIBLE);
		}
    	return view;
    }
	
	private void queryProducts(final int index, final JSONObject extraObj){
		try {
			ReqUtil.send("iticket/product/info/" + prodList.getJSONObject(index).getString("pdID") , null, new COIMCallListener() {
				@Override
				public void onInvalidToken() {
					new AlertDialog.Builder(getActivity()).setTitle("warning").setMessage("invalid token").show();
				}
				
				@Override
				public void onSuccess(JSONObject result) {
					if(Assist.getErrCode(result) == 0) {
						
						try {
							JSONObject res = (JSONObject) result.get("value");
							JSONObject promo = res.getJSONObject("promotion");
							prodList.getJSONObject(index).put("price", res.getString("price"));
							prodList.getJSONObject(index).put("offer", promo.getString("offer"));
							if((index+1) < prodList.length()) {
								queryProducts(index+1,extraObj);
							}
							else {
								extraObj.put("prodList", prodList);
								Log.i(TAG, "extra: " + extraObj.toString());
								Intent intent = new Intent(getActivity(), TicketDetailActivity.class);
								intent.putExtra("ticketInfo", extraObj.toString());
								startActivity(intent);
							}
						} catch (JSONException e) {
							if((index+1) < prodList.length()) {
								queryProducts(index+1,extraObj);
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
	
	@Override
	public void onResume() {
		Cursor newCursor = db.select(DBHelper.TABLE_TICKET);
		if(newCursor.getCount() > 0)
			adapter.changeCursor(newCursor);
		else {
			mList.setVisibility(View.INVISIBLE);
			nothing.setVisibility(View.VISIBLE);
		}
		super.onResume();
	}
	
	@Override
	public void onDestroy() {
		cursor.close();
		db.close();
		super.onDestroy();
	}
}
