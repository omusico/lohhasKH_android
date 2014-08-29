package com.gocharm.lohaskh;

import java.util.List;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AnalogClock;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class DrawerAdapter extends BaseAdapter{
	
	Context context;
	List<DrawerRow> rowItem;
	
	DrawerAdapter(Context context, List<DrawerRow> rowItem) {
		this.context = context;
		this.rowItem = rowItem;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		if (convertView == null) {
			LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.row_drawer, null);
        }

        ImageView imgIcon = (ImageView) convertView.findViewById(R.id.icon);
        TextView txtTitle = (TextView) convertView.findViewById(R.id.title);
        
        DrawerRow row_pos = rowItem.get(position);
        imgIcon.setImageResource(row_pos.getIcon());
        txtTitle.setText(row_pos.getTitle());
        if(position < 6) {
        	convertView.setBackgroundColor(Color.parseColor("#ddeefe"));
        }
        return convertView;
	}

	@Override
	public int getCount() {
		return rowItem.size();
	}

	@Override
	public Object getItem(int position) {
		return rowItem.get(position);
	}

	@Override
	public long getItemId(int position) {
		return rowItem.indexOf(getItem(position));
	}
}
