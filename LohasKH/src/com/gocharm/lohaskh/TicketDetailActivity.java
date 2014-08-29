package com.gocharm.lohaskh;

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

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
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
import android.view.View;
import android.webkit.WebView;
import android.widget.ScrollView;
import android.widget.SlidingDrawer;
import android.widget.Toast;

public class TicketDetailActivity extends ActionBarActivity {

	private static final String TAG = "ticketDetailAct";
	private DetailPagerAdapter mPagerAdapter;
	private ViewPager mViewPager;
	private JSONObject frag1Param;
	private String qureyString;
	private Map<String, String> dbParam;
	private ArrayList<Map<String, String>> frag3Param;
	private JSONArray frag2Param, locationFrag;
	private DBHelper db;
	private String siID;
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		db.close();
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		Log.i(TAG, "save instance");
		outState.putString("ticketInfo", getIntent().getExtras().getString("ticketInfo"));
		super.onSaveInstanceState(outState);
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
		
		
		setContentView(R.layout.activity_detail);
		super.setTheme(R.style.StyledIndicators1);
		db = new DBHelper(getApplicationContext());
		locationFrag = new JSONArray();
		super.setTheme(R.style.StyledIndicators1);
		frag3Param = new ArrayList<Map<String, String>>();
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mPagerAdapter = new DetailPagerAdapter(getSupportFragmentManager());
		mViewPager.setOnPageChangeListener(listener);
		mViewPager.setAdapter(mPagerAdapter);
		TabPageIndicator tabIndicator = (TabPageIndicator) findViewById(R.id.detailTitles);
		tabIndicator.setViewPager(mViewPager);
		tabIndicator.setOnPageChangeListener(listener);
		
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeButtonEnabled(true);
		
		dbParam = new HashMap<String, String>();
		String ticketInfo = "";
		if(savedInstanceState == null) {
			ticketInfo = (String) getIntent().getExtras().get("ticketInfo");
		}
		else {
			ticketInfo = savedInstanceState.getString("ticketInfo");
		}
		Log.i(TAG, "ticketInfo: " + ticketInfo);
		try {
			JSONObject object = new JSONObject(ticketInfo);
			Map<String, String> tmp1 = new HashMap<String, String>();
			getSupportActionBar().setTitle(object.getString("name"));
			siID = object.getString("siID");
			String tmp = "";
			tmp = object.getString("summary");
			tmp = tmp.equals("")?"未提供":tmp;
			
			tmp1.put("descTx", tmp);
			tmp1.put("imgURL", object.getString("imgURL"));
			tmp = object.getString("openTime");
			tmp = tmp.equals("null")?"未提供":tmp;
			tmp1.put("openTime", tmp);
			
			tmp = object.getString("closeTime");
			tmp = tmp.equals("null")?"未提供":tmp;
			tmp1.put("closeTime", tmp);
			
			tmp = object.getString("restTo");
			tmp = tmp.equals("null")?"未提供":tmp;
			tmp1.put("restTo", tmp);
			
			tmp = object.getString("restFrom");
			tmp = tmp.equals("null")?"未提供":tmp;
			tmp1.put("restFrom", tmp);
			
			tmp = object.getString("tel");
			tmp = tmp.equals("")?"未提供":tmp;
			tmp1.put("tel", tmp);
			
			frag1Param = new JSONObject(tmp1);
			
			frag2Param = object.getJSONArray("prodList");
			
			dbParam.put(DBHelper.FIELD_SIID, siID);
			dbParam.put(DBHelper.FIELD_TITLE, object.getString("name"));
			dbParam.put("addr", object.getString("addr"));
			dbParam.put("tel", object.getString("tel"));
			dbParam.put("lat", object.getString("lat"));
			dbParam.put("lng", object.getString("lng"));
			dbParam.put("imgURL", object.getString("imgURL"));
			
			qureyString = object.getString("name");
			
			Map<String, String> place = new HashMap<String, String>();
			JSONObject jPlace = new JSONObject();
			jPlace.put("placeName", object.getString("name"));
			jPlace.put("addr", object.getString("addr"));
			jPlace.put("latitude", object.getString("lat"));
			jPlace.put("longitude", object.getString("lng"));
			locationFrag.put(jPlace);
			place.put("placeName", object.getString("name"));
			place.put("addr", object.getString("addr"));
			place.put("latitude", object.getString("lat"));
			place.put("longitude", object.getString("lng"));
			frag3Param.add(place);
		} catch (JSONException e) {
		}
		
	}

	public OnPageChangeListener listener = new OnPageChangeListener() {
		
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
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.art_detail, menu);
		MenuItem favor = menu.findItem(R.id.action_favor);
		if(db.hasID(siID, DBHelper.TABLE_TICKET))
			favor.setIcon(getResources().getDrawable(R.drawable.favor_remove));
		else
			favor.setIcon(getResources().getDrawable(R.drawable.favor_add));
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()){
			case R.id.action_favor:
				/*getSharedPreferences(Util.PREF_NAME, MODE_PRIVATE).edit().putBoolean("logout", true).commit();
				Intent intent = new Intent(this, SplashActivity.class);
				startActivity(intent);*/
				
				
				if(db.hasID(siID, DBHelper.TABLE_TICKET)) {
					db.delete(siID, DBHelper.TABLE_TICKET);
					item.setIcon(getResources().getDrawable(R.drawable.favor_add));
				}
				else {
					db.insert(dbParam, DBHelper.TABLE_TICKET);
					item.setIcon(getResources().getDrawable(R.drawable.favor_remove));
				}
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
			// getItem is called to instantiate the fragment for the given page.
			// Return a PlaceholderFragment (defined as a static inner class
			// below).
			Fragment fragment = null;;
			switch (position) {
				case 0:
					fragment = TicketDetailFrag1.newInstance(frag1Param);
					break;
				case 1:
					fragment = TicketDetailFrag2.newInstance(frag2Param);
					break;
				case 2:
					fragment = WebDetailFrag.newInstance(qureyString);//new WebDetailFrag(qureyString);
					break;
				case 3:
					int clientGoogleplayVer = 0;
					try {
						clientGoogleplayVer = getPackageManager().getPackageInfo("com.google.android.gms", 0 ).versionCode;
					} catch (NameNotFoundException e) {
						fragment = new OverdateGoogleplayFragment();
						e.printStackTrace();
					}
					if(clientGoogleplayVer >= GooglePlayServicesUtil.isGooglePlayServicesAvailable(getApplicationContext())) {
						fragment = LocationDetailFrag.newInstance(locationFrag);
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
			return 4;
		}
		
		@Override
		public CharSequence getPageTitle(int position) {
			String string = "";
			switch (position) {
				case 0:
					string = "餐廳介紹"; 
					break;
				case 1:
					string = "優惠"; 
					break;
				case 2:
					string = "食記"; 
					break;
				case 3:
					string = "交通位置";
					break;
			}
			return string;
			
		}
	}
	
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		if(newConfig.orientation==Configuration.ORIENTATION_PORTRAIT){
			Toast.makeText(this, "portrait", Toast.LENGTH_SHORT).show();
		}
		if(newConfig.orientation==Configuration.ORIENTATION_LANDSCAPE){
			Toast.makeText(this, "landscape", Toast.LENGTH_SHORT).show();
		}
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (mViewPager.getCurrentItem() == 2) {
			WebView wv = (WebView) getSupportFragmentManager().findFragmentByTag("android:switcher:" + R.id.pager + ":2").getView().findViewById(R.id.webView);
			if ((keyCode == KeyEvent.KEYCODE_BACK) && wv.canGoBack()) {   
	            wv.goBack();   
	            return true;   
	        }else if(keyCode == KeyEvent.KEYCODE_BACK){
	        	ConfirmExit();
	        	return true; 
	        }
		}
		else if (mViewPager.getCurrentItem() == 1) {
			ScrollView scv = (ScrollView) getSupportFragmentManager().findFragmentByTag("android:switcher:" + R.id.pager + ":1").getView().findViewById(R.id.shopView);
			if(scv.isShown()) {
				scv.setVisibility(View.INVISIBLE);
				return true;
			}
		}
		else if (mViewPager.getCurrentItem() == 3) {
			SlidingDrawer sd = (SlidingDrawer) getSupportFragmentManager().findFragmentByTag("android:switcher:" + R.id.pager + ":3").getView().findViewById(R.id.slidedrawer);
			if(sd.isOpened()) {
				sd.animateClose();
				return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}
	
	public void ConfirmExit(){
    	AlertDialog.Builder ad = new AlertDialog.Builder(TicketDetailActivity.this);
    	ad.setTitle("退出");
    	ad.setMessage("是否退出？");
    	ad.setPositiveButton("是", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int i) {
				TicketDetailActivity.this.finish();
			}
		});
    	ad.setNegativeButton("否",new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int i) {
			}
		});
    	ad.show();
    }
	
	
}
