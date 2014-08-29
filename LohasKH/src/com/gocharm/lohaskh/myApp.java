package com.gocharm.lohaskh;

import com.coimotion.csdk.common.COIMException;
import com.coimotion.csdk.util.ReqUtil;
import com.coimotion.csdk.util.sws;

import android.app.Application;

public class myApp extends Application{

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		try {
			ReqUtil.initSDK(this);
			sws.initSws(this);
		} catch (COIMException e) {
		} catch (Exception e) {
		}
		super.onCreate();
	}
	
}
