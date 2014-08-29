package com.gocharm.lohaskh;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleAdapter;

@SuppressLint("ValidFragment")
public class ArtDetailFrag2 extends Fragment{
	private static final String TAG = "artDetailFrag2";
	private JSONArray content;
	private ArrayList<HashMap<String,String>> dataArray = new ArrayList<HashMap<String,String>>();
	public ArtDetailFrag2 () {
		//this.content = cont;
	}
	
	public static ArtDetailFrag2 newInstance(JSONArray cont) {
		ArtDetailFrag2 f = new ArtDetailFrag2();
	    Bundle args = new Bundle();
	    args.putString("args", cont.toString());
	    f.setArguments(args);
		
	    return f;
	 }
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		String contString = getArguments().getString("args");
		try {
			content = new JSONArray(contString);
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		View view = inflater.inflate(R.layout.fragment_list, container, false);
		ListView lv = (ListView) view.findViewById(R.id.timeList);
		for(int i=0,l=content.length(); i < l; i++) {
			HashMap<String, String> tmp = new HashMap<String, String>();
			try {
				String datetime = content.getJSONObject(i).getString("time");
				String [] chunk = datetime.split("[ ]");
				tmp.put("date", chunk[0]);
				tmp.put("time", chunk[1]);
				tmp.put("place", content.getJSONObject(i).getString("placeName"));
			} catch (JSONException e) {
				e.printStackTrace();
			}
			dataArray.add(tmp);
		}
		SimpleAdapter adapter = new SimpleAdapter(
				getActivity(), 
				dataArray, 
				R.layout.row_art_time, 
				new String[]{"date", "time", "place"}, 
				new int[]{R.id.dateText,R.id.timeText, R.id.placeText});
		lv.setAdapter(adapter);
		return view;
	}
}
