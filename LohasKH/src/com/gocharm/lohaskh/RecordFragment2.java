package com.gocharm.lohaskh;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class RecordFragment2 extends Fragment {

	private String[] date = {"2014/05/11", "2014/05/05", "2014/05/02", "2014/04/21", "2014/04/04", "2014/04/01"  };
	private String[] co2 = {"0.003 KgCO2e" , "0.002 KgCO2e", "0.004 KgCO2e", "0.005 KgCO2e", "0.002 KgCO2e", "0.001 KgCO2e"};
	private String[] duration = {"25分鐘", "30分鐘", "55分鐘", "95分鐘", "26分鐘", "18分鐘"};
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.fragment_record_2, container, false);
		ListView lv = (ListView) view.findViewById(R.id.recordList);
		ArrayList<Map<String, String>> data = new ArrayList<Map<String, String>>();
		for (int i=0; i< 6; i++){
			Map<String, String> map = new HashMap<String, String>();
			map.put("date", date[i]);
			map.put("co2", co2[i]);
			map.put("duration", duration[i]);
			data.add(map);
		}
		
		SimpleAdapter adapter = new SimpleAdapter(getActivity().getApplicationContext(),
												data, 
												R.layout.row_records,
												new String[]{"date", "co2", "duration"}, 
												new int[]{R.id.recordDate, R.id.recordCO2, R.id.recordDuration});
		lv.setAdapter(adapter);
		return view;
	}
	
}
