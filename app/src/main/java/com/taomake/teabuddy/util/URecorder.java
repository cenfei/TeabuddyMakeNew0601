package com.taomake.teabuddy.util;

/**
 * Created by foxcen on 17/4/25.
 */

import android.media.MediaRecorder;
import android.util.Log;

import java.io.IOException;

public class URecorder implements IVoiceManager {

    private final String TAG = URecorder.class.getName();
    private String path;
    private MediaRecorder mRecorder;

    public URecorder(String path) {
        this.path = path;
        mRecorder = new MediaRecorder();
    }

    /*
     * 开始录音
     * @return boolean
     */
    @Override
    public boolean start() {
        //设置音源为Micphone
        try {
            mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            //设置封装格式
            mRecorder.setAudioSamplingRate(22050);//取频

            mRecorder.setAudioChannels(2);//通道
            mRecorder.setAudioEncodingBitRate(16);//kbps

            mRecorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_WB);
            mRecorder.setOutputFile(path);
            //设置编码格式
            mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_WB);


            try {
                Log.e(TAG, "mRecorder prepare() " + path);

                mRecorder.prepare();
            } catch (IOException e) {
                Log.e(TAG, "prepare() failed");
            }
            //录音
            mRecorder.start();
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    /*
     * 停止录音
     * @return boolean
     */
    @Override
    public boolean stop() {
        if (mRecorder != null) {

            try {
                //下面三个参数必须加，不加的话会奔溃，在mediarecorder.stop();
                //报错为：RuntimeException:stop failed
                mRecorder.setOnErrorListener(null);
                mRecorder.setOnInfoListener(null);
                mRecorder.setPreviewDisplay(null);
                mRecorder.stop();
            } catch (IllegalStateException e) {
                // TODO: handle exception
                Log.i("Exception", Log.getStackTraceString(e));
            } catch (RuntimeException e) {
                // TODO: handle exception
                Log.i("Exception", Log.getStackTraceString(e));
            } catch (Exception e) {
                // TODO: handle exception
                Log.i("Exception", Log.getStackTraceString(e));
            }
//            mRecorder.stop();
            mRecorder.release();
            mRecorder = null;
        }
        return false;

    }
}
