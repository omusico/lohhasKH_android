package com.gocharm.lohaskh;

import com.viewpagerindicator.TabPageIndicator;

import android.annotation.SuppressLint;
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

public class RecordsFragment extends Fragment{
	private static final String TAG = "favsFrag";
	PagerAdapter mPagerAdapter;
	ViewPager mViewPager;
    
	
    public RecordsFragment() {}
    
    
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
    		Bundle savedInstanceState) {
    	// TODO Auto-generated method stub
    	final Context contextThemeWrapper = new ContextThemeWrapper(getActivity(), R.style.StyledIndicators2);
    	LayoutInflater localInflater = inflater.cloneInContext(contextThemeWrapper);
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
		
		@SuppressLint("NewApi")
		@Override
		public void onPageSelected(int arg0) {
			// TODO Auto-generated method stub
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
    
    /**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the sections/tabs/pages.
	 */
	public class PagerAdapter extends FragmentStatePagerAdapter {

		public PagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			Fragment fragment = null;
			switch (position) {
				case 0:
					fragment = new RecordFragment1();
					break;
				case 1:
					fragment = new RecordFragment2();
					break;
				case 2:
					fragment = new RecordFragment3();
					break;
			default:
				break;
			}
			return fragment;
		}

		@Override
		public int getCount() {
			return 3;
		}
		
		@Override
		public CharSequence getPageTitle(int position) {
			String title = "";
			switch (position) {
				case 0:
					title = "累積記錄";
					break;
				case 1:
					title = "樂活記錄";
					break;
				case 2:
					title = "樂活排行";
					break;
			}
			return title; 
		}
	}
	
}
