package com.taomake.teabuddy.fragment;

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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.taomake.teabuddy.R;
import com.taomake.teabuddy.activity.BeginRecordActivity_;
import com.taomake.teabuddy.activity.MyCollectRecordsActivity_;
import com.taomake.teabuddy.activity.MyCreateRecordsActivity_;
import com.taomake.teabuddy.activity.MyGFRecordsActivity_;
import com.taomake.teabuddy.activity.WelcomeActivity_;
import com.taomake.teabuddy.base.MainApp;
import com.taomake.teabuddy.component.FoxProgressbarInterface;
import com.taomake.teabuddy.component.One_Permission_Popwindow;
import com.taomake.teabuddy.prefs.ConfigPref_;
import com.taomake.teabuddy.util.MyStringUtils;
import com.taomake.teabuddy.util.Util;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;

import quinticble.QuinticBleAPISdkBase;
import quinticble.QuinticCallbackTea;
import quinticble.QuinticDeviceTea;
import quinticble.QuinticException;

@EFragment(R.layout.home_design_fragment)
public class HomeTabFragment extends Fragment{

    @Pref
    ConfigPref_ configPref;


    @ViewById(R.id.main_title_line)
    LinearLayout main_title_line;
//    @ViewById(R.id.main_title_name_id)
    TextView main_title_name_id;
    @Click(R.id.main_title_line)
    void onmain_title_line() {
        setVoicePlay();

    }

    @Click(R.id.begin_czuo_line)
    void onbegin_czuo_line() {
        if(perssion_func())
        Util.startActivity(getActivity(), BeginRecordActivity_.class);
//        Util.startActivity(getActivity(), MyControlActivity_.class);

    }

    @Click(R.id.begin_myzhizuo_line)
    void onbegin_myzhizuo_line() {

        Util.startActivity(getActivity(), MyCreateRecordsActivity_.class);

    }
    public boolean perssion_func() {
        String unionid=configPref.userDeviceMac().get();

        if(unionid!=null&&!unionid.equals("")) {
           return true;
        }else{
            new One_Permission_Popwindow().showPopwindow(getActivity(), main_title_line,null,null,null, new One_Permission_Popwindow.CallBackPayWindow() {
                @Override
                public void handleCallBackChangeUser() {
                    Util.outLogin(getActivity(), configPref);
                    Util.startActivityNewTask(getActivity(), WelcomeActivity_.class);
                    getActivity().finish();


                }

                @Override
                public void handleCallBackBindDevice() {
                    Util.startActivityNewTask(getActivity(), WelcomeActivity_.class);
                    getActivity().finish();


                }
            });
            return false;

        }
    }
        @Click(R.id.begin_gf_line)
    void onbegin_gf_line() {


            if(perssion_func())
        Util.startActivity(getActivity(), MyGFRecordsActivity_.class);

    }
    @Click(R.id.begin_mycollect_line)
    void onbegin_mycollect_line() {
        if(perssion_func())
        Util.startActivity(getActivity(), MyCollectRecordsActivity_.class);

    }

    public static     String MYACTION_UPDATE_HOME = "com.changehome.broadcast";

    //fragment切换的时候广播
    BroadcastReceiver receiverHome = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (MYACTION_UPDATE_HOME.equals(intent.getAction())) {
                Log.i("onReceive", "change HOME...");

         String updatetype=       intent.getStringExtra("updatetype");
                if(TextUtils.isEmpty(updatetype)){
                    connectFindDevice();

                }else{
                    boolInitVoice=false;
                    connectSendCodeSuccessUi();
                }

            }
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(receiverHome);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View chatView = inflater.inflate(R.layout.home_design_fragment, container, false);

        main_title_name_id=(TextView) chatView.findViewById(R.id.main_title_name_id);
        IntentFilter filterhome = new IntentFilter();
        filterhome.addAction(MYACTION_UPDATE_HOME);
        getActivity().registerReceiver(receiverHome, filterhome);


//        if(TextUtils.isEmpty(FileUtilQq.existQQshareIcon())){
//            new Handler().post(new Runnable() {
//                @Override
//                public void run() {
//               Log.e("qqshare","下载qq图标到本地");
//                    Bitmap bmp= BitmapFactory.decodeResource(getResources(), R.drawable.app_logo);
//
//                    FileUtilQq.saveBitmap(bmp);
//
//                }
//            });
//        }


        return chatView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        init();
    }
    /**
     * 功能：查找设备
     */
    private String blindDeviceId;
    private QuinticDeviceTea resultDeviceAll;
    private Integer countError = 0;


    public void  closeProgress() {
        if (foxProgressbarInterface != null) {
            foxProgressbarInterface.stopProgressBar();
        }
    }

    FoxProgressbarInterface foxProgressbarInterface;

    public void connectFindDevice() {
        if(!MyStringUtils.isopenBluetooth(getActivity())) return;

        blindDeviceId = configPref.userDeviceMac().get();
        if(TextUtils.isEmpty(blindDeviceId)){

            return;
        }


        blindDeviceId = MyStringUtils.macStringToUpper(blindDeviceId);
        Log.e("blindDeviceId:", blindDeviceId);





        if (MyStringUtils.isNotNullAndEmpty(QuinticBleAPISdkBase.resultDevice)) {
//            foxProgressbarInterface = new FoxProgressbarInterface();
//
//            foxProgressbarInterface.startProgressBar(getActivity(), "初始化录音数据...");
            resultDeviceAll = QuinticBleAPISdkBase.resultDevice;
            // ************处理动作
            getCzidBle();
        }
//        else {
//            final Context context = getActivity();
//            QuinticDeviceFactoryTea quinticDeviceFactory = QuinticBleAPISdkBase
//                    .getInstanceFactory(context);
//
//            quinticDeviceFactory.findDevice(blindDeviceId,
//                    new QuinticCallbackTea<QuinticDeviceTea>() {
//
//
//                        @Override
//                        public void onComplete(final QuinticDeviceTea resultDevice) {
//                            super.onComplete(resultDevice);
//                            new Handler(context.getMainLooper())
//                                    .post(new Runnable() {
//                                        @Override
//                                        public void run() {
//                                            closeProgress();
//                                            resultDeviceAll = resultDevice;
//                                            QuinticBleAPISdkBase.resultDevice = resultDeviceAll;
//                                            // ************处理动作
//                                            getCzidBle();
//
//                                        }
//                                    });
//                        }
//
//                        @Override
//                        public void onError(final QuinticException ex) {
//                            new Handler(context.getMainLooper())
//                                    .post(new Runnable() {
//                                        @Override
//                                        public void run() {
//
//                                            closeProgress();
//
//                                        }
//                                    });
//                        }
//                    });
//        }
    }
public void connectSendCodeFailUi(String msg){
    MainApp mainappAll=(MainApp)getActivity().getApplicationContext();

    mainappAll.starttime=System.currentTimeMillis();

    closeProgress();
    }

    public void connectSendCodeSuccessUi(){
        MainApp   mainappAll=(MainApp)getActivity().getApplicationContext();

        mainappAll.starttime=System.currentTimeMillis();

        if(boolInitVoice) {
            main_title_name_id.setText("默认语音");
        }else{
            main_title_name_id.setText("个性化语音");

        }
        closeProgress();
    }

boolean boolInitVoice=true;
    public void getCzidBle() {
        if(!MyStringUtils.isopenBluetooth(getActivity())) return;

        if (resultDeviceAll == null) return;
        String code = "EA0C";

        resultDeviceAll.sendCommonCode(code, new QuinticCallbackTea<String>() {
            @Override
            public void onError(QuinticException ex) {
                super.onError(ex);
                new Handler(getActivity().getMainLooper())
                        .post(new Runnable() {
                            @Override
                            public void run() {




                                connectSendCodeFailUi("查询失败");
                            }
                        });
            }

            @Override
            public void onComplete(final String result) {
                super.onComplete(result);
                if (result == null) {
                    connectSendCodeFailUi("查询失败");

                    return;
                }
                new Handler(getActivity().getMainLooper())
                        .post(new Runnable() {
                            @Override
                            public void run() {
//BACK 0A 04 01 53result
                                String trimResult = result.replace(" ", "");
                                if (trimResult.contains("ea0c01")) {//默认语音
                                    boolInitVoice=true;
                                    connectSendCodeSuccessUi();

                                } else {//不是默认 显示个性化
                                    boolInitVoice=false;
                                    connectSendCodeSuccessUi();

                                }

                            }
                        });

            }
        });

    }



    public void setVoicePlay() {



        if(!MyStringUtils.isopenBluetooth(getActivity())) return;

        if (resultDeviceAll == null) return;

        foxProgressbarInterface = new FoxProgressbarInterface();

        foxProgressbarInterface.startProgressBar(getActivity(), "茶密切换语音...");
        String code = null;
        if(boolInitVoice){
            code="EB0D";//个性化
        }else{
            code="EB0E";//默认
        }

        resultDeviceAll.sendCommonCode(code, new QuinticCallbackTea<String>() {
            @Override
            public void onError(QuinticException ex) {
                super.onError(ex);
                new Handler(getActivity().getMainLooper())
                        .post(new Runnable() {
                            @Override
                            public void run() {
                                connectSendCodeFailUi("语音设置失败");
                            }
                        });
            }

            @Override
            public void onComplete(final String result) {
                super.onComplete(result);
                if (result == null) {
                    connectSendCodeFailUi("语音设置失败");

                    return;
                }
                new Handler(getActivity().getMainLooper())
                        .post(new Runnable() {
                            @Override
                            public void run() {
//BACK 0A 04 01 53result
                                String trimResult = result.replace(" ", "");
                                if (trimResult.contains("eb0c0001")) {//个性化设置成功语音
                                    boolInitVoice=false;
                                    connectSendCodeSuccessUi();

                                }
                              else  if (trimResult.contains("eb0c0000")) {//默认设置成功语音
                                    boolInitVoice=true;
                                    connectSendCodeSuccessUi();

                                }

                                else {//不是默认 显示个性化
//                                    boolInitVoice=false;
//                                    connectSendCodeSuccessUi();
closeProgress();
                                }

                            }
                        });

            }
        });

    }

}
