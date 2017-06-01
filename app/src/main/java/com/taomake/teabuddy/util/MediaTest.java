package com.taomake.teabuddy.util;//package com.taomake.teabuddy.util;
//
///**
// * Created by foxcen on 17/5/4.
// */
//
//
//        import java.io.File;
//        import java.io.FileOutputStream;
//        import java.io.IOException;
//        import java.io.InputStream;
//        import java.io.OutputStream;
//        import java.net.HttpURLConnection;
//        import java.net.URL;
//        import java.util.ArrayList;
//        import java.util.List;
//        import java.util.concurrent.ExecutorService;
//        import java.util.concurrent.Executors;
//        import java.util.concurrent.TimeUnit;
//
//        import android.app.Activity;
//        import android.media.MediaPlayer;
//        import android.media.MediaPlayer.OnCompletionListener;
//        import android.media.MediaRecorder;
//        import android.os.Bundle;
//        import android.os.Environment;
//        import android.os.Handler;
//        import android.os.Message;
//        import android.util.Log;
//        import android.view.View;
//        import android.view.View.OnClickListener;
//        import android.widget.Button;
//
//
///**
// *
// * @author kk
// *
// */
//
//public class PlayerActivity extends Activity implements OnClickListener {
//
//    // 开始录音
//    private Button start;
//    // 停止按钮
//    private Button stop;
//    // 播放按钮
//    private Button paly;
//    // 暂停播放
//    private Button pause_paly;
//    // 停止播放
//    private Button stop_paly;
//
//    // 录音类
//    private MediaRecorder mediaRecorder;
//    // 以文件的形式保存
//    private File recordFile;
//
//    private PlayRecord player;
//    String SDCARDPATH = Environment.getExternalStorageDirectory() + "/path";
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.player);
//
//        //
//        initView();
//        Listener();
//
//        for (int i = 0; i < 7; i++) {
//            final int index = i;
//            dbVoice("http://testserver.teabuddy.cn/voice/sys/3/00"+(i+3)+".mp3",
//                    SDCARDPATH + "/" + "fox" + i + ".mp3", "fox" + i + ".mp3",
//                    SDCARDPATH);
//        } //
//
//        fixedThreadPool.shutdown();
//
//        try {
//            fixedThreadPool.awaitTermination(10, TimeUnit.SECONDS);
//
//            mediaPlayer = new MediaPlayer();
//            mediaPlayer.setOnCompletionListener(new CompletionListener());
//
//            songplay();
//        } catch (InterruptedException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//
//    }
//
//    Handler handler = new Handler() {
//
//        public void handleMessage(android.os.Message msg) {
//
//            Bundle bundle = msg.getData();
//            String fileName = (String) bundle.get("fileName");
//
//            //songArrayList.add(SDCARDPATH+"/"+fileName);
//
//        };
//
//    };
//    private int songIndex = 0;
//    private List<String> songArrayList=new ArrayList<String>(); //播放声音列表
//    private MediaPlayer mediaPlayer;
//
//    private final class CompletionListener implements OnCompletionListener{
//
//        @Override
//        public void onCompletion(MediaPlayer mp) {
//            nextsong();
//        }
//
//    }
//    private void nextsong() {
//
//        if (songIndex < songArrayList.size() - 1) {
//            songIndex = songIndex + 1;
//            songplay();
//        }
//        else {
//            songArrayList.clear();
//            songIndex = 0;
//
//        }
//
//
//    }
//    private void songplay() {
//        try {
//            mediaPlayer.reset();
//            mediaPlayer.setDataSource(songArrayList.get(songIndex));
//            mediaPlayer.prepare();
//            mediaPlayer.start();
//        } catch (IllegalArgumentException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        } catch (SecurityException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        } catch (IllegalStateException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        } catch (IOException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//
//    }
//
//    @Override
//    protected void onDestroy() {
//        mediaPlayer.release();
//        mediaPlayer = null;
//        super.onDestroy();
//    }
//
//
//    ExecutorService fixedThreadPool = Executors.newFixedThreadPool(3);
//
//    public void dbVoice(final String urlStr, final String filePath,
//                        final String fileName, final String SDCard) {
//
//        fixedThreadPool.execute(new Runnable() {
//
//            @Override
//            public void run() {
//                // TODO Auto-generated method stub
//                OutputStream output = null;
//                try {
//
//
//
//                    File f_voice = new File(filePath);
//                    Log.i("文件地址", filePath);
//                    URL url = new URL(urlStr);
//                    // 根据URL对象创建一个HttpURLConnection连接
//                    HttpURLConnection urlConn = (HttpURLConnection) url
//                            .openConnection();
//                    // IO流读取数据
//                    InputStream inputStream = urlConn.getInputStream();
//                    int len = urlConn.getContentLength();
//                    // if (f_voice!=null&&f_voice.exists()) {
//                    // System.out.println("exits");
//                    // return;
//                    // } else {
//                    // String dir = SDCard + "/" + path;
//                    new File(SDCard).mkdir();// 新建文件夹
//                    f_voice.createNewFile();// 新建文件
//                    output = new FileOutputStream(f_voice);
//                    // 读取大文件
//
//                    byte[] voice_bytes = new byte[1024];
//                    int len1 = -1;
//                    while ((len1 = inputStream.read(voice_bytes)) != -1) {
//                        output.write(voice_bytes, 0, len1);
//                        output.flush();
//
//                    }
//                    System.out.println("success");
//                    output.close();
//                    songArrayList.add(SDCARDPATH+"/"+fileName);
//                    Message msgMessage = handler.obtainMessage();
//                    Bundle bundle = new Bundle();
//                    bundle.putString("fileName", fileName);
//
//                    msgMessage.setData(bundle);
//
//                    handler.sendMessage(msgMessage);
//
//                    // }
//                } catch (Exception e) {
//                    System.out.println(e.toString());
//                    e.printStackTrace();
//                    // return;
//                } finally {
//
//                }
//            }
//        });
//
//    }
//
//    private void initView() {
//        start = (Button) findViewById(R.id.start);
//        stop = (Button) findViewById(R.id.stop);
//        paly = (Button) findViewById(R.id.paly);
//        pause_paly = (Button) findViewById(R.id.pause_paly);
//        stop_paly = (Button) findViewById(R.id.stop_paly);
//    }
//
//    private void Listener() {
//        start.setOnClickListener(this);
//        stop.setOnClickListener(this);
//        paly.setOnClickListener(this);
//        pause_paly.setOnClickListener(this);
//        stop_paly.setOnClickListener(this);
//    }
//
//    @Override
//    public void onClick(View v) {
//        player = new PlayRecord(PlayerActivity.this);
//        int Id = v.getId();
//
//        switch (Id) {
//            case R.id.start:
//                startRecording();
//                break;
//            case R.id.stop:
//                stopRecording();
//                break;
//            case R.id.paly:
//                mediaPlayer = new MediaPlayer();
//                mediaPlayer.setOnCompletionListener(new CompletionListener());
//
//                songplay();
//
//
//
////            playRecording();
//                break;
//            case R.id.pause_paly:
//                pauseplayer();
//                break;
//            case R.id.stop_paly:
//                stopplayer();
//                break;
//        }
//    }
//
//    private void startRecording() {
//        mediaRecorder = new MediaRecorder();
//        // 判断，若当前文件已存在，则删除
//        if (recordFile.exists()) {
//            recordFile.delete();
//        }
//        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
//        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
//        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
//        mediaRecorder.setOutputFile(recordFile.getAbsolutePath());
//
//        try {
//            // 准备好开始录音
//            mediaRecorder.prepare();
//
//            mediaRecorder.start();
//        } catch (IllegalStateException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        } catch (IOException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//    }
//
//    private void stopRecording() {
//        if (recordFile != null) {
//            mediaRecorder.stop();
//            mediaRecorder.release();
//        }
//    }
//
//    private void playRecording() {
//        player.playRecordFile(recordFile);
//    }
//
//    private void pauseplayer() {
//        player.pausePalyer();
//    }
//
//    private void stopplayer() {
//        // TODO Auto-generated method stub
//        player.stopPalyer();
//    }
//}
