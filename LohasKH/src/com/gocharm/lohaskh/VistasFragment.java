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
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class VistasFragment extends Fragment{
	private static final String TAG = "vistasFrag";
	PagerAdapter mPagerAdapter;
	//ViewPager mViewPager;
    static VistasFragment vitasFrag = null;
	public static VistasFragment newInstance(){
		if(vitasFrag == null)
			vitasFrag = new VistasFragment();
		return  vitasFrag;
	}
	
    public VistasFragment() {
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
    	mPagerAdapter = new PagerAdapter(getFragmentManager());
    	//mVistasCatPagerAdapter = new VistasCatPagerAdapter(getChildFragmentManager());
    	mViewPager = (ViewPager) view.findViewById(R.id.pager);
    	mViewPager.setOnPageChangeListener(listener);
		mViewPager.setAdapter(mPagerAdapter);
		TabPageIndicator tabIndicator = (TabPageIndicator) view.findViewById(R.id.mainTitles);
		tabIndicator.setViewPager(mViewPager);
		tabIndicator.setOnPageChangeListener(listener);
		//TitlePageIndicator titleIndicator = (TitlePageIndicator) view.findViewById(R.id.titles);
		//titleIndicator.setViewPager(mViewPager);
		
    	return view;
    }
    
     public OnPageChangeListener listener = new OnPageChangeListener() {
		
		@Override
		public void onPageSelected(int arg0) {
			// TODO Auto-generated method stub
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
    	Log.i(TAG, "on act created");
    }
    
    public class PagerAdapter extends FragmentStatePagerAdapter {

		public PagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			// getItem is called to instantiate the fragment for the given page.
			// Return a PlaceholderFragment (defined as a static inner class
			// below).
			VistaFragment fragment = VistaFragment.newInstance(position);//new VistaFragment(position);
			Log.i(TAG, " paget adapter get item " + position);
			return fragment;
		}

		@Override
		public int getCount() {
			return getActivity().getSharedPreferences(Util.PREF_NAME, 0).getInt("vista_cat_num", 12);//Util.vistaCatMap.size();
		}
		
		@Override
		public CharSequence getPageTitle(int position) {
			return getActivity().getSharedPreferences(Util.PREF_NAME, 0).getString("vista_cat_name_" + position, "景點"+position);//Util.vistaCatMap.get(position).getValue(); 
		}
	}
    
    /**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the sections/tabs/pages.
	 */    
	/*public class VistasCatPagerAdapter extends FragmentPagerAdapter {

		public VistasCatPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			// getItem is called to instantiate the fragment for the given page.
			// Return a PlaceholderFragment (defined as a static inner class
			// below).
			VistaFragment fragment = VistaFragment.newInstance(position);//new VistaFragment(position);
			Log.i(TAG, " paget adapter get item " + position);
			return fragment;
		}

		@Override
		public int getCount() {
			// Show 3 total pages.
			return 11;
		}
		
		@Override
		public CharSequence getPageTitle(int position) {
			return Util.vistaCatMap.get(position).getValue(); 
		}
	}*/
}

