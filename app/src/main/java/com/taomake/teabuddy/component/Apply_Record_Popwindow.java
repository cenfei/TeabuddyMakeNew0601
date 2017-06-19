package com.taomake.teabuddy.component;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.google.gson.Gson;
import com.taomake.teabuddy.R;
import com.taomake.teabuddy.adapter.AdapterBeginRecordListView;
import com.taomake.teabuddy.fragment.HomeTabFragment;
import com.taomake.teabuddy.network.ProtocolUtil;
import com.taomake.teabuddy.network.RowMessageHandler;
import com.taomake.teabuddy.object.UpdateNineRecordsJson;
import com.taomake.teabuddy.object.UpdateRecordInfoObj;
import com.taomake.teabuddy.util.MyStringUtils;
import com.taomake.teabuddy.util.Util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import quinticble.QuinticBleAPISdkBase;
import quinticble.QuinticCallbackTea;
import quinticble.QuinticDeviceFactoryTea;
import quinticble.QuinticDeviceTea;
import quinticble.QuinticException;

/**
 * Created by foxcen on 16/4/14.
 */
public class Apply_Record_Popwindow {

    private Integer chooseType = 1;//1代表刷刷钱包
    PopupWindow window;
    CallBackPayWindow callBackPayWindow;
//    TextView pop_process_record_id;
//    final Handler writehandler = new Handler();
//
//    int process = 1;
//    Runnable runnable = new Runnable() {
//        @Override
//        public void run() {
//            // TODO Auto-generated method stub
//            //要做的事情
//            if (process <= 6) {
//
//                pop_process_record_id.setText((6 - process + ""));
//
//                writehandler.postDelayed(this, 1000);
//                process = process + 1;
//            } else {
//                callBackPayWindow.handleCallBackPayWindowFromStop("src");
//
//
//                writehandler.removeCallbacks(runnable);
//                closePopupWindow(context);
//
//            }
//        }
//    };


    String unionid;
    String indexid;

    /**
     * 显示popupWindow
     */
    public void showPopwindow(final Activity activity, View relView, String blindDeviceId,String unionid, String indexid, CallBackPayWindow callBackPayWindow) {
        // 利用layoutInflater获得View
        this.callBackPayWindow = callBackPayWindow;
        this.blindDeviceId=blindDeviceId;
        context = activity;
        this.unionid = unionid;
        this.indexid = indexid;
        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.cm_apply_popwindow, null);
        initUi(activity, view, relView);
        connectFindDevice(activity);
    }


    private Activity context;
    LinearLayout apply_connecting_line, apply_download_line, apply_ready_line;


    private void setShowLine(int value) {

        apply_connecting_line.setVisibility(View.GONE);
        apply_download_line.setVisibility(View.GONE);
        apply_ready_line.setVisibility(View.GONE);


        switch (value) {
            case 1:
                apply_connecting_line.setVisibility(View.VISIBLE);
                break;
            case 3:


                apply_download_line.setVisibility(View.VISIBLE);
                break;
            case 2:
                //这个时候需要去连接蓝牙  发送eb19

               setVoiceInit();
                apply_ready_line.setVisibility(View.VISIBLE);
                break;
            default:
                break;
        }


    }

    TextView connect_msg_id, connect_stop_record_id, apply_download_begin, apply_download_cancel, apply_download_comment;

    private Dialog dialog;
    /**
     * @param view
     */
    public void initUi(final Activity activity, View view, View relView) {

        apply_connecting_line = (LinearLayout) view.findViewById(R.id.apply_connecting_line);

        apply_download_line = (LinearLayout) view.findViewById(R.id.apply_download_line);
        apply_ready_line = (LinearLayout) view.findViewById(R.id.apply_ready_line);

        connect_msg_id = (TextView) view.findViewById(R.id.connect_msg_id);
        connect_stop_record_id = (TextView) view.findViewById(R.id.connect_stop_record_id);
        connect_stop_record_id.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closePopupWindow(activity);
            }
        });
//
        apply_download_begin = (TextView) view.findViewById(R.id.apply_download_begin);
        apply_download_begin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {//开始下载内容  先从网络下载  然后下载到设备

                setShowLine(3);
                //开始下载
                getUpdateBcNineRecords();
            }
        });


        apply_download_cancel = (TextView) view.findViewById(R.id.apply_download_cancel);
        apply_download_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closePopupWindow(activity);
            }
        });

        apply_download_comment = (TextView) view.findViewById(R.id.apply_download_comment);

        dialog = new Dialog(context, R.style.myprocessstyle);

//        writehandler.post(runnable);

// 加载popuwindow 菊花
        dialog.setContentView(view);

        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        try {
            dialog.show();
        } catch (Exception e) {

        }
//        window = new PopupWindow(view,
//                WindowManager.LayoutParams.WRAP_CONTENT,
//                WindowManager.LayoutParams.WRAP_CONTENT);
//        window = new PopupWindow(view,
//                ViewGroup.LayoutParams.MATCH_PARENT,
//                ViewGroup.LayoutParams.MATCH_PARENT, true);
//
//        view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
//        int popupWidth = view.getMeasuredWidth();    //  获取测量后的宽度
//        int popupHeight = view.getMeasuredHeight();  //获取测量后的高度
//        int[] location = new int[2];
//
//
//        // 设置popWindow弹出窗体可点击，这句话必须添加，并且是true
////        window.setFocusable(true);
//
//
//        // 实例化一个ColorDrawable颜色为半透明
//        ColorDrawable dw = new ColorDrawable(0x00000000);
////        window.setBackgroundDrawable(activity.getResources().getDrawable(R.drawable.rounded_choose));
////        window.setBackgroundDrawable( dw);
//
//
//        // 在底部显示
//        if (Util.checkDeviceHasNavigationBar(context)) {
//            window.showAtLocation(relView,
//                    Gravity.CENTER, 0, 0);
////            setBackgroundAlpha(activity, 0.5f);
//        } else {
//            window.showAtLocation(relView,
//                    Gravity.CENTER, 0, 0);
////            backgroundAlpha(0.5f, activity);
//
//        }


    }


    /**
     * 关闭窗口
     */
    public void closePopupWindow(Activity activity) {
        if(callBackPayWindow!=null)
        callBackPayWindow.handleCallBackPayWindowFromStart("");

        if(dialog!=null)
            dialog.dismiss();

//        WindowManager.LayoutParams params = activity.getWindow().getAttributes();
//        params.alpha = 1f;
//        activity.getWindow().setAttributes(params);
//
//        if (window != null) {
//            window.dismiss();
//            window = null;
//            System.out.println("popWindow消失 ...");
//
//        }
    }

    public interface CallBackPayWindow {

        void handleCallBackPayWindowFromStop(String recorddir);

        void handleCallBackPayWindowFromStart(String recorddir);


    }

    public static void backgroundAlpha(float bgAlpha, Activity activity) {
        WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
        lp.alpha = bgAlpha; //0.0-1.0
        activity.getWindow().setAttributes(lp);
    }

    /**
     * 设置页面的透明度
     *
     * @param bgAlpha 1表示不透明
     */
    public static void setBackgroundAlpha(Activity activity, float bgAlpha) {
        WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
        lp.alpha = bgAlpha;
        if (bgAlpha == 1) {
            activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);//不移除该Flag的话,在有视频的页面上的视频会出现黑屏的bug
        } else {
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);//此行代码主要是解决在华为手机上半透明效果无效的bug
        }
        activity.getWindow().setAttributes(lp);
    }


    //************************蓝牙操作*********************************
    final Handler writehandlerConnect = new Handler();

    int processConnect = 1;
    Runnable runnableConnect = new Runnable() {
        @Override
        public void run() {
            // TODO Auto-generated method stub
            switch (processConnect) {
                case 1:
                    connect_msg_id.setText("正在连接茶密.");
                    processConnect++;
                    break;
                case 2:
                    connect_msg_id.setText("正在连接茶密..");
                    processConnect++;

                    break;
                case 3:
                    connect_msg_id.setText("正在连接茶密...");
                    processConnect = 1;

                    break;


            }


        }
    };
    /**
     * 功能：查找设备
     */
    private String blindDeviceId;
    private QuinticDeviceTea resultDeviceAll;
    private Integer countError = 0;
    FoxProgressbarInterface foxProgressbarInterface;

    public void connectFindDevice(final Context context) {
        setShowLine(1);
        writehandlerConnect.post(runnableConnect);

//        foxProgressbarInterface = new FoxProgressbarInterface();
//
//        foxProgressbarInterface.startProgressBar(context, "蓝牙读取中...");
        if (MyStringUtils.isNotNullAndEmpty(QuinticBleAPISdkBase.resultDevice)) {
            resultDeviceAll = QuinticBleAPISdkBase.resultDevice;
            // ************处理动作
            setShowLine(2);
            writehandlerConnect.removeCallbacks(runnableConnect);
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
//                                        getLightStatus(context, false);
                                        setShowLine(2);
                                        writehandlerConnect.removeCallbacks(runnableConnect);
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
                                            callBackPayWindow.handleCallBackPayWindowFromStop("");
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

    List<byte[]> codeList = new ArrayList<byte[]>();

    public void connectSendCodeFailUi(String msg){

        Util.Toast(context,"下载语音到设备失败",null);
        closePopupWindow(context);
    }
    public void sendMp3ToDevice() {
        resultDeviceAll.sendListMp3Code(codeList, new QuinticCallbackTea<String>() {
            @Override
            public void onError(QuinticException ex) {
                super.onError(ex);
                new Handler(context.getMainLooper())
                        .post(new Runnable() {
                            @Override
                            public void run() {
                                connectSendCodeFailUi("");
                            }
                        });

            }

            @Override
            public void onProgress(final int progress) {
                super.onProgress(progress);
                new Handler(context.getMainLooper())
                        .post(new Runnable() {
                            @Override
                            public void run() {
                                apply_download_comment.setText("已完成 " + progress + "%");

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
                new Handler(context.getMainLooper())
                        .post(new Runnable() {
                            @Override
                            public void run() {
//BACK 0A 04 01 53result
                                String trimResult = result.replace(" ", "");
                                if (trimResult.contains("EB1701")) {
//
                                    Util.Toast(context, "应用成功",null);

                                    //需要设置
                                    Intent intent = new Intent(HomeTabFragment.MYACTION_UPDATE_HOME);
                                    Log.i("Broadcast Change home", "change home fragment");
                                    intent.putExtra("updatetype", "1");

                                    context.sendBroadcast(intent);


                                    closePopupWindow(context);
                                } else {


                                    connectSendCodeFailUi("下载录音失败");
                                }

                            }
                        });
            }
        });
    }



    public void setVoiceInit() {
        if (resultDeviceAll == null) return;
        String code = "EB19";

        resultDeviceAll.sendCommonCode(code, new QuinticCallbackTea<String>() {
            @Override
            public void onError(QuinticException ex) {
                super.onError(ex);
                new Handler(context.getMainLooper())
                        .post(new Runnable() {
                            @Override
                            public void run() {
                                connectSendCodeFailUi("语音清空失败");
                            }
                        });
            }

            @Override
            public void onComplete(final String result) {
                super.onComplete(result);
                if (result == null) {
                    connectSendCodeFailUi("语音清空失败");

                    return;
                }
                new Handler(context.getMainLooper())
                        .post(new Runnable() {
                            @Override
                            public void run() {
//BACK 0A 04 01 53result
                                String trimResult = result.replace(" ", "");
                                if (trimResult.contains("eb19")) {
                                    Log.d("语音清空", "录音初始化eb19成功");
                                } else {
                                    connectSendCodeFailUi("语音清空失败");
                                }

                            }
                        });

            }
        });

    }

    //***********************************************

    //    FoxProgressbarInterface foxProgressbarInterface;
    public void getUpdateBcNineRecords() {
        if (foxProgressbarInterface == null || (foxProgressbarInterface != null && !foxProgressbarInterface.isShowing())) {
            foxProgressbarInterface = new FoxProgressbarInterface();
            foxProgressbarInterface.startProgressBar(context, "加载中...");
        }
        ProtocolUtil.getUpdateRecordList(context, new GetBcNineRecordsHandler(), unionid, indexid);//devno 空表示所有


    }


    private class GetBcNineRecordsHandler extends RowMessageHandler {
        @Override
        protected void handleResp(String resp) {
            getBcNineRecordsHandler(resp);
        }
    }


    List<UpdateRecordInfoObj> recordInfoObjGloabl;
    boolean boolConntect=true;

    public void getBcNineRecordsHandler(String resp) {


        if (foxProgressbarInterface != null)
            foxProgressbarInterface.stopProgressBar();
        if(!boolConntect) return;

        boolConntect=false;
        if (resp != null && !resp.equals("")) {
            UpdateNineRecordsJson teaDetailJsonGloabl = new Gson().fromJson(resp, UpdateNineRecordsJson.class);
//            if ((teaDetailJsonGloabl.rcode + "").equals(Constant.RES_SUCCESS)) {


            recordInfoObjGloabl = teaDetailJsonGloabl.rows;
            for (UpdateRecordInfoObj updateRecordInfoObj : recordInfoObjGloabl) {

                Integer id = Integer.valueOf(updateRecordInfoObj.voicefile_no);
//                title_play_id.setText("开始播放录音");
//
//                content_play_id.setText(AdapterBeginRecordListView.luyinArrays[id - 3]);
                apply_download_comment.setText("正在下载录音:" + AdapterBeginRecordListView.luyinArrays[id - 3]);
                downloadMp3(updateRecordInfoObj.voicefile_url, AdapterBeginRecordListView.luyinArrays[id - 3] + ".mp3");


            }

//            fixedThreadPool.shutdown();

            try {
                fixedThreadPool.awaitTermination(10, TimeUnit.SECONDS);
                apply_download_comment.setText("准备烧录到茶密");

                //开始连接蓝牙----下载到设备
                sendMp3ToDevice();


            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }


        }
    }


    ExecutorService fixedThreadPool = Executors.newFixedThreadPool(3);
    public static String path = "teabuddy_record_file";
    public static String Voice_Path = Environment.getExternalStorageDirectory() + "/" + path + "/";

    public void downloadMp3(final String urlStr, final String fileName) {

        fixedThreadPool.execute(new Runnable() {
            @Override
            public void run() {
//                String fileName = "2.mp3";
                OutputStream output = null;
                String pathName = null;
                try {
                /*
                 * 通过URL取得HttpURLConnection
                 * 要网络连接成功，需在AndroidMainfest.xml中进行权限配置
                 * <uses-permission android:name="android.permission.INTERNET" />
                 */
                    URL url = new URL(urlStr);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    //取得inputStream，并将流中的信息写入SDCard

                /*
                 * 写前准备
                 * 1.在AndroidMainfest.xml中进行权限配置
                 * <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
                 * 取得写入SDCard的权限
                 * 2.取得SDCard的路径： Environment.getExternalStorageDirectory()
                 * 3.检查要保存的文件上是否已经存在
                 * 4.不存在，新建文件夹，新建文件
                 * 5.将input流中的信息写入SDCard
                 * 6.关闭流
                 */
                    String SDCard = Environment.getExternalStorageDirectory() + "";
                    pathName = SDCard + "/" + path + "/" + fileName;//文件存储路径

                    File file = new File(pathName);
                    InputStream input = conn.getInputStream();
                    if (file.exists()) {
                        System.out.println("exits");
//                        file.delete();
//                        return;
                    } else {
                        String dir = SDCard + "/" + path;
                        new File(dir).mkdir();//新建文件夹
                        file.createNewFile();//新建文件

                    }

                    output = new FileOutputStream(file);
                    byte[] voice_bytes = new byte[1024];
                    int len1 = -1;
                    while ((len1 = input.read(voice_bytes)) != -1) {
                        output.write(voice_bytes, 0, len1);
                        output.flush();

                    }

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        output.close();
                        System.out.println("success");
                    } catch (IOException e) {
                        System.out.println("fail");
                        e.printStackTrace();
                    }
                }
                codeList.add(MyStringUtils.getBytes(pathName));


//                Message msg = myHandler.obtainMessage();
//
//                Bundle bundle = new Bundle();
//                bundle.putString("pathname", pathName);
//                msg.setData(bundle);
//                myHandler.sendMessage(msg);

            }

        });
        // String urlStr="http://172.17.54.91:8080/download/1.mp3";


    }


}

