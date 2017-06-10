package com.taomake.teabuddy.activity;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.taomake.teabuddy.R;
import com.taomake.teabuddy.component.FoxProgressbarInterface;
import com.taomake.teabuddy.network.ProtocolUtil;
import com.taomake.teabuddy.network.RowMessageHandler;
import com.taomake.teabuddy.object.BaseJson;
import com.taomake.teabuddy.prefs.ConfigPref_;
import com.taomake.teabuddy.util.Constant;
import com.taomake.teabuddy.util.MyStringUtils;
import com.taomake.teabuddy.util.Util;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.sharedpreferences.Pref;

import quinticble.QuinticBleAPISdkBase;
import quinticble.QuinticCallbackTea;
import quinticble.QuinticDeviceFactoryTea;
import quinticble.QuinticDeviceTea;
import quinticble.QuinticException;

/**
 * Created by zhang on 2015/8/7.
 */
@EActivity(R.layout.cm_onlinedebug_two)
public class SetOnlineDebugTwoActivity extends BaseActivity {

    @Pref
    ConfigPref_ configPref;

    //    @ViewById(R.id.title_success_line_id)
    LinearLayout title_success_line_id;
    //    @ViewById(R.id.title_line_id)
    LinearLayout title_line_debug_id;


    int y = 0;
    int limit = 10;


    @Click(R.id.left_title_line)
    void onLeftTitleLine() {

        finish();

    }


    @Click(R.id.update_process_rel)
    void ontitle_line_id() {//开始诊断

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
        left_title_icon.setImageDrawable(getResources().getDrawable(R.drawable.topbar_btn_bank));

        ImageView right_title_icon = (ImageView) findViewById(R.id.right_title_icon);
        right_title_icon.setVisibility(View.GONE);

        TextView title = (TextView) findViewById(R.id.title);
        title.setVisibility(View.VISIBLE);
        title.setText("在线诊断");
        title.setTextColor(getResources().getColor(R.color.black));
        View title_line_id = (View) findViewById(R.id.title_line_id);
        title_line_id.setVisibility(View.GONE);


        LinearLayout right_title_line = (LinearLayout) findViewById(R.id.right_title_line);

        right_title_line.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                finish();
            }
        });
        title_success_line_id = (LinearLayout) findViewById(R.id.title_success_line_id);
        title_line_debug_id = (LinearLayout) findViewById(R.id.title_line_debug_id);

        title_success_line_id.setVisibility(View.GONE);
        title_line_debug_id.setVisibility(View.VISIBLE);

        initdata();

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBarTextview=(TextView) findViewById(R.id.progressBarTextview);
        progressBar.setProgress(1);
        progressBarTextview.setText("正在检测"+process+"%");
        writehandler.post(runnable);
        //开始模拟13秒100%的进程加载
    }
    TextView  progressBarTextview;
    private ProgressBar progressBar;

    final Handler writehandler = new Handler();

    int process = 1;
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            // TODO Auto-generated method stub
            //要做的事情
            if (process < 100) {

                progressBar.setProgress(process);
                progressBarTextview.setText("正在检测" + process + "%");

                writehandler.postDelayed(this, 130);
                process = process + 1;
            } else {
//开始查询固件 诊断数据并且上报

                writehandler.removeCallbacks(runnable);
                connectFindDevice();
            }
        }
    };


    @AfterViews
    void init() {
        initUi();
    }


    @Override
    protected void initActivityName() {
        activityName = SetOnlineDebugTwoActivity.class.getName();
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

    }


    //**********获取筛选的后的list***************/
    FoxProgressbarInterface foxProgressbarInterface;

    public void sendDebugLog() {


        foxProgressbarInterface = new FoxProgressbarInterface();
        foxProgressbarInterface.startProgressBar(this, "加载中...");
        ProtocolUtil.sendDebugLogToServer(this, new DelTeaListInfoHandler(), configPref.userDeviceMac().get(), logResult);//devno 空表示所有


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
            BaseJson dbRecordsJson = new Gson().fromJson(resp, BaseJson.class);
            if ((dbRecordsJson.rcode + "").equals(Constant.RES_SUCCESS)) {
//                Util.Toast(SetOnlineDebugTwoActivity.this, "诊断成功,请等候茶密客服反馈");
//                finish();


                title_success_line_id.setVisibility(View.VISIBLE);
                title_line_debug_id.setVisibility(View.GONE);

            }
        }
    }


    //************************蓝牙操作*********************************

    /**
     * 功能：查找设备
     */
    private String blindDeviceId;
    private QuinticDeviceTea resultDeviceAll;
    private Integer countError = 0;

    public void connectFindDevice() {
//        foxProgressbarInterface = new FoxProgressbarInterface();
//
//        foxProgressbarInterface.startProgressBar(getActivity(), "蓝牙读取中...");
        blindDeviceId = configPref.userDeviceMac().get();
        blindDeviceId = MyStringUtils.macStringToUpper(blindDeviceId);
        Log.e("blindDeviceId:", blindDeviceId);
        if (MyStringUtils.isNotNullAndEmpty(QuinticBleAPISdkBase.resultDevice)) {
            resultDeviceAll = QuinticBleAPISdkBase.resultDevice;
            // ************处理动作
            getLogHistory();
        } else {
            final Context context = SetOnlineDebugTwoActivity.this;
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
                                            getLogHistory();

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
//                                            unconnectUi();
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

    public void connectSendCodeFailUi(String failMsg) {
        Log.e("getLogHistory", failMsg);
        Util.Toast(SetOnlineDebugTwoActivity.this,"诊断数据查询没有数据",null);
        finish();

    }

    private String logResult = null;

    public void getLogHistory() {
        if (resultDeviceAll == null) return;
        String code = "EA12";
        final String failMsg = "诊断数据查询失败";

        resultDeviceAll.sendCommonCode(code, new QuinticCallbackTea<String>() {
            @Override
            public void onError(QuinticException ex) {
                super.onError(ex);
                connectSendCodeFailUi(failMsg);

            }

            @Override
            public void onComplete(final String result) {
                super.onComplete(result);
                if (result == null) {
                    connectSendCodeFailUi(failMsg);

                    return;
                }
                new Handler(getMainLooper())
                        .post(new Runnable() {
                            @Override
                            public void run() {
//BACK 0A 10 01
                                String trimResult = result.replace(" ", "");

                                if (trimResult.contains("ea12")) {

                                    logResult = trimResult;
                                    sendDebugLog();
//                                    getMinePersonInfoFunc();

//                                    connectSuccessUi();
                                } else {
                                    connectSendCodeFailUi(failMsg);
                                }

                            }
                        });

            }
        });

    }


}




