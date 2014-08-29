package com.gocharm.lohaskh;

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

public class FavorArtFragment extends Fragment{
	private static final String TAG = "favArtFrag";
	private ListView mList;
	private ImageView nothing;
	private ImageView loading;
	private LinearLayout error;
	private DBHelper db;
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
    	mList = (ListView) view.findViewById(R.id.artList);
    	loading = (ImageView) view.findViewById(R.id.loading);
    	error = (LinearLayout)view.findViewById(R.id.error);
    	mList.setVisibility(View.INVISIBLE);
    	nothing.setVisibility(View.INVISIBLE);
    	error.setVisibility(View.INVISIBLE);
    	loading.setVisibility(View.VISIBLE);
    	cursor = db.select(DBHelper.TABLE_ART);
		if(cursor.getCount() > 0) {
			adapter = new CursorAdapter(getActivity().getApplicationContext(), cursor) {
				
				@Override
				public View newView(Context context, Cursor c, ViewGroup vg) {
					View view = LayoutInflater.from(context).inflate(R.layout.row_main_list, vg, false);
					
					return view;
				}
				
				@Override
				public void bindView(View v, Context context, Cursor c) {
					
					final String spID = c.getString(c.getColumnIndex(DBHelper.FIELD_SPID));
					final String title = c.getString(c.getColumnIndex(DBHelper.FIELD_TITLE));
					final String addr = c.getString(c.getColumnIndex(DBHelper.FIELD_ADDR));
					final String imgURL = c.getString(c.getColumnIndex(DBHelper.FIELD_IMAGEURL));
					final String share = c.getString(c.getColumnIndex(DBHelper.FIELD_SHARE));
					ImageView icon = (ImageView)v.findViewById(R.id.listIcon);
					TextView titleText = (TextView)v.findViewById(R.id.rowTitle);
					TextView subtitleText = (TextView)v.findViewById(R.id.rowSubtitle);
					ImageButton removeBut = (ImageButton) v.findViewById(R.id.collectBut);
					removeBut.setImageDrawable(getResources().getDrawable(R.drawable.favor_remove));
					ImageButton shareBut = (ImageButton) v.findViewById(R.id.shareBut);
					
					icon.setImageDrawable(getResources().getDrawable(R.drawable.nopic_arts));
					if(!imgURL.equals(""))
						ImageLoader.getInstance().displayImage(imgURL,	icon, Util.artOptions);
					
					if(!share.equals("")) {
						shareBut.setVisibility(View.VISIBLE);
						shareBut.setOnClickListener(new OnClickListener() {
							
							@Override
							public void onClick(View v) {
								Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND); 
							    sharingIntent.setType("text/plain");
							    String shareBody = share;
							    sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, title);
							    sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
								startActivity(Intent.createChooser(sharingIntent, "Share via"));
							}
						});
					}
					icon.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View v) {
							
							ReqUtil.send("twShow/show/info/" + spID, null, new COIMCallListener() {
								
								@Override
								public void onSuccess(JSONObject result) {
									if(Assist.getErrCode(result) == 0) {
										
										try {
											JSONObject object = result.getJSONObject("value");
											object.put("spID", spID);
											object.put("addr", addr);//c.getString(c.getColumnIndex("addr")));
											String extra = object.toString();
											
											Intent intent = new Intent(getActivity(), ArtDetailActivity.class);
											intent.putExtra("artInfo", extra);
											startActivity(intent);
										} catch (JSONException e) {
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

								@Override
								public void onInvalidToken() {
									new AlertDialog.Builder(getActivity()).setTitle("warning").setMessage("invalid token").show();
								}
							});
						}
					});
					removeBut.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View v) {
							db.delete(spID, DBHelper.TABLE_ART);
							Cursor newCursor = db.select(DBHelper.TABLE_ART);
							if(newCursor.getCount() > 0)
								adapter.changeCursor(newCursor);
							else {
								mList.setVisibility(View.INVISIBLE);
								nothing.setVisibility(View.VISIBLE);
							}
						}
					});
					titleText.setText(title);
					subtitleText.setText(addr);
					
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
	
	@Override
	public void onResume() {
		Cursor newCursor = db.select(DBHelper.TABLE_ART);
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
		db.close();
		super.onDestroy();
	}
}
