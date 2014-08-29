package com.gocharm.lohaskh;

import android.app.Application;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class RecordFragment1 extends Fragment{
	private CheckBox defaultStart;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.fragment_record_1, container, false);
		defaultStart = (CheckBox) view.findViewById(R.id.defaultStart);
		defaultStart.setChecked(getActivity().getApplication().getSharedPreferences(Util.PREF_NAME, Application.MODE_PRIVATE).getBoolean("defaultStart", true));
		
		defaultStart.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				if(isChecked)
					getActivity().getApplication().getSharedPreferences(Util.PREF_NAME, Application.MODE_PRIVATE).edit().putBoolean("defaultStart", true).commit();
				else
					getActivity().getApplication().getSharedPreferences(Util.PREF_NAME, Application.MODE_PRIVATE).edit().putBoolean("defaultStart", false).commit();
				
					
			}
		});
		return view;
	}
}
