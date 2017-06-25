package com.taomake.teabuddy.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;

import com.taomake.teabuddy.activity.WebViewActivity_;
import com.taomake.teabuddy.base.MainApp;
import com.taomake.teabuddy.util.MyStringUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

import cn.jpush.android.api.JPushInterface;
import quinticble.QuinticBleAPISdkBase;
import quinticble.QuinticCallbackTea;
import quinticble.QuinticDeviceFactoryTea;
import quinticble.QuinticDeviceTea;
import quinticble.QuinticException;

/**
 * 自定义接收器
 * 
 * 如果不定义这个 Receiver，则：
 * 1) 默认用户会打开主界面
 * 2) 接收不到自定义消息
 */
public class MyReceiver extends BroadcastReceiver {
	private static final String TAG = "JPush";

	private Context contextm;
	@Override
	public void onReceive(Context context, Intent intent) {

	this.contextm=context;
        Bundle bundle = intent.getExtras();
		if(TextUtils.isEmpty(blindDeviceId)) {//需要设置


			SharedPreferences preferences=context.getSharedPreferences("dataUNIION", Context.MODE_PRIVATE);
			blindDeviceId=preferences.getString("mac", "");


		}


		Log.d(TAG, "[MyReceiver] onReceive - " + intent.getAction() + ", extras: " + printBundle(bundle));
		
        if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {
            String regId = bundle.getString(JPushInterface.EXTRA_REGISTRATION_ID);
            Log.d(TAG, "[MyReceiver] 接收Registration Id : " + regId);
            //send the Registration Id to your server...
                        
        } else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {
        	Log.d(TAG, "[MyReceiver] 接收到推送下来的自定义消息: " + bundle.getString(JPushInterface.EXTRA_MESSAGE));
        	processCustomMessage(context, bundle);
			if(!TextUtils.isEmpty(blindDeviceId)){
				connectFindDevice();
			}
        
        } else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {
            Log.d(TAG, "[MyReceiver] 接收到推送下来的通知");
            int notifactionId = bundle.getInt(JPushInterface.EXTRA_NOTIFICATION_ID);
            Log.d(TAG, "[MyReceiver] 接收到推送下来的通知的ID: " + notifactionId);
			if(!TextUtils.isEmpty(blindDeviceId)){
				connectFindDevice();
			}
        	
        } else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {
            Log.d(TAG, "[MyReceiver] 用户点击打开了通知");
            
        	//打开自定义的Activity
//        	Intent i = new Intent(context, TestActivity.class);
//			i.putExtras(bundle);
//			//i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//			i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP );
//			context.startActivity(i);

			String title = bundle.getString(JPushInterface.EXTRA_NOTIFICATION_TITLE);
			String content = bundle.getString(JPushInterface.EXTRA_ALERT);
			Log.d("msg recevie title:",title);
			Log.d("msg recevie content:",content);

			//跳转到网页
			Intent intent0 = new Intent(context, WebViewActivity_.class);
			intent0.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP );

			intent0.putExtra("url", "http://www.baidu.com");//测试数据
			intent0.putExtra("title", title);
			context.startActivity(intent0);
        	
        } else if (JPushInterface.ACTION_RICHPUSH_CALLBACK.equals(intent.getAction())) {
            Log.d(TAG, "[MyReceiver] 用户收到到RICH PUSH CALLBACK: " + bundle.getString(JPushInterface.EXTRA_EXTRA));
            //在这里根据 JPushInterface.EXTRA_EXTRA 的内容处理代码，比如打开新的Activity， 打开一个网页等..
        	
        } else if(JPushInterface.ACTION_CONNECTION_CHANGE.equals(intent.getAction())) {
        	boolean connected = intent.getBooleanExtra(JPushInterface.EXTRA_CONNECTION_CHANGE, false);
        	Log.w(TAG, "[MyReceiver]" + intent.getAction() +" connected state change to "+connected);
        } else {
        	Log.d(TAG, "[MyReceiver] Unhandled intent - " + intent.getAction());
        }
	}

	// 打印所有的 intent extra 数据
	private static String printBundle(Bundle bundle) {
		StringBuilder sb = new StringBuilder();
		for (String key : bundle.keySet()) {
			if (key.equals(JPushInterface.EXTRA_NOTIFICATION_ID)) {
				sb.append("\nkey:" + key + ", value:" + bundle.getInt(key));
			}else if(key.equals(JPushInterface.EXTRA_CONNECTION_CHANGE)){
				sb.append("\nkey:" + key + ", value:" + bundle.getBoolean(key));
			} else if (key.equals(JPushInterface.EXTRA_EXTRA)) {
				if (TextUtils.isEmpty(bundle.getString(JPushInterface.EXTRA_EXTRA))) {
					Log.i(TAG, "This message has no Extra data");
					continue;
				}

				try {
					JSONObject json = new JSONObject(bundle.getString(JPushInterface.EXTRA_EXTRA));
					Iterator<String> it =  json.keys();

					while (it.hasNext()) {
						String myKey = it.next().toString();
						sb.append("\nkey:" + key + ", value: [" +
								myKey + " - " +json.optString(myKey) + "]");
					}
				} catch (JSONException e) {
					Log.e(TAG, "Get message extra JSON error!");
				}

			} else {
				sb.append("\nkey:" + key + ", value:" + bundle.getString(key));
			}
		}
		return sb.toString();
	}
	
	//send msg to DeviceMainActivity
	private void processCustomMessage(Context context, Bundle bundle) {
//		if (DeviceMainActivity.isForeground) {
//			String message = bundle.getString(JPushInterface.EXTRA_MESSAGE);
//			String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);
//			Intent msgIntent = new Intent(DeviceMainActivity.MESSAGE_RECEIVED_ACTION);
//			msgIntent.putExtra(DeviceMainActivity.KEY_MESSAGE, message);
//			if (!ExampleUtil.isEmpty(extras)) {
//				try {
//					JSONObject extraJson = new JSONObject(extras);
//					if (extraJson.length() > 0) {
//						msgIntent.putExtra(DeviceMainActivity.KEY_EXTRAS, extras);
//					}
//				} catch (JSONException e) {
//
//				}
//
//			}
//			context.sendBroadcast(msgIntent);
//		}
	}





	//************************蓝牙操作*********************************

	/**
	 * 功能：查找设备
	 */
	private String blindDeviceId;
	private QuinticDeviceTea resultDeviceAll;
	private Integer countError = 0;

	public void connectFindDevice() {

		MainApp mainApp=(MainApp)contextm.getApplicationContext();
		if(mainApp.boolApplyRecord||mainApp.boolupdateSuccess==1){
			Log.e("MyReceiver","正在录音或者升级");
			return;
		}


//        foxProgressbarInterface = new FoxProgressbarInterface();
//
//        foxProgressbarInterface.startProgressBar(getActivity(), "蓝牙读取中...");
		blindDeviceId = MyStringUtils.macStringToUpper(blindDeviceId);
		Log.e("blindDeviceId:", blindDeviceId);
		if (MyStringUtils.isNotNullAndEmpty(QuinticBleAPISdkBase.resultDevice)) {
			resultDeviceAll = QuinticBleAPISdkBase.resultDevice;
			// ************处理动作
			setHaveMsgNoRead();
		} else {
			final Context context = contextm;
			QuinticDeviceFactoryTea quinticDeviceFactory = QuinticBleAPISdkBase
					.getInstanceFactory(context);

			quinticDeviceFactory.findDevice(blindDeviceId,
					new QuinticCallbackTea<QuinticDeviceTea>() {

						@Override
						public void onComplete(final QuinticDeviceTea resultDevice) {
							super.onComplete(resultDevice);
							new Handler(context.getMainLooper())
									.post(new Runnable() {
										@Override
										public void run() {
											resultDeviceAll = resultDevice;
											QuinticBleAPISdkBase.resultDevice = resultDeviceAll;
											// ************处理动作
											setHaveMsgNoRead();

										}
									});
						}

						@Override
						public void onError(final QuinticException ex) {
							new Handler(context.getMainLooper())
									.post(new Runnable() {
										@Override
										public void run() {
											if (countError < 1) {
												Log.d("connectFindDevice ex",
														ex.getCode()
																+ ""
																+ ex.getMessage());
												connectFindDevice();
												countError++;
											} else {
//                                            unconnectUi();
												// *****************连接失败
//                                                Util.Toast(context,
//                                                        "");
											}
										}
									});
						}
					});
		}
	}

	public void connectSendCodeFailUi(String failMsg){
		Log.e("getLogHistory", failMsg);

	}


	public void setHaveMsgNoRead() {
		if (resultDeviceAll == null) return;
		String code = "EB06";
		final String failMsg = "设置消息未读失败";

		resultDeviceAll.sendCommonCode(code, new QuinticCallbackTea<String>() {
			@Override
			public void onError(QuinticException ex) {
				super.onError(ex);
				connectSendCodeFailUi(failMsg);

			}

			@Override
			public void onComplete(final String result) {
				super.onComplete(result);
				if (result == null) {
					connectSendCodeFailUi(failMsg);

					return;
				}
				new Handler(contextm.getMainLooper())
						.post(new Runnable() {
							@Override
							public void run() {
//BACK 0A 10 01
								String trimResult = result.replace(" ", "");

								if (trimResult.contains("eb01")) {


								} else {
									connectSendCodeFailUi(failMsg);
								}

							}
						});

			}
		});
	}
}
