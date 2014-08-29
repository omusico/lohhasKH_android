package com.gocharm.lohaskh;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.coimotion.csdk.common.COIMCallListener;
import com.coimotion.csdk.util.Assist;
import com.coimotion.csdk.util.ReqUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;


@SuppressLint("ValidFragment")
public class TargetMapFragment extends SupportMapFragment{
	private static final String TAG = "targetMapFrag";
	private GoogleMap mapView = null;
	private JSONArray content;
	private JSONArray busStops;
	private ArrayList<Marker> markerList;
	private Map<String, JSONArray> routesForStop;
	
	public static TargetMapFragment newInstance(JSONArray cont) {
		TargetMapFragment f = new TargetMapFragment();
	    Bundle args = new Bundle();
	    args.putString("args", cont.toString());
	    f.setArguments(args);
	    return f;
	}
	
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		mapView = getMap();
		try {
			LatLng target = new LatLng(Double.parseDouble(content.getJSONObject(0).getString("latitude")), Double.parseDouble(content.getJSONObject(0).getString("longitude")));
			mapView.moveCamera(CameraUpdateFactory.newLatLngZoom(target, 16));
			MarkButStops(target);
			setMarker(target, content.getJSONObject(0).getString("placeName"), 354.0f);
		} catch (JSONException e) {
		}
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		try {
			content = new JSONArray(getArguments().getString("args"));
		} catch (JSONException e) {
		}
		markerList = new ArrayList<Marker>();
		routesForStop = new HashMap<String, JSONArray>();
	}
	
	public void setMarker(LatLng pos, String title, float HUE) {
		mapView.addMarker(new MarkerOptions()
			.position(pos)
			.title(title)
			.icon(BitmapDescriptorFactory.defaultMarker(HUE)));
	}
	
	public void MarkButStops(LatLng position){
		Map<String, Object> mapParam = new HashMap<String, Object>();
		Log.i(TAG, "mark bus stops");
		mapParam.put("lat", position.latitude);
		mapParam.put("lng", position.longitude);
		mapParam.put("r", "0.3");
		ReqUtil.send("twCtBus/busStop/search", mapParam, new COIMCallListener() {
			@Override
			public void onInvalidToken() {
				new AlertDialog.Builder(getActivity()).setTitle("warning").setMessage("invalid token").show();
				
			}
			@Override
			public void onSuccess(JSONObject result) {
				if(0 == Assist.getErrCode(result)) {
					try {
						busStops = ((JSONObject)result.get("value")).getJSONArray("list");
					} catch (JSONException e) {
					}
					for (int i =0, l= busStops.length(); i<l; i++) {
						try {
							JSONObject stop = busStops.getJSONObject(i);
							LatLng pos = new LatLng(stop.getDouble("latitude"), stop.getDouble("longitude"));
							Marker a = mapView.addMarker(new MarkerOptions()
														.position(pos)
														.title(stop.getString("stName"))
														.icon(BitmapDescriptorFactory.defaultMarker(108.0f)));
							markerList.add(a);
							
							mapView.setOnMarkerClickListener(new OnMarkerClickListener() {
								
								@Override
								public boolean onMarkerClick(final Marker marker) {
									Log.i(TAG, "marker click");
									if(markerList.contains(marker)) {
										String tsID = "";
										try {
											tsID = busStops.getJSONObject(markerList.indexOf(marker)).getString("tsID");
										} catch (JSONException e) {
										}
										ReqUtil.send("twCtBus/busStop/routes/"+tsID, null, new COIMCallListener() {
											@Override
											public void onInvalidToken() {
												new AlertDialog.Builder(getActivity()).setTitle("warning").setMessage("invalid token").show();
												
											}
											@Override
											public void onSuccess(JSONObject result) {
												Log.i(TAG, "res: " + result);
												if(0 == Assist.getErrCode(result)) {
													JSONArray list = new JSONArray();
													try {
														list = ((JSONObject)result.get("value")).getJSONArray("list");
														routesForStop.put(busStops.getJSONObject(markerList.indexOf(marker)).getString("tsID"), list);
													} catch (JSONException e) {
														e.printStackTrace();
													}
													
													String rList = "";
													for(int i = 0; i<list.length(); i++) {
														try {
															if(i > 0) {
																rList += ", ";
															}
															rList += list.getJSONObject(i).getString("rtName");
														} catch (JSONException e) {
															e.printStackTrace();
														}
													}
													marker.setSnippet(rList);
													marker.showInfoWindow();
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
									return false;
								}
							});
						
							mapView.setOnInfoWindowClickListener(new OnInfoWindowClickListener() {
								
								@Override
								public void onInfoWindowClick(Marker marker) {
									String tsID;
									try {
										tsID = busStops.getJSONObject(markerList.indexOf(marker)).getString("tsID");
									
										if (routesForStop.containsKey(tsID)) {
											 
											final JSONArray routesArray = routesForStop.get(tsID);
											Log.i(TAG, "routeForStop: " + routesForStop.get(tsID));
											Log.i(TAG, "routesArray: " + routesArray);
											String[] options = new String[routesArray.length()];
											for (int i=0, l=routesArray.length();i<l; i++) {
												options[i] = "" + routesArray.getJSONObject(i).getString("rtName") + "(" + routesArray.getJSONObject(i).getString("descTx") + ")";
											}
											AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
											builder.setTitle("選擇公車路線");
											builder.setItems(options, new DialogInterface.OnClickListener() {
												
												@Override
												public void onClick(DialogInterface dialog, final int which) {
													String brID = "";
													try {
														brID = routesArray.getJSONObject(which).getString("brID");
													} catch (JSONException e) {
													}
													Intent intent = new Intent(getActivity(), RouteActivity.class);
													intent.putExtra("brID", brID);
													try {
														intent.putExtra("title", routesArray.getJSONObject(which).getString("rtName") + "(" + routesArray.getJSONObject(which).getString("descTx") + ")");
														startActivity(intent);
													} catch (JSONException e) {
													}
												}
											});
											builder.show();
										}
									}
									catch (JSONException e) {
									}
								}
							});
						} catch (JSONException e) {
						} 
					}
				}
				else {
					Log.i(TAG, "err: " + Assist.getMessage(result));
				}
			}
			
			@Override
			public void onProgress(Integer progress) {
			}
			
			@Override
			public void onFail(HttpResponse response, Exception ex) {
				Log.i(TAG, "ex on get bus stop: " + ex.getLocalizedMessage());
			}
		});
	}
}
