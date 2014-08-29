package com.gocharm.lohaskh;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("ValidFragment")
public class TicketDetailFrag2 extends Fragment {

	private static final String TAG = "ticketDetailFrag2";
	private JSONArray content;
	private ArrayList<HashMap<String,String>> dataArray;
	private TextView titleTV, priceTV, offerTV, sumTV, totalTV, payText, takeText;
	private Spinner quantSpinner;
	private Button payBut, takeBut, buyBut, cancelBut;
	private ScrollView shoppingView;
	private int nOffer, nTax = 0, nQuant = 1, other;
	private String[] payMethods = {"第一銀行匯款", "板信銀行匯款", "郵政匯款", "高雄愛票網門市自取", "愛票網鳳山取票點", "台中愛票網-中港門市自取"};
	private String[] payDetails = {	"匯款後請於上班時間內 AM10:00~PM 19:00 來電07-5563890 告知客服人員帳號後5碼,或非上班時間 email 至 iticket888@gmail.com",
									"匯款之後請於上班時間內 AM10:00~PM 19:00 來電07-5563890 告知客服人員帳號後5碼,或非上班時間 email 至 iticket888@gmail.com",
									"匯款後請於上班時間內 AM10:00~PM 19:00 來電07-5563890 告知客服人員帳號後5碼,非上班時間請 email 至 iticket888@gmail.com",
									"可直接至愛票網門市付款並取票",
									"取票前請電07-7223241 先確認是否有票",
									"可直接至愛票網台中門市付款並取票"};
	private String[] takeMethods = {"普通掛號30元", "限時掛號35元", "宅急送", "愛票網門市自取", "愛票網鳳山取票點自取"};
	private String[] takeDetails = {"郵局寄送時間約2~3天(不包含例假日,實際狀況以郵局公告為準)",
									"郵局寄送時間約1~2天(不包含例假日,實際狀況以郵局公告為準)",
									"郵資100,隔天到貨(實際狀況以郵局公告為準)",
									"未註明取貨時間者，請在三天內取貨，超過三天則取消訂單,訂票且超過取票時間則取消訂單",
									"晚上五點前下單後隔天可前往取票，超過三天則票券退回愛票網門市"};
	private String[] quants = {"1", "2","3","4","5","其它"};
	public static TicketDetailFrag2 newInstance(JSONArray cont) {
		TicketDetailFrag2 f = new TicketDetailFrag2();
	    Bundle args = new Bundle();
	    args.putString("args", cont.toString());
	    f.setArguments(args);
	    return f;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		try {
			content = new JSONArray(getArguments().getString("args"));
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		View view = inflater.inflate(R.layout.fragment_ticket_detail_2, container, false);
		//view.setBackgroundColor(getResources().getColor(R.color.ListBackgroundColor));
		cancelBut = (Button) view.findViewById(R.id.shopCancelBut);
		cancelBut.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Log.i(TAG, "cancel shopping");
				shoppingView.setVisibility(View.INVISIBLE);
				getActivity().getApplicationContext();
				SharedPreferences pref = getActivity().getApplicationContext().getSharedPreferences(Util.PREF_NAME, Context.MODE_PRIVATE); 
				pref.edit().putBoolean("shopViewShown", false).commit();
			}
		});
		buyBut = (Button) view.findViewById(R.id.shopBuyBut);
		payText = (TextView) view.findViewById(R.id.shopPayText);
		payText.setText("");
		payBut = (Button) view.findViewById(R.id.shopPayBut);
		
		payBut.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
				ArrayList<Map<String, String>> dataArray = new ArrayList<Map<String,String>>();
				
				builder.setTitle("付款方式");
				dataArray.clear();
				for (int i=0, l=payMethods.length;i<l; i++) {
					Map<String, String> map = new HashMap<String, String>();
					map.put("method", ""+ payMethods[i]);
					map.put("detail", ""+ payDetails[i]);
					dataArray.add(map);
				}
				SimpleAdapter adapter = new SimpleAdapter(getActivity(), dataArray, android.R.layout.simple_list_item_2, 
						new String[] {"method", "detail"}, new int[] {android.R.id.text1, android.R.id.text2}) {
					@Override
					public View getView(int position, View convertView, ViewGroup parent) {
						// TODO Auto-generated method stub
						View view = super.getView(position, convertView, parent);
						TextView text2 = (TextView) view.findViewById(android.R.id.text2);
						text2.setTextColor(Color.RED);
						return view;
					}
				};
				
				builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						payText.setText(payMethods[which]);
					}
				});
				builder.show();
			}
		});
		
		takeText = (TextView) view.findViewById(R.id.shopTakeText);
		takeText.setText("");
		takeBut = (Button) view.findViewById(R.id.shopTakeBut);
		takeBut.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
				ArrayList<Map<String, String>> dataArray = new ArrayList<Map<String,String>>();
				
				builder.setTitle("取票方式");
				dataArray.clear();
				for (int i=0, l=takeMethods.length;i<l; i++) {
					Map<String, String> map = new HashMap<String, String>();
					map.put("method", ""+ takeMethods[i]);
					map.put("detail", ""+ takeDetails[i]);
					dataArray.add(map);
				}
				SimpleAdapter adapter = new SimpleAdapter(getActivity(), dataArray, android.R.layout.simple_list_item_2, 
						new String[] {"method", "detail"}, new int[] {android.R.id.text1, android.R.id.text2}) {
					@Override
					public View getView(int position, View convertView, ViewGroup parent) {
						// TODO Auto-generated method stub
						View view = super.getView(position, convertView, parent);
						TextView text2 = (TextView) view.findViewById(android.R.id.text2);
						text2.setTextColor(getResources().getColor(R.color.RedColor));
						return view;
					}
				};
				
				builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						takeText.setText(takeMethods[which]);
					}
				});
				builder.show();
			}
		});
		shoppingView = (ScrollView) view.findViewById(R.id.shopView);
		titleTV = (TextView) view.findViewById(R.id.shopNameText);
		priceTV = (TextView) view.findViewById(R.id.shopPrizeText);
		offerTV = (TextView) view.findViewById(R.id.shopOfferText);
		sumTV = (TextView) view.findViewById(R.id.shopSumText);
		totalTV = (TextView) view.findViewById(R.id.shopTotalText);
		
		quantSpinner = (Spinner) view.findViewById(R.id.quantSpin);
		
		//SpinnerAdapter sAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.quantity_list, android.R.id.text1);
		//ArrayAdapter<String> sAdapter = new ArrayAdapter<String>(getActivity().getApplicationContext(), R.layout.row_quant, R.array.quantity_list);
		
		ArrayAdapter<String> sAdapter = new ArrayAdapter<String>(
				getActivity().getApplicationContext(), 
				android.R.layout.simple_list_item_1,
				android.R.id.text1, 
				new String[] {"1", "2","3","4","5","其它"}) {
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				// TODO Auto-generated method stub
				View view = super.getView(position, convertView, parent);
				//view.setBackgroundColor(Color.CYAN);
				TextView tv = (TextView) view.findViewById(android.R.id.text1);
				
				tv.setTextColor(getResources().getColor(R.color.BlackColor));
				tv.setTextSize(16.0f);
				return view;
			}
			
			@Override
			public View getDropDownView(int position, View convertView,
					ViewGroup parent) {
				// TODO Auto-generated method stub
				View view = super.getDropDownView(position, convertView, parent);
				TextView tv = (TextView) view.findViewById(android.R.id.text1);
				tv.setTextColor(Color.BLACK);
				return view;
			}
		};
		quantSpinner.setAdapter(sAdapter);
		
		quantSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				// TODO Auto-generated method stub
				Log.i(TAG, "item " + arg2 + "selected");
				if(arg2 == 5) {
					quantInput();
				}
				else if(arg2 == 6) {
					nQuant = other;//arg2 + 1;
					sumTV.setText("" + ((nQuant)*nOffer ));
					totalTV.setText("" + ((nQuant)*nOffer + nTax));
				}
				else {
					nQuant = arg2 + 1;
					sumTV.setText("" + ((nQuant)*nOffer ));
					totalTV.setText("" + ((nQuant)*nOffer + nTax));
				}
				
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}

		});
		dataArray = new ArrayList<HashMap<String,String>>();
		ListView lv = (ListView) view.findViewById(R.id.ticketList);
		Log.i(TAG, "content: " + content.length());
		dataArray.clear();
		for(int i=0,l=content.length(); i < l; i++) {
			HashMap<String, String> tmp = new HashMap<String, String>();
			try {
				String diff = "N/A";
				tmp.put("price", content.getJSONObject(i).getString("price"));
				tmp.put("offer", content.getJSONObject(i).getString("offer"));
				if(!content.getJSONObject(i).getString("price").equalsIgnoreCase("null") && !content.getJSONObject(i).getString("offer").equalsIgnoreCase("null"))
					diff = "" + (content.getJSONObject(i).getInt("price") - content.getJSONObject(i).getInt("offer"));
				else {
					if (content.getJSONObject(i).getString("price").equalsIgnoreCase("null")) {
						tmp.put("price", "N/A");
					}
					if(content.getJSONObject(i).getString("offer").equalsIgnoreCase("null")) {
						tmp.put("offer", "N/A");
					}
				}
				tmp.put("title", content.getJSONObject(i).getString("title"));
				tmp.put("offiURL", content.getJSONObject(i).getString("offiURL"));
				tmp.put("imgURL",content.getJSONObject(i).getString("iconURI"));
				tmp.put("diff", ""+diff);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			dataArray.add(tmp);
		}
		SimpleAdapter adapter = new SimpleAdapter(
				getActivity(), 
				dataArray, 
				R.layout.row_ticket_list, 
				new String[]{"title", "price", "diff", "offer"}, 
				new int[]{R.id.nameText, R.id.priceText, R.id.savedText, R.id.offerText}) {
			@Override
			public View getView(final int position, View convertView,
					ViewGroup parent) {
				// TODO Auto-generated method stub
				View view = super.getView(position, convertView, parent);
				ImageView icon = (ImageView) view.findViewById(R.id.promoIcon); 
				ImageButton shopping = (ImageButton) view.findViewById(R.id.shoppingBut);
				ImageButton shareButton = (ImageButton) view.findViewById(R.id.ticketShareBut);
				final String url = dataArray.get(position).get("offiURL");
				final String title = dataArray.get(position).get("title");
				TextView offerText = (TextView) view.findViewById(R.id.offerText);
				offerText.setText(" $" + dataArray.get(position).get("offer"));
				shareButton.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND); 
					    sharingIntent.setType("text/plain");
					    String shareBody = url;
					    sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, title);
					    sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
						startActivity(Intent.createChooser(sharingIntent, "Share via"));
					}
				});
				
				if (!dataArray.get(position).get("imgURL").equals("")) {
					ImageLoader.getInstance().displayImage(dataArray.get(position).get("imgURL"), icon,Util.ticketOptions);
				}
				
				
				shopping.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						Map<String, String> map = dataArray.get(position);
						titleTV.setText(map.get("title") + "");
						nOffer = Integer.parseInt(""+map.get("offer"));
						offerTV.setText(""+nOffer);
						priceTV.setText("" + map.get("price"));
						sumTV.setText("" + nOffer);
						totalTV.setText("" + (nOffer + nTax));
						payText.setText("");
						takeText.setText("");
						quantSpinner.setSelection(0);
						
						shoppingView.setVisibility(View.VISIBLE);
						getActivity().getApplicationContext();
						SharedPreferences pref = getActivity().getApplicationContext().getSharedPreferences(Util.PREF_NAME, Context.MODE_PRIVATE); 
						pref.edit().putBoolean("shopViewShown", true).commit();
						//Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
						//startActivity(intent);
					}
				});
				return view;
			}
		};
		lv.setAdapter(adapter);
		
		buyBut.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String shoppingInfo = 	"訂單號碼: %s\n" +
										"付款方式: %s\n" +
										"取票方式: %s\n" +
										"票券名稱:\n%s\n" +
										"購買數量: %s\n" +
										"購買金額: %s";
				String orderNum = "20130303009328";
				final String confirmMsg = String.format(shoppingInfo, 
						orderNum, 
						payText.getText().toString(),
						takeText.getText().toString(),
						titleTV.getText().toString(),
						"" + (((quantSpinner.getSelectedItemPosition()+1) > 6)? nQuant:(quantSpinner.getSelectedItemPosition()+1)),
						totalTV.getText().toString());
				//String.format("%d items", nItem);
				Log.i(TAG, confirmMsg);
				AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
				builder.setTitle("確認購買");
				builder.setMessage(confirmMsg);
				builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						dialog.dismiss();
					}
				});
				builder.setPositiveButton("確定", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						shoppingView.setVisibility(View.INVISIBLE);
						getActivity().getApplicationContext();
						SharedPreferences pref = getActivity().getApplicationContext().getSharedPreferences(Util.PREF_NAME, Context.MODE_PRIVATE); 
						pref.edit().putBoolean("shopViewShown", false).commit();
						dialog.dismiss();
						
						showAlert("購買成功", confirmMsg);
					}
				});
				builder.show();
			}
		});
		
		return view;
	}

	private void quantInput() {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		final EditText input = new EditText(getActivity());
		input.setSingleLine(true);
		builder.setTitle("輸入數量");
		builder.setView(input);
		builder.setOnCancelListener(new OnCancelListener() {
			
			@Override
			public void onCancel(DialogInterface dialog) {
				// TODO Auto-generated method stub
				quantSpinner.setSelection(0);
			}
		});
		builder.setPositiveButton("確定", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				Log.i(TAG, "input: " + input.getText());
				try{
					int tmp = Integer.parseInt(input.getText()+"");
					if (tmp > 0) {
						nQuant = tmp;
						sumTV.setText("" + ((nQuant) * nOffer));
						totalTV.setText("" + ((nQuant) * nOffer + nTax));
						other = nQuant;
						ArrayAdapter<String> sAdapter = new ArrayAdapter<String>(
								getActivity().getApplicationContext(), 
								android.R.layout.simple_list_item_1,
								android.R.id.text1, 
								new String[] {"1", "2","3","4","5","其它","其它 (" + nQuant + ")"}) {
							@Override
							public View getView(int position, View convertView, ViewGroup parent) {
								// TODO Auto-generated method stub
								View view = super.getView(position, convertView, parent);
								//view.setBackgroundColor(Color.CYAN);
								TextView tv = (TextView) view.findViewById(android.R.id.text1);
								
								tv.setTextColor(getResources().getColor(R.color.BlackColor));
								tv.setTextSize(16.0f);
								return view;
							}
							
							@Override
							public View getDropDownView(int position, View convertView,
									ViewGroup parent) {
								// TODO Auto-generated method stub
								View view = super.getDropDownView(position, convertView, parent);
								TextView tv = (TextView) view.findViewById(android.R.id.text1);
								tv.setTextColor(Color.BLACK);
								return view;
							}
						};
						quantSpinner.setAdapter(sAdapter);
						quantSpinner.setSelection(6);
						dialog.dismiss();
					}
					else {
						Toast.makeText(getActivity(), "數量需大於0", Toast.LENGTH_LONG).show();
					}
				}catch (Exception e) {
					Log.i(TAG, "ex: " + e.getLocalizedMessage());
					input.setText("");
					Toast.makeText(getActivity(), "數量需為正整數", Toast.LENGTH_LONG).show();
				}
			}
		});
		builder.show();
	}
	
	private void showAlert(String title, String message) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle(title);
		builder.setMessage(message);
		builder.show();
	}
}
