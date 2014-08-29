package com.gocharm.lohaskh;

import org.json.JSONArray;
import org.json.JSONException;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

public class TracingMapFragment extends SupportMapFragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View v = super.onCreateView(inflater, container, savedInstanceState); 
		getMap().setMyLocationEnabled(true);
		getMap().setMapType(GoogleMap.MAP_TYPE_NORMAL);
		getMap().getUiSettings().setAllGesturesEnabled(false);
		double lat = getActivity().getIntent().getExtras().getDouble("lat");
		double lng = getActivity().getIntent().getExtras().getDouble("lng");
		getMap().moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lng), 18));
		
		return v;
	}
	
	public void drawDirection(JSONArray points){
		PolylineOptions rectLine = new PolylineOptions().width(5).color(Color.BLUE);
		LatLng point;
    	for(int i = 0 ; i < points.length() ; i++) {
    		try {
    			double lat = points.getJSONObject(i).getDouble("lat");
    			double lng = points.getJSONObject(i).getDouble("lng");
    			point = new LatLng(lat, lng);
				rectLine.add(point);
				if(i == (points.length()-1)) {
					getMap().moveCamera(CameraUpdateFactory.newLatLng(new LatLng(lat, lng)));
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
    	
    	GoogleMap mapView = getMap();
    	try {
			mapView.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(points.getJSONObject(points.length()-1).getDouble("lat"), points.getJSONObject(points.length()-1).getDouble("lng"))));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	//mapView.clear();
    	getMap().addPolyline(rectLine);
	}
}
