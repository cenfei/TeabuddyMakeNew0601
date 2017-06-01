package com.taomake.teabuddy.activity;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;


import com.taomake.teabuddy.R;
import com.taomake.teabuddy.base.BaseActivity;
import com.taomake.teabuddy.util.Util;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by zhang on 2015/8/11.
 */
@EActivity(R.layout.activity_welcome_loding)
public class LoadingActivity extends BaseActivity {

    @ViewById(R.id.welcome_viewpager)
    ViewPager viewPager;

    List<View> viewList;

//    @Pref
//    ConfigPref_ configPref;

    @AfterViews
    void init() {
        View view1 = getLayoutInflater().from(this).inflate(R.layout.viewpager_welcome1, null);
        View view2 = getLayoutInflater().from(this).inflate(R.layout.viewpager_welcome2, null);
        View view3 = getLayoutInflater().from(this).inflate(R.layout.viewpager_welcome3, null);

        viewList = new ArrayList<View>();// 将要分页显示的View装入数组中
        viewList.add(view1);
        viewList.add(view2);
        viewList.add(view3);

        viewPager.setAdapter(new LodingViewPagerAdapter(viewList));

        view3.findViewById(R.id.viewpager_btn).setOnClickListener(startListener);
    }

    View.OnClickListener startListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            Util.startActivityNewTask(LoadingActivity.this, LoginActivity_.class);

//            SSDialogYesNo dialogYesNo = new SSDialogYesNo(LoadingActivity.this, new SSDialogYesNo.Callback() {
//                @Override
//                public void onConfirm() {
//                    configPref.getNewMessage().put(true);
//                    configPref.messageVoice().put(true);
//                    configPref.messageShake().put(true);
//                    showLoDialog();
//                }
//
//                @Override
//                public void onCancel() {
//                    configPref.getNewMessage().put(false);
//                    configPref.messageVoice().put(true);
//                    configPref.messageShake().put(true);
//                    showLoDialog();
//                }
//            });
//            dialogYesNo.setStringTitle("提示");
//            dialogYesNo.setStringContent("“刷刷手环”要给您发送推送通知\n" +
//                    "“通知”可以包括提醒、声音和图标上的标记。您可以在“设置”中进行配置");
//            dialogYesNo.setStringCancel("取消");
//            dialogYesNo.setStringConfirm("允许");
//            dialogYesNo.show();

        }
    };
//
//    private void showLoDialog() {
//        SSDialogYesNo dialogYesNo = new SSDialogYesNo(LoadingActivity.this, new SSDialogYesNo.Callback() {
//            @Override
//            public void onConfirm() {
//                if (!GpsUtil.isOPen(LoadingActivity.this)) {
//                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
//                    startActivityForResult(intent, Util.REQUEST_CODE_BT_SETTING);
//                } else {
//                    Util.startActivityNewTask(LoadingActivity.this, UserMainActivity_.class);
//                    finish();
//                }
//            }
//
//            @Override
//            public void onCancel() {
//                showCancelLoDialog();
//            }
//        });
//        dialogYesNo.setStringTitle("提示");
//        dialogYesNo.setStringContent("打开“定位服务”来允许“刷刷手环”确定您的位置\n" +
//                "刷刷手环使用定位服务来追踪您的运动与周边天气情况。");
//        dialogYesNo.setStringCancel("取消");
//        dialogYesNo.setStringConfirm("设置");
//        dialogYesNo.show();
//    }
//
//    private void showCancelLoDialog() {
//        SSDialogSure dialogYesNo = new SSDialogSure(LoadingActivity.this, new SSDialogSure.Callback() {
//            @Override
//            public void onConfirm() {
//                Util.startActivityNewTask(LoadingActivity.this, UserMainActivity_.class);
//                finish();
//            }
//
//        });
//        dialogYesNo.setStringTitle("定位服务已关闭");
//        dialogYesNo.setStringContent("如果您想追踪您的运动情况与周边天气情况，请前往：设置＞隐私＞定位服务，来允许刷刷手环使用您当前的位置信息");
//        dialogYesNo.setStringConfirm("好");
//        dialogYesNo.show();
//    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == Util.REQUEST_CODE_BT_SETTING) {
//            Util.startActivityNewTask(LoadingActivity.this, UserMainActivity_.class);
//            finish();
//        }
//    }

    @Override
    protected void initActivityName() {
        activityName = LoadingActivity.class.getName();
    }

    @Override
    protected void setActivityBg() {

    }

    private class LodingViewPagerAdapter extends PagerAdapter {
        private List<View> mListViews;

        public LodingViewPagerAdapter(List<View> mListViews) {
            this.mListViews = mListViews;//构造方法，参数是我们的页卡，这样比较方便。
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(mListViews.get(position));//删除页卡
        }


        @Override
        public Object instantiateItem(ViewGroup container, int position) {  //这个方法用来实例化页卡
            container.addView(mListViews.get(position), 0);//添加页卡
            return mListViews.get(position);
        }

        @Override
        public int getCount() {
            return mListViews.size();//返回页卡的数量
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;//官方提示这样写
        }
    }
}
