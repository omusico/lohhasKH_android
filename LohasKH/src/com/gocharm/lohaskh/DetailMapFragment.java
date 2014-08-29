package com.gocharm.lohaskh;

import java.util.Date;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class DetailMapFragment extends SupportMapFragment{
	private static final String TAG = "detailMapFrag";
	private LatLng curreLatLng;
	private LocationManager lm;
	private GoogleMap mapView;
	private class myLocationListener implements android.location.LocationListener {
		Location mLastLocation;
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
			lm.removeUpdates(mLL);
			// TODO Auto-generated method stub
			Log.i(TAG, new Date() + ": location updated, " + mLastLocation.getProvider());
			curreLatLng = new LatLng(location.getLatitude(), location.getLongitude());
			
			mapView.addMarker(new MarkerOptions()
					.title("目前位置")
					.position(curreLatLng)
					.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));
		}
	}
	
	private myLocationListener mLL = new myLocationListener(LocationManager.NETWORK_PROVIDER);
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		lm = (LocationManager)getActivity().getApplicationContext().getSystemService(Context.LOCATION_SERVICE);

		lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, mLL);
		super.onCreate(savedInstanceState);
	}
	
}
