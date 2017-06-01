package com.taomake.teabuddy.network;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by xiaoganghu on 15/6/29.
 */
abstract public class BaseMessageHandler implements Protocol.CallBack {
    static private String TAG = BaseMessageHandler.class.getName();

    @Override
    public void getMessage(String infomation, String url) {
        if (infomation != null && infomation.length() > 0) {
            Log.d(TAG, "send url" + url + " recv resp [" + infomation + "]");
            try {
                JSONObject jsonObject = new JSONObject(infomation);
                handleResp(jsonObject);
            } catch (JSONException e) {
                Log.e(TAG, "json exception " + e.toString());
            }
        } else {
            Log.e(TAG, "error " + infomation);
        }
    }

    abstract protected void handleResp(JSONObject resp);
}
