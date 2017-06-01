package com.taomake.teabuddy.activity;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.taomake.teabuddy.R;
import com.taomake.teabuddy.component.FoxProgressbarInterface;
import com.taomake.teabuddy.network.ProtocolUtil;
import com.taomake.teabuddy.network.RowMessageHandler;
import com.taomake.teabuddy.object.AppVersionJson;
import com.taomake.teabuddy.prefs.ConfigPref_;
import com.taomake.teabuddy.util.Constant;
import com.taomake.teabuddy.util.Util;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.sharedpreferences.Pref;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhang on 2015/8/7.
 */
@EActivity(R.layout.cm_mine_appsetting)
public class MineAppsettingActivity extends BaseActivity {

    @Pref
    ConfigPref_ configPref;
//    @ViewById(R.id.no_data_id)
//    RelativeLayout no_data_id;



    int y = 0;
    int limit = 10;


    @Click(R.id.aboutus_rel_id)
    void onaboutus_rel_id() {//关于我们
        Util.startActivity(MineAppsettingActivity.this, SetAboutUsActivity_.class);
//        finish();


    }

    boolean boolUpdate=false;
    @Click(R.id.checkupdate_rel_id)
    void oncheckupdate_rel_id() {//检查新版本
//如果需要更新则跳转到应用市场

if(boolUpdate) {

    goToMarket(MineAppsettingActivity.this,this.getPackageName());
}

    }

    /**
     * 去市场下载页面
     */
    public void goToMarket(Context context, String packageName) {
        Uri uri = Uri.parse("market://details?id=" + packageName);
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        try {
            context.startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
        }
    }
    @Click(R.id.backidea_rel_id)
    void onbackidea_rel_id() {//意见反馈

        Util.startActivity(MineAppsettingActivity.this, SetBackIdeaActivity_.class);
//        finish();

    }
    @Click(R.id.onlinedebug_rel_id)
    void ononlinedebug_rel_id() {//在线诊断

        Util.startActivity(MineAppsettingActivity.this, SetOnlineDebugActivity_.class);
//        finish();

    }

    @Click(R.id.left_title_line)
    void onLeftTitleLine() {

        finish();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        setContentView(R.layout.design_personal);
//        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

//        initUi();


    }
    ImageView update_dot_id;

    public void initUi() {

        RelativeLayout main_title_id = (RelativeLayout) findViewById(R.id.main_title_id);
//        main_title_id.setBackgroundColor(getResources().getColor(R.color.white));

        ImageView left_title_icon = (ImageView) findViewById(R.id.left_title_icon);
        left_title_icon.setVisibility(View.VISIBLE);
        left_title_icon.setImageDrawable(getResources().getDrawable(R.drawable.topbar_btn_bank));

        ImageView right_title_icon = (ImageView) findViewById(R.id.right_title_icon);
        right_title_icon.setVisibility(View.GONE);

        TextView title = (TextView) findViewById(R.id.title);
        title.setVisibility(View.VISIBLE);
        title.setText("APP设置");
        title.setTextColor(getResources().getColor(R.color.black));
        View title_line_id = (View) findViewById(R.id.title_line_id);
        title_line_id.setVisibility(View.GONE);


         update_dot_id = (ImageView) findViewById(R.id.update_dot_id);


        LinearLayout right_title_line = (LinearLayout) findViewById(R.id.right_title_line);

        right_title_line.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                finish();
            }
        });
        update_dot_id.setBackgroundResource(R.drawable.appset_checkupdate);
        TextView version_comment_id = (TextView) findViewById(R.id.version_comment_id);
        String version=   Util.getAppVersionName(MineAppsettingActivity.this);

        version_comment_id.setText("当前版本"+version);


        initdata();


        getUpdateDeviceVersion();

    }


    @AfterViews
    void init() {
        initUi();
    }


    @Override
    protected void initActivityName() {
        activityName = MineAppsettingActivity.class.getName();
    }

    @Override
    protected void setActivityBg() {
//        if (BgTransitionUtil.bgDrawable != null) {
//            mainPage.setBackgroundDrawable(BgTransitionUtil.bgDrawable);
//        }
    }


    int pageNum = 1;

    int i = 0;

    /**
     * 测试数据
     */
    public void getDataFromServer() {

//
//        getMessageListFromServerForMsg();


    }


    void initdata() {

    }


    @Override
    public void onDestroy() {
        //退出activity前关闭菜单

        super.onDestroy();

    }

    //**********网络***************/


    //**********获取筛选的参数***************/


    @Override
    public void onResume() {
        super.onResume();

        pageNum = 1;

        getDataFromServer();
    }


    //获取设备版本号





    //**********获取筛选的后的list***************/
    FoxProgressbarInterface foxProgressbarInterface;

    public void getUpdateDeviceVersion() {

//        czid="2533";//测试这个有数据
//        String deviceVersion=configPref.userDeviceVersion().get();
//        if(deviceVersion==null||deviceVersion.equals("")){
//            Util.Toast(MineAppsettingActivity.this,"请先获取设备版本号");
//
//            return;
//        }
        foxProgressbarInterface = new FoxProgressbarInterface();
        foxProgressbarInterface.startProgressBar(this, "加载中...");
        ProtocolUtil.getAppUpdateVersion(this, new DelTeaListInfoHandler(), configPref.userUnion().get());//devno 空表示所有


    }


    private class DelTeaListInfoHandler extends RowMessageHandler {
        @Override
        protected void handleResp(String resp) {
            delTeaListInfoHandler(resp);
        }
    }

    public void delTeaListInfoHandler(String resp) {
        foxProgressbarInterface.stopProgressBar();
        if (resp != null && !resp.equals("")) {
            AppVersionJson dbRecordsJson = new Gson().fromJson(resp, AppVersionJson.class);
            if ((dbRecordsJson.rcode + "").equals(Constant.RES_SUCCESS)) {

                String webVersion=dbRecordsJson.obj.version;
                Double webVersionD=     Double.valueOf(webVersion);

                //获取当前版本号
                String version=   Util.getAppVersionName(MineAppsettingActivity.this);

                Double dversionD=     Double.valueOf(version);
                Log.d("version:",webVersionD+"....."+dversionD);
                if(webVersionD>dversionD){
                    boolUpdate=true;
                    //提示红点
                    update_dot_id.setBackgroundResource(R.drawable.appset_checkupdate_u);
                }else{
                    boolUpdate=false;
                    update_dot_id.setBackgroundResource(R.drawable.appset_checkupdate);


                }


//                Util.Toast(MineAppsettingActivity.this, "收藏成功");

//                getMyCreateRecordListInfo();
            }
        }
    }




    public List<String > setTestData() {
        List<String> jobJsons = new ArrayList<String>();
        for (int i = 1; i < 10; i++) {




            jobJsons.add("打招呼");

        }

        return jobJsons;
    }



}




