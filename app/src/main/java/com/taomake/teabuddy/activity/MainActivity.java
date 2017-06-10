package com.taomake.teabuddy.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.taomake.teabuddy.R;
import com.taomake.teabuddy.base.BaseFragmentActivity;
import com.taomake.teabuddy.base.MainApp;
import com.taomake.teabuddy.component.One_Permission_Popwindow;
import com.taomake.teabuddy.fragment.DesignTabFragment_;
import com.taomake.teabuddy.fragment.HomeTabFragment;
import com.taomake.teabuddy.fragment.HomeTabFragment_;
import com.taomake.teabuddy.fragment.HotFragment;
import com.taomake.teabuddy.fragment.MineTabFragment;
import com.taomake.teabuddy.fragment.MineTabFragment_;
import com.taomake.teabuddy.network.ProtocolUtil;
import com.taomake.teabuddy.network.RowMessageHandler;
import com.taomake.teabuddy.object.UnreadSumJson;
import com.taomake.teabuddy.prefs.ConfigPref_;
import com.taomake.teabuddy.util.Constant;
import com.taomake.teabuddy.util.ImageLoaderUtil;
import com.taomake.teabuddy.util.Util;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;


//import java.util.Timer;
//import java.util.TimerTask;

//import java.util.Timer;
//import java.util.TimerTask;

@EActivity(R.layout.activity_main)
public class MainActivity extends BaseFragmentActivity {

    static String TAG = MainActivity.class.getName();

    @Extra
    int flag = 0;
    @Pref
    ConfigPref_ configPref;
    @ViewById(R.id.container)
    FrameLayout container;

    @ViewById(R.id.my_content_view)
    View myContentView;

    Fragment fragmentLuyin;
    Fragment fragmentPaocha;
    Fragment fragmentMine;


    private Fragment currentFragment = null;

    @ViewById(R.id.tab_luyin)
    View tab_luyin;
    @ViewById(R.id.tab_luyin_img)
    ImageView tab_luyin_img;
    @ViewById(R.id.tab_luyin_img_c)
    ImageView tab_luyin_img_c;
    @ViewById(R.id.tab_luyin_text)
    TextView tab_luyin_text;

    @ViewById(R.id.tab_paocha)
    View tab_paocha;
    @ViewById(R.id.tab_paocha_img)
    ImageView tab_paocha_img;

    @ViewById(R.id.tab_paocha_img_c)
    ImageView tab_paocha_img_c;
    @ViewById(R.id.tab_paocha_text)
    TextView tab_paocha_text;

    @ViewById(R.id.tab_mine)
    View tab_mine;
    @ViewById(R.id.tab_mine_img)
    ImageView tab_mine_img;
    @ViewById(R.id.tab_mine_img_c)
    ImageView tab_mine_img_c;
    @ViewById(R.id.tab_mine_text)
    TextView tab_mine_text;


    @ViewById(R.id.mine_msg_id)

    TextView mine_msg_id;
    @ViewById(R.id.mine_msg_line)

    LinearLayout mine_msg_line;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String deviceid = getIntent().getStringExtra("deviceid");
        String mac = getIntent().getStringExtra("mac");

        String unionid = getIntent().getStringExtra("unionid");


        if (!TextUtils.isEmpty(deviceid)) {
            configPref.userDeviceId().put(deviceid);
            SharedPreferences.Editor editor = getSharedPreferences("dataUNIION",MODE_PRIVATE).edit();
            editor.putString("deviceid",deviceid);
            editor.commit();

        }
        if (!TextUtils.isEmpty(mac)) {
            SharedPreferences.Editor editor = getSharedPreferences("dataUNIION",MODE_PRIVATE).edit();
            editor.putString("mac",mac);
            editor.commit();
            configPref.userDeviceMac().put(mac);
        }
        if (!TextUtils.isEmpty(unionid)) {
            configPref.userUnion().put(unionid);
        }
        SharedPreferences preferences=getSharedPreferences("dataUNIION", MODE_PRIVATE);
        String nickname=preferences.getString("nickname", "");
        if (!TextUtils.isEmpty(nickname)) {
            configPref.userName().put(nickname);
        }

        mainappAll=(MainApp)getApplicationContext();


//        mine_msg_id= (TextView)   findViewById(R.id.mine_msg_id);
//        mine_msg_line= (LinearLayout)   findViewById(R.id.mine_msg_line);
//        mine_msg_line.setVisibility(View.GONE);
    }
MainApp  mainappAll=null;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @AfterViews
    void onInit() {
        ImageLoaderUtil.initDefaulLoader(getApplicationContext());

        fragmentLuyin = HomeTabFragment_.builder().build();
        fragmentPaocha = DesignTabFragment_.builder().build();
        fragmentMine = MineTabFragment_.builder().build();
        //预先add fragments,防止anr时切换tab导致重复add exception
        String FromActivity = getIntent().getStringExtra("FromActivity");

        if(TextUtils.isEmpty(FromActivity)) {
            changePage(fragmentLuyin);
//        changePage(fragmentPaocha);
            changePage(fragmentMine);

//        if (flag == 0) {
            selectPay();
        }else{
        changePage(fragmentPaocha);
            changePage(fragmentMine);
            selectRun();
        }
////            showIntroDialog();
//        } else if (flag == 1) {
//            selectPay();
//        }


    }

boolean firstSelect=false;
    @Click(R.id.tab_luyin)
    void onTabRun() {
        selectRun();
    }

    @Click(R.id.tab_paocha)
    void onTabPay() {
        selectPay();
    }


    @Click(R.id.tab_mine)
    void onTabMine() {
        selectMine();
    }

    final int TAB_TRANS_TIME = 200;

    private void resetTab(View tab) {
//        if (tab != null) {
//            TransitionDrawable transitionDrawable = (TransitionDrawable) tab.getBackground();
//            transitionDrawable.reverseTransition(TAB_TRANS_TIME);
//        }
    }

    private void selectTab(View tab) {
        currentTab = tab;
//        TransitionDrawable transitionDrawable = (TransitionDrawable) tab.getBackground();
//        transitionDrawable.startTransition(TAB_TRANS_TIME);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
    }

    private View currentTab = null;

    private void selectRun() {

long endtime=System.currentTimeMillis();

        current=0;
        resetImg();
        resetTab(currentTab);
        selectTab(tab_luyin);
        changePage(fragmentLuyin);

        tab_luyin_img.setVisibility(View.GONE);
        tab_luyin_img_c.setVisibility(View.VISIBLE);
        tab_luyin_text.setTextColor(getResources().getColor(R.color.white));
long starttime=mainappAll.starttime;
        if(endtime-starttime>6000) {
            Intent intent = new Intent(HomeTabFragment.MYACTION_UPDATE_HOME);
            Log.i("Broadcast Change home", "change home fragment");

            sendBroadcast(intent);
        }

    }

    private void selectPay() {


        current=1;
        String unionid = configPref.userDeviceMac().get();

        if (unionid != null && !unionid.equals("")) {
            resetImg();
            resetTab(currentTab);
            selectTab(tab_paocha);
            changePage(fragmentPaocha);

            tab_paocha_img.setVisibility(View.GONE);
            tab_paocha_img_c.setVisibility(View.VISIBLE);
            tab_paocha_text.setTextColor(getResources().getColor(R.color.white));

            MainApp mainApp=(MainApp)getApplicationContext();
         if(mainApp.boolchoosePaocha) {
long endtime=System.currentTimeMillis();

             long starttime=mainappAll.starttime;
             if(endtime-starttime>6000) {

                 Intent intent = new Intent(HotFragment.MYACTION_UPDATE);
                 Log.i("Broadcast Change Hot", "change hot fragment");

                 sendBroadcast(intent);
             }
         }
        } else {//弹出选择对话框

            perssion_func();
        }

    }

    public void perssion_func() {
        new One_Permission_Popwindow().showPopwindow(MainActivity.this, tab_paocha, null, null, null, new One_Permission_Popwindow.CallBackPayWindow() {
            @Override
            public void handleCallBackChangeUser() {
                Util.outLogin(MainActivity.this, configPref);
                Util.startActivityNewTask(MainActivity.this, WelcomeActivity_.class);
                finish();


            }

            @Override
            public void handleCallBackBindDevice() {
                Util.startActivityNewTask(MainActivity.this, WelcomeActivity_.class);
                finish();


            }
        });

    }

    public void selectMine() {
        current=2;
        String unionid = configPref.userDeviceMac().get();

        if (unionid != null && !unionid.equals("")) {
            resetImg();
            resetTab(currentTab);
            selectTab(tab_mine);
            changePage(fragmentMine);
            tab_mine_img.setVisibility(View.GONE);
            tab_mine_img_c.setVisibility(View.VISIBLE);
            tab_mine_text.setTextColor(getResources().getColor(R.color.white));

            long endtime=System.currentTimeMillis();

            long starttime=mainappAll.starttime;
            if(endtime-starttime>6000) {
                Intent intent = new Intent(MineTabFragment.MYACTION_UPDATE_Mine);
                Log.i("Broadcast Change MINE", "change MINE fragment");

                sendBroadcast(intent);
            }
        } else {//当没有登录的时候不能被点击
            perssion_func();


        }

    }

    public static Integer current=0;

    private void changePage(Fragment fragment) {

        if (currentFragment != fragment) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

            if (currentFragment != null) {
                transaction.hide(currentFragment);
            }

            if (fragment.isAdded()) {
                transaction.show(fragment);
            } else {
                transaction.add(R.id.container, fragment);
            }
            transaction.commit();
            currentFragment = fragment;
        }
    }

    static int count = 0;


    @Override
    protected void onDestroy() {
        super.onDestroy();

//        Util.eventUnregister(this);
//        unregisterReceiver(timeTickReceiver);
    }
//    long starttime=0;
    @Override
    protected void onResume() {
        super.onResume();
        String FromActivity = getIntent().getStringExtra("FromActivity");

        if(TextUtils.isEmpty(FromActivity)) {

            getSumMsg();
        }

        if(current==1){
            long endtime=System.currentTimeMillis();

            long starttime=mainappAll.starttime;
            if(endtime-starttime>6000) {
                Intent intent = new Intent(HotFragment.MYACTION_UPDATE);
                Log.i("Broadcast Change Hot", "change hot fragment");
                intent.putExtra("updateteaid", "1");
                sendBroadcast(intent);
            }
        }

    }

    @Override
    protected void setActivityBg() {
//        if (BgTransitionUtil.bgDrawable != null) {
//            mainPage.setBackground(BgTransitionUtil.bgDrawable);
//        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "return from setttings... in UserMain");

    }

//


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            moveTaskToBack(false);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void resetImg() {

        tab_luyin_img.setVisibility(View.VISIBLE);
        tab_luyin_img_c.setVisibility(View.GONE);
        tab_paocha_img.setVisibility(View.VISIBLE);
        tab_paocha_img_c.setVisibility(View.GONE);
        tab_mine_img.setVisibility(View.VISIBLE);
        tab_mine_img_c.setVisibility(View.GONE);

        tab_luyin_text.setTextColor(getResources().getColor(R.color.white_alpha60));
        tab_paocha_text.setTextColor(getResources().getColor(R.color.white_alpha60));
        tab_mine_text.setTextColor(getResources().getColor(R.color.white_alpha60));


    }


    public void getSumMsg() {
//        foxProgressbarInterface = new FoxProgressbarInterface();
//        foxProgressbarInterface.startProgressBar(getActivity(), "加载中...");

        Log.d("获取有未读短信", "");

        ProtocolUtil.getSumUnReadMsg(MainActivity.this, new SendLogToServerHandler(), configPref.userUnion().get());


    }


    private class SendLogToServerHandler extends RowMessageHandler {
        @Override
        protected void handleResp(String resp) {
            sendLogToServerHandler(resp);
        }
    }


    public void sendLogToServerHandler(String resp) {
//        foxProgressbarInterface.stopProgressBar();
        if (resp != null && !resp.equals("")) {

            //解析返回json 数据


            UnreadSumJson baseJson = new Gson().fromJson(resp, UnreadSumJson.class);
            if ((baseJson.rcode + "").equals(Constant.RES_SUCCESS)) {

                Log.d("未读消息总共", baseJson.obj+"条");
                if(!TextUtils.isEmpty(baseJson.obj)) {
                    Integer unreadnum = Integer.valueOf(baseJson.obj);
                    configPref.sedentaryInterval().put(unreadnum);

                    if (unreadnum > 0) {
                        mine_msg_line.setVisibility(View.VISIBLE);

                        mine_msg_id.setText(baseJson.obj);
                    }else{
                        mine_msg_line.setVisibility(View.GONE);

                    }
                }
            }

        }
    }







//
//    private void setFullScreen() {
////        this.requestWindowFeature(Window.FEATURE_NO_TITLE);// 去标题栏
//        if (Build.VERSION.SDK_INT >= 19){
//            View decorView = getWindow().getDecorView();
//            int option = View.SYSTEM_UI_FLAG_LAYOUT_STABLE  //去掉好像无影响
//                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION //去掉好像无影响
//                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
//                    | View.SYSTEM_UI_FLAG_FULLSCREEN
//                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
//            decorView.setSystemUiVisibility(option);
//        }else {
//            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
//        }
//    }
////退出全屏函数：
//
//    private void quitFullScreen() {
////        this.requestWindowFeature(Window.FEATURE_NO_TITLE);// 去标题栏
//        if (Build.VERSION.SDK_INT >= 19) {
//            View decorView = getWindow().getDecorView();
//            int option = View.SYSTEM_UI_FLAG_VISIBLE;
//            decorView.setSystemUiVisibility(option);
//
//        } else {
//
//
//            final WindowManager.LayoutParams attrs = getWindow().getAttributes();
//            attrs.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
//            getWindow().setAttributes(attrs);
//            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
//        }
//    }
}
