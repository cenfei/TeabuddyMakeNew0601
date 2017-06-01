package com.taomake.teabuddy.util;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;

import java.lang.ref.WeakReference;

public class FoxHandler extends Handler {

    WeakReference<Activity> mActivityReference;

    public FoxHandler(Activity activity) {
        mActivityReference = new WeakReference<Activity>(activity);
    }

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        if (mActivityReference.get() == null) {
            return;
        }
    }

}