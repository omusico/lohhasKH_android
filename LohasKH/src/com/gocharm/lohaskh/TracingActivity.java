package com.gocharm.lohaskh;


import org.json.JSONArray;
import org.json.JSONException;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class TracingActivity extends FragmentActivity {
	private static final String TAG = "tracingAct";
	private BroadcastReceiver receiver;
	private IntentFilter filter;
	
	private class checkReceiver extends BroadcastReceiver{
		@Override
		public void onReceive(Context context, Intent intent) {
			String array = intent.getStringExtra("tracing");
			JSONArray points;
			try {
				points = new JSONArray(array);
				((TracingMapFragment)getSupportFragmentManager().findFragmentById(R.id.container)).drawDirection(points);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_tracing);
		
		receiver = new checkReceiver();
		filter = new IntentFilter();
		filter.addAction("com.gocharm.lohaskh.tracing");
		registerReceiver(receiver, filter);
		
		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction().add(R.id.container, new TracingMapFragment()).commit();
		}
	}
	
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		String array = getIntent().getStringExtra("tracing");
		Log.i(TAG, "passed pos: \n" + array);
		JSONArray points;
		try {
			points = new JSONArray(array);
			if(points.length() > 0)
				((TracingMapFragment)getSupportFragmentManager().findFragmentById(R.id.container)).drawDirection(points);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.tracing, menu);
		return true;
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		unregisterReceiver(receiver);
		super.onDestroy();
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			Intent intent = new Intent(this, TracingService.class);
			stopService(intent);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

}
