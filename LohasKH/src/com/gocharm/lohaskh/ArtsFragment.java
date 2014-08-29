package com.gocharm.lohaskh;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.viewpagerindicator.TabPageIndicator;

import android.content.Context;
import android.content.SharedPreferences;
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

public class ArtsFragment extends Fragment{
	private static final String TAG = "artsFrag";
	
    //static ArtsFragment artsFrag = null;
	
	public static ArtsFragment newInstance(){
		ArtsFragment artsFrag = new ArtsFragment();
		return  artsFrag;
	}
	
    public ArtsFragment() {
    }
    
    @Override
    public void onResume() {
    	// TODO Auto-generated method stub
    	Log.i(TAG, "arts Resume");
    	
    	super.onResume();
    }
    
    @Override
    public void onStop() {
    	// TODO Auto-generated method stub
    	Log.i(TAG, "arts Stop");
    	ImageLoader.getInstance().stop();
    	super.onStop();
    }
    
    @Override
    public void onPause() {
    	// TODO Auto-generated method stub
    	Log.i(TAG, "arts Pause");
    	super.onPause();
    }
    
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		Log.i(TAG, "arts Destroy");
		
		super.onDestroy();
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		Log.i(TAG, "arts Create");
		super.onCreate(savedInstanceState);
	}
    
	@Override
	public void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		Log.i(TAG, "saved instance");
	    super.onSaveInstanceState(outState);
	}
	
    //@SuppressLint("NewApi")
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	Log.i(TAG, "onCreateView");
    	final Context contextThemeWrapper = new ContextThemeWrapper(getActivity(), R.style.StyledIndicators2);
    	LayoutInflater localInflater = inflater.cloneInContext(contextThemeWrapper);
    	PagerAdapter mPagerAdapter;
    	ViewPager mViewPager;
    	View view = localInflater.inflate(R.layout.fragment_pager, container,false);
    	mViewPager = (ViewPager) view.findViewById(R.id.pager);
    	//mPagerAdapter = new PagerAdapter(getChildFragmentManager());
    	mPagerAdapter = new PagerAdapter(getFragmentManager());
    	TabPageIndicator tabIndicator = (TabPageIndicator) view.findViewById(R.id.mainTitles);
    	mViewPager.setOnPageChangeListener(listener);
		mViewPager.setAdapter(mPagerAdapter);
    	tabIndicator.setViewPager(mViewPager);
		tabIndicator.setOnPageChangeListener(listener);
		
    	return view;
    }
    
     public OnPageChangeListener listener = new OnPageChangeListener() {
		
		//@SuppressLint("NewApi")
		@Override
		public void onPageSelected(int arg0) {
			getActivity().supportInvalidateOptionsMenu();
		}
		
		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
			
		}
		
		@Override
		public void onPageScrollStateChanged(int arg0) {
		}
	};
    
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
    	super.onActivityCreated(savedInstanceState);
    }
    
    public class PagerAdapter extends FragmentStatePagerAdapter {

		public PagerAdapter(FragmentManager fm) {
			super(fm);
			// TODO Auto-generated constructor stub
		}

		@Override
		public Fragment getItem(int position) {
			ArtFragment fragment = ArtFragment.newInstance(position);//new ArtFragment(position);
			return fragment;
		}

		@Override
		public int getCount() {
			SharedPreferences preferences = getActivity().getApplication().getSharedPreferences(Util.PREF_NAME, 0);
			return preferences.getInt("art_cat_num", 13);
		}
		
		@Override
		public CharSequence getPageTitle(int position) {
			// TODO Auto-generated method stub
			SharedPreferences preferences = getActivity().getApplication().getSharedPreferences(Util.PREF_NAME, 0);
			return preferences.getString("art_cat_name_" + position, "活動" + position); //Util.artCatMap.get(position).getValue();
		}
    	
    }
    
    /**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the sections/tabs/pages.
	 */
	/*public class PagerAdapter extends FragmentPagerAdapter {

		public PagerAdapter(FragmentManager fm) {
			super(fm);
			
		}

		
		
		@Override
		public Fragment getItem(int position) {
			Log.i(TAG, "getItem " + position);
			ArtFragment fragment = ArtFragment.newInstance(position);//new ArtFragment(position);
			return fragment;
		}

		@Override
		public int getCount() {
			return 13;
		}
		
		@Override
		public CharSequence getPageTitle(int position) {
			return Util.artCatMap.get(position).getValue(); 
		}
	}*/
}

