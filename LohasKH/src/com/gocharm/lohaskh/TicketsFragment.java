package com.gocharm.lohaskh;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.viewpagerindicator.TabPageIndicator;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class TicketsFragment extends Fragment{
	private static final String TAG = "ticketFrag";
	//PagerAdapter mPagerAdapter;
	//ViewPager mViewPager;
    
    public TicketsFragment() {
    }
    
    @Override
    public void onStop() {
    	// TODO Auto-generated method stub
    	ImageLoader.getInstance().stop();
    	super.onStop();
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
    		Bundle savedInstanceState) {
    	// TODO Auto-generated method stub
    	final Context contextThemeWrapper = new ContextThemeWrapper(getActivity(), R.style.StyledIndicators2);
    	LayoutInflater localInflater = inflater.cloneInContext(contextThemeWrapper);
    	View view = localInflater.inflate(R.layout.fragment_pager, container,false);
    	PagerAdapter mPagerAdapter;
    	ViewPager mViewPager;
    	//mPagerAdapter = new PagerAdapter(getChildFragmentManager());
    	mPagerAdapter = new PagerAdapter(getFragmentManager());
    	mViewPager = (ViewPager) view.findViewById(R.id.pager);
    	mViewPager.setOnPageChangeListener(listener);
		mViewPager.setAdapter(mPagerAdapter);
		TabPageIndicator tabIndicator = (TabPageIndicator) view.findViewById(R.id.mainTitles);
		tabIndicator.setViewPager(mViewPager);
		tabIndicator.setOnPageChangeListener(listener);
		
    	return view;
    }
    
     public OnPageChangeListener listener = new OnPageChangeListener() {
		
		@Override
		public void onPageSelected(int arg0) {
			getActivity().supportInvalidateOptionsMenu();
			
		}
		
		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void onPageScrollStateChanged(int arg0) {
			// TODO Auto-generated method stub
			
		}
	};
    
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
    	// TODO Auto-generated method stub
    	super.onActivityCreated(savedInstanceState);
    }
    
    public class PagerAdapter extends FragmentStatePagerAdapter {

		public PagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			TicketFragment fragment = TicketFragment.newInstance(position);//new TicketFragment(position);
			return fragment;
		}

		@Override
		public int getCount() {
			return getActivity().getSharedPreferences(Util.PREF_NAME, 0).getInt("ticket_cat_num", 6);
		}
		
		@Override
		public CharSequence getPageTitle(int position) {
			return getActivity().getSharedPreferences(Util.PREF_NAME, 0).getString("ticket_cat_name_" + position, "Ãþ§O"+position);
			//Util.ticketCatMap.get(position).getValue(); 
		}
	}
}
