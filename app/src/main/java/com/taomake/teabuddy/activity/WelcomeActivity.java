package com.taomake.teabuddy.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.taomake.teabuddy.R;
import com.taomake.teabuddy.base.BaseActivity;
import com.taomake.teabuddy.base.MainApp;
import com.taomake.teabuddy.component.CustomVideoView;
import com.taomake.teabuddy.component.FoxProgressbarInterface;
import com.taomake.teabuddy.network.ProtocolUtil;
import com.taomake.teabuddy.network.RowMessageHandler;
import com.taomake.teabuddy.object.BindDeviceCodeJson;
import com.taomake.teabuddy.object.DeviceCubImgJson;
import com.taomake.teabuddy.prefs.ConfigPref_;
import com.taomake.teabuddy.util.Constant;
import com.taomake.teabuddy.util.FoxHandler;
import com.taomake.teabuddy.util.Util;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.sharedpreferences.Pref;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * Created by zhang on 2015/8/11.
 */
@EActivity(R.layout.activity_welcome)
public class WelcomeActivity extends BaseActivity {

//    private AdvertiseInfo mAdvertiseInfo = null;
//    private String adLogo;

    private boolean isStartActivity = false;

    @Pref
    ConfigPref_ configPref;


//
//    @Pref
//    UserPref_ userPref;

    RelativeLayout welcome_rel_id, login_rel_id;
 boolean uselogin=true;
    @AfterViews
    void init() {

        welcome_rel_id = (RelativeLayout) findViewById(R.id.welcome_rel_id);
        login_rel_id = (RelativeLayout) findViewById(R.id.login_rel_id);
        if (configPref.showWelcome4().get() ||
                (configPref.userDeviceMac().get() == null || configPref.userDeviceMac().get().equals("")) ||
                (configPref.userUnion().get() == null || configPref.userUnion().get().equals(""))) {

            welcome_rel_id.setVisibility(View.GONE);
            login_rel_id.setVisibility(View.VISIBLE);
            configPref.showWelcome4().put(false);
            initVideo();

            if (Build.VERSION.SDK_INT >= 23) {
                uselogin=true;
                permissionAll();
            }
        } else {

            uselogin=false;
            welcome_rel_id.setVisibility(View.VISIBLE);
            login_rel_id.setVisibility(View.GONE);
//            permissionBle();
            if (Build.VERSION.SDK_INT >= 23){
                permissionAll();
            }else {
                postStart();
            }
        }
    }

//    private void getLocation() {
//        ProtocolUtil.getAdvertiseDataCity(WelcomeActivity.this, new AdvertiseMsgHandler(), userPref.accessToken().get(), "startup",
//                "", "");
//    }

    private synchronized void startActivity() {
        if (isStartActivity) return;
        isStartActivity = true;


        if (configPref.userUnion().get() != null && !configPref.userUnion().get().equals("")) {
            getDetailReameUrl();
            /**
             * 1.有则进入泡茶页面
             * 2.无则跳转到 设备激活页面
             */

        } else {
//            Util.startActivityNewTask(this, LoginActivity_.class);
//            finish();

            welcome_rel_id.setVisibility(View.GONE);
            login_rel_id.setVisibility(View.VISIBLE);
            initVideo();
        }


    }

    @Override
    protected void onResume() {
        super.onResume();
        if (foxProgressbarInterface != null) {
            foxProgressbarInterface.stopProgressBar();
        }

    }

    public void postStart() {
        if(!uselogin) {
            new FoxHandler(this).postDelayed(new Runnable() {

                @Override
                public void run() {
                    startActivity();
                }
            }, 2000);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[]
            grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_PERMISSIONS_REQUEST_ACCESS_ALL) {
            postStart();
        }


    }


//    int RECORD_AUDIO_ID = 3004;
//
//    public void permissionRecord() {
//        if (ContextCompat.checkSelfPermission(this,
//                Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
////请求权限
//            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO},
//                    RECORD_AUDIO_ID);
////判断是否需要 向用户解释，为什么要申请该权限
//
//        } else {
//            postStart();
//        }
//
//    }
//
//    int MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION = 3003;
//
//    public void permissionBle() {
//        if (ContextCompat.checkSelfPermission(this,
//                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
////请求权限
//            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
//                    MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION);
////判断是否需要 向用户解释，为什么要申请该权限
//            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
//                    Manifest.permission.READ_CONTACTS)) {
//                Toast.makeText(this, "shouldShowRequestPermissionRationale", Toast.LENGTH_SHORT).show();
//            }
//        } else {
//            permissionRecord();
//        }
//
//    }


    int  MY_PERMISSIONS_REQUEST_ACCESS_ALL=5005;
    public void permissionAll() {

        List<String> plist=new ArrayList<String>();
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            plist.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        }

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            plist.add(Manifest.permission.RECORD_AUDIO);
        }

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            plist.add(Manifest.permission.READ_PHONE_STATE);
        }
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            plist.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            plist.add(Manifest.permission.READ_CONTACTS);
        }
        if(plist.size()>0) {
            String[] toBeStored = plist.toArray(new String[plist.size()]);

//请求权限
            ActivityCompat.requestPermissions(this, toBeStored,
                    MY_PERMISSIONS_REQUEST_ACCESS_ALL);
//判断是否需要 向用户解释，为什么要申请该权限
        }else{
            postStart();

        }


    }


    String WX_APP_ID = "wxeb1b89052d8f8794";


    void initVideo() {


        final CustomVideoView videoview = (CustomVideoView) findViewById(R.id.videoview);
        //设置播放加载路径
        videoview.setVideoURI(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.main_video));
        //播放
        videoview.start();
        //循环播放
        videoview.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                videoview.start();
            }
        });

        SharedPreferences pref = getSharedPreferences("dataUNIION", MODE_PRIVATE);
        final String unionidPref = pref.getString("unionid", "");

        final String unionidConf = configPref.userUnion().get();


        findViewById(R.id.wx_login_id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                if ((unionidConf != null && !unionidConf.equals("")) || (unionidPref != null && !unionidPref.equals(""))) {


//                    Intent intent = new Intent();
//                    intent.setClass(WelcomeActivity.this, SecondQrCode.class);
////			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                    Bundle bundle = new Bundle();
//                    String val = null;
//                    if ((unionidConf != null && !unionidConf.equals(""))) {
//                        val = configPref.userUnion().get();
//                    }
//                    if ((unionidPref != null && !unionidPref.equals(""))) {
//                        val = unionidPref;
//                        configPref.userUnion().put(val);
//                    }
//
//
//                    bundle.putString("unionid", val);
//
//                    startActivity(intent);
//                    finish();
//
//                } else {


                foxProgressbarInterface = new FoxProgressbarInterface();
                foxProgressbarInterface.startProgressBar(WelcomeActivity.this, "微信跳转中...");

                MainApp mainApp = (MainApp) getApplicationContext();
                IWXAPI api = mainApp.api;

                if (api == null) {
                    api = WXAPIFactory.createWXAPI(WelcomeActivity.this, WX_APP_ID, false);
                    api.registerApp(WX_APP_ID);
                }
                if (!api.isWXAppInstalled()) {
                    //提醒用户没有按照微信
                    Toast.makeText(WelcomeActivity.this, "没有安装微信,请先安装微信!", Toast.LENGTH_SHORT).show();
                    return;
                }
                SendAuth.Req req = new SendAuth.Req();
                req.scope = "snsapi_userinfo";
                req.state = "wechat_sdk_demo_test";
                api.sendReq(req);
//                }

//                Util.startActivity(LoginActivity.this,MainActivity_.class);
//                finish();
            }
        });

    }

    @Override
    protected void onPause() {
        super.onPause();


    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // 隐藏状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

//        MobclickAgent.openActivityDurationTrack(false);
        //******************友盟测试*****************************
//        MobclickAgent.setDebugMode(true);
//        Log.i("UMENG_INFO", "device=" + getDeviceInfo(this));
        //******************友盟测试*****************************
        try {
            android.net.wifi.WifiManager wifi = (android.net.wifi.WifiManager) getSystemService(Context.WIFI_SERVICE);

//            WxUtil.wxMac = wifi.getConnectionInfo().getMacAddress().replace(":", "");
        } catch (Exception e) {
        }
//        PushManager.getInstance().initialize(this.getApplicationContext());
//        Intent intent = getIntent();
//        TencentUtils.QQ_OPENID = intent.getStringExtra("openid");
//        TencentUtils.QQ_TOKEN = intent.getStringExtra("accesstoken");
    }

    @Override
    protected void initActivityName() {
        activityName = WelcomeActivity.class.getName();
    }

    @Override
    protected void setActivityBg() {

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

    public boolean checklogin(String resp) {
        Map<String, Object> orderMap = new Gson().fromJson(resp,
                new TypeToken<Map<String, Object>>() {
                }.getType());
        Double return_code_int = (Double) orderMap.get("rcode");

        if (return_code_int == 0) {
            welcome_rel_id.setVisibility(View.GONE);
            login_rel_id.setVisibility(View.VISIBLE);
            initVideo();
            return false;

        }
        return true;
    }

    public void getBindDeviceHandler(String resp) {
//        foxProgressbarInterface.stopProgressBar();
        if (resp != null && !resp.equals("")) {

            //解析返回json 数据
            if (!checklogin(resp)) return;

            BindDeviceCodeJson baseJson = new Gson().fromJson(resp, BindDeviceCodeJson.class);
            if ((baseJson.rcode + "").equals(Constant.RES_SUCCESS)) {
                setRegisterId();

                if (baseJson.obj == null || (baseJson.obj.mac == null || baseJson.obj.mac.equals(""))) {

                    Intent intent = new Intent();
                    intent.setClass(WelcomeActivity.this, SecondQrCode.class);
                    Bundle bundle = new Bundle();


                    bundle.putString("unionid", configPref.userUnion().get());

                    startActivity(intent);
                    finish();


                } else {
                    configPref.userDeviceId().put(baseJson.obj.deviceid);
                    configPref.userDeviceMac().put(baseJson.obj.mac);
                    Util.startActivityNewTask(this, MainActivity_.class);
                    finish();

                }


            }

        }
    }


    public void getDetailReameUrl() {

//        foxProgressbarInterface = new FoxProgressbarInterface();
//        foxProgressbarInterface.startProgressBar(this, "加载中...");
        ProtocolUtil.getReadmeUrl(this, new GetDetailHandler(), "purchaseinstruction", configPref.userDeviceId().get());//devno 空表示所有


    }


    private class GetDetailHandler extends RowMessageHandler {
        @Override
        protected void handleResp(String resp) {
            getDetailHandler(resp);
        }
    }

    public void getDetailHandler(String resp) {
//        foxProgressbarInterface.stopProgressBar();
        if (resp != null && !resp.equals("")) {

            if (!checklogin(resp)) return;


            DeviceCubImgJson dbRecordsJson = new Gson().fromJson(resp, DeviceCubImgJson.class);
            if ((dbRecordsJson.rcode + "").equals(Constant.RES_SUCCESS)) {
                final String url = dbRecordsJson.obj;
                configPref.payUrl().put(url);
                getMessageBindDeviceForMsg(configPref.userUnion().get());

            }
        }

    }

    public void setRegisterId() {
        if (configPref.getuiClientId().get() == null || configPref.getuiClientId().get().equals("")) {

            MainApp mainapp = (MainApp) getApplicationContext();
            String regid = mainapp.registrationId;

            configPref.getuiClientId().put(regid);

//        foxProgressbarInterface = new FoxProgressbarInterface();
//        foxProgressbarInterface.startProgressBar(this, "加载中...");
            ProtocolUtil.registerJpushId(this, new SetRegisterIdHandler(), configPref.userUnion().get(), regid);//devno 空表示所有
        }

    }


    private class SetRegisterIdHandler extends RowMessageHandler {
        @Override
        protected void handleResp(String resp) {
            setRegisterIdHandler(resp);
        }
    }

    public void setRegisterIdHandler(String resp) {


    }


}
