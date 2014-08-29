package com.gocharm.lohaskh;

import com.viewpagerindicator.CirclePageIndicator;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

public class TutorialActivity extends FragmentActivity {
	private static final String TAG = "TutorialAct";
	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide
	 * fragments for each of the sections. We use a {@link FragmentPagerAdapter}
	 * derivative, which will keep every loaded fragment in memory. If this
	 * becomes too memory intensive, it may be best to switch to a
	 * {@link android.support.v13.app.FragmentStatePagerAdapter}.
	 */
	SectionsPagerAdapter mSectionsPagerAdapter;

	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	private ViewPager mViewPager;
	private CirclePageIndicator mIndicator;

	private static final int PAGE_NUM = 4;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tutorial);
		
		
		Button skipBut = (Button) findViewById(R.id.skipButton);
		//indicatorHolder = (LinearLayout) findViewById(R.id.indicatorHolder);
		//updateIndicator(0);
		skipBut.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(TutorialActivity.this, WelcomeActivity.class);
				startActivity(intent);
				finish();
			}
		});
		boolean firstTime = getSharedPreferences(Util.PREF_NAME, MODE_PRIVATE).getBoolean(Util.PROP.FIRST_TIME, true);
		Log.i(TAG, "first time? " + firstTime);
		if(!firstTime) {
			skipBut.setVisibility(View.INVISIBLE);
		}
		// Create the adapter that will return a fragment for each of the three
		// primary sections of the activity.
		mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
		
		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);
		
		mViewPager.setOnPageChangeListener(new OnPageChangeListener() {
			
			@Override
			public void onPageSelected(int arg0) {
				// TODO Auto-generated method stub
				//updateIndicator(arg0);
				
			}
			
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onPageScrollStateChanged(int arg0) {
				// TODO Auto-generated method stub
				
			}
		});

		mIndicator = (CirclePageIndicator)findViewById(R.id.indicator);
        mIndicator.setViewPager(mViewPager);
	}
	
	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the sections/tabs/pages.
	 */
	public class SectionsPagerAdapter extends FragmentPagerAdapter {

		public SectionsPagerAdapter(FragmentManager fragmentManager) {
			super(fragmentManager);
		}

		@Override
		public Fragment getItem(int position) {
			// getItem is called to instantiate the fragment for the given page.
			// Return a PlaceholderFragment (defined as a static inner class
			// below).
			
			return PlaceholderFragment.newInstance(position + 1);
		}

		@Override
		public int getCount() {
			// Show 3 total pages.
			return PAGE_NUM;
		}
		
		@Override
		public CharSequence getPageTitle(int position) {
			
			return null;
		}
	}
	
	/*public void updateIndicator(int currentPage) {
        indicatorHolder.removeAllViews();
        DotsScrollBar.createDotScrollBar(this, indicatorHolder,
                currentPage, PAGE_NUM);
    }*/

	/*public static class DotsScrollBar
	{
	    LinearLayout main_image_holder;
	    public static void createDotScrollBar(Context context, LinearLayout main_holder,int selectedPage,int count)
	    {
	        for(int i=0;i<count;i++)
	        {
	            ImageView dot = null;
	            dot= new ImageView(context);
	            LinearLayout.LayoutParams vp = 
	                new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, 
	                                LayoutParams.WRAP_CONTENT);
	            vp.setMargins(0, 10, 10, 0);
	            dot.setLayoutParams(vp);    
	            if(i==selectedPage)
	            {
	                try {
	                    dot.setImageResource(R.drawable.paging_h);
	                } catch (Exception e) 
	                {
	                    Log.d("inside DotsScrollBar.java","could not locate identifier");
	                }
	            }else
	            {
	                dot.setImageResource(R.drawable.paging_n);
	            }
	            main_holder.addView(dot);
	        }
	        main_holder.invalidate();
	    }
	}*/
	
	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {
		/**
		 * The fragment argument representing the section number for this
		 * fragment.
		 */
		private static final String ARG_SECTION_NUMBER = "section_number";

		/**
		 * Returns a new instance of this fragment for the given section number.
		 */
		public static PlaceholderFragment newInstance(int sectionNumber) {
			PlaceholderFragment fragment = new PlaceholderFragment();
			Bundle args = new Bundle();
			args.putInt(ARG_SECTION_NUMBER, sectionNumber);
			fragment.setArguments(args);
			return fragment;
		}

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_tutorial,
					container, false);
			ImageView imgView = (ImageView) rootView.findViewById(R.id.tutoImage);
			int index = getArguments().getInt(
					ARG_SECTION_NUMBER);
			rootView.setBackgroundColor(getResources().getColor(R.color.pinkIvy));
			switch (index) {
			case 1:
				imgView.setImageDrawable(getResources().getDrawable(R.drawable.tuto1));
				break;
			case 2:
				imgView.setImageDrawable(getResources().getDrawable(R.drawable.tuto2));
				break;
			case 3:
				imgView.setImageDrawable(getResources().getDrawable(R.drawable.tuto3));
				break;
			default:
				imgView.setImageDrawable(getResources().getDrawable(R.drawable.tuto4));
				break;
			}
			return rootView;
		}
	}

}
