package com.taomake.teabuddy.fragment;

/**
 * Created by foxcen on 16/7/29.
 */

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.taomake.teabuddy.R;
import com.taomake.teabuddy.activity.ChooseTeaActivity;
import com.taomake.teabuddy.activity.ChooseTeaActivity_;
import com.taomake.teabuddy.activity.DeviceManagerActivity_;
import com.taomake.teabuddy.activity.DeviceUpdateTwoActivity_;
import com.taomake.teabuddy.activity.WebViewActivity_;
import com.taomake.teabuddy.base.MainApp;
import com.taomake.teabuddy.component.DynamicWave;
import com.taomake.teabuddy.component.FoxProgressbarInterface;
import com.taomake.teabuddy.component.FoxProgressbarInterfaceHot;
import com.taomake.teabuddy.component.One_Permission_Popwindow;
import com.taomake.teabuddy.network.ProtocolUtil;
import com.taomake.teabuddy.network.RowMessageHandler;
import com.taomake.teabuddy.object.AppVersionJson;
import com.taomake.teabuddy.object.BaseJson;
import com.taomake.teabuddy.object.DeviceVersionJson;
import com.taomake.teabuddy.object.DeviceVersionObj;
import com.taomake.teabuddy.object.GetTeaInfoJson;
import com.taomake.teabuddy.prefs.ConfigPref_;
import com.taomake.teabuddy.sensoractivity.DeviceActivityTea;
import com.taomake.teabuddy.util.Constant;
import com.taomake.teabuddy.util.MyStringUtils;
import com.taomake.teabuddy.util.Util;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;

import java.util.List;
import java.util.Map;

import quinticble.QuinticBleAPISdkBase;
import quinticble.QuinticCallbackTea;
import quinticble.QuinticCommon;
import quinticble.QuinticDeviceFactoryTea;
import quinticble.QuinticDeviceTea;
import quinticble.QuinticException;

import static android.content.Context.WINDOW_SERVICE;

@EFragment(R.layout.cm_tea_main)

public class HotFragment extends Fragment {
    @Pref
    ConfigPref_ configPref;
    //    @ViewById(R.id.tea_name_id)
    TextView tea_name_id;

    //    @ViewById(R.id.tea_name_id_back)
    ImageView tea_name_id_back;
    //    @ViewById(R.id.tea_name_line)
    LinearLayout tea_name_line;

    ImageView update_device_id;


    @ViewById(R.id.bluetooth_rel)
    RelativeLayout bluetooth_rel;

//    @ViewById(R.id.tea_status_id)
    TextView tea_status_id;


    @Click(R.id.test_id)
    void ontest_id() {//测试
//        Intent intent = new Intent(getActivity(), ChooseTeaActivity_.class);
//
//        intent.putExtra("Tea_Name", tea_name_id.getText().toString());
//
//        startActivity(intent);

    }


    @Click(R.id.tea_main_dl_mg)
    void ontea_main_dl_mg() {
        if (isLoading) return;

        if(!boolCheckBattery) {

            Util.Toast(getActivity(),"后台正在同步",null);
            return;
        }

        getbatteryLevel(true);

    }

    @Click(R.id.pc_tea_choose_line)
    void onpc_tea_choose_line() {
        if (isLoading) return;


        Intent intent = new Intent(getActivity(), ChooseTeaActivity_.class);

        intent.putExtra("Tea_Name", tea_name_id.getText().toString());

        startActivity(intent);

    }

    @Click(R.id.tea_main_device_mg)
    void ontea_main_device_mg() {
        Intent intent = new Intent(getActivity(), DeviceManagerActivity_.class);


        startActivity(intent);

    }

    @Click(R.id.tea_main_set_mg)
    void ontea_main_set_mg() {

        if (isLoading) return;


        if (needupdate) {

            Intent intent = new Intent(getActivity(), DeviceUpdateTwoActivity_.class);

            intent.putExtra("sysdownloadsize", sysdownloadsize);
            intent.putExtra("upversion", sysUpdateVersion);

            startActivity(intent);

        } else {
//            Util.startActivity(getActivity(), DeviceUpdateActivity_.class);
            Util.Toast(getActivity(), "固件是最新版本!", null);
        }
    }


    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (MYACTION.equals(intent.getAction())) {
                Log.i("onReceive", "get the broadcast from DownLoadService...");
                final String result = intent.getStringExtra("CurrentLoading");
                Log.i("Broadcast receive", result);
                String trimResult = result.replace(" ", "");

                //判断返回的设备信息  并更新数据
                byte[] data = QuinticCommon.stringToBytes(trimResult);

                if (trimResult.contains("ec02")) {//不用解析直接调用接口上传


                } else if (trimResult.contains("ec0401")) {//电池电量 需要更新ui
                    batteryLevelValue = QuinticCommon.unsignedByteToInt(data[3]);
                    Log.d("当前电量", batteryLevelValue + "");

                    if (showcontnect) {
                        connectSuccessUi();
                    }
                } else if (trimResult.contains("ec0a01")) {
                    //泡茶状态： 1 Byte ， 0 : 非泡茶，1 : 泡茶中
                    //第几泡：   1 Byte ，包括：1 ~ 99
                    //当前泡茶第几秒： 用于手机同步泡茶计时
                    //空杯状态： 1byte，01：空杯  00：非空杯
                    teaStatusValue = QuinticCommon.unsignedByteToInt(data[3]);
                    teaingCountValue = QuinticCommon.unsignedByteToInt(data[4]);

                    teaingTimeValue = (QuinticCommon.unsignedByteToInt(data[5]) << 8) +
                            QuinticCommon.unsignedByteToInt(data[6]);
                    if (data.length > 7) {
                        teaingIsNull = QuinticCommon.unsignedByteToInt(data[7]);
                    }
                    Log.d("当前teaStatusValue", teaStatusValue + "");
                    Log.d("当前teaingCountValue", teaingCountValue + "");
                    Log.d("当前teaingTimeValue", teaingTimeValue + "");
                    Log.d("当前teaingIsNull", teaingIsNull + "");

                    if (showcontnect) {
                        connectSuccessUi();
                    }

                } else if (trimResult.contains("ec0e")) {//是否在充电
                    int isbatterying = QuinticCommon.unsignedByteToInt(data[2]);
                    Log.d("是否在充电", isbatterying + "");


                } else if (trimResult.contains("ec0f")) {//当前温度
                    int nowTemperature = QuinticCommon.unsignedByteToInt(data[3]);
                    Log.d("当前温度", nowTemperature + "");


                }

                sendLogToServer(trimResult);

            }
        }
    };


    String MYACTION = "com.pushtest.broadcast";
boolean boolShowLoadingFromTry=false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View chatView = inflater.inflate(R.layout.cm_tea_main, container, false);


        init(chatView);
        IntentFilter filter = new IntentFilter();
        filter.addAction(MYACTION);
        getActivity().registerReceiver(receiver, filter);


        IntentFilter filterHot = new IntentFilter();
        filterHot.addAction(MYACTION_UPDATE);
        getActivity().registerReceiver(receiverHot, filterHot);

        getActivity().registerReceiver(mReceiver, makeFilter());

        changeui();
        firstchange++;
        getUpdateDeviceVersion();
//        updateUiResume();

        return chatView;
    }

    int firstchange = 0;

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        getActivity().unregisterReceiver(receiver);
        getActivity().unregisterReceiver(receiverHot);
        getActivity().unregisterReceiver(mReceiver);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (!isVisibleToUser) {
            Log.d("HotFragment", "不可见");

        } else {
            Log.d("HotFragment", "当前可见");


        }
    }

    public void changeui() {


        connectFindDevice();

        Log.d("HotFragment", "changeui");
    }


    @Override
    public void onResume() {
        super.onResume();


    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        init();
    }

    int width = 0;
    TextView dccontrol_id, update_id;

    public void init(View view) {

        TextView pay_button_id = (TextView) view.findViewById(R.id.pay_button_id);
        final String payUrl = configPref.payUrl().get();
        if (payUrl != null && !payUrl.equals("")) {
            pay_button_id.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getActivity(), WebViewActivity_.class);
                    intent.putExtra("url", payUrl);
                    intent.putExtra("title", "购买");
                    startActivity(intent);

                }
            });
        }
        tea_name_line = (LinearLayout) view.findViewById(R.id.tea_name_line);


        pc_connecting_rel = (RelativeLayout) view.findViewById(R.id.pc_connecting_rel);
        pc_unconnected_rel = (RelativeLayout) view.findViewById(R.id.pc_unconnected_rel);
        pc_tryagain_rel = (RelativeLayout) view.findViewById(R.id.pc_tryagain_rel);
        connect_status_commnet_id = (TextView) view.findViewById(R.id.connect_status_commnet_id);

        tea_bat_img_id = (ImageView) view.findViewById(R.id.tea_bat_img_id);

        dccontrol_id = (TextView) view.findViewById(R.id.dccontrol_id);

        tea_sum_cub_text_id = (TextView) view.findViewById(R.id.tea_sum_cub_text_id);
        tea_tryagainconnect_comment = (TextView) view.findViewById(R.id.tea_tryagainconnect_comment);
        tea_unconnect_comment = (TextView) view.findViewById(R.id.tea_unconnect_comment);
        WindowManager wm = (WindowManager) getActivity().getSystemService(WINDOW_SERVICE);

        Display d = wm.getDefaultDisplay();

        width = d.getWidth();

        int height = d.getHeight();
        tea_sum_cub_text_id.setTextSize(110);
        tea_name_id = (TextView) view.findViewById(R.id.tea_name_id);
        tea_name_id_back = (ImageView) view.findViewById(R.id.tea_name_id_back);


        update_device_id = (ImageView) view.findViewById(R.id.update_device_id);
        update_id = (TextView) view.findViewById(R.id.update_id);

        update_device_id.setBackgroundResource(R.drawable.cm_update_device);

        tea_cub_dynamicwave = (DynamicWave) view.findViewById(R.id.tea_cub_dynamicwave);
        tea_cub_gif = (LinearLayout) view.findViewById(R.id.tea_cub_gif);
        tea_cub_dynamicwave.setVisibility(View.GONE);
        tea_cub_gif.setVisibility(View.GONE);

        cicrle_line_id = (LinearLayout) view.findViewById(R.id.cicrle_line_id);

        tea_status_id = (TextView) view.findViewById(R.id.tea_status_id);

        LinearLayout right_title = (LinearLayout) view.findViewById(R.id.right_title_line);
        right_title.setVisibility(View.GONE);
        LinearLayout left_title_line = (LinearLayout) view.findViewById(R.id.left_title_line);

        left_title_line.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                unconnectUi();
            }
        });

        tea_unconnect_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tryconnectUi();
            }
        });
        tea_tryagainconnect_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downupcount=1;
                boolShowLoadingFromTry=true;



                connectUi();
                connectFindDevice();

            }
        });
        connectUi();

    }

    LinearLayout cicrle_line_id;
    DynamicWave tea_cub_dynamicwave;
    LinearLayout tea_cub_gif;

    RelativeLayout pc_connecting_rel, pc_unconnected_rel, pc_tryagain_rel;
    TextView connect_status_commnet_id, tea_sum_cub_text_id, tea_tryagainconnect_comment, tea_unconnect_comment;
    ImageView tea_bat_img_id;

    boolean showcontnect = true;

    public void connectUi() {
        showcontnect = true;
        pc_connecting_rel.setVisibility(View.VISIBLE);
        pc_unconnected_rel.setVisibility(View.GONE);
        pc_tryagain_rel.setVisibility(View.GONE);

    }


    public static boolean canChangePager = true;

    public void unconnectUi() {
        showcontnect = false;
        cicrle_line_id.setVisibility(View.GONE);
        canChangePager = false;

        pc_connecting_rel.setVisibility(View.GONE);
        pc_unconnected_rel.setVisibility(View.VISIBLE);
        pc_tryagain_rel.setVisibility(View.GONE);


    }

    public void tryconnectUi() {
        showcontnect = false;

        pc_connecting_rel.setVisibility(View.GONE);
        pc_unconnected_rel.setVisibility(View.GONE);
        pc_tryagain_rel.setVisibility(View.VISIBLE);

    }


    public void connectSuccessUi() {




        MainApp mainappAll = (MainApp) getActivity().getApplicationContext();

//        if(mainappAll.boolDownup&&downupcount==0){
//            connectSendCodeFailUi("");
//            mainappAll.boolDownup=true;
//            downupcount=1;
//
//            return;
//        }


        boolCheckBattery=true;


        mainappAll.starttime = System.currentTimeMillis();

        cicrle_line_id.setVisibility(View.VISIBLE);

        connectUi();
        canChangePager = true;
        mainappAll.mPager.setIsCanScroll(true);

//        if (foxProgressbarInterface != null) {
//            foxProgressbarInterface.stopProgressBar();
//        }
        closeLoading();

        connect_status_commnet_id.setVisibility(View.GONE);
        changeBatImage();//电池电量切换
        tea_sum_cub_text_id.setTextColor(getResources().getColor(R.color.white));
        if (teaingCountValue == 0) {
            tea_sum_cub_text_id.setTextColor(getResources().getColor(R.color.white_alpha60));

        }

//        teaingCountValue=22;
        if (teaingCountValue < 10) {
            tea_sum_cub_text_id.setText(teaingCountValue + "");
            tea_sum_cub_text_id.setTextSize(110);


        } else {
            tea_sum_cub_text_id.setText(teaingCountValue + "");
//            tea_sum_cub_text_id.setTextSize(getResources().getDimension(R.dimen.font_60));
            tea_sum_cub_text_id.setTextSize(55);

        }


        if (teaStatusValue == 1) {
            //1.当前是泡茶  则需要gif图和 曲线图--------waiting

            tea_cub_gif.setVisibility(View.VISIBLE);
            tea_cub_dynamicwave.setVisibility(View.VISIBLE);

        } else {
            tea_cub_gif.setVisibility(View.GONE);
            tea_cub_dynamicwave.setVisibility(View.GONE);

        }

        //teaingIsNull 是否空杯

        if (teaingIsNull == 1) {
            tea_cub_dynamicwave.setVisibility(View.GONE);

        } else {
            tea_cub_dynamicwave.setVisibility(View.VISIBLE);
        }
        if (teaStatusValue == 1) {
            if (teaingCountValue == 0 && teaStatusValue == 1) {
                tea_status_id.setText("请洗茶");

            } else {
                tea_status_id.setText("泡茶中");
            }
        } else if (teaStatusValue == 0 && teaingIsNull == 0) {
            tea_status_id.setText("茶泡好了");
        } else {
            tea_status_id.setText("");
        }



        if (!TextUtils.isEmpty(teaname)) {
            tea_name_line.setVisibility(View.VISIBLE);
            if(teaname.length()>4){

                tea_name_id.setText(teaname.substring(0,4)+"\n"+teaname.substring(4));

            }else{
                tea_name_id.setText(teaname);
            }
//            tea_name_id.setText(teaname);
            tea_name_id_back.setVisibility(View.VISIBLE);
        }

    }

    int teaStatusValue = 0;//0非泡茶  1泡茶中
    int teaingCountValue = 0;//第几泡
    int teaingTimeValue = 0;//当前泡茶第几秒
    int teaingIsNull = 1;//是否空杯 1空杯  0 非空

    public void connectSendCodeFailUi(String msg) {
        QuinticBleAPISdkBase.resultDevice = null;
        MainApp mainappAll = (MainApp) getActivity().getApplicationContext();

        mainappAll.starttime = System.currentTimeMillis();

//        if (foxProgressbarInterface != null) {
//            foxProgressbarInterface.stopProgressBar();
//        }
        closeLoading();
        if (connect_status_commnet_id != null) {
            connect_status_commnet_id.setText(msg);

            unconnectUi();
        }
    }


    public void changeBatImage() {
        if (batteryLevelValue == 0) {
            tea_bat_img_id.setImageDrawable(getResources().getDrawable(R.drawable.bat_0));
        } else if (batteryLevelValue >= 1 && batteryLevelValue <= 30) {
            tea_bat_img_id.setImageDrawable(getResources().getDrawable(R.drawable.bat_1_30));

        } else if (batteryLevelValue >= 31 && batteryLevelValue <= 60) {
            tea_bat_img_id.setImageDrawable(getResources().getDrawable(R.drawable.bat_31_60));

        } else if (batteryLevelValue >= 61 && batteryLevelValue <= 80) {
            tea_bat_img_id.setImageDrawable(getResources().getDrawable(R.drawable.bat_61_80));

        } else if (batteryLevelValue >= 81 && batteryLevelValue <= 90) {
            tea_bat_img_id.setImageDrawable(getResources().getDrawable(R.drawable.bat_81_90));

        } else if (batteryLevelValue >= 91 && batteryLevelValue <= 98) {
            tea_bat_img_id.setImageDrawable(getResources().getDrawable(R.drawable.bat_91_98));

        } else {
            tea_bat_img_id.setImageDrawable(getResources().getDrawable(R.drawable.bat_100));

        }

    }


    //************************服务器接口*********************************

    //**********获取筛选的后的list***************/
    public void getUpdateDeviceVersion() {


        ProtocolUtil.getAppUpdateVersion(getActivity(), new DelTeaListInfoHandler(), configPref.userUnion().get());//devno 空表示所有


    }


    private class DelTeaListInfoHandler extends RowMessageHandler {
        @Override
        protected void handleResp(String resp) {
            delTeaListInfoHandler(resp);
        }
    }

    public void delTeaListInfoHandler(String resp) {
//        foxProgressbarInterface.stopProgressBar();
        if (resp != null && !resp.equals("")) {
            AppVersionJson dbRecordsJson = new Gson().fromJson(resp, AppVersionJson.class);
            if ((dbRecordsJson.rcode + "").equals(Constant.RES_SUCCESS)) {

                String webVersion = dbRecordsJson.obj.version;
                Double webVersionD = Double.valueOf(webVersion);

                //获取当前版本号
                String version = Util.getAppVersionName(getActivity());

                Double dversionD = Double.valueOf(version);
                Log.d("version:", webVersionD + "....." + dversionD);
                if (webVersionD > dversionD) {

                    new One_Permission_Popwindow().showPopwindow(getActivity(), tea_name_id, "APP有新版本啦!", "马上更新", "以后再说", new One_Permission_Popwindow.CallBackPayWindow() {
                        @Override
                        public void handleCallBackChangeUser() {
                            MyStringUtils.goToMarket(getActivity(), getActivity().getPackageName());

                        }

                        @Override
                        public void handleCallBackBindDevice() {


                        }
                    });


                } else {
//                    boolUpdate=false;
//                    update_dot_id.setBackgroundResource(R.drawable.appset_checkupdate);


                }


//
            }
        }
    }


    FoxProgressbarInterfaceHot foxProgressbarInterface;

    public void getTeaInfoByUnionid(String czid) {
//        foxProgressbarInterface = new FoxProgressbarInterface();
//        foxProgressbarInterface.startProgressBar(getActivity(), "加载中...");

        ProtocolUtil.getTeainfoByUnionidAndCzidMsg(getActivity(), new GetTeaInfoByUnionidHandler(), configPref.userUnion().get(), czid);


    }


    private class GetTeaInfoByUnionidHandler extends RowMessageHandler {
        @Override
        protected void handleResp(String resp) {
            getTeaInfoByUnionidHandler(resp);
        }
    }

    String teaname = null;

    public void getTeaInfoByUnionidHandler(String resp) {
//        foxProgressbarInterface.stopProgressBar();
        if (resp != null && !resp.equals("")) {

            //解析返回json 数据


            GetTeaInfoJson baseJson = new Gson().fromJson(resp, GetTeaInfoJson.class);
            if ((baseJson.rcode + "").equals(Constant.RES_SUCCESS)) {

                //获取到茶种名字
                teaname = baseJson.obj.czname;
                if (teaname == null || "".equals(teaname)) {
                    teaname = "默认茶\n自定义茶";
                    tea_name_line.setVisibility(View.VISIBLE);
                    tea_name_id.setText("默认茶\n自定义茶");
                    tea_name_id_back.setVisibility(View.VISIBLE);
                } else {
                    teaname = MyStringUtils.decodeUnicode(teaname);
                    Log.i("teaname", teaname);
                    tea_name_line.setVisibility(View.VISIBLE);
                    if(teaname.length()>4){

                        tea_name_id.setText(teaname.substring(0,4)+"\n"+teaname.substring(4));

                    }else{
                        tea_name_id.setText(teaname);
                    }
                    tea_name_id_back.setVisibility(View.VISIBLE);

                }

            }

        }
    }


    public void sendLogToServer(String logBytes) {
//        foxProgressbarInterface = new FoxProgressbarInterface();
//        foxProgressbarInterface.startProgressBar(getActivity(), "加载中...");

        Log.d("开始上传Log", logBytes);

        ProtocolUtil.sendLogToServer(getActivity(), new SendLogToServerHandler(), configPref.userUnion().get(), configPref.userDeviceId().get(), logBytes);


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


            BaseJson baseJson = new Gson().fromJson(resp, BaseJson.class);
            if ((baseJson.rcode + "").equals(Constant.RES_SUCCESS)) {

                Log.d("开始上传Log", "上传成功");


            }

        }
    }


    public void checkDeviceUpdateToServer() {
//        foxProgressbarInterface = new FoxProgressbarInterface();
//        foxProgressbarInterface.startProgressBar(getActivity(), "加载中...");

        if (deviceVersion == null) {
            deviceVersion = configPref.userDeviceVersion().get();
        }
        if (deviceVersion == null) {
            Util.Toast(getActivity(), "设备当前没有版本号", null);

            mustUpdate = false;

            return;
        }
        Log.d("开始获取固件版本信息", deviceVersion);

        ProtocolUtil.getDeviceUpdateVersion(getActivity(), new CheckDeviceUpdateToServerHandler(), configPref.userDeviceId().get(), deviceVersion);


    }


    private class CheckDeviceUpdateToServerHandler extends RowMessageHandler {
        @Override
        protected void handleResp(String resp) {
            checkDeviceUpdateToServerHandler(resp);
        }
    }

    boolean needupdate = false;

    public void checkDeviceUpdateToServerHandler(String resp) {
//        foxProgressbarInterface.stopProgressBar();
        if (resp != null && !resp.equals("")) {

            //解析返回json 数据
            Map<String, Object> orderMap = new Gson().fromJson(resp,
                    new TypeToken<Map<String, Object>>() {
                    }.getType());
            // 将 infomation 转成需要的 order信息

            Double return_code_int = (Double) orderMap.get("rcode");
            if (return_code_int == 0) {
//                configPref.userDeviceVersion().put(deviceVersion);

                MainApp mainApp = (MainApp) getActivity().getApplicationContext();
                sysUpdateVersion = configPref.userDeviceVersion().get();
                mainApp.boolupdateSuccess = 0;
                needupdate = false;
//                configPref.userDeviceVersion().put();
                update_device_id.setBackgroundResource(R.drawable.cm_update_device);


                return;
            }


            DeviceVersionJson baseJson = new Gson().fromJson(resp, DeviceVersionJson.class);
            if ((baseJson.rcode + "").equals(Constant.RES_SUCCESS)) {

                List<DeviceVersionObj> deviceVersionObjList = baseJson.obj;
                DeviceVersionObj deviceVersionObj = null;
                if (deviceVersionObjList != null) {
                    deviceVersionObj = deviceVersionObjList.get(0);
                }
                MainApp mainApp = (MainApp) getActivity().getApplicationContext();

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
                        update_device_id.setBackgroundResource(R.drawable.cm_update_device_c);
                        perssion_func(update_device_id, "固件有新版本啦！", "马上升级", "以后再说");
                    } else {
                        if (!TextUtils.isEmpty(deviceVersion)) {

                            configPref.userDeviceVersion().put(deviceVersion);
                        }

                        mainApp.boolupdateSuccess = 0;
                        needupdate = false;

                        update_device_id.setBackgroundResource(R.drawable.cm_update_device);

                    }

                } else {
                    configPref.userDeviceVersion().put(deviceVersion);


                    mainApp.boolupdateSuccess = 0;
                    needupdate = false;

                    update_device_id.setBackgroundResource(R.drawable.cm_update_device);

                }


//                Log.d("开始上传Log", "上传成功");


            }

        }
    }
    //************************服务器接口*********************************

    String sysUpdateVersion = "";
    String sysdownloadsize = "";
    //************************蓝牙操作*********************************

    /**
     * 功能：查找设备
     */
    private String blindDeviceId;
    private QuinticDeviceTea resultDeviceAll;
    private Integer countError = 0;


    public void closeProgress() {
//        if (foxProgressbarInterface != null) {
//            foxProgressbarInterface.stopProgressBar();
//        }
        closeLoading();
    }

    boolean mustUpdate = false;


    //*********************加载条***************

    boolean isLoading = false;
    final Handler writehandler = new Handler();


    int process = 1;
    protected Runnable runnable = new Runnable() {
        @Override
        public void run() {
            // TODO Auto-generated method stub
            //要做的事情
            if (process <= 20) {
                connect_status_commnet_id.setVisibility(View.VISIBLE);

                int processcase = process % 3;
                switch (processcase) {
                    case 1:
                        connect_status_commnet_id.setText("正在连接中.");
                        break;
                    case 2:
                        connect_status_commnet_id.setText("正在连接中..");
                        break;
                    case 0:
                        connect_status_commnet_id.setText("正在连接中...");
                        break;

                    default:
                        break;
                }

                writehandler.postDelayed(this, 1000);
                process = process + 1;
            } else {
                process = 0;

                connect_status_commnet_id.setVisibility(View.GONE);
                writehandler.removeCallbacks(runnable);
                loadOuttime();

            }
        }
    };

    public void loadOuttime() {
        isLoading = false;
        connectSendCodeFailUi("");
//        Util.Toast(getActivity(), "设备无法连接,建议重启设备", null);
//QuinticBleAPISdkBase.getInstanceFactory(getActivity()).setConnectionNull();


        new One_Permission_Popwindow().showPopwindow(getActivity(), tea_name_id, "当前程序异常无法连接设备请重启APP", "马上重启", "以后再说", new One_Permission_Popwindow.CallBackPayWindow() {
            @Override
            public void handleCallBackChangeUser() {
                System.exit(0);
            }

            @Override
            public void handleCallBackBindDevice() {


            }
        });


    }


    public void startLoading() {
        isLoading = true;

        writehandler.post(runnable);
        dccontrol_id.setTextColor(getResources().getColor(R.color.white_alpha60));
        update_id.setTextColor(getResources().getColor(R.color.white_alpha60));
        tea_sum_cub_text_id.setText(teaingCountValue + "");
        tea_sum_cub_text_id.setTextSize(110);

        tea_cub_dynamicwave.setVisibility(View.GONE);
        //tea_bat_img_id.setImageDrawable(getResources().getDrawable(R.drawable.bat_0));

//        if (TextUtils.isEmpty(teaname)) {
            tea_name_line.setVisibility(View.VISIBLE);
            tea_name_id.setText("正在同步");
            tea_name_id_back.setVisibility(View.GONE);
//        }
        tea_sum_cub_text_id.setTextColor(getResources().getColor(R.color.white_alpha60));
        tea_sum_cub_text_id.setText("0");
        tea_sum_cub_text_id.setTextSize(110);
        tea_status_id.setText("");
        tea_bat_img_id.setAlpha(0.6f);
        update_device_id.setAlpha(0.6f);
    }

    public void closeLoading() {
        isLoading = false;
        process = 0;
        dccontrol_id.setTextColor(getResources().getColor(R.color.white));
        update_id.setTextColor(getResources().getColor(R.color.white));
        tea_bat_img_id.setAlpha(1f);
        update_device_id.setAlpha(1f);
        connect_status_commnet_id.setVisibility(View.GONE);
        if (writehandler != null)
            writehandler.removeCallbacks(runnable);


    }

    //*********************加载条***************
int downupcount=0;
boolean  boolCheckBattery=true;
    public void connectFindDevice() {
        if (!MyStringUtils.isopenBluetooth(getActivity())) {
            connectSendCodeFailUi("");


            return;
        }
        MainApp mainappAll = (MainApp) getActivity().getApplicationContext();
        long endtime = System.currentTimeMillis();

        long starttime = mainappAll.starttime;
        if (endtime - starttime < 2000) {
            return;

        }


        blindDeviceId = configPref.userDeviceMac().get();
        blindDeviceId = MyStringUtils.macStringToUpper(blindDeviceId);


//        blindDeviceId = "88:4A:EA:83:A5:62";
//        configPref.userDeviceMac().put("884AEA83A562");
        Log.e("blindDeviceId:", blindDeviceId);
//            getTeaInfoByUnionid();

        boolCheckBattery=false;
        if (MyStringUtils.isNotNullAndEmpty(QuinticBleAPISdkBase.resultDevice)) {

//            Log.e("QuinticBleAPISdkBaseresultDevice", "not null");
            if (mainappAll.boolDownup) {
                startLoading();
            }else{
                if(boolShowLoadingFromTry){
                    startLoading();
                    boolShowLoadingFromTry=false;
                }

            }


            resultDeviceAll = QuinticBleAPISdkBase.resultDevice;
            // ************处理动作
            setNowTimeBle();
        } else {
//            Log.e("QuinticBleAPISdkBaseresultDevice", " null");
            startLoading();
            if (QuinticBleAPISdkBase.getInstanceFactory(getActivity()).conn != null) {

//                                        QuinticBleAPISdkBase.getInstanceFactory(DeviceUpdateTwoActivity.this).deviceMap.clear();
//
                Log.e("HOT","getInstanceFactory abort");
                QuinticBleAPISdkBase.getInstanceFactory(getActivity()).abort();

//                                        QuinticBleAPISdkBase.getInstanceFactory(DeviceUpdateTwoActivity.this).conn.disconnect();

            }


            QuinticBleAPISdkBase.getInstanceFactory(getActivity()).deviceMap.clear();//每次重连都会重新获取连接

            final Context context = getActivity();
            QuinticDeviceFactoryTea quinticDeviceFactory = QuinticBleAPISdkBase
                    .getInstanceFactory(context);

            quinticDeviceFactory.findDevice(blindDeviceId,
                    new QuinticCallbackTea<QuinticDeviceTea>() {
                        @Override
                        public void oadUpdate(final QuinticDeviceTea bluetoothDevice) {
                            super.oadUpdate(bluetoothDevice);

                            new Handler(context.getMainLooper())
                                    .post(new Runnable() {
                                        @Override
                                        public void run() {

                                            closeProgress();

                                            resultDeviceAll = bluetoothDevice;
                                            QuinticBleAPISdkBase.resultDevice = resultDeviceAll;
                                            // ************处理动作
                                            if (QuinticBleAPISdkBase.getInstanceFactory(getActivity()).conn != null) {
                                                QuinticBleAPISdkBase.getInstanceFactory(getActivity()).conn.disconnect();
                                            }
                                            Util.Toast(getActivity(), "将立马前往升级", null);

                                            mustUpdate = true;
                                            checkDeviceUpdateToServer();

//                                            try {
//                                                Thread.sleep(6000);
//                                            } catch (InterruptedException e) {
//                                                e.printStackTrace();
//                                            }
//
//
//                                            Intent intent = new Intent(getActivity(), DeviceActivityTea.class);
//                                            intent.putExtra("MAC_DEVICE", blindDeviceId);
//                                            startActivity(intent);

                                        }
                                    });


                        }

                        @Override
                        public void onComplete(final QuinticDeviceTea resultDevice) {
                            super.onComplete(resultDevice);
                            new Handler(context.getMainLooper())
                                    .post(new Runnable() {
                                        @Override
                                        public void run() {
//                                            closeProgress();
                                            MainApp mainappAll = (MainApp) getActivity().getApplicationContext();
                                            mainappAll.boolDownup = false;
                                            resultDeviceAll = resultDevice;
                                            QuinticBleAPISdkBase.resultDevice = resultDeviceAll;
                                            // ************处理动作
                                            setNowTimeBle();

                                        }
                                    });
                        }

                        @Override
                        public void onError(final QuinticException ex) {
                            new Handler(context.getMainLooper())
                                    .post(new Runnable() {
                                        @Override
                                        public void run() {

                                            QuinticBleAPISdkBase.resultDevice = null;
                                            closeProgress();
                                            unconnectUi();


                                        }
                                    });
                        }
                    });
        }
    }


    public void setNowTimeBle() {
        if (resultDeviceAll == null) return;
        String code = MyStringUtils.getNowTimeBleCode();
        code=code.toUpperCase();
        MainApp mainappAll = (MainApp) getActivity().getApplicationContext();
        mainappAll.starttime = System.currentTimeMillis();


















        resultDeviceAll.sendCommonCode(code, new QuinticCallbackTea<String>() {
            @Override
            public void onError(QuinticException ex) {
                super.onError(ex);
                new Handler(getActivity().getMainLooper())
                        .post(new Runnable() {
                            @Override
                            public void run() {
                                connectSendCodeFailUi("设置时间失败");
                            }
                        });
            }

            @Override
            public void onComplete(final String result) {
                super.onComplete(result);
                if (result == null) {
                    connectSendCodeFailUi("设置时间失败");

                    return;
                }
                new Handler(getActivity().getMainLooper())
                        .post(new Runnable() {
                            @Override
                            public void run() {
//BACK 0A 04 01 53result
                                MainApp mainappAll = (MainApp) getActivity().getApplicationContext();
                                mainappAll.boolDownup = false;
                                String trimResult = result.replace(" ", "");
                                if (trimResult.contains("eb01")) {
//                                    byte[] data = QuinticCommon.stringToBytes(trimResult);
//                                    int czid = (QuinticCommon.unsignedByteToInt(data[3]) << 32) + (QuinticCommon.unsignedByteToInt(data[4]) << 24) +
//                                            (QuinticCommon.unsignedByteToInt(data[5]) << 16) + (QuinticCommon.unsignedByteToInt(data[6]) << 8) +
//                                            QuinticCommon.unsignedByteToInt(data[7]);
//                                    Log.d("茶种id查询", batteryLevelValue + "");
//
//                                    getTeaInfoByUnionid(czid + "");
//
//                                    getbatteryLevel(false);
                                    getCzidBle();
                                    MainApp mainappAll0 = (MainApp) getActivity().getApplicationContext();
                                    mainappAll0.starttime = System.currentTimeMillis();

                                } else {
                                    connectSendCodeFailUi("设置时间失败");
                                }

                            }
                        });

            }
        });

    }




    public void getCzidBle() {
        if (resultDeviceAll == null) return;
        String code = "EA05";
        MainApp mainappAll = (MainApp) getActivity().getApplicationContext();
        mainappAll.starttime = System.currentTimeMillis();

        resultDeviceAll.sendCommonCode(code, new QuinticCallbackTea<String>() {
            @Override
            public void onError(QuinticException ex) {
                super.onError(ex);
                new Handler(getActivity().getMainLooper())
                        .post(new Runnable() {
                            @Override
                            public void run() {
                                connectSendCodeFailUi("茶种id查询失败");
                            }
                        });
            }

            @Override
            public void onComplete(final String result) {
                super.onComplete(result);
                if (result == null) {
                    connectSendCodeFailUi("茶种id查询失败");

                    return;
                }
                new Handler(getActivity().getMainLooper())
                        .post(new Runnable() {
                            @Override
                            public void run() {
//BACK 0A 04 01 53result
                                MainApp mainappAll = (MainApp) getActivity().getApplicationContext();
                                mainappAll.boolDownup = false;
                                String trimResult = result.replace(" ", "");
                                if (trimResult.contains("ea0501")) {
                                    byte[] data = QuinticCommon.stringToBytes(trimResult);
                                    int czid = (QuinticCommon.unsignedByteToInt(data[3]) << 32) + (QuinticCommon.unsignedByteToInt(data[4]) << 24) +
                                            (QuinticCommon.unsignedByteToInt(data[5]) << 16) + (QuinticCommon.unsignedByteToInt(data[6]) << 8) +
                                            QuinticCommon.unsignedByteToInt(data[7]);
                                    Log.d("茶种id查询", batteryLevelValue + "");

                                    getTeaInfoByUnionid(czid + "");

                                    getbatteryLevel(false);
                                    MainApp mainappAll0 = (MainApp) getActivity().getApplicationContext();
                                    mainappAll0.starttime = System.currentTimeMillis();

                                } else {
                                    connectSendCodeFailUi("茶种id查询失败");
                                }

                            }
                        });

            }
        });

    }


    int batteryLevelValue = 0;
    FoxProgressbarInterface foxProgressbarInterfaceonly = null;

    public void getbatteryLevel(final boolean only) {
        if (resultDeviceAll == null) return;
        String code = "EA04";
        MainApp mainappAll = (MainApp) getActivity().getApplicationContext();
        mainappAll.starttime = System.currentTimeMillis();
        if (only) {
            foxProgressbarInterfaceonly = new FoxProgressbarInterface();
            foxProgressbarInterfaceonly.startProgressBar(getActivity(), "设置中");
        }
        resultDeviceAll.sendCommonCode(code, new QuinticCallbackTea<String>() {
            @Override
            public void onError(QuinticException ex) {
                super.onError(ex);
                new Handler(getActivity().getMainLooper())
                        .post(new Runnable() {
                            @Override
                            public void run() {
                                connectSendCodeFailUi("电量查询失败");
                            }
                        });
            }

            @Override
            public void onComplete(final String result) {
                super.onComplete(result);
                if (result == null) {
                    connectSendCodeFailUi("电量查询失败");

                    return;
                }
                new Handler(getActivity().getMainLooper())
                        .post(new Runnable() {
                            @Override
                            public void run() {
//BACK 0A 04 01 53result
                                String trimResult = result.replace(" ", "");
                                if (trimResult.contains("ea0401")) {
                                    byte[] data = QuinticCommon.stringToBytes(trimResult);
                                    batteryLevelValue = QuinticCommon.unsignedByteToInt(data[3]);
                                    Log.d("当前电量", batteryLevelValue + "");

                                    if (only) {
                                        if (foxProgressbarInterfaceonly != null) {
                                            foxProgressbarInterfaceonly.stopProgressBar();
                                        }
                                        MainApp mainappAll = (MainApp) getActivity().getApplicationContext();
                                        mainappAll.starttime = System.currentTimeMillis();
                                        changeBatImage();
                                    } else {
                                        getTeaStatus();
                                        MainApp mainappAll = (MainApp) getActivity().getApplicationContext();
                                        mainappAll.starttime = System.currentTimeMillis();
                                    }
                                } else {
                                    connectSendCodeFailUi("电量查询失败");
                                }

                            }
                        });

            }
        });

    }


    public void getTeaStatus() {
        if (resultDeviceAll == null) return;
        String code = "EA0A";
        final String failMsg = "茶状态查询失败";
        resultDeviceAll.sendCommonCode(code, new QuinticCallbackTea<String>() {
            @Override
            public void onError(QuinticException ex) {
                super.onError(ex);
                new Handler(getActivity().getMainLooper())
                        .post(new Runnable() {
                            @Override
                            public void run() {
                                connectSendCodeFailUi(failMsg);
                            }
                        });
            }

            @Override
            public void onComplete(final String result) {
                super.onComplete(result);
                if (result == null) {
                    connectSendCodeFailUi(failMsg);

                    return;
                }
                new Handler(getActivity().getMainLooper())
                        .post(new Runnable() {
                            @Override
                            public void run() {
//BACK EA 0A 01 00 0A 00 00
                                String trimResult = result.replace(" ", "");
                                if (trimResult.contains("ea0a01")) {
                                    byte[] data = QuinticCommon.stringToBytes(trimResult);

                                    teaStatusValue = QuinticCommon.unsignedByteToInt(data[3]);
                                    teaingCountValue = QuinticCommon.unsignedByteToInt(data[4]);

                                    teaingTimeValue = (QuinticCommon.unsignedByteToInt(data[5]) << 8) +
                                            QuinticCommon.unsignedByteToInt(data[6]);
                                    Log.d("当前teaStatusValue", teaStatusValue + "");
                                    Log.d("当前teaingCountValue", teaingCountValue + "");
                                    Log.d("当前teaingTimeValue", teaingTimeValue + "");

                                    getCubIsNull();
                                    MainApp mainappAll = (MainApp) getActivity().getApplicationContext();
                                    mainappAll.starttime = System.currentTimeMillis();

                                } else {
                                    connectSendCodeFailUi(failMsg);
                                }

                            }
                        });

            }
        });

    }


    public void getCubIsNull() {
        if (resultDeviceAll == null) return;
        String code = "EA10";
        final String failMsg = "茶杯是否空杯查询失败";
        MainApp mainappAll = (MainApp) getActivity().getApplicationContext();
        mainappAll.starttime = System.currentTimeMillis();

        resultDeviceAll.sendCommonCode(code, new QuinticCallbackTea<String>() {
            @Override
            public void onError(QuinticException ex) {
                super.onError(ex);
                new Handler(getActivity().getMainLooper())
                        .post(new Runnable() {
                            @Override
                            public void run() {
                                connectSendCodeFailUi(failMsg);
                            }
                        });
            }

            @Override
            public void onComplete(final String result) {
                super.onComplete(result);
                if (result == null) {
                    connectSendCodeFailUi(failMsg);

                    return;
                }
                new Handler(getActivity().getMainLooper())
                        .post(new Runnable() {
                            @Override
                            public void run() {
//BACK 0A 10 01
                                String trimResult = result.replace(" ", "");

                                if (trimResult.contains("ea10")) {
                                    byte[] data = QuinticCommon.stringToBytes(trimResult);
                                    teaingIsNull = QuinticCommon.unsignedByteToInt(data[2]);
                                    Log.d("是否空杯", teaingIsNull + "");

//                                    connectSuccessUi();
                                    if (deviceVersion == null) {
                                        getDeviceUpdate();
                                    } else {
                                        getLogHistory();//同时获取log日志  2.并且向网络判断当前版本更新ui

                                    }
                                    MainApp mainappAll = (MainApp) getActivity().getApplicationContext();
                                    mainappAll.starttime = System.currentTimeMillis();

                                } else {
                                    connectSendCodeFailUi(failMsg);
                                }

                            }
                        });

            }
        });

    }


    private String deviceVersion = null;

    public void getDeviceUpdate() {
        if (resultDeviceAll == null) return;
        String code = "EA0B";
        final String failMsg = "固件版本查询失败";
        MainApp mainappAll = (MainApp) getActivity().getApplicationContext();
        mainappAll.starttime = System.currentTimeMillis();

        resultDeviceAll.sendCommonCode(code, new QuinticCallbackTea<String>() {
            @Override
            public void onError(QuinticException ex) {
                super.onError(ex);
                new Handler(getActivity().getMainLooper())
                        .post(new Runnable() {
                            @Override
                            public void run() {
                                connectSendCodeFailUi(failMsg);
                            }
                        });
            }

            @Override
            public void onComplete(final String result) {
                super.onComplete(result);
                if (result == null) {
                    connectSendCodeFailUi(failMsg);

                    return;
                }
                new Handler(getActivity().getMainLooper())
                        .post(new Runnable() {
                            @Override
                            public void run() {
//BACK 0A 10 01
                                String trimResult = result.replace(" ", "");

                                if (trimResult.contains("ea0b")) {
                                    byte[] data = QuinticCommon.stringToBytes(trimResult);

                                    int hVersion = QuinticCommon.unsignedByteToInt(data[3]);
                                    int lVersion = QuinticCommon.unsignedByteToInt(data[4]);

                                    Log.d("当前固件版本", hVersion + "," + lVersion);
                                    double versionD = (double) (hVersion * 100 + lVersion) / (double) 100;
                                    Log.d("当前固件版本versionD", versionD + "");

                                    deviceVersion = versionD + "";
//                                    deviceVersion="2.45";//fox测试
                                    if (deviceVersion.length() == 3) {
                                        deviceVersion = deviceVersion + "0";
                                    }
                                    configPref.userDeviceVersion().put(deviceVersion);
                                    checkDeviceUpdateToServer();
                                    getLogHistory();//同时获取log日志  2.并且向网络判断当前版本更新ui

                                    MainApp mainappAll = (MainApp) getActivity().getApplicationContext();
                                    mainappAll.starttime = System.currentTimeMillis();

//                                    connectSuccessUi();
                                } else {
                                    connectSendCodeFailUi(failMsg);
                                }

                            }
                        });

            }
        });

    }


    public void getLogHistory() {
        if (resultDeviceAll == null) return;
        String code = "EA07";
        final String failMsg = "历史LOG查询失败";
        MainApp mainappAll = (MainApp) getActivity().getApplicationContext();
        mainappAll.starttime = System.currentTimeMillis();

        resultDeviceAll.sendCommonCode(code, new QuinticCallbackTea<String>() {
            @Override
            public void onError(QuinticException ex) {
                super.onError(ex);
                new Handler(getActivity().getMainLooper())
                        .post(new Runnable() {
                            @Override
                            public void run() {
                                connectSendCodeFailUi(failMsg);
                            }
                        });
            }

            @Override
            public void onComplete(final String result) {
                super.onComplete(result);
                if (result == null) {
                    connectSendCodeFailUi(failMsg);

                    return;
                }
                new Handler(getActivity().getMainLooper())
                        .post(new Runnable() {
                            @Override
                            public void run() {
//BACK 0A 10 01
                                String trimResult = result.replace(" ", "");

                                if (trimResult.contains("ea07")) {
                                    byte[] data = QuinticCommon.stringToBytes(trimResult);
                                    int logValue = (QuinticCommon.unsignedByteToInt(data[3]) << 8) +
                                            QuinticCommon.unsignedByteToInt(data[4]);
                                    Log.d("是否空杯", teaingIsNull + "");
                                    Log.d("log总数", logValue + "");
                                    MainApp mainappAll = (MainApp) getActivity().getApplicationContext();
                                    mainappAll.starttime = System.currentTimeMillis();

                                    connectSuccessUi();
                                } else {
                                    connectSendCodeFailUi(failMsg);
                                }

                            }
                        });

            }
        });

    }


    //fragment切换的时候广播
    BroadcastReceiver receiverHot = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (MYACTION_UPDATE.equals(intent.getAction())) {

                String updateteaid = intent.getStringExtra("updateteaid");

                String updateVoice = intent.getStringExtra("updateVoice");

                if(!TextUtils.isEmpty(updateVoice)&&updateVoice.equals("1")){

                    unconnectUi();

                    return;
                }




                Log.i("onReceive", "change hot...");
                if (!TextUtils.isEmpty(updateteaid)) {
                    if (ChooseTeaActivity.chooseTeaValue != null) {
                        teaname=ChooseTeaActivity.chooseTeaValue;
//                        tea_name_id.setText(ChooseTeaActivity.chooseTeaValue);

                        if(teaname.length()>4){

                            tea_name_id.setText(teaname.substring(0,4)+"\n"+teaname.substring(4));

                        }else{
                            tea_name_id.setText(teaname);
                        }
                        tea_status_id.setText("");//清空上一杯缓存

                    }
                } else {
                    if (firstchange > 0) {

                        MainApp mainApp = (MainApp) getActivity().getApplicationContext();

                        if (mainApp.boolupdateSuccess == 1) {
                            deviceVersion = null;
                        } else if (mainApp.boolupdateSuccess == 2) {
                            if (!TextUtils.isEmpty(sysUpdateVersion)) {
                                configPref.userDeviceVersion().put(sysUpdateVersion);
                            }
                            needupdate = mainApp.boolupdateSuccess == 2 ? false : true;

                            update_device_id.setBackgroundResource(R.drawable.cm_update_device);
                        }


                        changeui();


                    }
                }
            }
        }
    };


    public static String MYACTION_UPDATE = "com.changehot.broadcast";


    public void perssion_func(View tab_paocha, String comment, String text1, String text2) {
        new One_Permission_Popwindow().showPopwindow(getActivity(), tab_paocha, comment, text1, text2, new One_Permission_Popwindow.CallBackPayWindow() {
            @Override
            public void handleCallBackChangeUser() {
//                Util.outLogin(getActivity(), configPref);
//                Util.startActivityNewTask(getActivity(), WelcomeActivity_.class);
//                getActivity().finish();

                if (mustUpdate) {
                    mustUpdate = false;
                    Intent intent = new Intent(getActivity(), DeviceActivityTea.class);
                    intent.putExtra("MAC_DEVICE", blindDeviceId);
                    String version = configPref.userDeviceVersion().get();
                    intent.putExtra("upversion", sysUpdateVersion);
                    intent.putExtra("deviceVersionObj", configPref.deviceUpdateInfo().get());
                    intent.putExtra("sysdownloadsize", sysdownloadsize);


                    startActivity(intent);
                } else {
                    Intent intent = new Intent(getActivity(), DeviceUpdateTwoActivity_.class);

                    intent.putExtra("sysdownloadsize", sysdownloadsize);
                    intent.putExtra("upversion", sysUpdateVersion);

                    startActivity(intent);
                }


            }

            @Override
            public void handleCallBackBindDevice() {
//                Util.startActivityNewTask(MainActivity.this, WelcomeActivity_.class);
//                finish();


            }
        });

    }


    //蓝牙监听广播
    private IntentFilter makeFilter() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        return filter;
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.e("HOT", "onReceive---------");
            switch (intent.getAction()) {
                case BluetoothAdapter.ACTION_STATE_CHANGED:
                    MainApp mainApp = (MainApp) getActivity().getApplicationContext();
                    int blueState = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, 0);
                    switch (blueState) {
                        case BluetoothAdapter.STATE_TURNING_ON:
                            Log.e("HOT", "onReceive---------STATE_TURNING_ON");
                            if (showcontnect) {
                                bluetooth_rel.setVisibility(View.GONE);
                                unconnectUi();
                            }


                            break;
                        case BluetoothAdapter.STATE_ON:
                            Log.e("HOT", "onReceive---------STATE_ON");

                            break;
                        case BluetoothAdapter.STATE_TURNING_OFF://蓝牙关掉---切换到没有连接页面
                            Log.e("HOT", "onReceive---------STATE_TURNING_OFF");
                            if (mainApp.boolupdateSuccess == 2 || mainApp.boolupdateSuccess == 1) {
                                openBle();


                            }

                            unconnectUi();
//                            BleUtil.toReset(mContext);
                            break;
                        case BluetoothAdapter.STATE_OFF:
//                            if (showcontnect) {
//                                bluetooth_rel.setVisibility(View.VISIBLE);
//                                //应该提示打开蓝牙
//                            }

                            Log.e("HOT", "onReceive---------STATE_OFF");
//                            BleUtil.toReset(mContext);
                            break;
                    }
                    break;
            }
        }
    };


    public void openBle() {

        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter
                .getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            Toast.makeText(getActivity(), "本机没有找到蓝牙硬件或驱动！", Toast.LENGTH_SHORT).show();
        }
        // 如果本地蓝牙没有开启，则开启
        if (!mBluetoothAdapter.isEnabled()) {
            // 用enable()方法来开启，无需询问用户(实惠无声息的开启蓝牙设备),这时就需要用到android.permission.BLUETOOTH_ADMIN权限。
            mBluetoothAdapter.enable();


        }


    }


}