package com.gocharm.lohaskh;

import com.google.android.gms.maps.SupportMapFragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

public class NearbyFragment extends Fragment 
{
	private static final String TAG = "nearbyFrag";
    
	private boolean showArts = true;
	private boolean showVistas = true;
	private boolean showTickets = true;
	private ImageButton artsBut, vistasBut, ticketBut;
	
    public NearbyFragment() {}
    

    @Override
    public void onCreate(Bundle savedInstanceState) {
    	// TODO Auto-generated method stub
    	super.onCreate(savedInstanceState);
    }
    
    @Override
    public View onCreateView(LayoutInflater mInflater, ViewGroup arg1, Bundle arg2) {
    	Log.i(TAG, "fragment on create view");
        View view = mInflater.inflate(R.layout.fragment_nearby, arg1, false);
		getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.map, new NearMapFragment()).commit();
		artsBut = (ImageButton) view.findViewById(R.id.artsDisplayBut);
		if (showArts) {
			artsBut.setImageDrawable(getResources().getDrawable(R.drawable.art_hide));
		}
		else {
			artsBut.setImageDrawable(getResources().getDrawable(R.drawable.art_show));
		}
		artsBut.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (showArts) {
					NearMapFragment mapFrag = (NearMapFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.map);
					mapFrag.hideArts();
					showArts = false;
					//artsBut.setText("顯示活動");
					artsBut.setImageDrawable(getResources().getDrawable(R.drawable.art_show));
				}
				else {
					NearMapFragment mapFrag = (NearMapFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.map);
					mapFrag.showArts();
					showArts = true;
					//artsBut.setText("隱藏活動");
					artsBut.setImageDrawable(getResources().getDrawable(R.drawable.art_hide));
				}
			}
		});
		
		vistasBut = (ImageButton) view.findViewById(R.id.vistaDisplayBut);
		if (showVistas) {
			vistasBut.setImageDrawable(getResources().getDrawable(R.drawable.vista_hide));
		}
		else {
			vistasBut.setImageDrawable(getResources().getDrawable(R.drawable.vista_show));
		}
		vistasBut.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (showVistas) {
					NearMapFragment mapFrag = (NearMapFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.map);
					mapFrag.hideVistas();
					//vistasBut.setText("顯示景點");
					vistasBut.setImageDrawable(getResources().getDrawable(R.drawable.vista_show));
					showVistas = false;
				}
				else {
					NearMapFragment mapFrag = (NearMapFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.map);
					mapFrag.showVistas();
					vistasBut.setImageDrawable(getResources().getDrawable(R.drawable.vista_hide));
					showVistas = true;
				}
			}
		});
		
		ticketBut = (ImageButton) view.findViewById(R.id.ticketDisplayBut);
		if (showTickets) {
			ticketBut.setImageDrawable(getResources().getDrawable(R.drawable.ticket_hide));
		}
		else {
			ticketBut.setImageDrawable(getResources().getDrawable(R.drawable.ticket_show));
		}
		ticketBut.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (showTickets) {
					NearMapFragment mapFrag = (NearMapFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.map);
					mapFrag.hideTickets();
					ticketBut.setImageDrawable(getResources().getDrawable(R.drawable.ticket_show));
				}
				else {
					NearMapFragment mapFrag = (NearMapFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.map);
					mapFrag.showTickets();
					ticketBut.setImageDrawable(getResources().getDrawable(R.drawable.ticket_hide));
				}
				showTickets = !showTickets;
			}
		});
		
        return view;
    }
    
    @Override
    public void onResume() {
    	// TODO Auto-generated method stub
    	super.onResume();
    }
    
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
    	// TODO Auto-generated method stub
    	super.onActivityCreated(savedInstanceState);
    	//mapFrag = ((MapFragment)getFragmentManager().findFragmentById(R.id.map));
    }
    
    @Override
    public void onDestroy() {
    	// TODO Auto-generated method stub
    	Log.i(TAG, "fragment destoy");
    	SupportMapFragment mapFrag = ((SupportMapFragment)getActivity().getSupportFragmentManager().findFragmentById(R.id.map));
    	Log.i(TAG, "map fragment: " + mapFrag);
    	if(mapFrag != null)
    		mapFrag.onDestroyView();
    	super.onDestroy();
    }
}
