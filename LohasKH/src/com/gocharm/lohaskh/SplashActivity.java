package com.gocharm.lohaskh;

import java.util.Map;

import org.apache.http.HttpResponse;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.coimotion.csdk.common.COIMCallListener;
import com.coimotion.csdk.common.COIMException;
import com.coimotion.csdk.util.Assist;
import com.coimotion.csdk.util.ReqUtil;
import com.coimotion.csdk.util.sws;
import com.google.android.gms.internal.im;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

import android.R.integer;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.webkit.WebView;

public class SplashActivity extends Activity {
	private static final String TAG = "splashAct";
	private SharedPreferences pref;
	private boolean f1 = false, f2 = false, f3= false;
	private ProgressDialog pd;
	private Handler handler;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		pref = getSharedPreferences(Util.PREF_NAME, MODE_PRIVATE);
		pref.edit().putBoolean("closeApp", false).commit();
		super.onCreate(savedInstanceState);
		handler = new Handler();
		setContentView(R.layout.activity_splash);
		
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this)
	      .threadPriority(Thread.MIN_PRIORITY)
	      .threadPoolSize(1)
	      .denyCacheImageMultipleSizesInMemory()
	      .discCacheFileNameGenerator(new Md5FileNameGenerator())
	      .tasksProcessingOrder(QueueProcessingType.LIFO)
	      .writeDebugLogs()
	      .build();
		ImageLoader.getInstance().init(config);
		
		try {
			ReqUtil.initSDK(getApplication());
			sws.initSws(getApplication());
		} catch (COIMException e) {
		} catch (Exception e) {
		}
		
		ReqUtil.send("iticket/cat/list/2", null, new COIMCallListener() {
			
			@Override
			public void onSuccess(JSONObject result) {
				if(0 == Assist.getErrCode(result)) {
					try {
						JSONArray list = ((JSONObject) result.get("value")).getJSONArray("list");
						Log.i(TAG, "ticket: " + list.length());
						pref.edit().putInt("ticket_cat_num", list.length()).commit();
						for(int i=0; i< list.length(); i++) {
							pref.edit()
							.putString("ticket_cat_id_" + i, list.getJSONObject(i).getString("btID"))
							.putString("ticket_cat_name_" + i, list.getJSONObject(i).getString("title"))
							.commit();
						}
						f1 = true;
					} catch (JSONException e) {
					}
				}
				else {
					Log.i(TAG, "err: " + Assist.getMessage(result));
				}
			}
			
			@Override
			public void onFail(HttpResponse response, Exception ex) {
				f1 = true;
				Log.i(TAG,"3 ex: " + ex.getLocalizedMessage());
				Assist.showAlert(SplashActivity.this, ex.getLocalizedMessage());
			}
		});
		
		ReqUtil.send("twShow/show/catList", null, new COIMCallListener() {
			
			@Override
			public void onSuccess(JSONObject result) {
				if(0 == Assist.getErrCode(result)) {
					try {
						JSONArray list = ((JSONObject) result.get("value")).getJSONArray("list");
						Log.i(TAG, "art: " + list.length());
						pref.edit().putInt("art_cat_num", list.length()).commit();
						for(int i=0; i< list.length(); i++) {
							pref.edit()
							.putString("art_cat_id_" + i, list.getJSONObject(i).getString("cat"))
							.putString("art_cat_name_" + i, list.getJSONObject(i).getString("name"))
							.commit();
						}
						f2 = true;
					} catch (JSONException e) {
					}
				}
				else {
					Log.i(TAG, "err: " + Assist.getMessage(result));
				}
			}
			
			@Override
			public void onFail(HttpResponse response, Exception ex) {
				f2 = true;
				Log.i(TAG, "2 ex: " + ex.getLocalizedMessage());
			}
		});
		
		ReqUtil.send("twVista/vista/listTag", null, new COIMCallListener() {
			
			@Override
			public void onSuccess(JSONObject result) {
				if(0 == Assist.getErrCode(result)) {
					try {
						JSONArray list = ((JSONObject) result.get("value")).getJSONArray("list");
						Log.i(TAG, "vista: " + list.length());
						pref.edit().putInt("vista_cat_num", list.length()).commit();
						for(int i=0; i< list.length(); i++) {
							pref.edit()
							.putString("vista_cat_id_" + i, list.getJSONObject(i).getString("tag"))
							.putString("vista_cat_name_" + i, list.getJSONObject(i).getString("tagName"))
							.commit();
						}
						f3 = true;
					} catch (JSONException e) {
					}
				}
				else {
					Log.i(TAG, "err: " + Assist.getMessage(result));
				}
			}
			
			@Override
			public void onFail(HttpResponse response, Exception ex) {
				f3 = true;
				Log.i(TAG, "1 ex: " + ex.getLocalizedMessage());
			}
		});
	}
	
	@Override
	protected void onResume() {
		if(pref.getBoolean("closeApp", false)) {
			pref.edit().remove("closeApp").commit();
			finish();
		}
		else {
			//if(pd == null)
			//	pd = new ProgressDialog(SplashActivity.this);
			//pd.setTitle("Varifying sp...");
			//pd.show();
			new Thread(new Runnable(){
			    @Override
			    public void run() {
			    	while(true) {
			    		if(f1 && f2 && f3) {
			    			Log.i(TAG, "categories loaded");
			    			break;
			    		}
		                try {
							Thread.sleep(500);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
		            }
			    	Log.i(TAG, "# ticket cat: " + pref.getInt("ticket_cat_num", 9999));
					Log.i(TAG, "# vista cat: " + pref.getInt("vista_cat_num", 9999));
					Log.i(TAG, "# art cat: " + pref.getInt("art_cat_num", 9999));
			    	
			        boolean firstTime = getSharedPreferences(Util.PREF_NAME, MODE_PRIVATE).getBoolean(Util.PROP.FIRST_TIME, true);
					if(firstTime) {
						Intent intent = new Intent(SplashActivity.this, TutorialActivity.class);
						startActivity(intent);
					}
					else {
						//Log.i(TAG, "pref logout: " + pref.getBoolean("logout", false));
						if(pref.getBoolean("logout", false)) {
							Log.i(TAG, "show welcome");
							pref.edit().remove("logout").commit();
							Intent intent = new Intent(SplashActivity.this, WelcomeActivity.class);
							startActivity(intent);
						}
						else {
							runOnUiThread(new Runnable() {
								
								@Override
								public void run() {
									// TODO Auto-generated method stub
									ReqUtil.send("core/user/profile",null, new COIMCallListener() {
										@Override
										public void onInvalidToken() {
											new AlertDialog.Builder(getApplicationContext()).setTitle("warning").setMessage("invalid token").show();
										}
										
										@Override
										public void onSuccess(JSONObject result) {
											//pd.dismiss();
											if(0 == Assist.getErrCode(result)) {
												
												
												try {
													JSONObject values = (JSONObject) result.get("value");
													Log.i(TAG, "res: " + values);
													if(values.getString("dspName").equalsIgnoreCase("Guest")) {
														Intent intent = new Intent(SplashActivity.this, MainActivity.class);
														startActivity(intent);
													}
													else {
														if(values.has("fbID")) {
															sws.loginFB(SplashActivity.this, new COIMCallListener() {
																
																@Override
																public void onSuccess(JSONObject result) {
																	// TODO Auto-generated method stub
																	Log.i(TAG, "res: " + result);
																	Intent intent = new Intent(SplashActivity.this, MainActivity.class);
																	startActivity(intent);
																	
																}
																
																@Override
																public void onFail(HttpResponse response, Exception ex) {
																	// TODO Auto-generated method stub
																	Assist.showAlert(SplashActivity.this, "timeout!!!!!");
																	Log.i(TAG, "ex for checkFB: " + ex.getLocalizedMessage());
																	Intent intent = new Intent(SplashActivity.this, WelcomeActivity.class);
																	startActivity(intent);
																}
															});
														}
														else {
															Intent intent = new Intent(SplashActivity.this, WelcomeActivity.class);
															startActivity(intent);
														}
													}
												} catch (JSONException e) {
													Intent intent = new Intent(SplashActivity.this, WelcomeActivity.class);
													startActivity(intent);
												}
											}
											else {
												Intent intent = new Intent(SplashActivity.this, WelcomeActivity.class);
												startActivity(intent);
											}
										}
										
										@Override
										public void onFail(HttpResponse response, Exception ex) {
											//Assist.showAlert(SplashActivity.this, "timeout");
											//pd.dismiss();
											Intent intent = new Intent(SplashActivity.this, WelcomeActivity.class);
											startActivity(intent);
										}
									});
								}
							});
						}
					}
			    //}            
			    }}).start();
		}
		super.onResume();
	}
}
