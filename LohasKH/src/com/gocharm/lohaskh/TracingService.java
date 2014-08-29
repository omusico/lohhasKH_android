package com.gocharm.lohaskh;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class TracingService extends Service {
	private static final String TAG = "tracingService";
	private LocationManager lm;
	private boolean isGPSEnabled, isNETEnabled;
	private JSONArray tracingPos;
	private Location mLastLocation;
	private class myLocationListener implements android.location.LocationListener {
		public myLocationListener(String provider) {
	        Log.e(TAG, "LocationListener " + provider);
	        mLastLocation = new Location(provider);
	    }
		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {}
		
		@Override
		public void onProviderEnabled(String provider) { }
		
		@Override
		public void onProviderDisabled(String provider) { }
		
		@Override
		public void onLocationChanged(Location location) {
			// TODO Auto-generated method stub
			Log.i(TAG, System.currentTimeMillis()/1000 + " - new pos: " + location);
			//LatLng newPoint = new LatLng(location.getLatitude(), location.getLongitude());
			mLastLocation = location;
			JSONObject newPoint = new JSONObject();
			try {
				newPoint.put("lat", location.getLatitude());
				newPoint.put("lng", location.getLongitude());
				tracingPos.put(newPoint);
				Intent i = new Intent();
				i.putExtra("tracing", tracingPos.toString());
				i.setAction("com.gocharm.lohaskh.tracing");
				sendBroadcast(i);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
		}
	}
	
	myLocationListener[] mLocationListeners = new myLocationListener[] {
        new myLocationListener(LocationManager.GPS_PROVIDER),
        new myLocationListener(LocationManager.NETWORK_PROVIDER)
    };
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		lm.removeUpdates(mLocationListeners[0]);
		lm.removeUpdates(mLocationListeners[1]);
		super.onDestroy();
	}
	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub\
		Log.i(TAG, "service created");
		tracingPos = new JSONArray();
		lm = (LocationManager)getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        isGPSEnabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        isNETEnabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        if(!isGPSEnabled && ! isNETEnabled) {
        	
        }
        else {
	        if(isGPSEnabled) {
	        	lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, mLocationListeners[0]);
	        }
	        if(isNETEnabled)
	        	lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, mLocationListeners[1]);
        }
        setNotification("start foreground service");
        /*Notification notification = new Notification(R.drawable.appicon, "notification of fg service",
		        System.currentTimeMillis());
		Intent notificationIntent = new Intent(this, TracingActivity.class);
		
		//Bundle b = new Bundle();
		//notificationIntent.putExtras(b);
		
		PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent, 0);
		notification.setLatestEventInfo(getApplicationContext(), "fg noti title",
		        "fg noti message", pendingIntent);
		startForeground(999, notification);*/
		super.onCreate();
	}
	
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	private void setNotification(String notificationMessage) {

		//**add this line**
		//int requestID = (int) System.currentTimeMillis();

		//NotificationManager mNotificationManager  = (NotificationManager) getApplication().getSystemService(Context.NOTIFICATION_SERVICE);

		Intent notificationIntent = new Intent(getApplicationContext(), TracingActivity.class);

		//**add this line**
		notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP); 
		notificationIntent.putExtra("tracing", tracingPos.toString());
		//**edit this line to put requestID as requestCode**
		PendingIntent contentIntent = PendingIntent.getActivity(this, 999, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		notificationIntent.putExtra("lat", mLastLocation.getLatitude());
		notificationIntent.putExtra("lnt", mLastLocation.getLongitude());
		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext())
		.setSmallIcon(R.drawable.appicon)
		.setContentTitle("My Notification")
		.setStyle(new NotificationCompat.BigTextStyle()
		.bigText(notificationMessage))
		.setContentText(notificationMessage).setAutoCancel(true);
		mBuilder.setContentIntent(contentIntent);
		//Notification notification = mBuilder.getNotification();
		startForeground(999, mBuilder.build());
	}
	
}
