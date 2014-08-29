package com.gocharm.lohaskh;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.coimotion.csdk.common.COIMException;
import com.coimotion.csdk.util.ReqUtil;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.viewpagerindicator.TabPageIndicator;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.graphics.drawable.StateListDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SlidingDrawer;

@SuppressLint("SimpleDateFormat")
public class ArtDetailActivity extends ActionBarActivity {
	private static final String TAG = "artDetailAct";
	private DetailPagerAdapter mPagerAdapter;
	private ViewPager mViewPager;
	
	private JSONObject frag1Param;
	private JSONArray frag2Param, frag3Param;
	private String spID;
	private Map<String, String> dbParam;
	private DBHelper db;
	private String artInfo;
	
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		db.close();
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		Log.i(TAG, "saving instance");
		outState.putString("artInfo", getIntent().getExtras().getString("artInfo"));
		super.onSaveInstanceState(outState);
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		
		if (mViewPager.getCurrentItem() == 2) {
			SlidingDrawer sd = (SlidingDrawer) getSupportFragmentManager().findFragmentByTag("android:switcher:" + R.id.pager + ":2").getView().findViewById(R.id.slidedrawer);
			if(sd.isOpened()) {
				sd.animateClose();
				return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		try {
			ReqUtil.initSDK(getApplication());
		} catch (COIMException e) {
		} catch (Exception e) {
		}
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this)
	      .threadPriority(Thread.MIN_PRIORITY)
	      .threadPoolSize(1)
	      .denyCacheImageMultipleSizesInMemory()
	      .discCacheFileNameGenerator(new Md5FileNameGenerator())
	      .tasksProcessingOrder(QueueProcessingType.LIFO)
	      .writeDebugLogs()
	      .build();
		if(!ImageLoader.getInstance().isInited())
			ImageLoader.getInstance().init(config);
		
		
		if(savedInstanceState == null) {
			artInfo = getIntent().getExtras().getString("artInfo");
		}
		else {
			artInfo = savedInstanceState.getString("artInfo");
		}
		
		setContentView(R.layout.activity_detail);
		setTheme(R.style.StyledIndicators1);
		
		dbParam = new HashMap<String, String>(); 
		db = new DBHelper(getApplicationContext());
		frag3Param = new JSONArray();
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mPagerAdapter = new DetailPagerAdapter(getSupportFragmentManager());
		mViewPager.setOnPageChangeListener(listener);
		mViewPager.setAdapter(mPagerAdapter);
		TabPageIndicator tabIndicator = (TabPageIndicator) findViewById(R.id.detailTitles);
		
		StateListDrawable states = new StateListDrawable();
		states.addState(new int[] {android.R.attr.state_pressed},
		    getResources().getDrawable(R.drawable.tab_ind_selected));
		states.addState(new int[] {android.R.attr.state_focused},
		    getResources().getDrawable(R.drawable.tab_ind_selected));
		states.addState(new int[] { },
		    getResources().getDrawable(R.drawable.tab_ind_unselect));
		
		tabIndicator.setViewPager(mViewPager);
		tabIndicator.setOnPageChangeListener(listener);
		
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeButtonEnabled(true);
	}

	public OnPageChangeListener listener = new OnPageChangeListener() {
		@Override
		public void onPageSelected(int arg0) {}
		
		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {}
		
		@Override
		public void onPageScrollStateChanged(int arg0) {}
	};
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.art_detail, menu);
		MenuItem favor = menu.findItem(R.id.action_favor);
		if(db.hasID(spID, DBHelper.TABLE_ART))
			favor.setIcon(getResources().getDrawable(R.drawable.favor_remove));
		else
			favor.setIcon(getResources().getDrawable(R.drawable.favor_add));
		return true;
	}

	
	@Override
	protected void onStart() {
		Log.i(TAG, "on start");
		super.onStart();
		
		artInfo = (String) getIntent().getExtras().get("artInfo");
		try {
			JSONObject object = new JSONObject(artInfo);
			Map<String, String> tmp1 = new HashMap<String, String>();
			getSupportActionBar().setTitle(object.getString("title"));
			String tmp = "";
			spID = object.getString("spID");
			tmp1.put("imgURL", object.getString("imgURL"));
			tmp = object.getString("descTx");
			tmp = tmp.equals("")?"未提供":tmp;
			tmp1.put("descTx", tmp);
			
			tmp = object.getString("showOrg");
			tmp = tmp.equals("")?"未提供":tmp;
			tmp = tmp.split("[/]")[0];
			tmp1.put("showOrg", tmp);
			
			tmp1.put("infoSrc", object.getString("infoSrc"));
			tmp = object.getString("organizer");
			tmp = tmp.equals("")?"未提供":tmp;
			tmp1.put("organizer", tmp);
			tmp1.put("offiURL", object.getString("offiURL"));
			tmp1.put("saleURL", object.getString("saleURL"));
			frag1Param = new JSONObject(tmp1);
			dbParam.put(DBHelper.FIELD_SPID, spID);
			dbParam.put(DBHelper.FIELD_TITLE, object.getString("title"));
			dbParam.put(DBHelper.FIELD_IMAGEURL, object.getString("imgURL"));
			dbParam.put("addr", object.getString("addr"));
			
			frag2Param = new JSONArray();
			JSONArray showtimeArr = object.getJSONArray("showInfo");
			ArrayList<String> tmpArr = new ArrayList<String>(); 
			for(int i = 0, l = showtimeArr.length(); i < l; i++) {
				JSONObject place = new JSONObject();
				
				Date today = new Date(System.currentTimeMillis());
				
				String datetime = showtimeArr.getJSONObject(i).getString("time");
				
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

				java.util.Date aDate;
				try {
					aDate = sdf.parse(datetime);
					
					if(aDate.getTime() > today.getTime()) {
						frag2Param.put(showtimeArr.getJSONObject(i));
						
						place.put("placeName", showtimeArr.getJSONObject(i).getString("placeName").trim());
						place.put("addr", showtimeArr.getJSONObject(i).getString("addr").trim());
						place.put("latitude", showtimeArr.getJSONObject(i).getString("latitude").trim());
						place.put("longitude", showtimeArr.getJSONObject(i).getString("longitude").trim());
						if(!tmpArr.contains(place.toString())) {
							tmpArr.add(place.toString());
							frag3Param.put(place);
						}
					}
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()){
			case R.id.action_favor:
				Intent intent = new Intent(getApplicationContext(), WelcomeActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
				break;
			case android.R.id.home:
				finish();
				break;
		}
		return super.onOptionsItemSelected(item);
	}

	public class DetailPagerAdapter extends FragmentPagerAdapter {

		public DetailPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			Fragment fragment = null;;
			switch (position) {
				case 0:
					fragment = ArtDetailFrag1.newInstance(frag1Param); //new ArtDetailFrag1(frag1Param);
					break;
				case 1:
					fragment = ArtDetailFrag2.newInstance(frag2Param);//new ArtDetailFrag2(frag2Param);
					break;
				case 2:
					int clientGoogleplayVer = 0;
					try {
						clientGoogleplayVer = getPackageManager().getPackageInfo("com.google.android.gms", 0 ).versionCode;
					} catch (NameNotFoundException e) {
						fragment = new OverdateGoogleplayFragment();
						e.printStackTrace();
					}
					if(clientGoogleplayVer >= GooglePlayServicesUtil.isGooglePlayServicesAvailable(getApplicationContext())) {
						fragment = LocationDetailFrag.newInstance(frag3Param);
					}
					else {
						fragment = new OverdateGoogleplayFragment();
					}
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
			String string = "";
			switch (position) {
				case 0:
					string = "活動介紹"; 
					break;
				case 1:
					string = "場次資訊"; 
					break;
				case 2:
					string = "活動地點";
					break;
			}
			return string;
			
		}
	}
	
	
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		if(newConfig.orientation==Configuration.ORIENTATION_PORTRAIT){
		}
		if(newConfig.orientation==Configuration.ORIENTATION_LANDSCAPE){
		}
	}
	
}
