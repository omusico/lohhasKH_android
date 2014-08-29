package com.gocharm.lohaskh;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.google.android.gms.maps.model.LatLng;


public class GMapV2Direction {
	private static final String TAG = "GMapV2Direction";
    public final static String MODE_DRIVING = "driving";
    public final static String MODE_WALKING = "walking";
    private String dest;
 
    public GMapV2Direction() { }
 
    public JSONObject getDocument(LatLng start, LatLng end, String destName) {
    	dest = destName;
        String URL = "http://maps.googleapis.com/maps/api/directions/json?" +
				"departure_time="+(System.currentTimeMillis()/1000+60*10) + 
				"&mode=transit&origin="+ start.latitude + "," + start.longitude + 
				"&destination=" + end.latitude + "," + end.longitude + 
				"&language=zh-TW&sensor=true&alternatives=true";
        try {
            HttpClient httpClient = new DefaultHttpClient();
            HttpContext localContext = new BasicHttpContext();
            HttpPost httpPost = new HttpPost(URL);
            //HttpResponse response = httpClient.execute(httpPost, localContext);
            //String line = "";
            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            String Content = httpClient.execute(httpPost, responseHandler);
            return new JSONObject(Content);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
	
	public ArrayList<Map<String, Object>> getAlterDirections (JSONObject doc){
		
        ArrayList<Map<String, Object>> routesInfo = new ArrayList<Map<String, Object>>();
        JSONArray routes;
		try {
			routes = doc.getJSONArray("routes");
	        for(int i=0; i < routes.length(); i++ ) {
	        	String routeName = "起點";
	        	Map<String, Object> routeInfo = new HashMap<String, Object>(); 
	        	JSONObject legs = (JSONObject) routes.getJSONObject(i).getJSONArray("legs").get(0);
	        	routeInfo.put("distance", legs.getJSONObject("distance").getString("value"));//in meters
	        	routeInfo.put("duration", legs.getJSONObject("duration").getString("value"));//in seconds
	        	routeInfo.put("startAddr", legs.getString("start_address"));
	        	routeInfo.put("endAddr", legs.getString("end_address"));
	        	Double start_lat, start_lng, end_lat, end_lng;
	        	start_lat = legs.getJSONObject("start_location").getDouble("lat");
	        	start_lng = legs.getJSONObject("start_location").getDouble("lng");
	        	end_lat = legs.getJSONObject("end_location").getDouble("lat");
	        	end_lng = legs.getJSONObject("end_location").getDouble("lng");
	        	JSONArray steps = ((JSONObject)routes.getJSONObject(i).getJSONArray("legs").get(0)).getJSONArray("steps");
	        	ArrayList<Map<String, Object>> listPolylines = new ArrayList<Map<String, Object>>();
	        	ArrayList<Map<String, Object>> listInstructions = new ArrayList<Map<String, Object>>();
	        	ArrayList<Map<String, Object>> checkPoint = new ArrayList<Map<String, Object>>();
	        	{
	        		Map<String, Object> m = new HashMap<String, Object>();
	        		m.put("name","起點");
	        		m.put("pos", new LatLng(start_lat, start_lng));
	        		checkPoint.add(m);
	        	}
	        	for(int j = 0; j < steps.length();j++) {
	        		{
		        		Map<String, Object> instruction = new HashMap<String, Object>();
		        		instruction.put("instruction", ((JSONObject)steps.get(j)).getString("html_instructions"));
		        		listInstructions.add(instruction);
	        		}	        		
	        		String type = ((JSONObject)steps.get(j)).getString("travel_mode");
	        		if(type.equals("TRANSIT")) {
	        			
	        			Double s_lat, e_lat, s_lng, e_lng;
	        			s_lat = ((JSONObject)steps.get(j)).getJSONObject("start_location").getDouble("lat");
	        			s_lng = ((JSONObject)steps.get(j)).getJSONObject("start_location").getDouble("lng");
	        			e_lat = ((JSONObject)steps.get(j)).getJSONObject("end_location").getDouble("lat");
	        			e_lng = ((JSONObject)steps.get(j)).getJSONObject("end_location").getDouble("lng");
	        			JSONObject detail = ((JSONObject)steps.get(j)).getJSONObject("transit_details");
	        			{
	        				String transitIns = (String) listInstructions.get(j).get("instruction");
	        				transitIns += "("+ detail.getJSONObject("line").getString("short_name") +" 至 " + detail.getJSONObject("arrival_stop").getString("name") + ")";
	        				Map<String, Object> ins = new HashMap<String, Object>();
			        		ins.put("instruction", transitIns);
	        				listInstructions.set(j, ins);
	        			}
	        			
	        			{
	        				Map<String, Object> m = new HashMap<String, Object>();
	    	        		m.put("name",detail.getJSONObject("departure_stop").getString("name"));
	    	        		m.put("pos", new LatLng(s_lat, s_lng));
	    	        		checkPoint.add(m);
	    	        	}
	        			//checkPoint.add(new LatLng(s_lat, s_lng));
	        			{
	        				Map<String, Object> m = new HashMap<String, Object>();
	    	        		m.put("name", detail.getJSONObject("arrival_stop").getString("name"));
	    	        		m.put("pos", new LatLng(e_lat, e_lng));
	    	        		checkPoint.add(m);
	    	        	}
	        			//checkPoint.add(new LatLng(e_lat, e_lng));
	        			
	        			type = detail.getJSONObject("line").getJSONObject("vehicle").getString("type");
	        			if(type.equalsIgnoreCase("BUS")) {
	        				//String butName = 
	        				routeName += " > <img src=\"bus.9.png\"/>" + 
	        							detail.getJSONObject("line").getString("short_name");// +
	        							//"(��" + 
	        							//detail.getJSONObject("departure_stop").getString("name") + ", " +
	        							//detail.getJSONObject("arrival_stop").getString("name") + ")";
	        				
	        			}
	        			else if(type.equalsIgnoreCase("SUBWAY")) {
	        				routeName += " > <img src=\"mrt.9.png\"/>";//+
	        							//"(��" + 
	        							//detail.getJSONObject("departure_stop").getString("name") + ", " +
	        							//detail.getJSONObject("arrival_stop").getString("name") + ")";
	        				
	        			}
	        			else if(type.equalsIgnoreCase("HEAVY_RAIL")) {
	        				routeName += " > <img src=\"train.9.png\"/>";//+"(��" + 
	        							//detail.getJSONObject("departure_stop").getString("name") + ", " +
	        							//detail.getJSONObject("arrival_stop").getString("name") + ")";
	        				
	        			}
	        		}
	        		String encodedPath = ((JSONObject)steps.get(j)).getJSONObject("polyline").getString("points");
	        		Map<String, Object> polyLineMap = new HashMap<String, Object>();
	        		polyLineMap.put("type", type);
	        		polyLineMap.put("path", encodedPath);
	        		
	        		listPolylines.add(polyLineMap);
	        		
	        		//ArrayList<LatLng> arr = decodePoly(polyline);
	        		 
	                //for(int k = 0 ; k < arr.size() ; k++) {
	                //	Map<String, Object> point = new HashMap<String, Object>();
	        		//	point.put("type", type);
	        		//	point.put("point", new LatLng(arr.get(k).latitude, arr.get(k).longitude));
	        		//	listGeopoints.add(point);
	                //}
	        	}
	        	{
	        		Map<String, Object> m = new HashMap<String, Object>();
	        		m.put("name", dest);
	        		m.put("pos", new LatLng(end_lat, end_lng));
	        		checkPoint.add(m);
	        	}
	        	
	        	{
	        		Map<String, Object> m = new HashMap<String, Object>();
	        		m.put("instruction", "目的地: " + dest);
	        		listInstructions.add(m);
        		}
	        	
	        	routeName += " > " + dest;
	        	
	        	routeInfo.put("title", routeName);
	        	routeInfo.put("instructions", listInstructions);
	        	routeInfo.put("polyline", listPolylines);
	        	routeInfo.put("checkPoints", checkPoint);
	        	routesInfo.add(routeInfo);
	        }
	        
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
       return routesInfo;
    }
 
	
}