/*
 * 官网地站:http://www.ShareSDK.cn
 * 技术支持QQ: 4006852216
 * 官方微信:ShareSDK   （如果发布新版本的话，我们将会第一时间通过微信将版本更新内容推送给您。如果使用过程中有任何问题，也可以通过微信与我们取得联系，我们将会在24小时内给予回复）
 *
 * Copyright (c) 2013年 ShareSDK.cn. All rights reserved.
 */

package com.taomake.teabuddy.wxapi;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.taomake.teabuddy.R;
import com.taomake.teabuddy.activity.LoginByphoneActivity_;
import com.taomake.teabuddy.activity.MainActivity_;
import com.taomake.teabuddy.activity.SecondQrCode;
import com.taomake.teabuddy.base.MainApp;
import com.taomake.teabuddy.component.FoxProgressbarInterface;
import com.taomake.teabuddy.network.ProtocolUtil;
import com.taomake.teabuddy.network.RowMessageHandler;
import com.taomake.teabuddy.object.BindDeviceCodeJson;
import com.taomake.teabuddy.util.Constant;
import com.taomake.teabuddy.util.ImageLoaderUtil;
import com.taomake.teabuddy.util.Util;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

import cz.msebera.android.httpclient.Header;

public class WXEntryActivity extends AppCompatActivity implements IWXAPIEventHandler {
	private Context context = WXEntryActivity.this;
	private ImageLoader imageLoader;
	private DisplayImageOptions options;
	private void handleIntent(Intent paramIntent)
	{
		MainApp mainApp=(MainApp)getApplicationContext();

		mainApp.api.handleIntent(paramIntent, this);
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.flash_activity);
		imageLoader = ImageLoader.getInstance();
		options = ImageLoaderUtil.getAvatarOptionsInstance();
		handleIntent(getIntent());
	}

	@Override
	protected void onNewIntent(Intent intent) {
// TODO Auto-generated method stub
		super.onNewIntent(intent);
		setIntent(intent);
		handleIntent(intent);
	}
	@Override
	public void onReq(BaseReq arg0) {
// TODO Auto-generated method stub
		finish();
	}

	public void onResp(BaseResp resp) {
		int errorCode = resp.errCode;
		switch (errorCode) {
			case BaseResp.ErrCode.ERR_OK:
				//用户同意
				final String code = ((SendAuth.Resp) resp).code;
				Log.d("wx code:", code);

				new Thread(new Runnable() {
					@Override
					public void run() {
						String	nickname=null;
						String	headimgurl=null;
						String result = getAccessToken(code);
						String	city=null;
						String	province=null;

						JSONObject object = null;
						try {
							object = new JSONObject(result);

							String	accessToken = object.getString("access_token");
							String	openID = object.getString("openid");
							String	refreshToken = object.getString("refresh_token");
							Long	expires_in = object.getLong("expires_in");
							unionid = object.getString("unionid");
							String result2=	getUserInfo(accessToken, openID);

							JSONObject object2 = new JSONObject(result2);
							nickname = object2.getString("nickname");
							headimgurl = object2.getString("headimgurl");
							city = object2.getString("city");

							province = object2.getString("province");

						} catch (JSONException e) {
							e.printStackTrace();
						}

						Log.d("wx unionid:", unionid);

						SharedPreferences.Editor editor = getSharedPreferences("dataUNIION",MODE_PRIVATE).edit();
						editor.putString("unionid",unionid);
						editor.putString("nickname",nickname);

						editor.putString("headimgurl",headimgurl);

						editor.commit();



						Message msg=new Message();
						Bundle data = new Bundle();
						data.putString("unionid", unionid);
						data.putString("nickname", nickname);

						data.putString("headimgurl", headimgurl);
						data.putString("city", city);

						data.putString("province", province);
						msg.setData(data);
						handler.sendMessage(msg);
					}
				}).start();

				break;
			case BaseResp.ErrCode.ERR_AUTH_DENIED:
				//用户拒绝
				finish();
				break;
			case BaseResp.ErrCode.ERR_USER_CANCEL:
				//用户取消
				finish();
				break;
			default:
				break;
		}
		Toast.makeText(this, resp.errStr, Toast.LENGTH_LONG);
//		ToastUtil.showMessageLong(this, resp.errStr);
	}
	String unionid=null;

	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			Bundle data = msg.getData();
			String val = data.getString("unionid");
			final	String nickname = data.getString("nickname");

			String headimgurl = data.getString("headimgurl");
			final	String city = data.getString("city");
			final String province = data.getString("province");


			unionid=val;
			Log.i("unionid", "请求结果为-->" + val);

			//需要查询当前的账号是否已经绑定设备或者手机号----联网判断
			imageLoader.loadImage(headimgurl, new ImageLoadingListener() {
				@Override
				public void onLoadingStarted(String imageUri, View view) {

				}

				@Override
				public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

				}

				@Override
				public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
					byte[]	avarByte = Util.getBitmapByte(loadedImage);

					sendUserForMsg(nickname, avarByte,city,province);
				}

				@Override
				public void onLoadingCancelled(String imageUri, View view) {

				}
			});

			// TODO
			// UI界面的更新等相关操作


//			startActivityForResult(intent, SCANNIN_GREQUEST_CODE);

		}
	};


	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
			case SCANNIN_GREQUEST_CODE:
				if(resultCode == RESULT_OK){
					Bundle bundle = data.getExtras();
					String ticket=bundle.getString("result");
					Log.e("Recv QRcode result",ticket);

					//跳到手机号页面
					Intent intent = new Intent(WXEntryActivity.this, LoginByphoneActivity_.class);

					intent.putExtra("ticket",ticket);
					intent.putExtra("unionid",unionid);

					context.startActivity(intent);

					finish();
//					mTextView.setText(bundle.getString("result"));
//					mImageView.setImageBitmap((Bitmap) data.getParcelableExtra("bitmap"));
				}
				break;
		}
	}

	FoxProgressbarInterface foxProgressbarInterface;

	public void getMessageBindDeviceForMsg(String unionid) {
//        foxProgressbarInterface = new FoxProgressbarInterface();
//        foxProgressbarInterface.startProgressBar(this, "加载中...");
		ProtocolUtil.getBindDeviceMsg(this, new GetBindDeviceHandler(), unionid);


	}


	private class GetBindDeviceHandler extends RowMessageHandler {
		@Override
		protected void handleResp(String resp) {
			getBindDeviceHandler(resp);
		}
	}


	public void getBindDeviceHandler(String resp) {
		foxProgressbarInterface.stopProgressBar();
		if (resp != null && !resp.equals("")) {

			//解析返回json 数据
			Map<String, Object> orderMap = new Gson().fromJson(resp,
					new TypeToken<Map<String, Object>>() {
					}.getType());
			// 将 infomation 转成需要的 order信息

			Double return_code_int = (Double) orderMap.get("rcode");

			if(return_code_int==1) {

				BindDeviceCodeJson baseJson = new Gson().fromJson(resp, BindDeviceCodeJson.class);
				if ((baseJson.rcode + "").equals(Constant.RES_SUCCESS)) {

					if (baseJson.obj == null || (baseJson.obj.mac == null || baseJson.obj.mac.equals(""))) {


						checkCameraPersimion();

					} else {


						Intent intent = new Intent();
						intent.setClass(WXEntryActivity.this, MainActivity_.class);

						intent.putExtra("deviceid", baseJson.obj.deviceid);
						intent.putExtra("mac", baseJson.obj.mac);
						intent.putExtra("unionid", unionid);

						startActivity(intent);
						finish();

					}


				}
			}else{
				checkCameraPersimion();
			}

		}
	}

	public void  toSencodQrcode(){
		Intent intent = new Intent();
		intent.setClass(WXEntryActivity.this, SecondQrCode.class);

		intent.putExtra("unionid", unionid);
		startActivity(intent);
		finish();
	}
	int MY_PERMISSIONS_REQUEST_CALL_PHONE2=2002;

	public void checkCameraPersimion(){
		if (ContextCompat.checkSelfPermission(this,
				Manifest.permission.CAMERA)
				!= PackageManager.PERMISSION_GRANTED)
		{
			ActivityCompat.requestPermissions(this,
					new String[]{Manifest.permission.CAMERA},
					MY_PERMISSIONS_REQUEST_CALL_PHONE2);
			//权限还没有授予，需要在这里写申请权限的代码
		}else {
			//权限已经被授予，在这里直接写要执行的相应方法即可
			toSencodQrcode();
		}

	}

	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
	{




		if (requestCode == MY_PERMISSIONS_REQUEST_CALL_PHONE2)
		{
			if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
			{
				toSencodQrcode();
			} else
			{
				// Permission Denied
				Toast.makeText(WXEntryActivity.this, "Permission Denied", Toast.LENGTH_SHORT).show();
			}
		}
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
	}



	final int SCANNIN_GREQUEST_CODE=1001;
	public static String APPID="wxeb1b89052d8f8794";
	public static String SECRET="210222646dbd203d2bdd07b06bcde466";

	private String  getAccessToken(String code) {
		String	unionid;
		String urlStr = "https://api.weixin.qq.com/sns/oauth2/access_token?appid="+APPID+"&secret="+SECRET+"&code="+code+"&grant_type=authorization_code";
		String result = null;

		HttpURLConnection urlConnection = null;

		try {





			Log.d("输入参数", urlStr);



			// Thread.sleep(5000);
			URL url = new URL(urlStr);
			urlConnection = (HttpURLConnection) url.openConnection();
			urlConnection.setConnectTimeout(20000);
			urlConnection.setReadTimeout(20000);

			urlConnection.setRequestMethod("GET");
			urlConnection.setRequestProperty("Content-Type",
					"text/plain; charset=utf-8"); // "application/json"


			urlConnection.setDoOutput(false);
			urlConnection.setUseCaches(false);


			urlConnection.connect();
			int code1 = urlConnection.getResponseCode();
			Log.d("STATUS code", String.format("%d", code1));
			// String string = urlConnection.getResponseMessage();
			System.out.println(urlConnection.toString());
			InputStream in = new BufferedInputStream(
					urlConnection.getInputStream());
			result = convertStreamToString(in);
			Log.d("INFO", "**RESULT** " + result);




			return result;
		} catch (Exception e) {
			e.printStackTrace();
			Log.e("tag", "exception >>>>>>>" + e.getLocalizedMessage());
		} finally {
			if (urlConnection != null)
				urlConnection.disconnect();
		}


		return result;
	}





	private String getUserInfo(String accsstoken,String openid) {
		String	unionid;
		String urlStr = "https://api.weixin.qq.com/sns/userinfo?access_token="+accsstoken+"&openid="+openid;
		String result = null;

		HttpURLConnection urlConnection = null;

		try {





			Log.d("输入参数", urlStr);



			// Thread.sleep(5000);
			URL url = new URL(urlStr);
			urlConnection = (HttpURLConnection) url.openConnection();
			urlConnection.setConnectTimeout(20000);
			urlConnection.setReadTimeout(20000);

			urlConnection.setRequestMethod("GET");
			urlConnection.setRequestProperty("Content-Type",
					"text/plain; charset=utf-8"); // "application/json"


			urlConnection.setDoOutput(false);
			urlConnection.setUseCaches(false);


			urlConnection.connect();
			int code1 = urlConnection.getResponseCode();
			Log.d("STATUS code", String.format("%d", code1));
			// String string = urlConnection.getResponseMessage();
			System.out.println(urlConnection.toString());
			InputStream in = new BufferedInputStream(
					urlConnection.getInputStream());
			result = convertStreamToString(in);
			Log.d("INFO", "**RESULT** " + result);


//			JSONObject object = new JSONObject(result);
//			String	nickname = object.getString("nickname");
//			String	headimgurl = object.getString("headimgurl");
//			String	city = object.getString("city");


			return result;
		} catch (Exception e) {
			e.printStackTrace();
			Log.e("tag", "exception >>>>>>>" + e.getLocalizedMessage());
		} finally {
			if (urlConnection != null)
				urlConnection.disconnect();
		}


		return result;
	}

	private static String convertStreamToString(InputStream is) {

		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		StringBuilder sb = new StringBuilder();

		String line = null;
		try {
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return sb.toString();
	}


	public void sendUserForMsg(String nickname,byte[] headurl,String city,String province) {
		foxProgressbarInterface = new FoxProgressbarInterface();
		foxProgressbarInterface.startProgressBar(this, "加载中...");


//		SharedPreferences sp = getSharedPreferences("dataUNIION", MODE_PRIVATE);
//		String nickname = sp.getString("nickname", null);
//		String headurl = sp.getString("headimgurl", null);

		if (TextUtils.isEmpty(nickname) || (headurl==null||headurl.length==0)) {

			Util.Toast(context, "请重新微信登录获取用户信息");
			return;
		}


		ProtocolUtil.sendUserinfo(this, new SendUserForMsgHandler(), headurl, unionid, nickname,city,province);


	}

	private class SendUserForMsgHandler extends AsyncHttpResponseHandler {
		@Override
		public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
			foxProgressbarInterface.stopProgressBar();
			String bodyResp = new String(responseBody);
			Util.Toast(WXEntryActivity.this, "上传成功");
			Log.i("bodyResp", bodyResp);
			getMessageBindDeviceForMsg(unionid);
		}

		@Override
		public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
			foxProgressbarInterface.stopProgressBar();
			String bodyResp=null;
			if (responseBody != null) {

				bodyResp = new String(responseBody);
				Log.i("bodyResp", bodyResp);

			}
			Util.Toast(WXEntryActivity.this, "新建用户失败");

			finish();
		}
//        @Override
//        protected void handleResp(String resp) {
//            updateBcNineRecordsHandler(resp);
//        }
	}




//	private String getAccessToken(String code) {
//		String url = "https://api.weixin.qq.com/sns/oauth2/access_token?appid="+APPID+"&secret="+SECRET+"&code="+code+"&grant_type=authorization_code";
//		URI uri = URI.create(url);
//		HttpClient client = new DefaultHttpClient();
//		HttpGet get = new HttpGet(uri);
//
//		HttpResponse response;
//		try {
//			response = client.execute(get);
//			if (response.getStatusLine().getStatusCode() == 200) {
//				HttpEntity entity = response.getEntity();
//
//				BufferedReader reader = new BufferedReader(new InputStreamReader(entity.getContent(), "UTF-8"));
//				StringBuilder sb = new StringBuilder();
//
//				for (String temp = reader.readLine(); temp != null; temp = reader.readLine()) {
//					sb.append(temp);
//				}
//
//				JSONObject object = new JSONObject(sb.toString().trim());
//			String	accessToken = object.getString("access_token");
//				String	openID = object.getString("openid");
//				String	refreshToken = object.getString("refresh_token");
//				Long	expires_in = object.getLong("expires_in");
//				String	unionid = object.getString("unionid");
//
//
//				return unionid;
//			}
//		} catch (ClientProtocolException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IllegalStateException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (JSONException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//
//		return null;
//	}


}
