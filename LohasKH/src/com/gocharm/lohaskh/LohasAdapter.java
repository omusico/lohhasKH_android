package com.gocharm.lohaskh;

import java.util.ArrayList;
import java.util.HashMap;
import android.content.Context;
import android.util.Log;
import android.widget.Filter;
import android.widget.SimpleAdapter;

public class LohasAdapter extends SimpleAdapter{
	private static final String TAG = "LohasAdapter";
	private LohasFilter filter;
	ArrayList<HashMap<String, String>> dataArray;
	ArrayList<HashMap<String, String>> origArray;
	@SuppressWarnings("unchecked")
	public LohasAdapter(Context context, ArrayList<HashMap<String, String>> data, int resource, String[] from, int[] to) {
		super(context, data, resource, from, to);
		//filter = new LohasFilter();
		dataArray = data;
		origArray = (ArrayList<HashMap<String, String>>) data.clone();
		// TODO Auto-generated constructor stub
	}

	@Override
	public Filter getFilter() {
		// TODO Auto-generated method stub
		if(filter == null) {
			filter = new LohasFilter();
		}
		return filter;
	}
	
	private class LohasFilter extends Filter{
		public LohasFilter(){};
		//private ArrayList<HashMap<String,String>> dataArray = new ArrayList<HashMap<String,String>>();
		
		@Override
		protected FilterResults performFiltering(CharSequence constraint) {
			// TODO Auto-generated method stub
			Log.i(TAG, "perform filtering: |" + constraint + "|");
			FilterResults result = new FilterResults();
			if(!constraint.equals("")) {
				ArrayList<HashMap<String,String>> resList = new ArrayList<HashMap<String,String>>();
		        for (int i = 0; i<origArray.size(); i++) {
		        	Log.i(TAG, "title: " + (origArray.get(i)).get("title"));
		        	if ((origArray.get(i)).get("title").contains(constraint)){
		        		resList.add((origArray.get(i)));
		        	}
		        }
		        result.values = resList;
		        result.count = resList.size();
			}
			else {
				Log.i(TAG, "orig count: " + origArray.size());
				result.values = origArray;
				result.count = origArray.size();
			}
	        return result;
		}

		@SuppressWarnings("unchecked")
		@Override
		protected void publishResults(CharSequence constraint, FilterResults results) {
			// TODO Auto-generated method stub
			Log.i(TAG,"constraint: |" + constraint + "|");
		    Log.i(TAG,"result count: " + results.count);
		    Log.i(TAG,"adpter count: " + getCount());
		    //if ( getCount() > 0) {
		    	dataArray.clear();
			    ArrayList<HashMap<String, String>> resArray =  (ArrayList<HashMap<String, String>>) results.values;
		    	
		    	for(int i=0; i<results.count; i++) {
		    		dataArray.add(resArray.get(i));
		    	}
		    	notifyDataSetChanged();
		    //}
		    //else {
		    //	notifyDataSetInvalidated();
		    //}
			
			
		}

	}
}
