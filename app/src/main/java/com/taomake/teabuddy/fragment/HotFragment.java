package com.taomake.teabuddy.fragment;

/**
 * Created by foxcen on 16/7/29.
 */

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.taomake.teabuddy.R;
import com.taomake.teabuddy.activity.ChooseTeaActivity;
import com.taomake.teabuddy.activity.ChooseTeaActivity_;
import com.taomake.teabuddy.activity.DeviceManagerActivity_;
import com.taomake.teabuddy.activity.DeviceUpdateActivity_;
import com.taomake.teabuddy.activity.DeviceUpdateTwoActivity_;
import com.taomake.teabuddy.activity.WebViewActivity_;
import com.taomake.teabuddy.component.DynamicWave;
import com.taomake.teabuddy.component.FoxProgressbarInterface;
import com.taomake.teabuddy.component.Permission_Popwindow;
import com.taomake.teabuddy.network.ProtocolUtil;
import com.taomake.teabuddy.network.RowMessageHandler;
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

import quinticble.QuinticBleAPISdkBase;
import quinticble.QuinticCallbackTea;
import quinticble.QuinticCommon;
import quinticble.QuinticDeviceFactoryTea;
import quinticble.QuinticDeviceTea;
import quinticble.QuinticException;

@EFragment(R.layout.cm_tea_main)

public class HotFragment extends Fragment {
    @Pref
    ConfigPref_ configPref;
    @ViewById(R.id.tea_name_id)
    TextView tea_name_id;
    @ViewById(R.id.tea_name_line)
    LinearLayout tea_name_line;

    ImageView update_device_id;





    @Click(R.id.pc_tea_choose_line)
    void onpc_tea_choose_line() {
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

        if (needupdate) {
            Util.startActivity(getActivity(), DeviceUpdateTwoActivity_.class);

        } else {
            Util.startActivity(getActivity(), DeviceUpdateActivity_.class);

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

                    connectSuccessUi();

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

                    connectSuccessUi();

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


//        updateUiResume();

        return chatView;
    }

    int firstchange = 0;

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        getActivity().unregisterReceiver(receiver);
        getActivity().unregisterReceiver(receiverHot);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (!isVisibleToUser) {
//            Log.d("HotFragment", "不可见");

        } else {
//
            changeui();
            firstchange++;

        }
    }

    public void changeui() {


        connectFindDevice();

        Log.d("HotFragment", "当前可见");
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
        pc_connecting_rel = (RelativeLayout) view.findViewById(R.id.pc_connecting_rel);
        pc_unconnected_rel = (RelativeLayout) view.findViewById(R.id.pc_unconnected_rel);
        pc_tryagain_rel = (RelativeLayout) view.findViewById(R.id.pc_tryagain_rel);
        connect_status_commnet_id = (TextView) view.findViewById(R.id.connect_status_commnet_id);

        tea_bat_img_id = (ImageView) view.findViewById(R.id.tea_bat_img_id);


        tea_sum_cub_text_id = (TextView) view.findViewById(R.id.tea_sum_cub_text_id);
        tea_tryagainconnect_comment = (TextView) view.findViewById(R.id.tea_tryagainconnect_comment);
        tea_unconnect_comment = (TextView) view.findViewById(R.id.tea_unconnect_comment);

        tea_sum_cub_text_id.setTextSize(getResources().getDimension(R.dimen.font_100));
        update_device_id = (ImageView) view.findViewById(R.id.update_device_id);
        update_device_id.setBackgroundResource(R.drawable.cm_update_device);

        tea_cub_dynamicwave = (DynamicWave) view.findViewById(R.id.tea_cub_dynamicwave);
        tea_cub_gif = (LinearLayout) view.findViewById(R.id.tea_cub_gif);
        tea_cub_dynamicwave.setVisibility(View.GONE);
        tea_cub_gif.setVisibility(View.GONE);

        cicrle_line_id = (LinearLayout) view.findViewById(R.id.cicrle_line_id);

        tea_unconnect_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tryconnectUi();
            }
        });
        tea_tryagainconnect_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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

    public void connectUi() {
        pc_connecting_rel.setVisibility(View.VISIBLE);
        pc_unconnected_rel.setVisibility(View.GONE);
        pc_tryagain_rel.setVisibility(View.GONE);

    }


    public static boolean canChangePager = true;

    public void unconnectUi() {
        cicrle_line_id.setVisibility(View.GONE);
        canChangePager = false;
        pc_connecting_rel.setVisibility(View.GONE);
        pc_unconnected_rel.setVisibility(View.VISIBLE);
        pc_tryagain_rel.setVisibility(View.GONE);


    }

    public void tryconnectUi() {
        pc_connecting_rel.setVisibility(View.GONE);
        pc_unconnected_rel.setVisibility(View.GONE);
        pc_tryagain_rel.setVisibility(View.VISIBLE);

    }


    public void connectSuccessUi() {
        cicrle_line_id.setVisibility(View.VISIBLE);

        connectUi();
        canChangePager = true;

        if (foxProgressbarInterface != null) {
            foxProgressbarInterface.stopProgressBar();
        }

        connect_status_commnet_id.setVisibility(View.GONE);
        changeBatImage();//电池电量切换

        if (teaingCountValue < 10) {
            tea_sum_cub_text_id.setText(teaingCountValue + "");
//            tea_sum_cub_text_id.setTextSize(getResources().getDimension(R.dimen.font_140));

        } else {
            tea_sum_cub_text_id.setText(teaingCountValue + "");
            tea_sum_cub_text_id.setTextSize(getResources().getDimension(R.dimen.font_60));
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

    }

    public void connectSendCodeFailUi(String msg) {
        if (foxProgressbarInterface != null) {
            foxProgressbarInterface.stopProgressBar();
        }
        connect_status_commnet_id.setText(msg);

        unconnectUi();
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
    FoxProgressbarInterface foxProgressbarInterface;

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


    public void getTeaInfoByUnionidHandler(String resp) {
//        foxProgressbarInterface.stopProgressBar();
        if (resp != null && !resp.equals("")) {

            //解析返回json 数据


            GetTeaInfoJson baseJson = new Gson().fromJson(resp, GetTeaInfoJson.class);
            if ((baseJson.rcode + "").equals(Constant.RES_SUCCESS)) {

                //获取到茶种名字
                String teaname = baseJson.obj.czname;
                if (teaname == null || "".equals(teaname)) {
                    tea_name_line.setVisibility(View.VISIBLE);
                    tea_name_id.setText("默认茶种");
                } else {
                    teaname = MyStringUtils.decodeUnicode(teaname);
                    Log.i("teaname", teaname);
                    tea_name_line.setVisibility(View.VISIBLE);
                    tea_name_id.setText(teaname);
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
            Util.Toast(getActivity(), "设备当前没有版本号");

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


            DeviceVersionJson baseJson = new Gson().fromJson(resp, DeviceVersionJson.class);
            if ((baseJson.rcode + "").equals(Constant.RES_SUCCESS)) {

                List<DeviceVersionObj> deviceVersionObjList = baseJson.obj;
                DeviceVersionObj deviceVersionObj = null;
                if (deviceVersionObjList != null) {
                    deviceVersionObj = deviceVersionObjList.get(0);
                }
                if (deviceVersionObj != null && deviceVersionObj.url != null && !deviceVersionObj.url.equals("")) {


                    //将固件升级信息保存起来

//                    configPref.


                    needupdate = true;
                    configPref.deviceUpdateInfo().put(new Gson().toJson(deviceVersionObj));
                    update_device_id.setBackgroundResource(R.drawable.cm_update_device_c);
                    perssion_func(update_device_id, "您的茶密有固件了", "立即更新", "取消");

                } else {


                    needupdate = false;

                    update_device_id.setBackgroundResource(R.drawable.cm_update_device);

                }


//                Log.d("开始上传Log", "上传成功");


            }

        }
    }
    //************************服务器接口*********************************


    //************************蓝牙操作*********************************

    /**
     * 功能：查找设备
     */
    private String blindDeviceId;
    private QuinticDeviceTea resultDeviceAll;
    private Integer countError = 0;


    public void closeProgress() {
        if (foxProgressbarInterface != null) {
            foxProgressbarInterface.stopProgressBar();
        }
    }

    boolean mustUpdate = false;

    public void connectFindDevice() {
        blindDeviceId = configPref.userDeviceMac().get();
        blindDeviceId = MyStringUtils.macStringToUpper(blindDeviceId);
        Log.e("blindDeviceId:", blindDeviceId);
//            getTeaInfoByUnionid();
        foxProgressbarInterface = new FoxProgressbarInterface();

        foxProgressbarInterface.startProgressBar(getActivity(), "主人茶密连接中...");

        if (MyStringUtils.isNotNullAndEmpty(QuinticBleAPISdkBase.resultDevice)) {
            resultDeviceAll = QuinticBleAPISdkBase.resultDevice;
            // ************处理动作
            getCzidBle();
        } else {
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
                                            Util.Toast(getActivity(), "将立马前往升级");

                                            mustUpdate = true;
                                            checkDeviceUpdateToServer();

//                                            try {
//                                                Thread.sleep(3000);
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
                                            closeProgress();
                                            resultDeviceAll = resultDevice;
                                            QuinticBleAPISdkBase.resultDevice = resultDeviceAll;
                                            // ************处理动作
                                            getCzidBle();

                                        }
                                    });
                        }

                        @Override
                        public void onError(final QuinticException ex) {
                            new Handler(context.getMainLooper())
                                    .post(new Runnable() {
                                        @Override
                                        public void run() {
//                                            if (countError < 1) {
//                                                Log.d("connectFindDevice ex",
//                                                        ex.getCode()
//                                                                + ""
//                                                                + ex.getMessage());
////                                                connectFindDevice();
//                                                countError++;
//                                            } else {
                                            closeProgress();
                                            unconnectUi();

                                            // *****************连接失败
//                                                Util.Toast(context,
//                                                        "");
//                                            }
                                        }
                                    });
                        }
                    });
        }
    }


    public void getCzidBle() {
        if (resultDeviceAll == null) return;
        String code = "EA05";

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
                                String trimResult = result.replace(" ", "");
                                if (trimResult.contains("ea0501")) {
                                    byte[] data = QuinticCommon.stringToBytes(trimResult);
                                    int czid = (QuinticCommon.unsignedByteToInt(data[3]) << 32) + (QuinticCommon.unsignedByteToInt(data[4]) << 24) +
                                            (QuinticCommon.unsignedByteToInt(data[5]) << 16) + (QuinticCommon.unsignedByteToInt(data[6]) << 8) +
                                            QuinticCommon.unsignedByteToInt(data[7]);
                                    Log.d("茶种id查询", batteryLevelValue + "");

                                    getTeaInfoByUnionid(czid + "");

                                    getbatteryLevel();
                                } else {
                                    connectSendCodeFailUi("茶种id查询失败");
                                }

                            }
                        });

            }
        });

    }


    int batteryLevelValue = 0;

    public void getbatteryLevel() {
        if (resultDeviceAll == null) return;
        String code = "EA04";

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
                                    getTeaStatus();
                                } else {
                                    connectSendCodeFailUi("电量查询失败");
                                }

                            }
                        });

            }
        });

    }


    int teaStatusValue = 0;//0非泡茶  1泡茶中
    int teaingCountValue = 0;//第几泡
    int teaingTimeValue = 0;//当前泡茶第几秒
    int teaingIsNull = 1;//是否空杯 1空杯  0 非空

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

                                    connectSuccessUi();
                                    if (deviceVersion == null) {
                                        getDeviceUpdate();
                                    } else {
                                        getLogHistory();//同时获取log日志  2.并且向网络判断当前版本更新ui

                                    }

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
                                    double versionD = (hVersion * 100 + lVersion) / 100;
                                    Log.d("当前固件版本versionD", versionD + "");

                                    deviceVersion = versionD + "";

                                    configPref.userDeviceVersion().put(deviceVersion);
                                    checkDeviceUpdateToServer();
                                    getLogHistory();//同时获取log日志  2.并且向网络判断当前版本更新ui


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

//                                    connectSuccessUi();
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

                String updateteaid=intent.getStringExtra("updateteaid");

                Log.i("onReceive", "change hot...");
                if(!TextUtils.isEmpty(updateteaid)){
                    if (ChooseTeaActivity.chooseTeaValue != null) {
                        tea_name_id.setText(ChooseTeaActivity.chooseTeaValue);


                    }
                }else {
                    if (firstchange > 0) {
                        changeui();


                    }
                }
            }
        }
    };


    public static String MYACTION_UPDATE = "com.changehot.broadcast";


    public void perssion_func(View tab_paocha, String comment, String text1, String text2) {
        new Permission_Popwindow().showPopwindow(getActivity(), tab_paocha, comment, text1, text2, new Permission_Popwindow.CallBackPayWindow() {
            @Override
            public void handleCallBackChangeUser() {
//                Util.outLogin(getActivity(), configPref);
//                Util.startActivityNewTask(getActivity(), WelcomeActivity_.class);
//                getActivity().finish();

                if (mustUpdate) {
                    mustUpdate = false;
                    Intent intent = new Intent(getActivity(), DeviceActivityTea.class);
                    intent.putExtra("MAC_DEVICE", blindDeviceId);
                    intent.putExtra("deviceVersionObj", configPref.deviceUpdateInfo().get());

                    startActivity(intent);
                } else {
                    Util.startActivity(getActivity(), DeviceUpdateTwoActivity_.class);

                }


            }

            @Override
            public void handleCallBackBindDevice() {
//                Util.startActivityNewTask(MainActivity.this, WelcomeActivity_.class);
//                finish();


            }
        });

    }
}