package com.taomake.teabuddy.wheelviewdialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.taomake.teabuddy.R;
import com.taomake.teabuddy.wheelview.OnWheelChangedListener;
import com.taomake.teabuddy.wheelview.OnWheelScrollListener;
import com.taomake.teabuddy.wheelview.WheelView;
import com.taomake.teabuddy.wheelview.adapters.AbstractWheelTextAdapter;
import com.taomake.teabuddy.wheelview.adapters.ArrayWheelAdapter;

import java.util.ArrayList;

/**
 * Created by zhang on 2015/8/8.
 */
public class DialogWheelArrayInfo extends Dialog implements DialogInterface.OnDismissListener {
    private Activity context;
    private IWheelCallBack giftWheelCallBack;
    private OnDismissListener dismissCallback;
    private String initValue = "";
    private String chooseValue;
    private WheelView arrayWheel;
    //    private WheelView month;
//    private WheelView day;
    private String title;
    private boolean isConfirmed = false;

    private int maxTextSize = 18;
    private int minTextSize = 14;

    //    WheelDateAdapter yearAdapter;
//    WheelDateAdapter dayAdapter;
    ArrayWheelAdapter monthAdapter;
    private String[] arraysinfo;

    public DialogWheelArrayInfo(Activity context) {
        super(context);
        this.context = context;
    }

    public DialogWheelArrayInfo(Activity context, int theme, String title,String initValue,  String[] arraysinfo, IWheelCallBack giftWheelCallBack) {
        super(context, theme);
        this.context = context;
        chooseValue=initValue;

        this.initValue = initValue;
        this.arraysinfo = arraysinfo;
        this.giftWheelCallBack = giftWheelCallBack;
        this.title = title;
    }

    public DialogWheelArrayInfo(Activity context, int theme, String title, String initValue, String[] arraysinfo, IWheelCallBack giftWheelCallBack,
                                OnDismissListener dismissCallBack) {
        super(context, theme);
        this.context = context;
        this.arraysinfo = arraysinfo;
        chooseValue=initValue;
        this.initValue = initValue;
        this.giftWheelCallBack = giftWheelCallBack;
        this.dismissCallback = dismissCallBack;
        this.title = title;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.wheel_date);


//        month = (WheelView) findViewById(R.id.wheel_view_month);
        arrayWheel = (WheelView) findViewById(R.id.wheel_view_wv);
//        day = (WheelView) findViewById(R.id.wheel_view_day);


        int temp = 0;
        for (String a : arraysinfo) {

            if (a.equals(initValue)) {
                break;
            } else {
                temp++;
            }
        }

//


        OnWheelChangedListener wheellistener = new OnWheelChangedListener() {
            @Override
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                String currentText = (String) monthAdapter.getItemText(wheel.getCurrentItem());
                setTextviewSize(currentText, monthAdapter);
                chooseValue = currentText;


            }
        };


        OnWheelScrollListener onWheelScrollListener=new OnWheelScrollListener() {
            @Override
            public void onScrollingStarted(WheelView wheel) {
//                Log.e("start",wheel.getCurrentItem()+"");

            }

            @Override
            public void onScrollingFinished(WheelView wheel) {
//                Log.e("finish",wheel.getCurrentItem()+"");
                String currentText = (String) monthAdapter.getItemText(wheel.getCurrentItem());
                setTextviewSize(currentText, monthAdapter);
                chooseValue = currentText;
            }
        };

        monthAdapter = new ArrayWheelAdapter<String>(context, arraysinfo, R.id.wheel_text, temp,true);

        arrayWheel.setViewAdapter(monthAdapter);
        arrayWheel.setCurrentItem(temp);
        arrayWheel.addChangingListener(wheellistener);
        setOnDismissListener(this);
        arrayWheel.addScrollingListener(onWheelScrollListener);

        TextView titleTextView = (TextView) findViewById(R.id.wheel_title);
        titleTextView.setText(title);

        findViewById(R.id.wheel_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                giftWheelCallBack=null;
                dismiss();
            }
        });

        findViewById(R.id.wheel_confirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dismiss();
            }
        });
    }

    /**
     * Updates day wheel. Sets max days according to selected month and year
     */
//    void updateDays(WheelView year, WheelView month, WheelView day) {
//        Calendar calendar = Calendar.getInstance();
//        calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR) + year.getCurrentItem());
//        calendar.set(Calendar.MONTH, month.getCurrentItem());
//
//        int maxDays = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
//        int curDay = Math.min(maxDays, day.getCurrentItem() + 1);
//        dayAdapter = new WheelDateAdapter(context, 1, maxDays, "日", curDay - 1);
//        day.setViewAdapter(dayAdapter);
//        day.setCurrentItem(curDay - 1, true);
//    }
    public void showDialog() {
        try {
            windowDeploy();

            // 设置触摸对话框意外的地方取消对话框
            setCanceledOnTouchOutside(true);
            show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 设置窗口显示
    public void windowDeploy() {
        final Window w = getWindow();
        final WindowManager.LayoutParams wlps = w.getAttributes();
        wlps.width = WindowManager.LayoutParams.MATCH_PARENT;
        wlps.height = WindowManager.LayoutParams.WRAP_CONTENT;
//		wlps.dimAmount = 0.7f;
//		wlps.flags |= WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        wlps.gravity = Gravity.BOTTOM;
        w.setAttributes(wlps);
        setBackgroundAlpha(context,0.7f);
    }


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


    public interface IWheelCallBack {
        public void getWheelCallBack(String chooseValue);
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        WindowManager.LayoutParams params = context.getWindow().getAttributes();
        params.alpha = 1f;
        context.getWindow().setAttributes(params);
        if (giftWheelCallBack != null) {
            giftWheelCallBack.getWheelCallBack(chooseValue);
        }
        if (dismissCallback != null) {
            dismissCallback.onDismiss(dialog);
        }
    }

    /**
     * 设置字体大小
     *
     * @param curriteItemText
     * @param adapter
     */
    public void setTextviewSize(String curriteItemText, AbstractWheelTextAdapter adapter) {
        ArrayList<View> arrayList = adapter.getTestViews();
        int size = arrayList.size();
        String currentText;
        for (int i = 0; i < size; i++) {
            TextView textvew = (TextView) arrayList.get(i);
            currentText = textvew.getText().toString();
            if (curriteItemText.equals(currentText)) {
                textvew.setBackgroundColor(context.getResources().getColor(R.color.back_fb));
                textvew.setTextColor(context.getResources().getColor(R.color.line_hot_all));

                textvew.setTextSize(maxTextSize);
            } else {
                textvew.setTextSize(minTextSize);

                textvew.setTextColor(context.getResources().getColor(R.color.font_titie2));
                textvew.setBackgroundColor(context.getResources().getColor(R.color.white));

            }
        }
    }
}
