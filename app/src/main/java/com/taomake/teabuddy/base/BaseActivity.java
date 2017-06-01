package com.taomake.teabuddy.base;

import android.app.Activity;
import android.os.Bundle;

/**
 * Created by zhang on 2015/8/21.
 */
public abstract class BaseActivity extends Activity {

    public String activityName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initActivityName();
    }


    protected abstract void initActivityName();

    protected abstract void setActivityBg();

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    protected void onResume() {
        super.onResume();
        setActivityBg();
//        MobclickAgent.onPageStart(activityName); //统计页面(仅有Activity的应用中SDK自动调用，不需要单独写)
//        MobclickAgent.onResume(this);          //统计时长
    }

    protected void onPause() {
        super.onPause();
//        MobclickAgent.onPageEnd(activityName); // （仅有Activity的应用中SDK自动调用，不需要单独写）保证 onPageEnd 在onPause 之前调用,因为 onPause 中会保存信息
//        MobclickAgent.onPause(this);
    }

}