package com.taomake.teabuddy.activity;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.taomake.teabuddy.R;
import com.taomake.teabuddy.base.MainApp;
import com.taomake.teabuddy.component.CustomVideoView;
import com.taomake.teabuddy.prefs.ConfigPref_;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.sharedpreferences.Pref;

/**
 * Created by zhang on 2015/8/7.
 */
@EActivity(R.layout.login_view)
public class LoginActivity extends BaseActivity {

    @Pref
    ConfigPref_ configPref;
//IWXAPI api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // 隐藏状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

//        api = WXAPIFactory.createWXAPI(LoginActivity.this, WX_APP_ID, true);
//        api.registerApp(WX_APP_ID);


    }

    String WX_APP_ID = "wxeb1b89052d8f8794";

    @AfterViews
    void init() {


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


        findViewById(R.id.wx_login_id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainApp mainApp=(MainApp)getApplicationContext();
                IWXAPI api = mainApp.api;

                if (api == null) {
                    api = WXAPIFactory.createWXAPI(LoginActivity.this, WX_APP_ID, false);
                    api.registerApp(WX_APP_ID);
                }
                if (!api.isWXAppInstalled()) {
                    //提醒用户没有按照微信
                    Toast.makeText(LoginActivity.this, "没有安装微信,请先安装微信!", Toast.LENGTH_SHORT).show();
                    return;
                }
                SendAuth.Req req = new SendAuth.Req();
                req.scope = "snsapi_userinfo";
                req.state = "wechat_sdk_demo_test";
                api.sendReq(req);


//                Util.startActivity(LoginActivity.this,MainActivity_.class);
//                finish();
            }
        });

    }


    @Override
    protected void initActivityName() {
        activityName = LoginActivity.class.getName();
    }

    @Override
    protected void setActivityBg() {

    }

    @Override
    protected void onResume() {
        super.onResume();

    }


}
