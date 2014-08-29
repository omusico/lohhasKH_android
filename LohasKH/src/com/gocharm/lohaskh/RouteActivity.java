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

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class RouteActivity extends ActionBarActivity {
	private static final String TAG = "routeInfo";
	private ListView stopList;
	private SimpleAdapter adapter;
	private ArrayList<HashMap<String,String>> dataArray = new ArrayList<HashMap<String,String>>();
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putString("brID", getIntent().getExtras().getString("brID"));
		outState.putString("title", getIntent().getExtras().getString("title"));
		super.onSaveInstanceState(outState);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		String brID = "";
		String title = "";
		if(savedInstanceState == null) {
			title = getIntent().getExtras().getString("title");
			brID = getIntent().getExtras().getString("brID");
		}
		else {
			title = savedInstanceState.getString("title");
			brID = savedInstanceState.getString("brID");
		}
		setContentView(R.layout.activity_route);
		
		stopList = (ListView) findViewById(R.id.stopList);
		getSupportActionBar().setTitle(title);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		ReqUtil.send("twCtBus/busRoute/next/" + brID, null, new COIMCallListener() {
			@Override
			public void onInvalidToken() {
				new AlertDialog.Builder(getApplicationContext()).setTitle("warning").setMessage("invalid token").show();
				
			}
			@Override
			public void onSuccess(JSONObject result) {
				if(0 == Assist.getErrCode(result)) {
					
					try {
						JSONObject value = (JSONObject)result.get("value");
						JSONArray list = value.getJSONArray("list");
						for (int i=0; i<list.length();i++) {
							HashMap<String, String> item = new HashMap<String, String>();
							String stName = (list.getJSONObject(i).getString("stName") == null)?"":list.getJSONObject(i).getString("stName");
							String arvTime = (list.getJSONObject(i).has("arvTime"))?list.getJSONObject(i).getString("arvTime"):"";
							if (arvTime.equals("")) {
								arvTime = "未提供";
							}
							item.put("stName",  stName);
							item.put("arvTime", arvTime);
							item.put("isInBd", list.getJSONObject(i).getString("isInBd"));
							dataArray.add(item);
						}
						Log.i(TAG, "success\n" + dataArray);
						
						adapter = new SimpleAdapter(getApplicationContext(), 
								dataArray, 
								R.layout.row_stop_time,
								new String[]{"stName", "arvTime"}, 
								new int[]{R.id.stopText, R.id.timeText}){
	 
				        		@SuppressLint("ResourceAsColor")
								public View getView(int position, View convertView, ViewGroup parent) {
				        			View view = super.getView(position, convertView, parent);
				        			TextView text1 = (TextView) view.findViewById(R.id.stopText);
				        			text1.setTextColor(R.color.BlueColor);
				        			TextView text2 = (TextView) view.findViewById(R.id.timeText);
				        			text2.setTextColor(R.color.BlackColor);
				        			ImageView gobackImg = (ImageView) view.findViewById(R.id.gobackImage);
				        			if(dataArray.get(position).get("isInBd").equals("0")) {
				        				gobackImg.setImageResource(R.drawable.go);
				        				view.setBackgroundResource(R.drawable.bg_green);
				        			}
				        			else {
				        				gobackImg.setImageResource(R.drawable.back);
				        				view.setBackgroundResource(R.drawable.bg_pink);
				        			}
				        			return view;
	
				        		};
						};
						try {
						stopList.setAdapter(adapter);
						}catch(Exception e) {
							Log.i(TAG, "e: " + e.getLocalizedMessage());
						}
					} catch (JSONException e) {
					}
				}
				else {
					Log.i(TAG, "err: " + Assist.getMessage(result));
				}
			}
			
			@Override
			public void onFail(HttpResponse response, Exception ex) {
				Log.i(TAG, "fail\n" + ex.getLocalizedMessage());
			}
		});
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()){
			case android.R.id.home:
				finish();
				break;
		}
		return super.onOptionsItemSelected(item);
	}
}
