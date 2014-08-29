package com.gocharm.lohaskh;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

@SuppressLint("ValidFragment")
public class WebDetailFrag extends Fragment{
	private static final String TAG = "webViewFrag";
	private String url = "http://www.google.com/search?hl=zh-TW&q=高雄+";
	private WebView wv;
	public WebDetailFrag() {}
	
	public static WebDetailFrag newInstance(String query) {
		WebDetailFrag f = new WebDetailFrag();
		Bundle args = new Bundle();
		args.putString("args", query);
		f.setArguments(args);
		return f;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		url += getArguments().getString("args");
		View view = inflater.inflate(R.layout.fragment_webview_detail, container, false);
		wv = (WebView) view.findViewById(R.id.webView);
		WebSettings wSettings = wv.getSettings();
		wSettings.setSupportZoom(true);
		wv.setWebViewClient(new WebViewClient());
		wv.loadUrl(url);
		return view;
	}
}
