package com.gocharm.lohaskh;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class ServiceActivity extends FragmentActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_service);
		int id = getIntent().getExtras().getInt("content");
		TextView servText = (TextView) findViewById(R.id.serviceText);
		servText.setText(Html.fromHtml(getResources().getString(id)));
		
		Button closeButton = (Button) findViewById(R.id.ruleCloseBut);
		closeButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
	}

}
