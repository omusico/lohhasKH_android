package com.gocharm.lohaskh;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.nostra13.universalimageloader.core.ImageLoader;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

@SuppressLint("ValidFragment")
public class VistaDetailFrag1 extends Fragment{
	private static final String TAG = "vistaDetailFrag1";
	private JSONObject content;
	
	
	public static VistaDetailFrag1 newInstance(JSONObject cont) {
		VistaDetailFrag1 f = new VistaDetailFrag1();
	    Bundle args = new Bundle();
	    args.putString("args", cont.toString());
	    f.setArguments(args);
	    return f;
	}
	
	public VistaDetailFrag1 () {}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		try {
			content = new JSONObject(getArguments().getString("args"));
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		View view = inflater.inflate(R.layout.fragment_vista_detail_1, container, false);
		ImageView icon = (ImageView) view.findViewById(R.id.vistaImg);
		TextView desc = (TextView) view.findViewById(R.id.vistaDesc);
		TextView openTime = (TextView) view.findViewById(R.id.vistaOpenTime);
		TextView tel = (TextView) view.findViewById(R.id.vistaTEL);
		icon.setImageDrawable(getResources().getDrawable(R.drawable.nopic_vista));
		try {
			desc.setText( "景點介紹\n" + content.getString("descTx") );
			openTime.setText(content.getString("openTime"));
			tel.setText(content.getString("tel"));
			if(!content.getString("imgURL").equals("")) {
				ImageLoader.getInstance().displayImage(content.getString("imgURL"), icon, Util.vistaOptions);
			}
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return view;	}
}