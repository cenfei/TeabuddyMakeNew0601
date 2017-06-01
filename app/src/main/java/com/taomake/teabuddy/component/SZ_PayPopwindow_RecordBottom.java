package com.taomake.teabuddy.component;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.taomake.teabuddy.R;
import com.taomake.teabuddy.util.Util;


/**
 * Created by foxcen on 16/4/14.
 */
public class SZ_PayPopwindow_RecordBottom {

    private Integer chooseType = 1;//1代表刷刷钱包
    PopupWindow window;
     CallBackPayWindow callBackPayWindow;
boolean boolFav;
    /**
     * 显示popupWindow
     */
    public void showPopwindow(final Activity activity, View relView,boolean boolFav, final CallBackPayWindow callBackPayWindow) {
        // 利用layoutInflater获得View
        this.callBackPayWindow=callBackPayWindow;
        this.boolFav=boolFav;
        context = activity;
        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.record_bottom_popwindow, null);
        initUi(activity, view, relView);
    }


    private Activity context;

    LinearLayout rb_play_line,rb_collect_line,rb_share_line,rb_apply_line;
    /**
     * @param view
     */
    public void initUi(final Activity activity, View view, View relView) {


        rb_play_line = (LinearLayout) view.findViewById(R.id.rb_play_line);
        rb_collect_line = (LinearLayout) view.findViewById(R.id.rb_collect_line);

        TextView   rb_collect_text = (TextView) view.findViewById(R.id.rb_collect_text);
        ImageView rb_collect_img = (ImageView) view.findViewById(R.id.rb_collect_img);

        if(!boolFav){//取消收藏
            rb_collect_text.setText("取消收藏");
            rb_collect_img.setImageDrawable(context.getResources().getDrawable(R.drawable.rc_cancle_collect));
        }
        rb_share_line = (LinearLayout) view.findViewById(R.id.rb_share_line);

        rb_apply_line = (LinearLayout) view.findViewById(R.id.rb_apply_line);


//        choose_photo_src.setText("从相册中选择");
        rb_play_line.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //从相册中选择
                callBackPayWindow.handleCallBackPlay();
                closePopupWindow(activity);

            }
        });

        rb_collect_line.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //从相册中选择
                callBackPayWindow.handleCallBackCollect();



                closePopupWindow(activity);

            }
        });
        rb_share_line.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //从相册中选择
                callBackPayWindow.handleCallBackShare();
                closePopupWindow(activity);

            }
        });
        rb_apply_line.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //从相册中选择
                callBackPayWindow.handleCallBackApply();
                closePopupWindow(activity);

            }
        });


        window = new PopupWindow(view,
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT);


        // 设置popWindow弹出窗体可点击，这句话必须添加，并且是true
        window.setFocusable(true);


        // 实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(0xff000000);
        window.setBackgroundDrawable(activity.getResources().getDrawable(R.drawable.rounded_choose));
//        window.setBackgroundDrawable(dw);


        // 设置popWindow的显示和消失动画
        window.setAnimationStyle(R.style.mypopwindow_anim_style);

//        window.showAsDropDown(relView, 0,
//                0);

        // 在底部显示
        if(Util.checkDeviceHasNavigationBar(context)){
            window.showAtLocation(relView,
                    Gravity.BOTTOM, 0, 130);
            setBackgroundAlpha(activity,0.6f);
        }else{
            window.showAtLocation(relView,
                    Gravity.BOTTOM, 0, 0);
            backgroundAlpha(0.6f, activity);

        }


        //popWindow消失监听方法
        window.setOnDismissListener(new PopupWindow.OnDismissListener() {

            @Override
            public void onDismiss() {
                System.out.println("popWindow消失");
                closePopupWindow(activity);
            }
        });
    }
    /***
     * 获取PopupWindow实例
     */
//    private void getPopupWindow() {
//
//        if (null != window) {
//            closePopupWindow();
//            return;
//        } else {
//            initPopuptWindow();
//        }
//    }

    /**
     * 关闭窗口
     */
    public void closePopupWindow(Activity activity) {
        WindowManager.LayoutParams params = activity.getWindow().getAttributes();
        params.alpha = 1f;
        activity.getWindow().setAttributes(params);

        if (window != null) {
            window.dismiss();
            window = null;
            System.out.println("popWindow消失 ...");

        }
    }

    public interface CallBackPayWindow {

        void handleCallBackPlay();

        void handleCallBackCollect();

        void handleCallBackShare();
        void handleCallBackApply();


//        void handleDialogResultCancle();

    }

    public static void backgroundAlpha(float bgAlpha, Activity activity) {
        WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
        lp.alpha = bgAlpha; //0.0-1.0
        activity.getWindow().setAttributes(lp);
    }
    /**
     * 设置页面的透明度
     * @param bgAlpha 1表示不透明
     */
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

}

