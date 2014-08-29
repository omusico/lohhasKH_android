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

public class FavoritesFragment extends Fragment{
	private static final String TAG = "favsFrag";
	private PagerAdapter mPagerAdapter;
	private ViewPager mViewPager;
    
	
    public FavoritesFragment() {}
    
    
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
    		Bundle savedInstanceState) {
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
			switch (position+1) {
				case DBHelper.TABLE_ART:
					fragment = new FavorArtFragment();
					break;
				case DBHelper.TABLE_TICKET:
					fragment = new FavorTicketFragment();
					break;
				case DBHelper.TABLE_VISTA:
					fragment = new FavorVistaFragment();
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
			return Util.favorCatMap.get(position).getValue(); 
		}
	}
}
