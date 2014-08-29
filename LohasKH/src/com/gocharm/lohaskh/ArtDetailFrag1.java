package com.gocharm.lohaskh;

import org.json.JSONException;
import org.json.JSONObject;

import com.nostra13.universalimageloader.core.ImageLoader;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

@SuppressLint("ValidFragment")
public class ArtDetailFrag1 extends Fragment{
	private static final String TAG = "artDetailFrag1";
	private JSONObject content;
	
	public static ArtDetailFrag1 newInstance(JSONObject cont) {
		ArtDetailFrag1 f = new ArtDetailFrag1();
	    Bundle args = new Bundle();
	    args.putString("args", cont.toString());
	    f.setArguments(args);
		
	    return f;
	}
	
	public ArtDetailFrag1 () {
		//this.content = cont;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		String contString = getArguments().getString("args");
		try {
			content = new JSONObject(contString);
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		View view = inflater.inflate(R.layout.fragment_art_detail_1,container,false);
		TextView desc = (TextView) view.findViewById(R.id.artDesc);
		TextView showOrg = (TextView) view.findViewById(R.id.artShowOrg);
		TextView infoSrc = (TextView) view.findViewById(R.id.artInfoSrc);
		TextView organizer = (TextView) view.findViewById(R.id.artOrganizer);
		TextView offiURL = (TextView) view.findViewById(R.id.artOffiURL);
		ImageView icon = (ImageView) view.findViewById(R.id.artImg);
		ImageButton shopBut = (ImageButton) view.findViewById(R.id.shopBut);
		try {
			desc.setText( "活動介紹：\n" + content.getString("descTx") );
			showOrg.setText("演出單位：" + content.getString("showOrg"));
			infoSrc.setText("售票資訊：" + content.getString("infoSrc"));
			organizer.setText("主辦單位：" + content.getString("organizer"));
			offiURL.setText("相關連結" + content.getString("offiURL"));
			final String saleURL = content.getString("saleURL"); 
			if(saleURL.equals("")) {
				shopBut.setVisibility(View.INVISIBLE);
			}
			icon.setImageDrawable(getResources().getDrawable(R.drawable.nopic_arts));
			if(!content.getString("imgURL").equals("")) {
				ImageLoader.getInstance().displayImage(content.getString("imgURL"), icon, Util.artOptions);
			}
			else {
				shopBut.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(saleURL));
						startActivity(intent);
					}
				});
			}	
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return view;	
	}
}
