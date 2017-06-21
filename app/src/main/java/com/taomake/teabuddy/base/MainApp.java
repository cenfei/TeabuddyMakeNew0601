package com.taomake.teabuddy.base;

import android.app.Application;
import android.content.Context;
import android.os.StrictMode;
import android.util.Log;
import android.webkit.WebView;

import com.sina.weibo.sdk.api.share.IWeiboShareAPI;
import com.sina.weibo.sdk.api.share.WeiboShareSDK;
import com.taomake.teabuddy.component.MyViewpager;
import com.taomake.teabuddy.object.DeviceVersionObj;
import com.taomake.teabuddy.util.ImageLoaderUtil;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.tencent.tauth.Tencent;
import com.umeng.analytics.MobclickAgent;
import com.uuzuche.lib_zxing.activity.ZXingLibrary;

import cn.jpush.android.api.JPushInterface;


/**
 * Created by xiaoganghu on 15/6/26.
 */
public class MainApp extends Application {

    private static String TAG = "MainApp";
public static WebView webView;
    public static boolean WX_SHOP=false;

   public  IWXAPI api;
    String    WX_APP_ID="wxeb1b89052d8f8794";
    public IWeiboShareAPI mWeiboShareAPI;

    public String registrationId;
public long starttime=0;
    public  boolean openBluetooth=true;
    public MyViewpager mPager;

    public  Tencent mTencent;

    public static final String mAppid = "1105799749";

    public static final String sinamAppid = "4db46388e95774dd60e37e5baeace63e";

    public  boolean boolchoosePaocha=true;

    public static int boolupdateSuccess=0;

    public DeviceVersionObj deviceVersionObj;


    public int changeDefaultVoice=0; //默认是0 第一次则需要立马获取蓝牙信息
    public boolean  boofirstFragmentMine=true;
    public boolean  boofirstFragmentAll=true;
    public boolean  boolDownup=false;

    @Override
    public void onCreate() {
        super.onCreate();
         api = WXAPIFactory.createWXAPI(getApplicationContext(), WX_APP_ID, true);
api.registerApp(WX_APP_ID);

        if (mTencent == null) {
            mTencent = Tencent.createInstance(mAppid, this);
        }

        mWeiboShareAPI = WeiboShareSDK.createWeiboAPI(this, sinamAppid);

        // 注册第三方应用到微博客户端中，注册成功后该应用将显示在微博的应用列表中。
        // 但该附件栏集成分享权限需要合作申请，详情请查看 Demo 提示
        // NOTE：请务必提前注册，即界面初始化的时候或是应用程序初始化时，进行注册
        mWeiboShareAPI.registerApp();

        Log.d(TAG, "MainApp start");
       // strictModeBuild();
        initImageLoader(getApplicationContext());

//        QbSdk.allowThirdPartyAppDownload(true);
//        QbSdk.initX5Environment(this, QbSdk.WebviewInitType.FIRSTUSE_AND_PRELOAD, null);

//        JPushInterface.setDebugMode(true);
        JPushInterface.init(this);

        registrationId = JPushInterface.getRegistrationID(this);
        Log.e("1099", "run:--------->registrationId： " + registrationId );
        ZXingLibrary.initDisplayOpinion(this);

        MobclickAgent.setScenarioType(this, MobclickAgent.EScenarioType.E_UM_NORMAL);



//        getLeservice();
    }



    public void strictModeBuild(){
        StrictMode.VmPolicy.Builder builder=new  StrictMode.VmPolicy.Builder();
        builder.detectAll();
        builder.penaltyLog();
        StrictMode.VmPolicy vmp=builder.build();
        StrictMode.setVmPolicy(vmp);
    }


    @Override
    public void onTerminate() {
        super.onTerminate();

        Log.d(TAG, "MainApp terminate");
    }

    public static void initImageLoader(Context context) {
//        ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(context));

      ImageLoaderUtil.initAdLoader(context);

    }


}
