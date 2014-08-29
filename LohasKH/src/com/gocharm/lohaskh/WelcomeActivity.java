package com.gocharm.lohaskh;

import org.apache.http.HttpResponse;
import org.json.JSONObject;

import com.coimotion.csdk.common.COIMCallListener;
import com.coimotion.csdk.util.Assist;
import com.coimotion.csdk.util.ReqUtil;
import com.coimotion.csdk.util.sws;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.ImageButton;

public class WelcomeActivity extends ActionBarActivity {
	private static final String TAG = "welcomeAct";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		DisplayMetrics metrics = getResources().getDisplayMetrics();
		Log.i(TAG, "welcome created");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_welcome);
		getSupportActionBar().setTitle("登入或註冊帳號");
		getSharedPreferences(Util.PREF_NAME, MODE_PRIVATE).edit().putBoolean(Util.PROP.FIRST_TIME, false).commit();
		
		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction().add(R.id.container, new PlaceholderFragment()).commit();
		}
	}

	
	
	@Override
	public void onBackPressed() {
		getSupportActionBar().setTitle("登入或註冊帳號");
		getSupportActionBar().setDisplayHomeAsUpEnabled(false);
		if(getSupportFragmentManager().getBackStackEntryCount()>1) {
			getSupportFragmentManager().popBackStack();
		}
		else {
			getSharedPreferences(Util.PREF_NAME, MODE_PRIVATE).edit().putBoolean("closeApp", true).commit();
			super.onBackPressed();
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.welcome, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent intent;
		switch (item.getItemId()) {
			case android.R.id.home:
				getSupportActionBar().setTitle("登入或註冊帳號");
				getSupportFragmentManager().popBackStack();
				getSupportActionBar().setDisplayHomeAsUpEnabled(false);
				break;
			case R.id.action_privacy:
				intent = new Intent(WelcomeActivity.this, ServiceActivity.class);
				intent.putExtra("content", R.string.privacy_text);
				startActivity(intent);
				break;
			case R.id.action_service:
				intent = new Intent(WelcomeActivity.this, ServiceActivity.class);
				intent.putExtra("content", R.string.service_text);
				startActivity(intent);
				break;
			case R.id.action_tutorial:
				intent = new Intent(WelcomeActivity.this, TutorialActivity.class);
				startActivity(intent);
				break;
			default:
				break;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment implements OnClickListener{

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_welcome, container, false);
			ImageButton lhLoginBut = (ImageButton) rootView.findViewById(R.id.lhLoginBut);
			ImageButton fbLoginBut = (ImageButton) rootView.findViewById(R.id.fbLoginBut);
			ImageButton regBut = (ImageButton) rootView.findViewById(R.id.regBut);
			lhLoginBut.setOnClickListener(this);
			fbLoginBut.setOnClickListener(this);
			regBut.setOnClickListener(this);
			return rootView;
		}

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.lhLoginBut:
				((ActionBarActivity)getActivity()).getSupportActionBar().setTitle("登入");
				((ActionBarActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
				((ActionBarActivity)getActivity()).getSupportFragmentManager().beginTransaction().add(R.id.container, new LoginFragment()).addToBackStack("login").commit();
				
				break;
			case R.id.fbLoginBut:
				//Intent intent = new Intent(getActivity(), MainActivity.class);
				//startActivity(intent);
				//getActivity().finish();
				sws.loginFB(getActivity(), new COIMCallListener() {
					
					@Override
					public void onSuccess(JSONObject result) {
						// TODO Auto-generated method stub
						
						Log.i(TAG, "fb login result:" + result);
						Intent intent = new Intent(getActivity(), MainActivity.class);
						startActivity(intent);
						getActivity().finish();
					}
					
					@Override
					public void onFail(HttpResponse response, Exception ex) {
						// TODO Auto-generated method stub
						Assist.showAlert(getActivity(), "ex: " + ex.getLocalizedMessage());
					}
				});
				
				break;
			case R.id.regBut:
				//CookieSyncManager.createInstance(MainActivity.this);
				CookieManager cookieManager = CookieManager.getInstance();
				cookieManager.removeAllCookie();
				//((ActionBarActivity)getActivity()).getSupportActionBar().setTitle("註冊帳號");
				//((ActionBarActivity)getActivity()).getSupportFragmentManager().beginTransaction().add(R.id.container, new RegisterFragment()).addToBackStack("reg").commit();
				//((ActionBarActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
				
				break;
			}
		}
		
	}
}
