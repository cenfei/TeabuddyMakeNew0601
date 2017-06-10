package com.taomake.teabuddy.fragment;

/**
 * Created by foxcen on 16/7/29.
 */

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.taomake.teabuddy.R;
import com.taomake.teabuddy.activity.MainActivity;
import com.taomake.teabuddy.adapter.FuncControlGridAdapter;
import com.taomake.teabuddy.base.MainApp;
import com.taomake.teabuddy.component.FoxProgressbarInterface;
import com.taomake.teabuddy.component.One_Permission_Popwindow;
import com.taomake.teabuddy.component.Record_Setting_Popwindow;
import com.taomake.teabuddy.component.Volume_Popwindow;
import com.taomake.teabuddy.prefs.ConfigPref_;
import com.taomake.teabuddy.util.MyStringUtils;
import com.taomake.teabuddy.util.Util;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;

import quinticble.QuinticBleAPISdkBase;
import quinticble.QuinticCallbackTea;
import quinticble.QuinticCommon;
import quinticble.QuinticDeviceFactoryTea;
import quinticble.QuinticDeviceTea;
import quinticble.QuinticException;

/**
 * Created by foxcen on 16/7/29.
 */
@EFragment(R.layout.cm_function_control)

public class AllFragment extends Fragment {


    @Pref
    ConfigPref_ configPref;
    @ViewById(R.id.shutdown_line)
    LinearLayout shutdown_line;
    @Click(R.id.shutdown_line)
    void onshutdown_line() {
        if (!MyStringUtils.isopenBluetooth(getActivity())) return;

        new One_Permission_Popwindow().showPopwindow(getActivity(), shutdown_line, "确认关闭茶密电源？插电即可重新开机", "确认", "取消", new One_Permission_Popwindow.CallBackPayWindow() {
            @Override
            public void handleCallBackChangeUser() {//确定

                shutDownCub(getActivity());

            }

            @Override
            public void handleCallBackBindDevice() {

            }
        });

    }

//    shutDownCub

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View chatView = inflater.inflate(R.layout.cm_function_control, container, false);

        initUi(chatView);
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


    public void initUi(View view) {

        RelativeLayout main_title_id = (RelativeLayout) view.findViewById(R.id.main_title_id);
//        main_title_id.setBackgroundColor(getResources().getColor(R.color.white));

        ImageView left_title_icon = (ImageView) view.findViewById(R.id.left_title_icon);
        left_title_icon.setVisibility(View.GONE);
        ImageView right_title_icon = (ImageView) view.findViewById(R.id.right_title_icon);
        right_title_icon.setVisibility(View.GONE);

        TextView title = (TextView) view.findViewById(R.id.title);
        title.setVisibility(View.VISIBLE);
        title.setText("功能控制");
        View title_line_id = (View) view.findViewById(R.id.title_line_id);
        title_line_id.setVisibility(View.GONE);


        initdata(view);
    }

    void initdata(View view) {

        gridview = (GridView) view.findViewById(R.id.gridview);

        funcControlGridAdapter = new FuncControlGridAdapter(getActivity());
        gridview.setAdapter(funcControlGridAdapter);
        gridview.setOnItemClickListener(new ItemClickListener());

    }

    GridView gridview = null;
    FuncControlGridAdapter funcControlGridAdapter = null;

    class ItemClickListener implements AdapterView.OnItemClickListener {
        public void onItemClick(AdapterView<?> arg0,// The AdapterView where the
                                // click happened
                                View arg1,// The view within the AdapterView that was clicked
                                int postion,// The position of the view in the adapter
                                long arg3// The row id of the item that was clicked
        ) {
            if (!MyStringUtils.isopenBluetooth(getActivity())) return;

            // 显示所选Item的ItemText
            blindDeviceId = configPref.userDeviceMac().get();
            blindDeviceId = MyStringUtils.macStringToUpper(blindDeviceId);
            Log.e("blindDeviceId:", blindDeviceId);
            switch (postion) {

                case 0://泡茶音乐
                    if(!MyStringUtils.isopenBluetooth(getActivity())) return;

                    new Record_Setting_Popwindow().showPopwindow(getActivity(), arg1, blindDeviceId, new Record_Setting_Popwindow.CallBackPayWindow() {

                        @Override
                        public void handleCallBackDbSelect(String recorddir) {

                        }
                    });

                    break;
                case 1://音量
                    if(!MyStringUtils.isopenBluetooth(getActivity())) return;

                    new Volume_Popwindow().showPopwindow(getActivity(), arg1, blindDeviceId, new Volume_Popwindow.CallBackPayWindow() {
                        @Override
                        public void handleCallBackDbSelect(String recorddir) {

                        }
                    });

                    break;
                case 2://灯光
String code=null;
                    if(light_value==0){
                        code="EB02";
                        lookLightStatus(getActivity(), code,false);

                    }else{
                        code="EB03";
                        final String codef=code;
new One_Permission_Popwindow().showPopwindow(getActivity(), shutdown_line, "主人，您确定\n要关闭灯光吗？", "确认", "取消", new One_Permission_Popwindow.CallBackPayWindow() {
    @Override
    public void handleCallBackChangeUser() {//确定

        lookLightStatus(getActivity(), codef,false);

    }

    @Override
    public void handleCallBackBindDevice() {

    }
});

                    }


                    break;
                case 3://找我
                    setCubMusic(getActivity());
                    break;
                default:
                    break;

            }


        }

    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (!isVisibleToUser) {
            Log.d("AllFragment", "不可见");

        } else {
            blindDeviceId = configPref.userDeviceMac().get();
            blindDeviceId = MyStringUtils.macStringToUpper(blindDeviceId);
            Log.e("blindDeviceId:", blindDeviceId);


            connectFindDevice(getActivity());

            Log.d("AllFragment", "当前可见");

        }
    }


    //************************蓝牙操作*********************************

    /**
     * 功能：查找设备
     */
    private String blindDeviceId;
    private QuinticDeviceTea resultDeviceAll;
    private Integer countError = 0;
    FoxProgressbarInterface foxProgressbarInterface;

    public void connectFindDevice(final Context context) {
        if(!MyStringUtils.isopenBluetooth(getActivity())) return;

//        foxProgressbarInterface=new FoxProgressbarInterface();
//        foxProgressbarInterface.startProgressBar(getActivity(),"茶密初始化");

        if (MyStringUtils.isNotNullAndEmpty(QuinticBleAPISdkBase.resultDevice)) {
            resultDeviceAll = QuinticBleAPISdkBase.resultDevice;
            // ************处理动作
            MainApp mainApp=(MainApp)getActivity().getApplicationContext();
            long starttime=mainApp.starttime;
            long endtime=System.currentTimeMillis();
            if(endtime-starttime>6000) {
                getLightStatus(context, false);
            }
        } else {
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
                                        MainApp mainApp=(MainApp)getActivity().getApplicationContext();
                                        long starttime=mainApp.starttime;
                                        long endtime=System.currentTimeMillis();
                                        if(endtime-starttime>6000) {
                                            getLightStatus(context, false);
                                        }
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
                                            connectFindDevice(context);
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


    public void connectSendCodeFailUi(String msg) {
        MainApp mainApp=(MainApp)getActivity().getApplicationContext();
        mainApp.starttime=System.currentTimeMillis();
        if (foxProgressbarInterface != null)
            foxProgressbarInterface.stopProgressBar();

    }

    public void connectSendCodeSuccesslUi_Light(int value) {
        MainApp mainApp=(MainApp)getActivity().getApplicationContext();
        mainApp.starttime=System.currentTimeMillis();
        if (foxProgressbarInterface != null)
            foxProgressbarInterface.stopProgressBar();


        if (value == 0) {
            funcControlGridAdapter.setSeclectionLight(2, false);

        } else {
            funcControlGridAdapter.setSeclectionLight(2, true);

        }
        funcControlGridAdapter.notifyDataSetChanged();
        gridview.invalidate();


    }

    int light_value = 0;//0是off 1on
    int second_value = 0;

    public void lookLightStatus(final Context context,final String code, final boolean boolplay) {
        if(!MyStringUtils.isopenBluetooth(getActivity())) return;

        if (resultDeviceAll == null) return;
       // String code = "EB02";
        final String msg = "设置指示灯失败";
        resultDeviceAll.sendCommonCode(code, new QuinticCallbackTea<String>() {
            @Override
            public void onError(QuinticException ex) {
                super.onError(ex);
                connectSendCodeFailUi(msg);

            }

            @Override
            public void onComplete(final String result) {
                super.onComplete(result);
                if (result == null) {
                    connectSendCodeFailUi(msg);

                    return;
                }
                new Handler(context.getMainLooper())
                        .post(new Runnable() {
                            @Override
                            public void run() {
//BACK ea 09 01 00 00 00 00 00 01 e2 63 00
                                String trimResult = result.replace(" ", "");
                                if (trimResult.contains("eb01")) {


                                    getLightStatus(context, false);


                                } else {
                                    connectSendCodeFailUi(msg);
                                }

                            }
                        });

            }
        });

    }


    public void getLightStatus(final Context context, final boolean boolplay) {

        if(!MyStringUtils.isopenBluetooth(getActivity())) return;

        if (resultDeviceAll == null) return;
        String code = "EA02";
        final String msg = "查询指示灯失败";
        resultDeviceAll.sendCommonCode(code, new QuinticCallbackTea<String>() {
            @Override
            public void onError(QuinticException ex) {
                super.onError(ex);
                connectSendCodeFailUi(msg);

            }

            @Override
            public void onComplete(final String result) {
                super.onComplete(result);
                if (result == null) {
                    connectSendCodeFailUi(msg);

                    return;
                }
                new Handler(context.getMainLooper())
                        .post(new Runnable() {
                            @Override
                            public void run() {
//BACK ea 09 01 00 00 00 00 00 01 e2 63 00
                                String trimResult = result.replace(" ", "");
                                if (trimResult.contains("ea0201")) {

                                    byte[] data = QuinticCommon.stringToBytes(trimResult);
                                    light_value = QuinticCommon.unsignedByteToInt(data[3]);
                                    Log.d("light_value", light_value + "");


                                    connectSendCodeSuccesslUi_Light(light_value);


//                                    seekbar_first.setProgress(first_value * 100 / 15);
//
//                                    getSecond(context);
//                                    getTeaStatus();
                                } else {
                                    connectSendCodeFailUi(msg);
                                }

                            }
                        });

            }
        });

    }

    FoxProgressbarInterface foxProgressbarInterface05;
    public void setCubMusic(final Context context) {
//        foxProgressbarInterface05 = new FoxProgressbarInterface();
//
//        foxProgressbarInterface05.startProgressBar(context, "蓝牙读取中...");
        if(!MyStringUtils.isopenBluetooth(getActivity())) return;


        if (resultDeviceAll == null) return;
        String code = "EB0501";
        final String msg = "呼叫茶杯失败";
//        process = 1;
//        writehandler.post(runnable);
        funcControlGridAdapter.setSeclectionLook(3, false);


        funcControlGridAdapter.notifyDataSetChanged();
        gridview.invalidate();

        resultDeviceAll.sendCommonCode(code, new QuinticCallbackTea<String>() {
            @Override
            public void onError(QuinticException ex) {
                super.onError(ex);
                connectSendCodeFailUi(msg);

            }

            @Override
            public void onComplete(final String result) {
                super.onComplete(result);
                if (result == null) {
                    connectSendCodeFailUi(msg);

                    return;
                }
                new Handler(context.getMainLooper())
                        .post(new Runnable() {
                            @Override
                            public void run() {
//BACK ea 09 01 00 00 00 00 00 01 e2 63 00
                                String trimResult = result.replace(" ", "");
                                if (trimResult.contains("ea0201")) {


                                } else {
                                    connectSendCodeFailUi(msg);
                                }

                            }
                        });

            }
        });

    }

    final Handler writehandler = new Handler();

    int process = 1;
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            // TODO Auto-generated method stub
            //要做的事情
            if (process <= 2) {


                writehandler.postDelayed(this, 15000);
                process = process + 1;
            } else {
//if(foxProgressbarInterface05!=null)
//{foxProgressbarInterface05.stopProgressBar();}
                funcControlGridAdapter.setSeclectionLook(3, true);


                funcControlGridAdapter.notifyDataSetChanged();
                gridview.invalidate();


                writehandler.removeCallbacks(runnable);

            }
        }
    };


    public void connectSendCodeFailUiShutDown(String msg) {
        if (foxProgressbarInterface != null)
            foxProgressbarInterface.stopProgressBar();
        shutDownCount++;
        if (shutDownCount < 3) {
            shutDownCub(getActivity());
        }
    }

    public void connectSendCodeSuccesslUiShutDown() {
        if (foxProgressbarInterface != null)
            foxProgressbarInterface.stopProgressBar();

        //跳转到 我的页面

        Util.Toast(getActivity(), "主人，茶密已关机成功",null);
        MainActivity mainActivity = (MainActivity) getActivity();
        mainActivity.selectMine();

    }

    int shutDownCount = 0;

    public void shutDownCub(final Context context) {
        if(!MyStringUtils.isopenBluetooth(getActivity())) return;

        if (resultDeviceAll == null) return;
        String code = "EB0B";
        final String msg = "关机失败";


        resultDeviceAll.sendCommonCode(code, new QuinticCallbackTea<String>() {
            @Override
            public void onError(QuinticException ex) {
                super.onError(ex);

                new Handler(context.getMainLooper())
                        .post(new Runnable() {
                            @Override
                            public void run() {
//BACK ea 09 01 00 00 00 00 00 01 e2 63 00

                                connectSendCodeFailUiShutDown(msg);


                            }
                        });

            }

            @Override
            public void onComplete(final String result) {
                super.onComplete(result);

                new Handler(context.getMainLooper())
                        .post(new Runnable() {
                            @Override
                            public void run() {
//BACK ea 09 01 00 00 00 00 00 01 e2 63 00
                                String trimResult = result.replace(" ", "");
                                if (trimResult.contains("eb01")) {

                                    connectSendCodeSuccesslUiShutDown();
                                    QuinticDeviceFactoryTea quinticDeviceFactory = QuinticBleAPISdkBase
                                            .getInstanceFactory(context);
                                    quinticDeviceFactory.abort();
                                } else {

                                    connectSendCodeFailUiShutDown(msg);
                                }

                            }
                        });

            }
        });

    }
}




