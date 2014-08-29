package com.gocharm.lohaskh;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.apache.http.message.BasicNameValuePair;

import android.app.AlertDialog;
import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.View;
import android.widget.ImageView;

import com.google.android.gms.maps.model.LatLng;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

public class Util {

	public static final String PREF_NAME = "LOHASKH";
	
	public static class PROP{
		public static String FIRST_TIME = "firstTime";
	}
	
	public static void noNetworkAlert(Context c) {
		AlertDialog.Builder alert = new AlertDialog.Builder(c);
		alert.setTitle("無法取得網路連線");
		alert.setMessage("請確認您的網路狀態");
		alert.show();
	}
	public static boolean isNetworkAvailable(Application a){  
	
		ConnectivityManager conMgr = (ConnectivityManager) a.getSystemService(Context.CONNECTIVITY_SERVICE);
		return !(conMgr.getNetworkInfo(0).getState() == NetworkInfo.State.DISCONNECTED) ||
			   !(conMgr.getNetworkInfo(1).getState() == NetworkInfo.State.DISCONNECTED);
	}
	
	
	public static ArrayList<BasicNameValuePair> artCatMap = new ArrayList<BasicNameValuePair>();
	static
	{
		{
			BasicNameValuePair kv = new BasicNameValuePair("1", "音樂表演");
    		artCatMap.add(kv);
		}
		{
			BasicNameValuePair kv = new BasicNameValuePair("2", "戲劇表演");
    		artCatMap.add(kv);
		}
		{
			BasicNameValuePair kv = new BasicNameValuePair("3", "舞蹈表演");
    		artCatMap.add(kv);
		}
		{
			BasicNameValuePair kv = new BasicNameValuePair("4", "親子活動");
    		artCatMap.add(kv);
		}
		{
			BasicNameValuePair kv = new BasicNameValuePair("5", "獨立音樂");
    		artCatMap.add(kv);
		}
		{
			BasicNameValuePair kv = new BasicNameValuePair("6", "展覽");
    		artCatMap.add(kv);
		}
		{
			BasicNameValuePair kv = new BasicNameValuePair("7", "講座");
    		artCatMap.add(kv);
		}
		{
			BasicNameValuePair kv = new BasicNameValuePair("8", "電影");
    		artCatMap.add(kv);
		}
		{
			BasicNameValuePair kv = new BasicNameValuePair("11", "綜藝活動");
    		artCatMap.add(kv);
		}
		{
			BasicNameValuePair kv = new BasicNameValuePair("13", "競賽活動");
    		artCatMap.add(kv);
		}
		{
			BasicNameValuePair kv = new BasicNameValuePair("14", "徵選活動");
    		artCatMap.add(kv);
		}
		{
			BasicNameValuePair kv = new BasicNameValuePair("15", "其它藝文資訊");
    		artCatMap.add(kv);
		}
		{
			BasicNameValuePair kv = new BasicNameValuePair("17", "演唱會");
    		artCatMap.add(kv);
		}
	};
	public static ArrayList<BasicNameValuePair> vistaCatMap = new ArrayList<BasicNameValuePair>();
	static
	{
		{
			BasicNameValuePair kv = new BasicNameValuePair("1", "商圈");
			vistaCatMap.add(kv);
		}
		{
			BasicNameValuePair kv = new BasicNameValuePair("2", "夜市");
			vistaCatMap.add(kv);
		}
		{
			BasicNameValuePair kv = new BasicNameValuePair("3", "著名景點");
			vistaCatMap.add(kv);
		}
		{
			BasicNameValuePair kv = new BasicNameValuePair("4", "歷史古蹟");
			vistaCatMap.add(kv);
		}
		{
			BasicNameValuePair kv = new BasicNameValuePair("5", "藝文館");
			vistaCatMap.add(kv);
		}
		{
			BasicNameValuePair kv = new BasicNameValuePair("6", "河岸碼頭");
			vistaCatMap.add(kv);
		}
		{
			BasicNameValuePair kv = new BasicNameValuePair("7", "廟宇教堂");
			vistaCatMap.add(kv);
		}
		{
			BasicNameValuePair kv = new BasicNameValuePair("8", "休閒踏青");
			vistaCatMap.add(kv);
		}
		{
			BasicNameValuePair kv = new BasicNameValuePair("9", "風俗節慶");
			vistaCatMap.add(kv);
		}
		{
			BasicNameValuePair kv = new BasicNameValuePair("10", "生態");
			vistaCatMap.add(kv);
		}
		{
			BasicNameValuePair kv = new BasicNameValuePair("11", "溫泉");
			vistaCatMap.add(kv);
		}
		{
			BasicNameValuePair kv = new BasicNameValuePair("12", "未分類");
			vistaCatMap.add(kv);
		}
    };
    
    public static ArrayList<BasicNameValuePair> ticketCatMap = new ArrayList<BasicNameValuePair>();
	static
	{
		{
			BasicNameValuePair kv = new BasicNameValuePair("3", "主題餐廳");
			ticketCatMap.add(kv);
		}
		{
			BasicNameValuePair kv = new BasicNameValuePair("4", "港式飲茶");
			ticketCatMap.add(kv);
		}
		{
			BasicNameValuePair kv = new BasicNameValuePair("5", "異國料理");
			ticketCatMap.add(kv);
		}
		{
			BasicNameValuePair kv = new BasicNameValuePair("6", "素食餐廳");
			ticketCatMap.add(kv);
		}
		{
			BasicNameValuePair kv = new BasicNameValuePair("7", "自助餐廳");
			ticketCatMap.add(kv);
		}
		{
			BasicNameValuePair kv = new BasicNameValuePair("8", "日本料理");
			ticketCatMap.add(kv);
		}
    };
    
    public static ArrayList<BasicNameValuePair> favorCatMap = new ArrayList<BasicNameValuePair>();
	static
	{
		{
			BasicNameValuePair kv = new BasicNameValuePair("1", "美食");
			favorCatMap.add(kv);
		}
		{
			BasicNameValuePair kv = new BasicNameValuePair("2", "景點");
			favorCatMap.add(kv);
		}
		{
			BasicNameValuePair kv = new BasicNameValuePair("3", "藝文");
			favorCatMap.add(kv);
		}
		
    };    
    public static class AnimateFirstDisplayListener extends SimpleImageLoadingListener {

		static final List<String> displayedImages = Collections.synchronizedList(new LinkedList<String>());

		@Override
		public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
			if (loadedImage != null) {
				ImageView imageView = (ImageView) view;
				boolean firstDisplay = !displayedImages.contains(imageUri);
				if (firstDisplay) {
					FadeInBitmapDisplayer.animate(imageView, 500);
					displayedImages.add(imageUri);
				}
			}
		}
	}    
    public static DisplayImageOptions options = new DisplayImageOptions.Builder()
											.showImageForEmptyUri(R.drawable.nopic)
											.cacheOnDisc(true)
											.considerExifParams(false)
											.bitmapConfig(Bitmap.Config.RGB_565)
											//.imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
											.imageScaleType(ImageScaleType.IN_SAMPLE_INT)
											.build();
    public static DisplayImageOptions artOptions = new DisplayImageOptions.Builder()
											.showImageForEmptyUri(R.drawable.nopic_arts)
											.cacheOnDisc(true)
											.considerExifParams(false)
											.bitmapConfig(Bitmap.Config.RGB_565)
											.imageScaleType(ImageScaleType.IN_SAMPLE_INT)
											.build();
    public static DisplayImageOptions vistaOptions = new DisplayImageOptions.Builder()
											.showImageForEmptyUri(R.drawable.nopic_vista)
											.cacheOnDisc(true)
											.considerExifParams(false)
											.bitmapConfig(Bitmap.Config.RGB_565)
											.imageScaleType(ImageScaleType.IN_SAMPLE_INT)
											.build();
    public static DisplayImageOptions ticketOptions = new DisplayImageOptions.Builder()
											.showImageForEmptyUri(R.drawable.nopic_ticket)
											.cacheOnDisc(true)
											.considerExifParams(false)
											.bitmapConfig(Bitmap.Config.RGB_565)
											.imageScaleType(ImageScaleType.IN_SAMPLE_INT)
											.build();
    
    public static ArrayList<LatLng> decodePoly(String encoded) {
        ArrayList<LatLng> poly = new ArrayList<LatLng>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;
        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;                 
                shift += 5;             
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;
            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;                 
                shift += 5;             
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;
 
            LatLng position = new LatLng((double) lat / 1E5, (double) lng / 1E5);
            poly.add(position);
        }
        return poly;
    }
    
}
