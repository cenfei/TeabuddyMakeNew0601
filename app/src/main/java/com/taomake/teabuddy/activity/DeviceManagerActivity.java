package com.taomake.teabuddy.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.taomake.teabuddy.R;
import com.taomake.teabuddy.base.MainApp;
import com.taomake.teabuddy.component.FoxProgressbarInterface;
import com.taomake.teabuddy.network.ProtocolUtil;
import com.taomake.teabuddy.network.RowMessageHandler;
import com.taomake.teabuddy.object.DeviceCubImgJson;
import com.taomake.teabuddy.object.DeviceVersionJson;
import com.taomake.teabuddy.object.DeviceVersionObj;
import com.taomake.teabuddy.prefs.ConfigPref_;
import com.taomake.teabuddy.util.Constant;
import com.taomake.teabuddy.util.ImageLoaderUtil;
import com.taomake.teabuddy.util.MyStringUtils;
import com.taomake.teabuddy.util.Util;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import quinticble.QuinticBleAPISdkBase;

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

    @Click(R.id.mg_unbind_id)
    void onmg_unbind_id() {

        checkCameraPersimion();

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

    public void toSencodQrcode(){
        Intent intent = new Intent();
        intent.setClass(DeviceManagerActivity.this, SecondQrCodeAddDevice_.class);

        startActivity(intent);
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
                Toast.makeText(DeviceManagerActivity.this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    @Click(R.id.left_title_line)
    void onLeftTitleLine() {

        finish();

    }

    /**********************************************/
    FoxProgressbarInterface foxProgressbarInterface0;
String deviceVersion=null;
    public void checkDeviceUpdateToServerBTN() {
        foxProgressbarInterface0 = new FoxProgressbarInterface();
        foxProgressbarInterface0.startProgressBar(this, "加载中...");

        if (deviceVersion == null) {
            deviceVersion = configPref.userDeviceVersion().get();
        }
        if (deviceVersion == null) {
            Util.Toast(this, "设备当前没有版本号", null);

            needupdate = false;

            return;
        }
        Log.d("BTN开始获取固件版本信息", deviceVersion);

        ProtocolUtil.getDeviceUpdateVersion(this, new CheckDeviceUpdateToServerHandlerBTN(), configPref.userDeviceId().get(), deviceVersion);


    }


    private class CheckDeviceUpdateToServerHandlerBTN extends RowMessageHandler {
        @Override
        protected void handleResp(String resp) {
            checkDeviceUpdateToServerHandlerBTN(resp);
        }
    }


    public void checkDeviceUpdateToServerHandlerBTN(String resp) {
        foxProgressbarInterface0.stopProgressBar();
        if (resp != null && !resp.equals("")) {

            //解析返回json 数据
            Map<String, Object> orderMap = new Gson().fromJson(resp,
                    new TypeToken<Map<String, Object>>() {
                    }.getType());
            // 将 infomation 转成需要的 order信息

            Double return_code_int = (Double) orderMap.get("rcode");
            if (return_code_int == 0) {
//                configPref.userDeviceVersion().put(deviceVersion);

                MainApp mainApp = (MainApp) getApplicationContext();
                sysUpdateVersion = configPref.userDeviceVersion().get();
                mainApp.boolupdateSuccess = 0;
                needupdate = false;
//                configPref.userDeviceVersion().put();
                dv_update_img_id.setBackgroundResource(R.drawable.cm_update_device);


            } else {


                DeviceVersionJson baseJson = new Gson().fromJson(resp, DeviceVersionJson.class);
                if ((baseJson.rcode + "").equals(Constant.RES_SUCCESS)) {

                    List<DeviceVersionObj> deviceVersionObjList = baseJson.obj;
                    DeviceVersionObj deviceVersionObj = null;
                    if (deviceVersionObjList != null) {
                        deviceVersionObj = deviceVersionObjList.get(0);
                    }
                    MainApp mainApp = (MainApp) getApplicationContext();

                    if (deviceVersionObj != null && deviceVersionObj.url != null && !deviceVersionObj.url.equals("")) {
                        mainApp.deviceVersionObj = deviceVersionObj;

                        //将固件升级信息保存起来

//                    configPref.

                        mainApp.boolupdateSuccess = 0;
                        needupdate = true;
                        sysUpdateVersion = deviceVersionObj.ver;
                        //当前版本和固件版本比较
                        Double sysUpdateVersionD = Double.valueOf(sysUpdateVersion);
                        String deviceVersion = configPref.userDeviceVersion().get();
                        double deviceVersionD = 0;
                        if (!TextUtils.isEmpty(deviceVersion)) {
                            deviceVersionD = Double.valueOf(deviceVersion);
                        }
                        if (sysUpdateVersionD > deviceVersionD) {

                            sysdownloadsize = deviceVersionObj.downloadsize;
                            configPref.deviceUpdateInfo().put(new Gson().toJson(deviceVersionObj));
                            dv_update_img_id.setBackgroundResource(R.drawable.cm_update_device_c);
//                            perssion_func(update_device_id, "固件有新版本啦！", "马上升级", "以后再说");
                        } else {
                            if (!TextUtils.isEmpty(deviceVersion)) {

                                configPref.userDeviceVersion().put(deviceVersion);
                            }

                            mainApp.boolupdateSuccess = 0;
                            needupdate = false;

                            dv_update_img_id.setBackgroundResource(R.drawable.cm_update_device);

                        }

                    } else {
                        configPref.userDeviceVersion().put(deviceVersion);


                        mainApp.boolupdateSuccess = 0;
                        needupdate = false;

                        dv_update_img_id.setBackgroundResource(R.drawable.cm_update_device);

                    }


//                Log.d("开始上传Log", "上传成功");

                }
            }




            if (needupdate) {

                Intent intent = new Intent(this, DeviceUpdateTwoActivity_.class);

                intent.putExtra("sysdownloadsize", sysdownloadsize);
                intent.putExtra("upversion", sysUpdateVersion);

                startActivity(intent);

            } else {
                Util.Toast(this, "固件是最新版本!", null);

            }
        }
    }





    final int SCANNIN_GREQUEST_CODE = 1001;

    @Click(R.id.right_title_line)
    void onright_title_line() {

//        finish();
        checkCameraPersimion();
    }

    @Click(R.id.update_line_id)
    void onupdate_line_id() {
        if (!MyStringUtils.isopenBluetooth(this)) {


            return;
        }
        if (MyStringUtils.isNotNullAndEmpty(QuinticBleAPISdkBase.resultDevice)) {
            checkDeviceUpdateToServerBTN();

        }

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
                if (resultCode == RESULT_OK) {
                    Bundle bundle = data.getExtras();
                    String ticket = bundle.getString("result");
                    Log.e("Recv QRcode result", ticket);


//                    //跳到手机号页面
                    Intent intent = new Intent(DeviceManagerActivity.this, LoginByphoneActivity_.class);

                    intent.putExtra("ticket", ticket);
                    intent.putExtra("unionid", configPref.userUnion().get());
                    intent.putExtra("MANGER", "1");

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
    ImageView dv_update_img_id;
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
        if (configPref.userName().get() != null)
            mg_title_id.setText(configPref.userName().get() + "的喝茶小伙伴");


         dv_update_img_id = (ImageView) findViewById(R.id.dv_update_img_id);


        MainApp mainApp = (MainApp) getApplicationContext();

        DeviceVersionObj deviceVersionObj = mainApp.deviceVersionObj;
        if (deviceVersionObj == null) {
            dv_update_img_id.setBackgroundResource(R.drawable.cm_update_device);

            needupdate = false;
        } else {
            sysUpdateVersion = deviceVersionObj.ver;
            sysdownloadsize = deviceVersionObj.downloadsize;
            Double sysUpdateVersionD = Double.valueOf(sysUpdateVersion);
            String deviceVersion = configPref.userDeviceVersion().get();
            Double deviceVersionD = Double.valueOf(deviceVersion);

            if (sysUpdateVersionD > deviceVersionD) {
                needupdate = true;
                dv_update_img_id.setBackgroundResource(R.drawable.cm_update_device_c);

            } else {
                dv_update_img_id.setBackgroundResource(R.drawable.cm_update_device);

                needupdate = false;
            }


        }
        imageLoader = ImageLoader.getInstance();
        options = ImageLoaderUtil.getAvatarOptionsInstance();
//        getDeviceCubImg();


//        initdata();
    }

    String sysUpdateVersion = null;
    String sysdownloadsize = null;
    boolean needupdate = true;

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
        getDeviceCubImg();

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
                String imgUrl = dbRecordsJson.obj;
                if (imgUrl != null && !imgUrl.equals("")) {

                    imageLoader.displayImage(imgUrl, mg_cubimg_id, options);

                }

//                Util.Toast(MineAppsettingActivity.this, "收藏成功");

//                getMyCreateRecordListInfo();
            }
        }
    }


    public List<String> setTestData() {
        List<String> jobJsons = new ArrayList<String>();
        for (int i = 1; i < 10; i++) {


            jobJsons.add("打招呼");

        }

        return jobJsons;
    }


}




