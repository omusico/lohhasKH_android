package com.gocharm.lohaskh;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.http.HttpResponse;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import com.coimotion.csdk.common.COIMCallListener;
import com.coimotion.csdk.util.Assist;
import com.coimotion.csdk.util.ReqUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class NearMapFragment extends SupportMapFragment {
	private static final String TAG = "nearMapFrag";
	private JSONArray prodList;
	private JSONObject ticketExtra;
	private Location mLastLocation;
	private ProgressDialog pd;
	private boolean showArt = true, showVista = true, showTicket = true;
	private GoogleMap mapView;
    private LatLng currnetLatlng;
    private Map<String, ArrayList<Map<String, Object>>> mArts;
    private Map<String, Map<String, Object>> mTickets,  mVistas;
    private Set<Marker> sArts, sTickets, sVistas;
    private LocationManager lm;
    
    private myLocationListener[] mLocationListeners = new myLocationListener[] {
            new myLocationListener(LocationManager.GPS_PROVIDER),
            new myLocationListener(LocationManager.NETWORK_PROVIDER)
    };
    
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
			lm.removeUpdates(mLocationListeners[0]);
			lm.removeUpdates(mLocationListeners[1]);
			Log.i(TAG, "getMap in onLocationChanged: " + getMap());
			Log.i(TAG, new Date() + ": location updated, " + mLastLocation.getProvider());
			currnetLatlng = new LatLng(location.getLatitude(), location.getLongitude());
			mapView.addMarker(new MarkerOptions()
					.title("目前位置")
					.position(currnetLatlng)
					.icon(BitmapDescriptorFactory.defaultMarker(57.0f)));
			mapView.moveCamera(CameraUpdateFactory.newLatLngZoom(currnetLatlng, 16));
		}
	}
    
    
    public static NearMapFragment newInstance(){
    	NearMapFragment frag = new NearMapFragment();
		return  frag;
	}
    
    public NearMapFragment() {}
    
    @Override
    public void onResume() {
    	Log.i(TAG, "getMap in onResume: " + getMap());
    	super.onResume();
    }
    
    @Override
    public void onCreate(Bundle arg0) {
    	Log.i(TAG, "getMap in onCreate: " + getMap());
        super.onCreate(arg0);
        mArts = new HashMap<String,ArrayList<Map<String, Object>>>();
        sArts = new HashSet<Marker>();
        mTickets = new HashMap<String, Map<String, Object>>();
        sTickets = new HashSet<Marker>();
        mVistas = new HashMap<String, Map<String, Object>>();
        sVistas = new HashSet<Marker>();
        lm = (LocationManager)getActivity().getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        boolean isGPSEnabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER),
        		isNETEnabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        
        if(!isGPSEnabled && ! isNETEnabled) {
        	
        }
        else {
	        if(isGPSEnabled) {
	        	lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 60, 300, mLocationListeners[0]);
	        }
	        if(isNETEnabled)
	        	lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 60, 300, mLocationListeners[1]);
        }
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mapView = getMap();
        mapView.setMyLocationEnabled(true);
        mapView.setOnCameraChangeListener(new OnCameraChangeListener() {
			
			@Override
			public void onCameraChange(CameraPosition position) {
				LatLng pos = new LatLng(position.target.latitude, position.target.longitude);
				retriveArts(pos);
				retriveTickets(pos);
				retriveVistas(pos);
			}
		});
        
        mapView.setOnMarkerClickListener(new OnMarkerClickListener() {
			
			@Override
			public boolean onMarkerClick(Marker marker) {
				if(sArts.contains(marker)) {
					ArrayList<Map<String, Object>> dataArr = mArts.get(marker.getSnippet());
					
					String mTitle = "";
					for(int i=0,l=dataArr.size();i<l; i++) {
						if(i>0)
							mTitle += ", "; 
						mTitle += "[ " + dataArr.get(i).get("title") + " ]";
						if(i > 1) {
							mTitle += "...";
							break;
						}
					}
					marker.setTitle(mTitle);
				}
				marker.showInfoWindow();
				return false;
			}
		});
		
		mapView.setOnInfoWindowClickListener(new OnInfoWindowClickListener() {
			
			@Override
			public void onInfoWindowClick(Marker marker) {
				
				if (sArts.contains(marker)) {
					final ArrayList<Map<String, Object>> data = mArts.get(marker.getSnippet());
					if(data.size() > 1) {
						String[] options = new String[data.size()];
						for (int i=0, l=data.size();i<l; i++) {
							options[i] = "" + data.get(i).get("title");
						}
						
						AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
						builder.setTitle("選擇活動");
						builder.setItems(options, new DialogInterface.OnClickListener() {
							
							@Override
							public void onClick(DialogInterface dialog, final int which) {
								pd = ProgressDialog.show(getActivity(), "讀取中", "取得活動資訊");
								ReqUtil.send("twShow/show/info/" + data.get(which).get("spID"), null, new COIMCallListener() {
									@Override
									public void onInvalidToken() {
										new AlertDialog.Builder(getActivity()).setTitle("warning").setMessage("invalid token").show();
									}
									
									@Override
									public void onSuccess(JSONObject result) {
										pd.dismiss();
										if(Assist.getErrCode(result) == 0) {
											
											try {
												JSONObject object = (JSONObject) result.get("value");
												object.put("spID", data.get(which).get("spID"));
												object.put("addr", data.get(which).get("addr"));
												String extra = object.toString();
												Intent intent = new Intent(getActivity(), ArtDetailActivity.class);
												intent.putExtra("artInfo", extra);
												startActivity(intent);
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
										showAlert(ex.getLocalizedMessage());
										Log.i(TAG, "ex: " + ex.getLocalizedMessage());
									}
								});
							}
						});
						builder.show();
					}
					else if(data.size() == 1) {
						pd = ProgressDialog.show(getActivity(), "讀取中", "取得活動資訊");
						ReqUtil.send("twShow/show/info/" + data.get(0).get("spID"), null, new COIMCallListener() {
							@Override
							public void onInvalidToken() {
								new AlertDialog.Builder(getActivity()).setTitle("warning").setMessage("invalid token").show();
							}
							
							@Override
							public void onSuccess(JSONObject result) {
								pd.dismiss();
								if(Assist.getErrCode(result) == 0) {
									
									try {
										JSONObject object = (JSONObject) result.get("value");
										object.put("spID", data.get(0).get("spID"));
										object.put("addr", data.get(0).get("addr"));
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
								pd.dismiss();
								showAlert(ex.getLocalizedMessage());
								Log.i(TAG, "ex: " + ex.getLocalizedMessage());
							}
						});
					}
				}
				else if(sVistas.contains(marker)) {
					final Map<String, Object> data = mVistas.get(marker.getTitle());
					Map<String, Object> mapParam = new HashMap<String, Object>();
					mapParam.put("detail", "1");
					mapParam.put("lat", data.get("latitude"));
					mapParam.put("lng", data.get("longitude"));
					final JSONObject extraObject = new JSONObject();
					pd = ProgressDialog.show(getActivity(), "讀取中", "取得景點資訊");
					ReqUtil.send("twVista/vista/info/" + data.get("geID"), mapParam, new COIMCallListener() {
						@Override
						public void onInvalidToken() {
							new AlertDialog.Builder(getActivity()).setTitle("warning").setMessage("invalid token").show();
						}
						
						@Override
						public void onSuccess(JSONObject result) {
							if(0 == Assist.getErrCode(result)) {
								try {
									JSONObject object = (JSONObject) result.get("value");
									JSONObject aux = new JSONObject(object.getString("aux"));
									extraObject.put("title", object.getString("title"));
									extraObject.put("lat", object.getString("latitude"));
									extraObject.put("lng", object.getString("longitude"));
									extraObject.put("addr", object.getString("addr"));
									extraObject.put("ngID", object.getString("ngID"));
									extraObject.put("tel", aux.getString("tel"));
									extraObject.put("openTime", aux.getString("openTime"));
								
									String ngID = object.getString("ngID");
									Map<String, Object> mapParam = new HashMap<String, Object>();
									mapParam.put("icon", "1");
									ReqUtil.send("twVista/vista/view/" + ngID, mapParam, new COIMCallListener() {
										@Override
										public void onInvalidToken() {
											new AlertDialog.Builder(getActivity()).setTitle("warning").setMessage("invalid token").show();
											
										}
										@Override
										public void onSuccess(JSONObject result) {
											pd.dismiss();
											if(0 == Assist.getErrCode(result)) {
												
												try {
													JSONObject value = (JSONObject) result.get("value");
													extraObject.put("imgURL", value.getString("iconURI"));
													extraObject.put("descTx", value.getString("body"));
												} catch (JSONException e) {
													e.printStackTrace();
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
											pd.dismiss();
											showAlert(ex.getLocalizedMessage());
										}
	
										@Override
										public void onProgress(Integer progress) {
											
										}
										
									});
								} catch (JSONException e) {
									e.printStackTrace();
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
							pd.dismiss();
							showAlert(ex.getLocalizedMessage());
							Log.i(TAG, "ex: " + ex.getLocalizedMessage());
						}
					});
				}
				else if(sTickets.contains(marker)) { 
					ticketExtra = new JSONObject(mTickets.get(marker.getTitle()));
					String siID = "";
					
					try {
						siID = ticketExtra.getString("siID");
					} catch (JSONException e1) {
						e1.printStackTrace();
					}
					
					Map<String, Object> mapParam = new HashMap<String, Object>();
					mapParam.put("detail", "1");
					pd = ProgressDialog.show(getActivity(), "讀取中", "取得餐廳資訊");
					ReqUtil.send("iticket/store/info/" + siID, mapParam, new COIMCallListener() {
						@Override
						public void onInvalidToken() {
							new AlertDialog.Builder(getActivity()).setTitle("warning").setMessage("invalid token").show();
							
						}
						@Override
						public void onSuccess(JSONObject result) {
							if(0 == Assist.getErrCode(result)) {
								try {
									JSONObject res = (JSONObject) result.get("value");
									ticketExtra.put("openTime", res.getString("openTime"));
									ticketExtra.put("closeTime", res.getString("closeTime"));
									ticketExtra.put("restFrom", res.getString("restFrom"));
									ticketExtra.put("restTo", res.getString("restTo"));
									ticketExtra.put("name", res.getString("name"));
									ticketExtra.put("summary", res.getJSONObject("doc").getString("summary"));
									ticketExtra.put("siID", res.getString("siID"));
									String siID = "";
									siID = res.getString("siID");
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
												}
											}
											else {
												Log.i(TAG, "err: " + Assist.getMessage(result));
											}
										}
										
										@Override
										public void onFail(HttpResponse response, Exception ex) {
											pd.dismiss();
											showAlert(ex.getLocalizedMessage());
										}
									});
								} catch (JSONException e) {
									e.printStackTrace();
								}
							}
							else{
								Log.i(TAG, "err: " + Assist.getMessage(result));
							}
						}
						
						@Override
						public void onFail(HttpResponse response, Exception ex) {
							Log.i(TAG, "ex: " + ex.getLocalizedMessage());
							pd.dismiss();
							showAlert(ex.getLocalizedMessage());
						}
					});
				}
				else {
					Log.i(TAG, "non set-markers");
				}
			}
		});
    }
    
    @Override
    public void onDestroyView() {
    	Log.i(TAG, "getMap in onDestroyView: " + getMap());
    	if (lm != null) {
            for (int i = 0; i < mLocationListeners.length; i++) {
                try {
                    lm.removeUpdates(mLocationListeners[i]);
                } catch (Exception ex) {
                    Log.i(TAG, "fail to remove location listners, ignore", ex);
                }
            }
        }
    	super.onDestroyView();
    	
    }
    
    @Override
    public void onStart() {
    	Log.i(TAG, "getMap in onStart: " + getMap());
    	super.onStart();
    }
    
    public void hideArts() {
    	mapView.clear();
    	showArt = false;
    	Log.i(TAG, "hide markers of arts");
    	mapView.addMarker(new MarkerOptions()
			.title("目前位置")
			.position(currnetLatlng)
			.icon(BitmapDescriptorFactory.defaultMarker(57.0f)));
    	if(showVista)
    		showVistas();
    	if(showTicket)
    		showTickets();
    }
    
    public void hideVistas() {
    	mapView.clear();
    	showVista = false;
    	mapView.addMarker(new MarkerOptions()
			.title("目前位置")
			.position(currnetLatlng)
			.icon(BitmapDescriptorFactory.defaultMarker(57.0f)));
    	if(showArt)
    		showArts();
    	if(showTicket)
    		showTickets();
    }
    
    public void hideTickets() {
    	mapView.clear();
    	showTicket = false;
    	mapView.addMarker(new MarkerOptions()
			.title("目前位置")
			.position(currnetLatlng)
			.icon(BitmapDescriptorFactory.defaultMarker(57.0f)));
    	if(showVista)
    		showVistas();
    	if(showArt)
    		showArts();
    }
    
    public void showArts(){
    	showArt = true;
    	Set<String> keySet = mArts.keySet();
    	//sArts.clear();
    	for(Iterator<String> i = keySet.iterator(); i.hasNext(); ) {
    		String snippet = i.next().toString();
			Map<String, Object> dataMap = (Map<String, Object>) mArts.get(snippet).get(0);
			LatLng pos = new LatLng(Double.parseDouble(dataMap.get("lat").toString()), Double.parseDouble(dataMap.get("lng").toString()));
    		Marker x = mapView.addMarker(new MarkerOptions()
    							.snippet(snippet)
    							.position(pos)
    							.icon(BitmapDescriptorFactory.defaultMarker(241.0f)));
    		sArts.add(x);
    	}
    }
    
    public void showVistas(){
    	showVista = true;
    	Set<String> keySet = mVistas.keySet();
    	//sArts.clear();
    	for(Iterator<String> i = keySet.iterator(); i.hasNext(); ) {
    		String title = i.next().toString();
			Map<String, Object> dataMap = mVistas.get(title);
			LatLng pos = new LatLng(Double.parseDouble(dataMap.get("lat").toString()), Double.parseDouble(dataMap.get("lng").toString()));
    		Marker x = mapView.addMarker(new MarkerOptions()
    							.title(title)
    							.snippet(dataMap.get("addr").toString())
    							.position(pos)
    							.icon(BitmapDescriptorFactory.defaultMarker(209.0f)));
    		sVistas.add(x);
    	}
    }
    
    public void showTickets(){
    	showTicket = true;
    	Set<String> keySet = mTickets.keySet();
    	//sArts.clear();
    	for(Iterator<String> i = keySet.iterator(); i.hasNext(); ) {
    		String title = i.next().toString();
			Map<String, Object> dataMap = mTickets.get(title);
			LatLng pos = new LatLng(Double.parseDouble(dataMap.get("lat").toString()), Double.parseDouble(dataMap.get("lng").toString()));
    		Marker x = mapView.addMarker(new MarkerOptions()
    							.title(title)
    							.snippet(dataMap.get("addr").toString())
    							.position(pos)
    							.icon(BitmapDescriptorFactory.defaultMarker(314.0f)));
    		sTickets.add(x);
    	}
    }
    
	@SuppressLint("SimpleDateFormat")
	public void retriveArts(LatLng position){
		Map<String, Object> mapParam = new HashMap<String, Object>();
		Date now = new Date(System.currentTimeMillis());
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		Calendar cal = Calendar.getInstance();
	    cal.setTime(now);
	    cal.add(Calendar.DAY_OF_MONTH, 14);
	    mapParam.put("fromTm", "" + formatter.format(now));
		mapParam.put("toTm", "" + formatter.format((Date) cal.getTime()));
		mapParam.put("lat", position.latitude);
		mapParam.put("lng", position.longitude);
			ReqUtil.send("twShow/show/byLoc", mapParam, new COIMCallListener() {
				@Override
				public void onInvalidToken() {
					new AlertDialog.Builder(getActivity()).setTitle("warning").setMessage("invalid token").show();
					
				}
				@Override
				public void onSuccess(JSONObject result) {
					if(0 == Assist.getErrCode(result)) {
						JSONArray list = new JSONArray();
						try {
							list = ((JSONObject)result.get("value")).getJSONArray("list");
						} catch (JSONException e) {
							e.printStackTrace();
						}
						String title = "";
						
						for(int i = 0, l = list.length(); i<l; i++) {
							HashMap<String, Object> item = new HashMap<String, Object>();
							JSONObject obj;
							try {
								obj = (JSONObject) list.get(i);
								if (!title.equals(obj.getString("title"))) {
									title = obj.getString("title");
									item.put("title", obj.getString("title"));
									item.put("addr", obj.getString("placeName"));
									item.put("spID", obj.getString("spID"));
									item.put("imgURL", obj.getString("imgURL"));
									item.put("lat",obj.getDouble("latitude"));
									item.put("lng",obj.getDouble("longitude"));
									
									String offiURL =  obj.getString("offiURL");
									String saleURL =  obj.getString("saleURL");
									if (offiURL != "" ) {
										item.put("share", offiURL);
									}
									else if(saleURL != ""){
										item.put("share", saleURL);
									}
									else{
										item.put("share", "");
									}
									LatLng latlng = new LatLng(obj.getDouble("latitude"),obj.getDouble("longitude"));
									String place = obj.getString("placeName");
									if (mArts.containsKey(place)) {
										ArrayList<Map<String, Object>> dataArr = mArts.get(place);
										if(!dataArr.contains(item)) {
											dataArr.add(item);
											mArts.put(place, dataArr);
										}
										else {
											Log.i(TAG,"repeated event!");
										}
									}
									else {
										ArrayList<Map<String, Object>> dataArr = new ArrayList<Map<String, Object>>();
										dataArr.add(item);
										mArts.put(place, dataArr);
										//if(showArt) {
										Marker aMarker = mapView.addMarker(new MarkerOptions()
											.position(latlng)
											.snippet(place)
											.icon(BitmapDescriptorFactory.defaultMarker(241.0f)));
										sArts.add(aMarker);
										if(!showArt)
											aMarker.remove();
									}
								}
								
							} catch (JSONException e) {
								e.printStackTrace();
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
					Log.i(TAG, "retrieve art ex: " + ex.getLocalizedMessage());
				}
			});
		//}
	}
	public void retriveVistas(LatLng position){
		Map<String, Object> mapParam = new HashMap<String, Object>();
		//mapParam.put("cat", "6");
		mapParam.put("lat", position.latitude);
		mapParam.put("lng", position.longitude);
		ReqUtil.send("twVista/vista/search", mapParam, new COIMCallListener() {
			@Override
			public void onInvalidToken() {
				new AlertDialog.Builder(getActivity()).setTitle("warning").setMessage("invalid token").show();
				
			}
			@Override
			public void onSuccess(JSONObject result) {
				if(0 == Assist.getErrCode(result)) {
					JSONArray list = new JSONArray();
					try {
						list = ((JSONObject)result.get("value")).getJSONArray("list");
					} catch (JSONException e) {
						e.printStackTrace();
					}
					String title = "";
					for(int i = 0, l = list.length(); i<l; i++) {
						HashMap<String, Object> item = new HashMap<String, Object>();
						JSONObject obj;
						try {
							obj = (JSONObject) list.get(i);
							if (!title.equals(obj.getString("title"))) {
								title = obj.getString("title");
								item.put("title", obj.getString("title"));
								item.put("addr", obj.getString("addr"));
								item.put("lat", obj.getDouble("latitude"));
								item.put("lng", obj.getDouble("longitude"));
								
								item.put("geID", obj.getString("geID"));
								LatLng latlng = new LatLng(obj.getDouble("latitude"),obj.getDouble("longitude"));
								Marker marker = mapView.addMarker(new MarkerOptions()
																.position(latlng)
																.title(obj.getString("title"))
																.snippet(obj.getString("addr"))
																.icon(BitmapDescriptorFactory.defaultMarker(209.0f)));
								
								mVistas.put(title, item);
								sVistas.add(marker);
								if(!showVista)
									marker.remove();
								
							}
						} catch (JSONException e) {
							e.printStackTrace();
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
				Log.i(TAG, "retrieve vista ex: " + ex.getLocalizedMessage());
			}
		});
	}
	public void retriveTickets(LatLng position){
		Map<String, Object> mapParam = new HashMap<String, Object>();
		//mapParam.put("cat", "6");
		mapParam.put("lat", position.latitude);
		mapParam.put("lng", position.longitude);
		ReqUtil.send("iticket/store/search/2", mapParam, new COIMCallListener() {
			@Override
			public void onInvalidToken() {
				new AlertDialog.Builder(getActivity()).setTitle("warning").setMessage("invalid token").show();
				
			}
			@Override
			public void onSuccess(JSONObject result) {
				if(0 == Assist.getErrCode(result)) {
					JSONArray list = new JSONArray();
					try {
						list = ((JSONObject)result.get("value")).getJSONArray("list");
					} catch (JSONException e) {
						e.printStackTrace();
					}
					String title = "";
					for(int i = 0, l = list.length(); i<l; i++) {
						HashMap<String, Object> item = new HashMap<String, Object>();
						JSONObject obj;
						try {
							obj = (JSONObject) list.get(i);
							if (!title.equals(obj.getString("storeName"))) {
								title = obj.getString("storeName");
								item.put("title", obj.getString("storeName"));
								item.put("addr", obj.getString("addr"));
								item.put("tel", obj.getString("phone"));
								item.put("siID", obj.getString("siID"));
								item.put("imgURL", obj.getString("iconURI"));
								item.put("lat", obj.getDouble("latitude"));
								item.put("lng", obj.getDouble("longitude"));
								
								LatLng latlng = new LatLng(obj.getDouble("latitude"),obj.getDouble("longitude"));
								Marker marker = mapView.addMarker(new MarkerOptions()
																.position(latlng)
																.title(obj.getString("storeName"))
																.snippet(obj.getString("addr"))
																.icon(BitmapDescriptorFactory.defaultMarker(314.0f)));
								
								mTickets.put(obj.getString("storeName"), item);
								sTickets.add(marker);
								if(!showTicket)
									marker.remove();
							}
								
						} catch (JSONException e) {
							e.printStackTrace();
						}
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
					if(0 == Assist.getErrCode(result)) {
						try {
							JSONObject res = (JSONObject) result.get("value");
							JSONObject promo = res.getJSONObject("promotion");
							prodList.getJSONObject(index).put("price", res.getString("price"));
							prodList.getJSONObject(index).put("offer", promo.getString("offer"));
							if((index+1) < prodList.length()) {
								queryProducts(index+1);
							}
							else {
								pd.dismiss();
								ticketExtra.put("prodList", prodList);
								Intent intent = new Intent(getActivity(), TicketDetailActivity.class);
								intent.putExtra("ticketInfo", ticketExtra.toString());
								startActivity(intent);
							}
						} catch (JSONException e) {
							if((index+1) < prodList.length()) {
								queryProducts(index+1);
							}
							else {
								pd.dismiss();
								try {
									ticketExtra.put("prodList", prodList);
								} catch (JSONException e1) {
								}
								Intent intent = new Intent(getActivity(), TicketDetailActivity.class);
								intent.putExtra("ticketInfo", ticketExtra.toString());
								startActivity(intent);
							}
						}
					}
					else {
						Log.i(TAG, "err: " + Assist.getMessage(result));
					}
				}

				@Override
				public void onFail(HttpResponse response, Exception ex) {
					if((index+1) < prodList.length()) {
						queryProducts(index+1);
					}
					else {
						pd.dismiss();
						try {
							ticketExtra.put("prodList", prodList);
						} catch (JSONException e) {
						}
						Intent intent = new Intent(getActivity(), TicketDetailActivity.class);
						intent.putExtra("ticketInfo", ticketExtra.toString());
						startActivity(intent);
					}
				}

				@Override
				public void onProgress(Integer progress) {
				}
			});
		} catch (Exception e) {
		}
    }

	public void showAlert(String message) {
		new AlertDialog.Builder(getActivity())
	    .setTitle("Something wrong")
	    .setMessage(message)
	    .show();
	}
}