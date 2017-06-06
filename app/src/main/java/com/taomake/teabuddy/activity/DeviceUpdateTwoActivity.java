package com.taomake.teabuddy.activity;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.taomake.teabuddy.R;
import com.taomake.teabuddy.base.MainApp;
import com.taomake.teabuddy.component.FoxProgressbarInterface;
import com.taomake.teabuddy.network.RowMessageHandler;
import com.taomake.teabuddy.prefs.ConfigPref_;
import com.taomake.teabuddy.sensoractivity.DeviceActivityTea;
import com.taomake.teabuddy.util.MyStringUtils;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.sharedpreferences.Pref;

import java.util.ArrayList;
import java.util.List;

import quinticble.QuinticBleAPISdkBase;
import quinticble.QuinticCallbackTea;
import quinticble.QuinticDeviceFactoryTea;
import quinticble.QuinticDeviceTea;
import quinticble.QuinticException;

/**
 * Created by zhang on 2015/8/7.
 */
@EActivity(R.layout.cm_device_update_two)
public class DeviceUpdateTwoActivity extends BaseActivity {

    @Pref
    ConfigPref_ configPref;
//    @ViewById(R.id.no_data_id)
//    RelativeLayout no_data_id;



    int y = 0;
    int limit = 10;


    @Click(R.id.left_title_line)
    void onLeftTitleLine() {

        finish();

    }
    @Click(R.id.one_text_line)
    void onone_text_line() {

//        Util.startActivity(DeviceUpdateTwoActivity.this, DeviceActivityTea.class);
//
//        finish();
//        getLeservice();
connectFindDevice();
    }






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        setContentView(R.layout.design_personal);
//        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

//        initUi();


    }


    public void initUi() {

        RelativeLayout main_title_id = (RelativeLayout) findViewById(R.id.main_title_id);
//        main_title_id.setBackgroundColor(getResources().getColor(R.color.white));

        ImageView left_title_icon = (ImageView) findViewById(R.id.left_title_icon);
        left_title_icon.setVisibility(View.VISIBLE);
//        left_title_icon.setImageDrawable(getResources().getDrawable(R.drawable.topbar_btn_bank));

        ImageView right_title_icon = (ImageView) findViewById(R.id.right_title_icon);
        right_title_icon.setVisibility(View.GONE);

        TextView title = (TextView) findViewById(R.id.title);
        title.setVisibility(View.VISIBLE);
        title.setText("固件更新");
        title.setTextColor(getResources().getColor(R.color.white));
        View title_line_id = (View) findViewById(R.id.title_line_id);
        title_line_id.setVisibility(View.GONE);

        blindDeviceId = configPref.userDeviceMac().get();
        blindDeviceId = MyStringUtils.macStringToUpper(blindDeviceId);
        Log.e("blindDeviceId:", blindDeviceId);


        TextView tea_os_version = (TextView) findViewById(R.id.tea_os_version);
        TextView text_db_size = (TextView) findViewById(R.id.text_db_size);


        String  sysdownloadsize=getIntent().getStringExtra("sysdownloadsize");
        String  version=getIntent().getStringExtra("upversion");

//        String version=configPref.userDeviceVersion().get();

        if(!TextUtils.isEmpty(version)) {
            tea_os_version.setText("Cha OS " +version);
            text_db_size.setText(sysdownloadsize);
        }else{
            tea_os_version.setText("Cha OS 1.0");
            text_db_size.setText("0kb");

        }

//        initdata();
    }


    @AfterViews
    void init() {
        initUi();
    }


    @Override
    protected void initActivityName() {
        activityName = DeviceUpdateTwoActivity.class.getName();
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

    public void getMessageListFromServerForMsg() {
        foxProgressbarInterface = new FoxProgressbarInterface();
        foxProgressbarInterface.startProgressBar(this, "加载中...");

//        ProtocolUtil.messageListFunction(this, new MessageListHandler(), configPref.userToken().get(), null);//devno 空表示所有


    }


    private class MessageListHandler extends RowMessageHandler {
        @Override
        protected void handleResp(String resp) {
            messageListHandler(resp);
        }
    }


    public void messageListHandler(String resp) {
        foxProgressbarInterface.stopProgressBar();
        if (resp != null && !resp.equals("")) {


//            MessageListJson baseJson = new Gson().fromJson(resp, MessageListJson.class);
//            if (baseJson.retCode.equals(Constant.RES_SUCCESS)) {
//
//                //测试数据
////            baseJson.rows = (ArrayList<MessageJson>) setTestData();
//
//
//                if (baseJson.rows != null && baseJson.rows.size() > 0) {//列表
//
//                    if (pageNum == 1) {
//                        designRoomInfos.clear();
//                        designRoomInfos.addAll(baseJson.rows);
//                    } else {
//                        designRoomInfos.addAll(baseJson.rows);
//                    }
//
//                }
//
//
//                if (designRoomInfos.size() <= 0 && pageNum == 1) {
//                    pullToRefreshListView.setVisibility(View.GONE);
//                    no_data_id.setVisibility(View.VISIBLE);
//                } else {
//                    pullToRefreshListView.setVisibility(View.VISIBLE);
//                    no_data_id.setVisibility(View.GONE);
//
//                    adapterHomeDesignListView.notifyDataSetChanged();
//                    // Call onRefreshComplete when the list has been refreshed.
//                    pullToRefreshListView.onRefreshComplete();
//                    pullToRefreshListView.getRefreshableView().setSelection(y);
//
//
//                }
//            }

        }
    }




    public List<String > setTestData() {
        List<String> jobJsons = new ArrayList<String>();
        for (int i = 1; i < 10; i++) {




            jobJsons.add("打招呼");

        }

        return jobJsons;
    }

    /**
     * 功能：查找设备
     */
    private String blindDeviceId;
    private QuinticDeviceTea resultDeviceAll;
    private Integer countError = 0;

    public void connectFindDevice() {
//                foxProgressbarInterface = new FoxProgressbarInterface();
//
//        foxProgressbarInterface.startProgressBar(getActivity(), "蓝牙读取中...");

        if (MyStringUtils.isNotNullAndEmpty(QuinticBleAPISdkBase.resultDevice)) {
            resultDeviceAll = QuinticBleAPISdkBase.resultDevice;
            // ************处理动作
            getbatteryLevel();
        } else {
            final Context context = DeviceUpdateTwoActivity.this;
            QuinticDeviceFactoryTea quinticDeviceFactory = QuinticBleAPISdkBase
                    .getInstanceFactory(context);

            quinticDeviceFactory.findDevice(blindDeviceId,
                    new QuinticCallbackTea<QuinticDeviceTea>() {

                        @Override
                        public void onComplete(final QuinticDeviceTea resultDevice) {
                            super.onComplete(resultDevice);
                            new Handler(context.getMainLooper())
                                    .post(new Runnable() {
                                        @Override
                                        public void run() {
                                            resultDeviceAll = resultDevice;
                                            QuinticBleAPISdkBase.resultDevice = resultDeviceAll;
                                            // ************处理动作
                                            getbatteryLevel();

                                        }
                                    });
                        }

                        @Override
                        public void onError(final QuinticException ex) {
                            new Handler(context.getMainLooper())
                                    .post(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (countError < 1) {
                                                Log.d("connectFindDevice ex",
                                                        ex.getCode()
                                                                + ""
                                                                + ex.getMessage());
                                                connectFindDevice();
                                                countError++;
                                            } else {
//                                                unconnectUi();
                                                // *****************连接失败
//                                                Util.Toast(context,
//                                                        "");
                                            }
                                        }
                                    });
                        }
                    });
        }
    }


    int batteryLevelValue = 0;
    public   void closeBle() {

        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter
                .getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            Toast.makeText(DeviceUpdateTwoActivity.this, "本机没有找到蓝牙硬件或驱动！", Toast.LENGTH_SHORT).show();
        }
        // 如果本地蓝牙没有开启，则开启
        if (mBluetoothAdapter.isEnabled()) {
            // 用enable()方法来开启，无需询问用户(实惠无声息的开启蓝牙设备),这时就需要用到android.permission.BLUETOOTH_ADMIN权限。
            mBluetoothAdapter.disable();
        }


    }
    public void getbatteryLevel() {
        if (resultDeviceAll == null) return;
        String code = "EB16";

        resultDeviceAll.sendCommonCode(code, new QuinticCallbackTea<String>() {
            @Override
            public void onError(QuinticException ex) {
                super.onError(ex);
//                connectSendCodeFailUi("电量查询失败");

            }

            @Override
            public void onComplete(final String result) {
                super.onComplete(result);
                if (result == null) {
//                    connectSendCodeFailUi("电量查询失败");

                    return;
                }
                new Handler(getMainLooper())
                        .post(new Runnable() {
                            @Override
                            public void run() {
//BACK 0A 04 01 53result
                                String trimResult = result.replace(" ", "");
                                if (trimResult.contains("eb1601")) {
                                    MainApp mainApp=(MainApp)getApplicationContext();

                                    mainApp.boolupdateSuccess=1;

//                                    QuinticBleAPISdkBase.getInstanceFactory(DeviceUpdateTwoActivity.this).conn.disconnect();
//
//                                    try {
//                                        Thread.sleep(3000);
//                                    } catch (InterruptedException e) {
//                                        e.printStackTrace();
//                                    }
                                    closeBle();
//                                    Util.startActivity(DeviceUpdateTwoActivity.this, DeviceActivityTea.class);
                                    Intent intent = new Intent(DeviceUpdateTwoActivity.this, DeviceActivityTea.class);
                                    intent.putExtra("deviceVersionObj", configPref.deviceUpdateInfo().get());

                                    String  version=getIntent().getStringExtra("upversion");
                                    intent.putExtra("upversion",version);

                                    intent.putExtra("MAC_DEVICE",blindDeviceId);
                                    startActivity(intent);
                                    finish();

//                                    byte[] data = QuinticCommon.stringToBytes(trimResult);
//                                    batteryLevelValue = QuinticCommon.unsignedByteToInt(data[3]);
//                                    Log.d("当前电量", batteryLevelValue + "");
//                                    getTeaStatus();
                                }
//                                else {
//                                    connectSendCodeFailUi("电量查询失败");
//                                }

                            }
                        });

            }
        });

    }




}




