package com.taomake.teabuddy.activity;

/**
 * Created by foxcen on 17/5/23.
 */

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.tencent.connect.share.QQShare;
import com.tencent.open.utils.ThreadManager;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;



public class QQActivity extends AppCompatActivity {

    public static Tencent mTencent;

    private static final String mAppid = "1105245621";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_qq);

        if (mTencent == null) {
            mTencent = Tencent.createInstance(mAppid, this);
        }
    }

    public void shareOnlyImageOnQZone(View view) {
        final Bundle params = new Bundle();
        //本地地址一定要传sdcard路径，不要什么getCacheDir()或getFilesDir()
        params.putString(QQShare.SHARE_TO_QQ_IMAGE_LOCAL_URL, Environment.getExternalStorageDirectory().getAbsolutePath().concat("/a.png"));
        params.putString(QQShare.SHARE_TO_QQ_APP_NAME, "测试应用");
        params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_IMAGE);
        params.putInt(QQShare.SHARE_TO_QQ_EXT_INT, QQShare.SHARE_TO_QQ_FLAG_QZONE_AUTO_OPEN); //打开这句话，可以实现分享纯图到QQ空间
        doShareToQQ(params);
    }

    public void shareOnlyImageOnQQ(View view) {
        final Bundle params = new Bundle();
        params.putString(QQShare.SHARE_TO_QQ_IMAGE_LOCAL_URL, Environment.getExternalStorageDirectory().getAbsolutePath().concat("/a.png"));


        params.putString(QQShare.SHARE_TO_QQ_APP_NAME, "茶密录音机");
        params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_IMAGE);
//        params.putInt(QQShare.SHARE_TO_QQ_EXT_INT, QQShare.SHARE_TO_QQ_FLAG_QZONE_AUTO_OPEN); //打开这句话，可以实现分享纯图到QQ空间

        doShareToQQ(params);
    }

    public void shareOnlyUrlOnQQ(View view,Integer type,String url) {
        final Bundle params = new Bundle();
        params.putString(QQShare.SHARE_TO_QQ_TARGET_URL,"http://blog.csdn.net/u013451048");
        params.putString(QQShare.SHARE_TO_QQ_APP_NAME, "测试应用");
        params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_IMAGE);
//        params.putInt(QQShare.SHARE_TO_QQ_EXT_INT, QQShare.SHARE_TO_QQ_FLAG_QZONE_AUTO_OPEN); //打开这句话，可以实现分享纯图到QQ空间

        doShareToQQ(params);
    }

    private void doShareToQQ(final Bundle params) {
        // QQ分享要在主线程做
        ThreadManager.getMainHandler().post(new Runnable() {

            @Override
            public void run() {
                if (null != mTencent) {
                    mTencent.shareToQQ(QQActivity.this, params, qqShareListener);
                }
            }
        });
    }

    IUiListener qqShareListener = new IUiListener() {
        @Override
        public void onCancel() {
//            Util.Toast(QQActivity.this, "onCancel: ");
        }

        @Override
        public void onComplete(Object response) {
            // TODO Auto-generated method stub
//            Util.Toast(QQActivity.this, "onComplete: " + response.toString());
        }

        @Override
        public void onError(UiError e) {
            // TODO Auto-generated method stub
//            Util.Toast(QQActivity.this, "onError: " + e.errorMessage+ "e");
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Tencent.onActivityResultData(requestCode, resultCode, data, qqShareListener);
    }
}

