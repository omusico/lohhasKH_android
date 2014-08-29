package com.gocharm.lohaskh;

import org.json.JSONException;
import org.json.JSONObject;

import com.nostra13.universalimageloader.core.ImageLoader;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

@SuppressLint("ValidFragment")
public class TicketDetailFrag1 extends Fragment{
	private static final String TAG = "ticketDetailFrag1";
	private JSONObject content;
	
	public static TicketDetailFrag1 newInstance(JSONObject cont) {
		TicketDetailFrag1 f = new TicketDetailFrag1();
	    Bundle args = new Bundle();
	    Log.i(TAG, "cont: " + cont);
	    Log.i(TAG, "cont: " + cont.toString());
	    args.putString("args", cont.toString());
	    f.setArguments(args);
	    return f;
	}
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		try {
			content = new JSONObject(getArguments().getString("args"));
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		View view = inflater.inflate(R.layout.fragment_ticket_detail_1,container,false);
		TextView desc = (TextView) view.findViewById(R.id.ticketDesc);
		TextView openTime = (TextView) view.findViewById(R.id.openText);
		TextView restTime = (TextView) view.findViewById(R.id.restText);
		TextView tel = (TextView) view.findViewById(R.id.telText);
		ImageView icon = (ImageView) view.findViewById(R.id.ticketImg);
		icon.setImageDrawable(getResources().getDrawable(R.drawable.nopic_ticket));
		try {
			
			desc.setText( "餐廳簡介：\n" + content.getString("descTx") );
			if(content.getString("openTime").equals("未提供") && content.getString("closeTime").equals("未提供")) {
				openTime.setText("未提供");
			}
			else {
				openTime.setText(content.getString("openTime") + " ~ " + content.getString("closeTime"));
			}
			if(content.getString("restFrom").equals("未提供") && content.getString("restTo").equals("未提供")){
				restTime.setText("未提供");
			}
			else {
				restTime.setText(content.getString("restFrom") + " ~ " + content.getString("restTo"));
			}
			
			tel.setText(content.getString("tel"));
			
			if(!content.getString("imgURL").equals("")) {
				ImageLoader.getInstance().displayImage(content.getString("imgURL"), icon, Util.ticketOptions);
			}
		} 
		catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return view;	
	}
}
