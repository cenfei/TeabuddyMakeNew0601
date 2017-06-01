package com.taomake.teabuddy.component;

import android.app.Activity;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.Environment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.google.gson.Gson;
import com.taomake.teabuddy.R;
import com.taomake.teabuddy.adapter.AdapterBeginRecordListView;
import com.taomake.teabuddy.network.ProtocolUtil;
import com.taomake.teabuddy.network.RowMessageHandler;
import com.taomake.teabuddy.object.DbRecordInfoObj;
import com.taomake.teabuddy.object.UpdateNineRecordsJson;
import com.taomake.teabuddy.object.UpdateRecordInfoObj;
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

/**
 * Created by foxcen on 16/4/14.
 */
public class Record_Play_Popwindow {

    private Integer chooseType = 1;//1代表刷刷钱包
    PopupWindow window;
    CallBackPayWindow callBackPayWindow;

    Activity activity;
    View relView;
    View view;

    /**
     * 显示popupWindow
     */
    public void showPopwindow(final Activity activity, View relView, String unionid, String voiceid, CallBackPayWindow callBackPayWindow) {
        // 利用layoutInflater获得View
        this.callBackPayWindow = callBackPayWindow;
        context = activity;
        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.cm_play_popwindow, null);
        title_play_id = (TextView) view.findViewById(R.id.title_play_id);

        content_play_id = (TextView) view.findViewById(R.id.content_play_id);

        close_play_id = (ImageView) view.findViewById(R.id.close_play_id);
        this.relView = relView;
        this.activity = activity;

        close_play_id.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closePopupWindow(activity);
                try {
                    mediaPlayer.stop();
                }catch (Exception e){

                }
            }
        });
        initUi(activity, view, relView);
        getUpdateBcNineRecords(unionid, voiceid);

    }

    public void initColor() {

//        choose_photo_lib.setTextColor(context.getResources().getColor(R.color.line_hot_all));
//
//        open_camara.setTextColor(context.getResources().getColor(R.color.line_hot_all));
//
//        choose_photo_src.setTextColor(context.getResources().getColor(R.color.line_hot_all));
    }

    private Activity context;
    TextView title_play_id, content_play_id;
    ImageView close_play_id;
    private List<DbRecordInfoObj> dbRecordInfoObjList = new ArrayList<DbRecordInfoObj>();
    GridView gridview;

    /**
     * @param view
     */
    public void initUi(final Activity activity, View view, View relView) {


        window = new PopupWindow(view,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT, true);


        // 设置popWindow弹出窗体可点击，这句话必须添加，并且是true
//        window.setFocusable(false);


        // 实例化一个ColorDrawable颜色为半透明
//        ColorDrawable dw = new ColorDrawable(0xff000000);
//        window.setBackgroundDrawable(activity.getResources().getDrawable(R.drawable.rounded_choose));
////        window.setBackgroundDrawable(dw);
//
//        window.setAnimationStyle(R.style.mypopwindow_anim_style);

        // 设置popWindow的显示和消失动画
        //   window.setAnimationStyle(R.style.mypopwindow_anim_style);
//        backgroundAlpha(0.5f, activity);

//        relView.getLocationOnScreen(location);
//        window.setAnimationStyle(R.style.mypopwindow_anim_style);  //设置动画
//这里就可自定义在上方和下方了 ，这种方式是为了确定在某个位置，某个控件的左边，右边，上边，下边都可以
//        window.showAtLocation(relView, Gravity.NO_GRAVITY, (location[0] +relView.getWidth() / 2) - popupWidth / 2, location[1] - popupHeight+10);
//        window.showAsDropDown(relView, 0,
//                0);

        // 在底部显示
        if (Util.checkDeviceHasNavigationBar(context)) {
            window.showAtLocation(relView,
                    Gravity.CENTER, 0, 0);
//            setBackgroundAlpha(activity, 0.5f);
        } else {
            window.showAtLocation(relView,
                    Gravity.CENTER, 0, 0);
//            backgroundAlpha(0.5f, activity);

        }


        //popWindow消失监听方法
        window.setOnDismissListener(new PopupWindow.OnDismissListener() {

            @Override
            public void onDismiss() {
                System.out.println("popWindow消失");
                closePopupWindow(activity);
            }
        });
    }
    /***
     * 获取PopupWindow实例
     */
//    private void getPopupWindow() {
//
//        if (null != window) {
//            closePopupWindow();
//            return;
//        } else {
//            initPopuptWindow();
//        }
//    }

    /**
     * 关闭窗口
     */
    public void closePopupWindow(Activity activity) {
        WindowManager.LayoutParams params = activity.getWindow().getAttributes();
        params.alpha = 1f;
        activity.getWindow().setAttributes(params);

        if (window != null) {
            window.dismiss();
            window = null;
            System.out.println("popWindow消失 ...");

        }
    }

    public interface CallBackPayWindow {

        void handleCallBackDbSelect(String recorddir);

//        void handleCallBackPayWindowFromLib();

//        void handleDialogResultCancle();

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


    class ItemClickListener implements AdapterView.OnItemClickListener {
        public void onItemClick(AdapterView<?> arg0,// The AdapterView where the
                                // click happened
                                View arg1,// The view within the AdapterView that was clicked
                                int postion,// The position of the view in the adapter
                                long arg3// The row id of the item that was clicked
        ) {
            DbRecordInfoObj dbRecordInfoObj = dbRecordInfoObjList.get(postion);

            // 显示所选Item的ItemText   选中的地址
            callBackPayWindow.handleCallBackDbSelect(dbRecordInfoObj.voicefile_url);
            closePopupWindow(activity);


        }

    }

    //    FoxProgressbarInterface foxProgressbarInterface;
    public void getUpdateBcNineRecords(String voicefileindex, String unionid) {
//        foxProgressbarInterface = new FoxProgressbarInterface();
//        foxProgressbarInterface.startProgressBar(context, "加载中...");
        title_play_id.setText("正在获取录音数据");
        content_play_id.setText("请稍等...");
        ProtocolUtil.getUpdateRecordList(context, new GetBcNineRecordsHandler(), unionid, voicefileindex);//devno 空表示所有


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
//        foxProgressbarInterface.stopProgressBar();
        if(!boolConntect) return;

        boolConntect=false;

        if (resp != null && !resp.equals("")) {
            UpdateNineRecordsJson teaDetailJsonGloabl = new Gson().fromJson(resp, UpdateNineRecordsJson.class);
//            if ((teaDetailJsonGloabl.rcode + "").equals(Constant.RES_SUCCESS)) {



            recordInfoObjGloabl = teaDetailJsonGloabl.rows;

            if(recordInfoObjGloabl==null||recordInfoObjGloabl.size()==0){
                Util.Toast(context,"服务器没有录音数据");
                closePopupWindow(context);
                return;
            }



            for (UpdateRecordInfoObj updateRecordInfoObj : recordInfoObjGloabl) {

                Integer id = Integer.valueOf(updateRecordInfoObj.voicefile_no);
                title_play_id.setText("开始播放录音");

                content_play_id.setText(AdapterBeginRecordListView.luyinArrays[id - 3]);

                downloadMp3(updateRecordInfoObj.voicefile_url, AdapterBeginRecordListView.luyinArrays[id - 3] + ".mp3");


            }

//            fixedThreadPool.shutdown();

            try {
                fixedThreadPool.awaitTermination(10, TimeUnit.SECONDS);

                mediaPlayer = new MediaPlayer();
                mediaPlayer.setOnCompletionListener(new CompletionListener());
                title_play_id.setText("开始播放录音");

                songplay();
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }


        }else{
            Util.Toast(context,"服务器没有录音数据");
            closePopupWindow(context);
        }
    }


//    Handler myHandler = new Handler() {
//        public void handleMessage(Message msg) {
//
//            title_play_id.setText("开始播放录音");
//
//
//            Bundle bundle = msg.getData();
//            String pathname = (String) bundle.get("pathname");
//            String pathVaule = pathname.replace(Voice_Path, "");
//            pathVaule = pathVaule.replace(".mp3", "");
//            content_play_id.setText(pathVaule);
//
//            File file = new File(pathname);
//            if (file.exists()) {
//                final UPlayer uPlayer = new UPlayer(pathname);
//                uPlayer.start();
//            }
//
//            super.handleMessage(msg);
//        }
//    };


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
                    songArrayList.add(pathName);

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


    private int songIndex = 0;
    private List<String> songArrayList = new ArrayList<String>(); //播放声音列表
    private MediaPlayer mediaPlayer;

    private final class CompletionListener implements MediaPlayer.OnCompletionListener {

        @Override
        public void onCompletion(MediaPlayer mp) {
            nextsong();
        }

    }

    private void nextsong() {

        if (songIndex < songArrayList.size() - 1) {
            songIndex = songIndex + 1;
            songplay();
        } else {
            songArrayList.clear();
            songIndex = 0;
            closePopupWindow(context);

        }


    }

    private void songplay() {
        try {
            mediaPlayer.reset();
            String pathn = songArrayList.get(songIndex);
            mediaPlayer.setDataSource(pathn);


            String pathVaule = pathn.replace(Voice_Path, "");
            pathVaule = pathVaule.replace(".mp3", "");
            content_play_id.setText(pathVaule);
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (SecurityException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalStateException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }


}

