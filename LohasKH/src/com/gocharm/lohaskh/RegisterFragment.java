package com.gocharm.lohaskh;

import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.json.JSONObject;

import com.coimotion.csdk.common.COIMCallListener;
import com.coimotion.csdk.util.Assist;
import com.coimotion.csdk.util.ReqUtil;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

public class RegisterFragment extends Fragment{
	private static final String TAG = "registerFrag";
	private EditText userName;
	private EditText password;
	private EditText password2;
	
	public RegisterFragment(){}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_reg, container, false);
		TextView warning = (TextView) rootView.findViewById(R.id.warning);
		userName = (EditText) rootView.findViewById(R.id.regUserText);
		password = (EditText) rootView.findViewById(R.id.passText1);
		password2 = (EditText) rootView.findViewById(R.id.passText2);
		ImageButton submit = (ImageButton) rootView.findViewById(R.id.registerBut);
		submit.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (userName.getText().toString().equals("") || password.getText().toString().equals("") || password2.getText().toString().equals("")) {
					Log.i(TAG, "all empty");
					return;
				}
				
				if(!password.getText().toString().equals(password2.getText().toString()) ){
					Log.i(TAG, "passes not the same");
					return;
				}
				
				Map<String, Object> mapParam = new HashMap<String, Object>();
				mapParam.put("accName", userName.getText().toString());
				mapParam.put("passwd", password.getText().toString());
				mapParam.put("passwd2", password2.getText().toString());
				
				ReqUtil.registerUser(mapParam, new COIMCallListener() {
					@Override
					public void onInvalidToken() {
						new AlertDialog.Builder(getActivity()).setTitle("warning").setMessage("invalid token").show();
						
					}
					@Override
					public void onSuccess(JSONObject result) {
						if(0 == Assist.getErrCode(result)) {
							Log.i(TAG, "result: " + result);
						}
						else 
							Log.i(TAG, "err: " + Assist.getMessage(result));
					}
					
					@Override
					public void onFail(HttpResponse response, Exception ex) {
						Log.i(TAG, "ex: " + ex.getLocalizedMessage());
						password.setText("");
					}
				});
				
			}
		});
		warning.setText(Html.fromHtml(getResources().getString(R.string.register_warning)));
		return rootView;
	}
}
