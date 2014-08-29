package com.gocharm.lohaskh;

import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpResponse;
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

public class FavorVistaFragment extends Fragment{
	private static final String TAG = "favVistaFrag";
	private ImageView nothing;
	private ImageView loading;
	private LinearLayout error;
	private DBHelper db;
	private ListView mList;
	private Cursor cursor;
	private CursorAdapter adapter;	
	
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
    	loading = (ImageView) view.findViewById(R.id.loading);
    	mList = (ListView) view.findViewById(R.id.artList);
    	error = (LinearLayout) view.findViewById(R.id.error);
    	loading.setVisibility(View.VISIBLE);
    	nothing.setVisibility(View.INVISIBLE);
    	error.setVisibility(View.INVISIBLE);
    	mList.setVisibility(View.INVISIBLE);
    	
    	cursor = db.select(DBHelper.TABLE_VISTA);
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
					final JSONObject extraObject = new JSONObject();
					final String ngID = c.getString(c.getColumnIndex(DBHelper.FIELD_NGID));
					final String title = c.getString(c.getColumnIndex(DBHelper.FIELD_TITLE));
					final String addr = c.getString(c.getColumnIndex(DBHelper.FIELD_ADDR));
					final String tel = c.getString(c.getColumnIndex(DBHelper.FIELD_TEL));
					final String imgURL = c.getString(c.getColumnIndex(DBHelper.FIELD_IMAGEURL));
					ImageView icon = (ImageView)v.findViewById(R.id.listIcon);
					TextView titleText = (TextView)v.findViewById(R.id.rowTitle);
					TextView subtitleText = (TextView)v.findViewById(R.id.rowSubtitle);
					Log.i(TAG, "imgURL: " + imgURL);
					if (imgURL!=null && !imgURL.equals("")) {
						ImageLoader.getInstance().displayImage(imgURL,icon, Util.options);
					}
					titleText.setText(title);
					subtitleText.setText(addr);
					
					try {
						extraObject.put("ngID", ngID);
						extraObject.put("title", title);
						extraObject.put("addr", addr);
						extraObject.put("tel", tel);
						extraObject.put("lat", c.getString(c.getColumnIndex("lat")));
						extraObject.put("lng", c.getString(c.getColumnIndex("lng")));
						extraObject.put("imgURL", c.getString(c.getColumnIndex("imgURL")));
						extraObject.put("openTime", c.getString(c.getColumnIndex("openTime")));
					} catch (JSONException e1) {
					}
					
					icon.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View v) {
							Log.i(TAG, "NGID: " + ngID);
							Map<String, Object> mapParam = new HashMap<String, Object>();
							mapParam.put("icon", "1");
							ReqUtil.send("twVista/vista/view/" + ngID, null, new COIMCallListener() {
								@Override
								public void onInvalidToken() {
									new AlertDialog.Builder(getActivity()).setTitle("warning").setMessage("invalid token").show();
								}
								
								@Override
								public void onSuccess(JSONObject result) {
									if(Assist.getErrCode(result) == 0) {
										try {
											JSONObject res = (JSONObject) result.get("value");
											extraObject.put("descTx", res.getString("body"));
										} catch (JSONException e) {
										}
										String extra = extraObject.toString();
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
									Log.i(TAG, "ex: " + ex.getLocalizedMessage());
								}
							});
						}
					});
					
					ImageButton favorBur = (ImageButton)v.findViewById(R.id.collectBut);
					favorBur.setImageDrawable(getResources().getDrawable(R.drawable.favor_remove));
					favorBur.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View v) {
							if(db.hasID(ngID, DBHelper.TABLE_VISTA)) {
								db.delete(ngID, DBHelper.TABLE_VISTA);
								Cursor newCursor = db.select(DBHelper.TABLE_VISTA);
								if(newCursor.getCount() > 0)
									adapter.changeCursor(newCursor);
								else {
									mList.setVisibility(View.INVISIBLE);
									nothing.setVisibility(View.VISIBLE);
								}
							}
						}
					});
				}
			};
			mList.setAdapter(adapter);
			loading.setVisibility(View.INVISIBLE);
	    	nothing.setVisibility(View.INVISIBLE);
	    	error.setVisibility(View.INVISIBLE);
	    	mList.setVisibility(View.VISIBLE);
		}
		else {
			loading.setVisibility(View.INVISIBLE);
	    	nothing.setVisibility(View.VISIBLE);
	    	error.setVisibility(View.INVISIBLE);
	    	mList.setVisibility(View.INVISIBLE);
		}
    	return view;
    }
	
	@Override
	public void onResume() {
		Cursor newCursor = db.select(DBHelper.TABLE_VISTA);
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
