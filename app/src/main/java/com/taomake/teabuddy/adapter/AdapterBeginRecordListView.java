package com.taomake.teabuddy.adapter;

import android.app.Activity;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.taomake.teabuddy.R;
import com.taomake.teabuddy.component.FoxProgressbarInterface;
import com.taomake.teabuddy.component.Record_Download_Popwindow;
import com.taomake.teabuddy.component.Record_Popwindow;
import com.taomake.teabuddy.object.RecordInfoObj;
import com.taomake.teabuddy.util.ImageLoaderUtil;
import com.taomake.teabuddy.util.URecorder;
import com.taomake.teabuddy.util.Util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Sam on 2015/7/27.
 */
public class AdapterBeginRecordListView extends BaseAdapter {

    private static Activity context;
    public List<RecordInfoObj> mPersonal;
    private LayoutInflater mInflater;

    private ImageLoader imageLoader;
    private DisplayImageOptions options;
String unionid=null;


    public static String[] luyinArraysComment = {"茶密开机第一句话", "水温过高不宜喝", "温度适喝提醒", "换茶提醒",
            "到喝茶时间提醒", "泡茶完毕提醒", "茶叶更换完成提示", "换茶成功", "需要洗茶提醒"};

    public static String[] luyinArrays = {"打招呼", "有点烫", "有消息来了", "水温正好", "该换茶叶了", "喝杯茶吧", "茶泡好了", "换茶成功", "请洗茶"};

    public AdapterBeginRecordListView(Activity context, List<RecordInfoObj> personalList,String uniond) {
        super();
        this.unionid=uniond;
        this.context = context;
        this.mPersonal = personalList;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        imageLoader = ImageLoader.getInstance();
        options = ImageLoaderUtil.getAvatarOptionsInstance();

    }

    public void addList(List<RecordInfoObj> addList) {
        if (mPersonal == null || mPersonal.size() == 0) {
            mPersonal = addList;
        } else {
            mPersonal.addAll(addList);
        }

    }


    @Override
    public int getCount() {
        return this.mPersonal != null ? this.mPersonal.size() : 0;
    }

    @Override
    public Object getItem(int i) {
        return mPersonal.get(i);
    }

//    @Override
//    public String getItem(int position) {
//        return this.mPersonal.get(position);
//    }

    @Override
    public long getItemId(int position) {
        return position;
    }


   public  Map<String,String> voiceMap=new HashMap<String,String>();

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        RecordInfoObj personalRanking = mPersonal.get(position);

        final ViewHolder viewholder;
        if (convertView == null) {
            viewholder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.cm_begin_record_item, null);
            convertView.setTag(viewholder);

            viewholder.record_item_name_id = (TextView) convertView.findViewById(R.id.record_item_name_id);
            viewholder.record_item_comment_id = (TextView) convertView.findViewById(R.id.record_item_comment_id);


            viewholder.img_download_id = (ImageView) convertView.findViewById(R.id.img_download_id);

            viewholder.img_record_id = (ImageView) convertView.findViewById(R.id.img_record_id);

            viewholder.img_play_id = (ImageView) convertView.findViewById(R.id.img_play_id);


        } else {
            viewholder = (ViewHolder) convertView.getTag();
        }

        final ImageView img = viewholder.img_record_id;

        viewholder.record_item_name_id.setText(luyinArrays[position]);
        viewholder.record_item_comment_id.setText(luyinArraysComment[position]);
        //下载每个mp3文件
        String mp3DbUrl = personalRanking.voicefile_url;
        final String recordName = luyinArrays[position] + ".mp3";
        voiceMap.put("voice"+(position+3),"1");

//       if(position==8){
//           downloadMp3(mp3DbUrl, recordName);
//
//       }






        viewholder.img_download_id.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("download", "download");
                viewholder.img_record_id.setImageDrawable(context.getResources().getDrawable(R.drawable.cm_bg_record));
                viewholder.img_download_id.setImageDrawable(context.getResources().getDrawable(R.drawable.cm_bg_db_c));
                voiceMap.put("voice"+(position+3),"1");
                new Record_Download_Popwindow().showPopwindow(context, img,unionid,(position+3)+"", new Record_Download_Popwindow.CallBackPayWindow() {
                    @Override
                    public void handleCallBackDbSelect(String recorddir) {

                        downloadMp3(recorddir, recordName);
                        Util.Toast(context, "更换成功");


                    }
                });
            }
        });
        final String pathR = Environment.getExternalStorageDirectory() + "/" + path + "/" + recordName;

        viewholder.img_record_id.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("record", "record");
                voiceMap.put("voice"+(position+3),"2");
                    viewholder.img_record_id.setImageDrawable(context.getResources().getDrawable(R.drawable.cm_bg_record_c));
                    viewholder.img_download_id.setImageDrawable(context.getResources().getDrawable(R.drawable.cm_bg_db));
                    final URecorder recorder = new URecorder(pathR);
                    new Record_Popwindow().showPopwindow(context, img, new Record_Popwindow.CallBackPayWindow() {
                        @Override
                        public void handleCallBackPayWindowFromStop(String recorddir) {


                            recorder.stop();

                        }

                        @Override
                        public void handleCallBackPayWindowFromStart(String recorddir) {
//                            String pathR = Environment.getExternalStorageDirectory() + "/" + path + "/" + luyinArrays[position];
//                            URecorder recorder = new URecorder(pathR);

                            recorder.start();

                        }
                    });

            }
        });
        viewholder.img_play_id.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("play", "play");
                File file = new File(pathR);
                if (file.exists()) {
//                    final UPlayer uPlayer = new UPlayer(pathR);
//                    uPlayer.start();
                    foxProgressbarInterface = new FoxProgressbarInterface();
                    foxProgressbarInterface.startProgressBar(context, luyinArrays[position]+"...");
                    mediaPlayer=new MediaPlayer();
                    mediaPlayer.setOnCompletionListener(new CompletionListener());

                    songplay(pathR);
                }
                else{
                    Util.Toast(context, "文件正在下载，请稍等");
                }

            }
        });

        return convertView;
    }
    FoxProgressbarInterface foxProgressbarInterface;
    private final class CompletionListener implements MediaPlayer.OnCompletionListener {

        @Override
        public void onCompletion(MediaPlayer mp) {

            if(foxProgressbarInterface!=null)
            foxProgressbarInterface.stopProgressBar();
        }

    }

    private static class ViewHolder {
        ImageView img_play_id;
        ImageView img_record_id;
        ImageView img_download_id;
        TextView record_item_name_id;
        TextView record_item_comment_id;

    }
    private MediaPlayer mediaPlayer;
    private void songplay(String path) {
        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(path);
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
    ExecutorService fixedThreadPool = Executors.newFixedThreadPool(1);
   public static   String  path = "teabuddy_record_file";
    public static   String  Voice_Path = Environment.getExternalStorageDirectory() + "/" + path + "/" ;






        public  void downloadMp3(final String urlStr, final String fileName) {

        fixedThreadPool.execute(new Runnable() {
            @Override
            public void run() {
//                String fileName = "2.mp3";
                OutputStream output = null;
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
                    String pathName = SDCard + "/" + path + "/" + fileName;//文件存储路径

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
                    //读取大文件
                    byte[] voice_bytes = new byte[1024];
                    int len1 = -1;
                    while ((len1 = input.read(voice_bytes)) != -1) {
                        output.write(voice_bytes, 0, len1);
                        output.flush();

                    }
                    System.out.println("success");
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
            }

        });
        // String urlStr="http://172.17.54.91:8080/download/1.mp3";


    }


}
