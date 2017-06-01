package com.taomake.teabuddy.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.taomake.teabuddy.R;
import com.taomake.teabuddy.component.FoxProgressbarInterface;
import com.taomake.teabuddy.network.ProtocolUtil;
import com.taomake.teabuddy.network.RowMessageHandler;
import com.taomake.teabuddy.object.DeviceCubImgJson;
import com.taomake.teabuddy.prefs.ConfigPref_;
import com.taomake.teabuddy.util.Constant;
import com.taomake.teabuddy.util.ImageLoaderUtil;
import com.taomake.teabuddy.util.Util;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhang on 2015/8/7.
 */
@EActivity(R.layout.cm_device_manager)
public class DeviceManagerActivity extends BaseActivity {

    @Pref
    ConfigPref_ configPref;
    @ViewById(R.id.mg_cubimg_id)
    ImageView mg_cubimg_id;



    @ViewById(R.id.mg_unbind_id)
    TextView mg_unbind_id;


    int y = 0;
    int limit = 10;


    @Click(R.id.left_title_line)
    void onLeftTitleLine() {

        finish();

    }
    final int SCANNIN_GREQUEST_CODE=1001;

    @Click(R.id.right_title_line)
    void onright_title_line() {
        Intent intent = new Intent();
        intent.setClass(DeviceManagerActivity.this, SecondQrCodeAddDevice.class);

        startActivity(intent);
//        finish();

    }

    @Click(R.id.update_line_id)
    void onupdate_line_id() {

        Util.startActivity(DeviceManagerActivity.this, DeviceUpdateActivity_.class);
//        finish();

    }

    @Click(R.id.readme_line_id)
    void onreadme_line_id() {//说明书

        Util.startActivity(DeviceManagerActivity.this, ReadmeActivity_.class);
//        finish();

    }
    @Click(R.id.findcode_line_id)
    void onfindcode_line_id() {//设备二维码
        Util.startActivity(DeviceManagerActivity.this, DeviceSecondCodeActivity_.class);
//        finish();


    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case SCANNIN_GREQUEST_CODE:
                if(resultCode == RESULT_OK){
                    Bundle bundle = data.getExtras();
                    String ticket=bundle.getString("result");
                    Log.e("Recv QRcode result", ticket);


//                    //跳到手机号页面
                    Intent intent = new Intent(DeviceManagerActivity.this, LoginByphoneActivity_.class);

                    intent.putExtra("ticket", ticket);
                    intent.putExtra("unionid", configPref.userUnion().get());
                    intent.putExtra("MANGER","1");

                    startActivity(intent);
                }
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        setContentView(R.layout.design_personal);
//        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

//        initUi();


    }
    private ImageLoader imageLoader;
    private DisplayImageOptions options;

    public void initUi() {

        RelativeLayout main_title_id = (RelativeLayout) findViewById(R.id.main_title_id);
//        main_title_id.setBackgroundColor(getResources().getColor(R.color.white));

        ImageView left_title_icon = (ImageView) findViewById(R.id.left_title_icon);
        left_title_icon.setVisibility(View.VISIBLE);
//        left_title_icon.setImageDrawable(getResources().getDrawable(R.drawable.topbar_btn_bank));

        ImageView right_title_icon = (ImageView) findViewById(R.id.right_title_icon);
        right_title_icon.setVisibility(View.VISIBLE);

        TextView title = (TextView) findViewById(R.id.title);
        title.setVisibility(View.VISIBLE);
        title.setText("设备管理");
        title.setTextColor(getResources().getColor(R.color.white));
        View title_line_id = (View) findViewById(R.id.title_line_id);
        title_line_id.setVisibility(View.GONE);

        TextView mg_title_id = (TextView) findViewById(R.id.mg_title_id);
if(configPref.userName().get()!=null)
        mg_title_id.setText(configPref.userName().get()+"的喝茶小伙伴");

        imageLoader = ImageLoader.getInstance();
        options = ImageLoaderUtil.getAvatarOptionsInstance();
        getDeviceCubImg();
//        initdata();
    }


    @AfterViews
    void init() {
        initUi();
    }


    @Override
    protected void initActivityName() {
        activityName = DeviceManagerActivity.class.getName();
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


    //**********获取筛选的后的list***************/
    FoxProgressbarInterface foxProgressbarInterface;

    public void getDeviceCubImg() {

        foxProgressbarInterface = new FoxProgressbarInterface();
        foxProgressbarInterface.startProgressBar(this, "加载中...");
        ProtocolUtil.getDeviceCubImgUrl(this, new DelTeaListInfoHandler(), configPref.userDeviceId().get());//devno 空表示所有


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
            DeviceCubImgJson dbRecordsJson = new Gson().fromJson(resp, DeviceCubImgJson.class);
            if ((dbRecordsJson.rcode + "").equals(Constant.RES_SUCCESS)) {
                String imgUrl=dbRecordsJson.obj;
                if(imgUrl!=null&&!imgUrl.equals("")){

                    imageLoader.displayImage(imgUrl,mg_cubimg_id,options);

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




