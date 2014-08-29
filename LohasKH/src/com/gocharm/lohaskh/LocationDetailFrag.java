package com.gocharm.lohaskh;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.Html.ImageGetter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.SlidingDrawer;
import android.widget.TextView;

@SuppressWarnings("deprecation")
@SuppressLint("ValidFragment")
public class LocationDetailFrag extends Fragment{
	private static final String TAG = "locationDetailFrag";
	//private ArrayList<Map<String, String>> content;
	private JSONArray content;
	private TextView titleText, addrText, routeName, routeInfo;
	private SlidingDrawer slidingDrawer;
	private ListView instructionsList;
	private ImageButton selBut;
	private Button routeSelBut, goBut, dirBut;
	private LocationManager lm;
	//private LinearLayout routeInfoLayout;
	private ArrayList<Map<String, Object>> directionInfo;
	private int locationIndex = 0, routeIndex = 0;
	private LatLng currentLatLng;
	private ProgressDialog pd;
	private AsyncTask<String, Void, ArrayList<Map<String, Object>>> mTask = null;
	
	private class myLocationListener implements android.location.LocationListener {
	    public myLocationListener(String provider) {
	        Log.e(TAG, "LocationListener " + provider);
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
			currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
			dirBut.setEnabled(true);
			dirBut.setBackgroundColor(Color.rgb(0xfd, 0x6f, 0x91));//#fd6f91
		}
	}
	
	private myLocationListener mLL = new myLocationListener(LocationManager.NETWORK_PROVIDER);
	
	@Override
	public void onDestroy() {
		if(mTask != null){
			mTask.cancel(true);
			mTask = null;
		}
			
		super.onDestroy();
	}
	
	public static LocationDetailFrag newInstance(JSONArray cont) {
		LocationDetailFrag f = new LocationDetailFrag();
	    Bundle args = new Bundle();
	    args.putString("args", cont.toString());
	    f.setArguments(args);
	    return f;
	}
	
	public LocationDetailFrag () {
	}
	
	@Override
	public void onPause() {
		lm.removeUpdates(mLL);
		super.onPause();
	}
	@Override
	public void onResume() {
		if(lm == null)
			lm = (LocationManager)getActivity().getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
		lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, mLL);
		if( isMyServiceRunning()) {
			Log.i(TAG, "service is running...");
			goBut.setVisibility(View.INVISIBLE);
		}
		else {
			Log.i(TAG, "service is not running...");
			goBut.setVisibility(View.VISIBLE);
		}
		super.onResume();
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_location_detail, container, false);
		try {
			content = new JSONArray(getArguments().getString("args"));
			getActivity().getSupportFragmentManager().beginTransaction().add(R.id.targetMap, TargetMapFragment.newInstance(content)).commit();
	        slidingDrawer = (SlidingDrawer) view.findViewById(R.id.slidedrawer);
	        pd = new ProgressDialog(getActivity());
			titleText = (TextView) view.findViewById(R.id.locationNameText);
			titleText.setText(content.getJSONObject(0).getString("placeName"));
			addrText = (TextView) view.findViewById(R.id.locationAddrText);
			addrText.setText(content.getJSONObject(0).getString("addr"));
			instructionsList = (ListView) view.findViewById(R.id.instructionList);
			selBut = (ImageButton) view.findViewById(R.id.locationSelBut);
			selBut.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					String[] options = new String[content.length()];
					for (int i=0, l=content.length();i<l; i++) {
						try {
							options[i] = "" + content.getJSONObject(i).getString("placeName");
						} catch (JSONException e) {
						}
					}
					AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
					builder.setTitle("�п�ܦa�I");
					builder.setItems(options, new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, final int which) {
							locationIndex = which;
							try {
								titleText.setText(content.getJSONObject(which).getString("placeName"));
								addrText.setText(content.getJSONObject(which).getString("addr"));
								LatLng pos = new LatLng(Double.parseDouble(content.getJSONObject(which).getString("latitude")), Double.parseDouble(content.getJSONObject(which).getString("longitude")));
								GoogleMap mapView = ((SupportMapFragment)getActivity().getSupportFragmentManager().findFragmentById(R.id.targetMap)).getMap();
								mapView.moveCamera(CameraUpdateFactory.newLatLngZoom(pos, 16));
								mapView.clear();
								((TargetMapFragment)getFragmentManager().findFragmentById(R.id.targetMap)).MarkButStops(pos);
								((TargetMapFragment)getFragmentManager().findFragmentById(R.id.targetMap)).setMarker(pos, content.getJSONObject(which).getString("placeName"), 354.0f);
								
								dirBut.setVisibility(View.VISIBLE);
						    	slidingDrawer.setVisibility(View.INVISIBLE);
							} catch (JSONException e) {
								e.printStackTrace();
							}
						}
					});
					builder.show();
				}
			});
			if(content.length()<2) {
				selBut.setVisibility(View.INVISIBLE);
			}
			//routeInfoLayout = (LinearLayout) view.findViewById(R.id.handle);
			dirBut = (Button)view.findViewById(R.id.dirBut);
			dirBut.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					pd.setTitle("規劃路線中");
					pd.setCancelable(true);
					pd.setOnCancelListener(new OnCancelListener() {
						
						@Override
						public void onCancel(DialogInterface dialog) {
							if (mTask != null) {
								mTask.cancel(true);
							}
							pd.dismiss();
						}
					});
					pd.setMessage("取得路線資料中…");
					pd.show();
					dirBut.setEnabled(false);
					dirBut.setBackgroundColor(Color.rgb(0xaa, 0xaa, 0xaa));//#fd6f91
					try {
						String[] params = {"" + currentLatLng.latitude, 
										   "" + currentLatLng.longitude,
										   content.getJSONObject(locationIndex).getString("latitude"),
										   content.getJSONObject(locationIndex).getString("longitude")};
						Log.i(TAG, "direction API");
						
						mTask = new GetDirectionsAsyncTask().execute(params);
					} catch (JSONException e){
						e.printStackTrace();
					}
				}
			});
			dirBut.setEnabled(false);
			dirBut.setBackgroundColor(Color.rgb(0xaa, 0xaa, 0xaa));//#fd6f91
			routeName = (TextView) view.findViewById(R.id.routeNameText);
			routeInfo = (TextView) view.findViewById(R.id.routeInfoText);
			routeSelBut = (Button) view.findViewById(R.id.routeSelBut);
			routeSelBut.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					final ArrayList<Map<String, Object>> dataArray = new ArrayList<Map<String, Object>>();
					String[] options = new String[directionInfo.size()];
					for (int i=0, l=directionInfo.size();i<l; i++) {
						Map<String, Object> map = new HashMap<String, Object>();
						map.put("title", ""+directionInfo.get(i).get("title"));
						options[i] = "" + directionInfo.get(i).get("title");
						dataArray.add(map);
					}
					AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
					builder.setTitle("選擇路線");
					SimpleAdapter adapter = new SimpleAdapter(
							getActivity().getApplicationContext(), 
							dataArray, 
							android.R.layout.simple_list_item_1, 
							new String[] {"title"}, 
							new int[] { android.R.id.text1}) {
						@Override
						public View getView(int position, View convertView, ViewGroup parent) {
							View view = super.getView(position, convertView, parent);
							TextView tView = (TextView) view.findViewById(android.R.id.text1);
							tView.setTextColor(getResources().getColor(R.color.BlueColor));
							String title = ((Map<String, Object>)dataArray.get(position)).get("title") + "";
							tView.setText(Html.fromHtml(title, mImageGetter, null));
							return view;
						}
					};
					builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, final int which) {
							routeIndex = which;
							@SuppressWarnings("unchecked")
							SimpleAdapter adapter = new SimpleAdapter(
					    			getActivity().getApplicationContext(),
					    			(List<? extends Map<String, ?>>) directionInfo.get(which).get("instructions"),
					    			android.R.layout.simple_list_item_1,
					    			new String[] {"instruction"},
					    			new int[] {android.R.id.text1}
					    			) {
								@Override
								public View getView(int position, View convertView, ViewGroup parent) {
									View view = super.getView(position, convertView, parent);
									((TextView)view.findViewById(android.R.id.text1)).setTextColor(Color.rgb(0x22, 0x22, 0x22));
									return view;
								}
							};
					    	instructionsList.setAdapter(adapter);
					    	routeName.setText(Html.fromHtml("" + directionInfo.get(which).get("title"), mImageGetter, null));
							int km = Integer.parseInt((String)directionInfo.get(which).get("distance"))/1000;
					    	int min = Integer.parseInt((String)directionInfo.get(which).get("duration"))/60;
					    	routeInfo.setText("預計" + km + "公里, " + min + "分鐘");
							drawDirection(which);
							slidingDrawer.animateClose();
						}
					});
					builder.show();
				}
			});
			
			goBut = (Button) view.findViewById(R.id.goBut);
			goBut.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					if(!lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {// gps not enabled
						if(!lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){//net not enabled
							AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
							builder.setTitle("未開啟定位服務");
							builder.setMessage("請至\"設定\"開啟定位服務");
							builder.show();
							return;
						}
						else {
							Log.i(TAG, "no GPS");
						}
					}
					Log.i(TAG, "start tracing");
					Intent intent = new Intent(getActivity(), TracingService.class);
					Log.i(TAG, "intent: " + intent);
					//sent check points to service
					getActivity().startService(intent);
				}
			});
			
		} catch (JSONException e) {
		}
		return view;
	}

	public class GetDirectionsAsyncTask extends AsyncTask<String, Void, ArrayList<Map<String, Object>>>
	{
	    @Override
	    protected ArrayList<Map<String, Object>> doInBackground(String... params)
	    {
	        try
	        {
	            LatLng fromPosition = new LatLng(Double.parseDouble(params[0]) , Double.parseDouble(params[1]));
	            LatLng toPosition = new LatLng(Double.parseDouble(params[2]) , Double.parseDouble(params[3]));
	            GMapV2Direction md = new GMapV2Direction();
	            JSONObject doc = md.getDocument(fromPosition, toPosition, content.getJSONObject(locationIndex).getString("placeName"));
	            ArrayList<Map<String, Object>> directionPoints = md.getAlterDirections(doc);
	            return directionPoints;
	        }
	        catch (Exception e)
	        {
	            return null;
	        }
	    }
	    @Override
	    protected void onPostExecute(ArrayList<Map<String, Object>> result) {
	    	if (result == null) {
	    		return;
	    	}
	    	if(isCancelled()) {
	    		return;
	    	}
	    	pd.dismiss();
	    	
	    	directionInfo = result;
	    	
	    	drawDirection(0);
	    	dirBut.setVisibility(View.INVISIBLE);
	    	dirBut.setEnabled(true);
			dirBut.setBackgroundColor(Color.rgb(0xfd, 0x6f, 0x91));//#fd6f91
	    	routeName.setText(Html.fromHtml("" + directionInfo.get(0).get("title"), mImageGetter, null));
	    	int km = Integer.parseInt((String)directionInfo.get(0).get("distance"))/1000;
	    	int min = Integer.parseInt((String)directionInfo.get(0).get("duration"))/60;
	    	@SuppressWarnings("unchecked")
			SimpleAdapter adapter = new SimpleAdapter(
	    			getActivity().getApplicationContext(),
	    			(List<? extends Map<String, ?>>) directionInfo.get(0).get("instructions"),
	    			android.R.layout.simple_list_item_1,
	    			new String[] {"instruction"},
	    			new int[] {android.R.id.text1}
	    			) {
	    		@Override
	    		public View getView(int position, View convertView,
	    				ViewGroup parent) {
	    			View view = super.getView(position, convertView, parent);
	    			((TextView)view.findViewById(android.R.id.text1)).setTextColor(Color.rgb(0x22, 0x22, 0x22));
	    			return view;
	    		}
	    	};
	    	instructionsList.setAdapter(adapter);
	    	routeInfo.setText("預計" + km + "公里, " + min + "分鐘");
	    	if(directionInfo.size() < 2) {
	    		routeSelBut.setVisibility(View.INVISIBLE);
	    	}
	    	slidingDrawer.setVisibility(View.VISIBLE);
	    	super.onPostExecute(result);
	    }
	}
	
	@SuppressWarnings("unchecked")
	public void drawDirection(int index){
		GoogleMap mapView = ((SupportMapFragment)getActivity().getSupportFragmentManager().findFragmentById(R.id.targetMap)).getMap();
		Map<String, Object> route = directionInfo.get(index);
		
    	ArrayList<Map<String, Object>> polylineMap = (ArrayList<Map<String, Object>>) route.get("polyline");
    	ArrayList<Map<String, Object>> checkArrayList = (ArrayList<Map<String, Object>>) route.get("checkPoints");
    	mapView.clear();
    	Log.i(TAG, "check points: " + checkArrayList);
    	
    	for (int i=0; i<checkArrayList.size(); i++) {
    		mapView.addMarker(new MarkerOptions()
    						.position((LatLng)checkArrayList.get(i).get("pos"))
    						.title(checkArrayList.get(i).get("name")+"")
    						.icon(BitmapDescriptorFactory.defaultMarker(57.0f)));
    	}
    	
    	for(int i = 0 ; i < polylineMap.size() ; i++) {
    		ArrayList<LatLng> pathPoints = Util.decodePoly((String)polylineMap.get(i).get("path"));
			String type = (String) polylineMap.get(i).get("type");
			if (type.equalsIgnoreCase("BUS")) {
				for(int p=0; p<pathPoints.size()-1; p++) {
					mapView.addPolyline(new PolylineOptions()
					  .add((LatLng) pathPoints.get(p), (LatLng) pathPoints.get(p+1))
					  .width(5)
					  .color(Color.BLUE));
				}
			}
			else if (type.equalsIgnoreCase("SUBWAY")) {
				for(int p=0; p<pathPoints.size()-1; p++) {
					mapView.addPolyline(new PolylineOptions()
					  .add((LatLng) pathPoints.get(p), (LatLng) pathPoints.get(p+1))
					  .width(5)
					  .color(Color.GREEN));
				}
			}
			else if (type.equalsIgnoreCase("HEAVY_RAIL")) {
				for(int p=0; p<pathPoints.size()-1; p++) {
					mapView.addPolyline(new PolylineOptions()
					  .add((LatLng) pathPoints.get(p), (LatLng) pathPoints.get(p+1))
					  .width(5)
					  .color(Color.DKGRAY));
				}
			}
			else {
				for(int p=0; p<pathPoints.size()-1; p++) {
					mapView.addPolyline(new PolylineOptions()
					  .add((LatLng) pathPoints.get(p), (LatLng) pathPoints.get(p+1))
					  .width(5)
					  .color(Color.RED));
				}
			}
    	}
	}
	private ImageGetter mImageGetter = new Html.ImageGetter() {

	    public Drawable getDrawable(String source) {
	        int id;

	        if (source.equals("bus.9.png")) {
	            id = R.drawable.bus;
	        }
	        else if (source.equals("mrt.9.png")) {
	            id = R.drawable.mrt;
	        }
	        else if (source.equals("train.9.png")) {
	            id = R.drawable.train;
	        }
	        else {
	            return null;
	        }

	        Drawable d = getResources().getDrawable(id);
	        d.setBounds(0,0,(int)(d.getIntrinsicWidth()*(0.6)),(int)(d.getIntrinsicHeight()*(0.6)));
	        
	        return d;
	    }
	};
	
	private boolean isMyServiceRunning() {
	    ActivityManager manager = (ActivityManager) getActivity().getSystemService(Context.ACTIVITY_SERVICE);
	    for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
	        if (TracingService.class.getName().equals(service.service.getClassName())) {
	            return true;
	        }
	    }
	    return false;
	}
}
