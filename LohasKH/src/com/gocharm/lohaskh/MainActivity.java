package com.gocharm.lohaskh;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.HttpResponse;
import org.json.JSONObject;

import com.coimotion.csdk.common.COIMCallListener;
import com.coimotion.csdk.common.COIMException;
import com.coimotion.csdk.util.ReqUtil;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.internal.view.SupportMenuInflater;
import android.support.v7.widget.SearchView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

@SuppressLint("NewApi")
public class MainActivity extends ActionBarActivity {
	private static final String TAG = "mainAct";
	String[] menutitles;
	TypedArray menuIcons;
	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	private ActionBarDrawerToggle mDrawerToggle;
	private List<DrawerRow> rowItems;
	private DrawerAdapter adapter;
	
	private double time1 = 0;
	private int currentFrag = 0;
	private int selected = -1;
	
	@SuppressLint("Recycle")
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
		
		setContentView(R.layout.activity_main);
		
		menutitles = getResources().getStringArray(R.array.drawer_titles);
		menuIcons = getResources().obtainTypedArray(R.array.drawer_icons);
		
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerList = (ListView) findViewById(R.id.drawer_list);
		
		rowItems = new ArrayList<DrawerRow>();
		
		for (int i = 0; i < menutitles.length; i++) {
			DrawerRow items = new DrawerRow(menutitles[i], menuIcons.getResourceId(i, -1));
			rowItems.add(items);
		}
		menuIcons.recycle();
		
		adapter = new DrawerAdapter(getApplicationContext(), rowItems);
		
		mDrawerList.setAdapter(adapter);
		
		mDrawerList.setOnItemClickListener(new DrawerListener());

		// enabling action bar app icon and behaving it as toggle button
		getSupportActionBar().setHomeButtonEnabled(true);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		
		mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.drawable.ic_drawer, R.string.app_name,R.string.app_name) {
			public void onDrawerClosed(View view) {
				supportInvalidateOptionsMenu();
			}
			
			public void onDrawerOpened(View drawerView) {
				supportInvalidateOptionsMenu();
			}
		};

		mDrawerLayout.setDrawerListener(mDrawerToggle);
		if(savedInstanceState!=null)
			updateDisplay(savedInstanceState.getInt("page"));
		else {
			boolean start = getSharedPreferences(Util.PREF_NAME, MODE_PRIVATE).getBoolean("defaultStart", true);
			if (start) {
				updateDisplay(5);
			}
			else {
				updateDisplay(0);
			}
		}
		
	}

	
	@SuppressLint("ShowToast")
	@Override
	public void onBackPressed() {
		double time2 = new Date().getTime();
		if (time2 - time1 < 1500 && time2 - time1 > 300) {
			getSharedPreferences(Util.PREF_NAME, MODE_PRIVATE).edit().putBoolean("closeApp", true).commit();
			super.onBackPressed();
		}
		else {
			time1 = time2;
			Toast.makeText(getApplicationContext(), "press back again to leave", Toast.LENGTH_SHORT).show();
		}
	}
	
	class DrawerListener implements ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id)
		{
			updateDisplay(position);
			changeColor();
		}
	}
	
	private void updateDisplay(int position) {
		Intent intent;
		
		if(selected == position) {
			mDrawerLayout.closeDrawer(mDrawerList);
			return;
		}
		currentFrag = position;
		switch (position){
			case 0:
				selected = 0;
				getSupportActionBar().setTitle("美食");
				getSupportFragmentManager().beginTransaction().replace(R.id.container, new TicketsFragment()).commit();
				break;
			case 1:
				selected = 1;
				getSupportActionBar().setTitle("景點");
				getSupportFragmentManager().beginTransaction().replace(R.id.container, new VistasFragment()).commit();
				break;
			case 2:
				selected = 2;
				getSupportActionBar().setTitle("藝文");
				getSupportFragmentManager().beginTransaction().replace(R.id.container, new ArtsFragment()).commit();
				break;
			case 3:
				selected = 3;
				getSupportActionBar().setTitle("附近");
				int clientGoogleplayVer = 0;
				try {
					clientGoogleplayVer = getPackageManager().getPackageInfo("com.google.android.gms", 0 ).versionCode;
				} catch (NameNotFoundException e) {
					getSupportFragmentManager().beginTransaction().replace(R.id.container, new OverdateGoogleplayFragment()).commit();
					e.printStackTrace();
				}
				if(clientGoogleplayVer >= GooglePlayServicesUtil.isGooglePlayServicesAvailable(getApplicationContext())) {
					getSupportFragmentManager().beginTransaction().replace(R.id.container, new NearbyFragment()).commit();
				}
				else {
					getSupportFragmentManager().beginTransaction().replace(R.id.container, new OverdateGoogleplayFragment()).commit();
				}
				break;
			case 4:
				selected = 4;
				getSupportActionBar().setTitle("收藏");
				getSupportFragmentManager().beginTransaction().replace(R.id.container, new FavoritesFragment()).commit();
				break;
			case 5:
				selected = 5;
				getSupportActionBar().setTitle("樂活記錄");
				getSupportFragmentManager().beginTransaction().replace(R.id.container, new RecordsFragment()).commit();
				break;
			
			case 6://�^��
				Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto","abc@gmail.com", null));
				emailIntent.putExtra(Intent.EXTRA_SUBJECT, "樂活玩高雄 版本1.0");
				emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, 
						"非常感謝您使用樂活玩高雄，歡迎給予寶貴建議，\n請選擇以下問題類型進行回報：\n" +
						"      功能服務問題\n\n" +
						"      定位問題\n\n" +
						"      資料問題\n\n" +
						"      其他問題或建議\n\n");
				startActivity(Intent.createChooser(emailIntent, "Send email..."));
				
				break;
				
			case 7://���
				Uri uri = Uri.parse("market://details?id=" + getApplicationContext().getPackageName());
			    Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
			    startActivity(goToMarket);
			    break;
			case 8://�p��ϥ�
				intent = new Intent(MainActivity.this, TutorialActivity.class);
				startActivity(intent);
				break;
			case 9://����
				showAbout();
				break;
			case 10://�n�X
				getSharedPreferences(Util.PREF_NAME, MODE_PRIVATE).edit().putBoolean("logout", true).commit();
				ReqUtil.logout(new COIMCallListener() {
					
					@Override
					public void onSuccess(JSONObject result) {
						// TODO Auto-generated method stub
						Intent i = new Intent(MainActivity.this, SplashActivity.class);
						//intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						startActivity(i);
						finish();
					}
					
					@Override
					public void onFail(HttpResponse response, Exception ex) {
						// TODO Auto-generated method stub
						Intent i = new Intent(MainActivity.this, SplashActivity.class);
						//intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						startActivity(i);
						finish();
					}
				});
				
				break;
		}
		mDrawerLayout.closeDrawer(mDrawerList);
	}
	
	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		mDrawerToggle.syncState();
	}
	
	private void changeColor(){
		
		for(int i = 0; i < 6; i++) {
			View v = mDrawerList.getChildAt(i).findViewById(R.id.rowImageLayout);
			if(i == selected) {
				v.setBackgroundColor(Color.rgb(37, 98, 176));
			}
			else 
				v.setBackgroundColor(Color.rgb(91, 153, 212));
			
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		
		Log.i(TAG, "create options menu");
		getMenuInflater().inflate(R.menu.list_frag, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Log.i(TAG, "select options menu");
		switch (item.getItemId()) {
			case android.R.id.home:
				if(mDrawerList.isShown())
					mDrawerLayout.closeDrawer(mDrawerList);
				else {
					mDrawerLayout.openDrawer(mDrawerList);
				}
				break;
			case R.id.action_search:
				Log.i(TAG, "mSearchView item: " + item.getClass().getSimpleName());
				
				break;
		}
		
		return true;
	}
	
	protected boolean isAlwaysExpanded() {
	    return false;
	}
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		
		switch (currentFrag) {
			case 0:
			case 1:
			case 2:
				menu.clear();
				SupportMenuInflater menuInflater = new SupportMenuInflater(this);
				menuInflater.inflate(R.menu.list_frag, menu);
				SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));
				Log.i(TAG, "searchView: " + searchView);
				final EditText searchText = (EditText) searchView.findViewById(R.id.search_src_text);
				searchText.setOnFocusChangeListener(new OnFocusChangeListener() {
					
					@Override
					public void onFocusChange(View v, boolean hasFocus) {
						if(!hasFocus) {
							InputMethodManager imm = (InputMethodManager)getSystemService(
								      Context.INPUT_METHOD_SERVICE);
								imm.hideSoftInputFromWindow(searchText.getWindowToken(), 0);
						}
					}
				});
				searchText.addTextChangedListener(new TextWatcher() {

						@Override
						public void onTextChanged(CharSequence s, int start, int before, int count) {
							switch (currentFrag) {
								case 0:
									ViewPager vp;
									ListView lv;
									TicketsFragment tickets = (TicketsFragment) getSupportFragmentManager().findFragmentById(R.id.container);
									vp = (ViewPager) tickets.getView().findViewById(R.id.pager);
									TicketFragment ticket = (TicketFragment) ((FragmentStatePagerAdapter) vp.getAdapter()).instantiateItem(vp, vp.getCurrentItem());
									lv = (ListView) ticket.getView().findViewById(R.id.artList);//vp.getRootView().findViewById(R.id.artList);
									if (lv.getAdapter() != null) {
										((LohasAdapter)lv.getAdapter()).getFilter().filter(s);
									}
									
									break;
								case 1:
									VistasFragment vistas = (VistasFragment) getSupportFragmentManager().findFragmentById(R.id.container);
									vp = (ViewPager) vistas.getView().findViewById(R.id.pager);
									VistaFragment vista = (VistaFragment) ((FragmentStatePagerAdapter) vp.getAdapter()).instantiateItem(vp, vp.getCurrentItem());
									lv = (ListView) vista.getView().findViewById(R.id.artList);//vp.getRootView().findViewById(R.id.artList);
									if (lv.getAdapter() != null) {
										((LohasAdapter)lv.getAdapter()).getFilter().filter(s);
									}
									break;
								case 2:
									ArtsFragment arts = (ArtsFragment) getSupportFragmentManager().findFragmentById(R.id.container);
									vp = (ViewPager) arts.getView().findViewById(R.id.pager);
									ArtFragment art = (ArtFragment) ((FragmentStatePagerAdapter) vp.getAdapter()).instantiateItem(vp, vp.getCurrentItem());
									lv = (ListView) art.getView().findViewById(R.id.artList);//vp.getRootView().findViewById(R.id.artList);
									if (lv.getAdapter() != null) {
										((LohasAdapter)lv.getAdapter()).getFilter().filter(s);
									}
									break;
								
							}
						}
	
						@Override
						public void beforeTextChanged(CharSequence s, int start, int count, int after) {
						}
	
						@Override
						public void afterTextChanged(Editable s) {
						}

					});
				break;
			default:
				menu.clear(); 
				break;
		}
		return true;
	}
	
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putInt("page", currentFrag);
		super.onSaveInstanceState(outState);
	}
	
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		if(newConfig.orientation==Configuration.ORIENTATION_PORTRAIT){
		}
		if(newConfig.orientation==Configuration.ORIENTATION_LANDSCAPE){
		}
	}

	public void showAbout(){
		Builder aboutDialog = new AlertDialog.Builder(this);
		aboutDialog.setTitle("關於樂活玩高雄");
		aboutDialog.setMessage("我們是一群熱愛高雄的團隊，高雄有許多的美食、有趣的景點和豐富的活動，" +
				"對於這樣的高雄，大家都不陌生。但常在機車陣中穿梭的我們，希望能換個方" +
				"式玩高雄，於是注入了樂活玩的概念──在一週中選個一兩天來搭乘大眾運輸" +
				"暢遊高雄，同時還可知道省下的碳排放量有多少，邊玩樂還可以愛地球！" +
				"想要一次滿足多種願望嗎？也許開始使用樂活玩高雄APP，是一個不錯的起點!");
		aboutDialog.show();
	}
	
}