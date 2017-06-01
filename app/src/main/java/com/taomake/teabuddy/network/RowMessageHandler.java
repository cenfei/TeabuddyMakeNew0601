package com.taomake.teabuddy.network;

import android.util.Log;

/**
 * Created by xiaoganghu on 15/6/29.
 */
abstract public class RowMessageHandler implements Protocol.CallBack {

    static private String TAG = RowMessageHandler.class.getName();

    @Override
    public void getMessage(String infomation, String url) {
        Log.d(TAG, "send url" + url + " recv resp [" + infomation + "]");

        handleResp(infomation);
    }

    abstract protected void handleResp(String resp);
}
