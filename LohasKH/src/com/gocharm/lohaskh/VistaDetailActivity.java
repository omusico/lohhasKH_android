package com.gocharm.lohaskh;

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
import android.webkit.WebView;
import android.widget.SlidingDrawer;

public class VistaDetailActivity extends ActionBarActivity {

	private static final String TAG = "vistaDetailAct";
	private DetailPagerAdapter mPagerAdapter;
	private ViewPager mViewPager;
	
	private JSONObject frag1Param;
	private String qureyString;
	private JSONArray frag3Param;
	private Map<String, String> dbParam;
	private DBHelper db;
	private String ngID;
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		db.close();
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		Log.i(TAG, "save instance");
		outState.putString("vistaInfo", getIntent().getExtras().getString("vistaInfo"));
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
		dbParam = new HashMap<String, String>();
		db = new DBHelper(getApplicationContext());
		super.setTheme(R.style.StyledIndicators1);
		frag3Param = new JSONArray();
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mPagerAdapter = new DetailPagerAdapter(getSupportFragmentManager());
		mViewPager.setOnPageChangeListener(listener);
		mViewPager.setAdapter(mPagerAdapter);
		TabPageIndicator tabIndicator = (TabPageIndicator) findViewById(R.id.detailTitles);
		
		tabIndicator.setViewPager(mViewPager);
		tabIndicator.setOnPageChangeListener(listener);
		
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeButtonEnabled(true);
		String vistaInfo = "";
		if (savedInstanceState == null)
			vistaInfo = (String) getIntent().getExtras().get("vistaInfo");
		else {
			vistaInfo = savedInstanceState.getString("vistaInfo");
		}
		try {
			JSONObject object = new JSONObject(vistaInfo);
			Map<String, String> tmp1 = new HashMap<String, String>();
			getSupportActionBar().setTitle(object.getString("title"));
			ngID = object.getString("ngID");
			String tmp = "";
			tmp = object.getString("descTx");
			tmp = tmp.equals("")?"未提供":tmp;
			
			tmp1.put("imgURL", object.getString("imgURL"));
			tmp1.put("descTx", tmp);
			
			tmp = object.getString("openTime");
			tmp = tmp.equals("")?"未提供":tmp;
			tmp1.put("openTime", tmp);
			
			tmp = object.getString("tel");
			tmp = tmp.equals("")?"未提供":tmp;
			tmp1.put("tel", tmp);			
			frag1Param = new JSONObject(tmp1);
			
			qureyString = object.getString("title");
			
			JSONObject place = new JSONObject();
			place.put("placeName", object.getString("title"));
			place.put("addr", object.getString("addr"));
			place.put("latitude", object.getString("lat"));
			place.put("longitude", object.getString("lng"));
			frag3Param.put(place);
			
			dbParam.put("title", object.getString("title"));
			dbParam.put("openTime", object.getString("openTime"));
			dbParam.put("tel", object.getString("tel"));
			dbParam.put("addr", object.getString("addr"));
			dbParam.put("ngID", object.getString("ngID"));
			dbParam.put("imgURL", object.getString("imgURL"));
			dbParam.put("lat", object.getString("lat"));
			dbParam.put("lng", object.getString("lng"));
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	public OnPageChangeListener listener = new OnPageChangeListener() {
		
		@Override
		public void onPageSelected(int arg0) {
			// TODO Auto-generated method stub
			Log.i(TAG, arg0 + " selected");
			//Menu menu = getActivity().getMenuInflater().inflate(R.menu.list_frag, getActivity().get);
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
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.art_detail, menu);
		MenuItem favor = menu.findItem(R.id.action_favor);
		if(db.hasID(ngID, DBHelper.TABLE_VISTA))
			favor.setIcon(getResources().getDrawable(R.drawable.favor_remove));
		else
			favor.setIcon(getResources().getDrawable(R.drawable.favor_add));
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		switch (item.getItemId()){
			case R.id.action_favor:
				if(db.hasID(ngID, DBHelper.TABLE_VISTA)) {
					db.delete(ngID, DBHelper.TABLE_VISTA);
					item.setIcon(getResources().getDrawable(R.drawable.favor_add));
					
				}
				else {
					db.insert(dbParam, DBHelper.TABLE_VISTA);
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
					fragment = VistaDetailFrag1.newInstance(frag1Param);
					break;
				case 1:
					fragment = WebDetailFrag.newInstance(qureyString);
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
			//ArtFragment fragment = new ArtFragment(position);
			//Log.i(TAG, " paget adapter get item " + position);
			return fragment;
		}

		@Override
		public int getCount() {
			// Show 3 total pages.
			return 3;
		}
		
		@Override
		public CharSequence getPageTitle(int position) {
			String string = "";
			switch (position) {
				case 0:
					string = "景點介紹"; 
					break;
				case 1:
					string = "遊記"; 
					break;
				case 2:
					string = "交通位置";
					break;
			}
			return string;
			
		}
	}
	
	
	@SuppressWarnings("deprecation")
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (mViewPager.getCurrentItem() == 1) {
			WebView wv = (WebView) getSupportFragmentManager().findFragmentByTag("android:switcher:" + R.id.pager + ":1").getView().findViewById(R.id.webView);
			if ((keyCode == KeyEvent.KEYCODE_BACK) && wv.canGoBack()) {   
	            wv.goBack();   
	            return true;   
	        }else if(keyCode == KeyEvent.KEYCODE_BACK){
	        	ConfirmExit();
	        	return true; 
	        }
		}
		else if (mViewPager.getCurrentItem() == 2) {
			SlidingDrawer sd = (SlidingDrawer) getSupportFragmentManager().findFragmentByTag("android:switcher:" + R.id.pager + ":2").getView().findViewById(R.id.slidedrawer);
			if(sd.isOpened()) {
				sd.animateClose();
				return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}
	
	public void ConfirmExit(){
    	AlertDialog.Builder ad=new AlertDialog.Builder(VistaDetailActivity.this);
    	ad.setTitle("退出");
    	ad.setMessage("是否退出？");
    	ad.setPositiveButton("是", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int i) {
				VistaDetailActivity.this.finish();
			}
		});
    	ad.setNegativeButton("否",new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int i) {
			}
		});
    	ad.show();
    }
	
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		if(newConfig.orientation==Configuration.ORIENTATION_PORTRAIT){
		}
		if(newConfig.orientation==Configuration.ORIENTATION_LANDSCAPE){
		}
	}
}
