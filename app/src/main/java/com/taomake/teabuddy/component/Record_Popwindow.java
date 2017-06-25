package com.taomake.teabuddy.component;

import android.app.Activity;
import android.app.Dialog;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.taomake.teabuddy.R;

/**
 * Created by foxcen on 16/4/14.
 */
public class Record_Popwindow {

    private Integer chooseType = 1;//1代表刷刷钱包
    PopupWindow window;
     CallBackPayWindow callBackPayWindow;
    TextView  pop_process_record_id;
    final Handler writehandler = new Handler();

    int process = 1;
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            // TODO Auto-generated method stub
            //要做的事情
            if (process <=6) {

                pop_process_record_id.setText((6- process+""));

                writehandler.postDelayed(this, 1000);
                process = process + 1;
            } else {
                callBackPayWindow.handleCallBackPayWindowFromStop("src");


                writehandler.removeCallbacks(runnable);
                closePopupWindow(context);

            }
        }
    };




    /**
     * 显示popupWindow
     */

    public void showPopwindow(final Activity activity, View relView,CallBackPayWindow callBackPayWindow) {
        // 利用layoutInflater获得View
        this.callBackPayWindow=callBackPayWindow;
        context = activity;
//        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        View view = inflater.inflate(R.layout.cm_record_popwindow, null);
        initUi(activity, null, relView);
    }

    public void initColor() {

//        choose_photo_lib.setTextColor(context.getResources().getColor(R.color.line_hot_all));
//
//        open_camara.setTextColor(context.getResources().getColor(R.color.line_hot_all));
//
//        choose_photo_src.setTextColor(context.getResources().getColor(R.color.line_hot_all));
    }

    private Activity context;
//    TextView choose_photo_src, choose_photo_lib, open_camara;

    /**
     * @param view
     */
    Dialog dialog;
    public void initUi(final Activity activity, View view, View relView) {

//        pop_process_record_id = (TextView) view.findViewById(R.id.pop_process_record_id);
//
//        TextView pop_stop_record_id = (TextView) view.findViewById(R.id.pop_stop_record_id);
//        callBackPayWindow.handleCallBackPayWindowFromStart("src");
//
//        writehandler.post(runnable);
//        pop_stop_record_id.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                callBackPayWindow.handleCallBackPayWindowFromStop("src");
//                writehandler.removeCallbacks(runnable);
//                closePopupWindow(activity);
//
//            }
//        });

//        view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
//        int popupWidth = view.getMeasuredWidth();    //  获取测量后的宽度
//        int popupHeight = view.getMeasuredHeight();  //获取测量后的高度
        int[] location = new int[2];
        relView.getLocationOnScreen(location);

//        dialog = new Dialog(context, R.style.myprocessstyle);
//
////        writehandler.post(runnable);
//
//// 加载popuwindow 菊花
//        dialog.setContentView(view);
//
//        dialog.setCanceledOnTouchOutside(false);
//        dialog.setCancelable(false);
//        try {
//            dialog.show();
//        } catch (Exception e) {
//
//        }
//        AlertDialog dialog =new AlertDialog.Builder(context).create();
       SelectDialog dialog=new SelectDialog(context,R.style.myprocessstyle,callBackPayWindow);

//        dialog.setView(view);
        Log.e("location", "x:" + location[0] + "y:" + location[1]);

        WindowManager wm = context.getWindowManager();

        int width = wm.getDefaultDisplay().getWidth();
        int height = wm.getDefaultDisplay().getHeight();
                Window win = dialog.getWindow();
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        Log.e("width", "width:" +width + "height:" + height);

        layoutParams.x = location[0]-width/2+relView.getWidth()/2;//设置x坐标
        layoutParams.y =location[1]-height/2-relView.getHeight()*2+10;//设置y坐标
        win.setAttributes(layoutParams);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.show();

//        window = new PopupWindow(view,
//                WindowManager.LayoutParams.WRAP_CONTENT,
//                WindowManager.LayoutParams.WRAP_CONTENT);
//
//
//
//
//        window.setOutsideTouchable(true);
//        // 设置popWindow弹出窗体可点击，这句话必须添加，并且是true
//        window.setFocusable(true);
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//
//            try {
//                Method method = PopupWindow.class.getDeclaredMethod("setTouchModal", boolean.class);
//
//                method.setAccessible(true);
//                method.invoke(window, false);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//
//        }
//        // 实例化一个ColorDrawable颜色为半透明
//        ColorDrawable dw = new ColorDrawable(0x00000000);
////        window.setBackgroundDrawable(activity.getResources().getDrawable(R.drawable.rounded_choose));
////        window.setBackgroundDrawable( dw);
//
//
//        // 设置popWindow的显示和消失动画
//        //   window.setAnimationStyle(R.style.mypopwindow_anim_style);
//        backgroundAlpha(0.5f, activity);
//
//        relView.getLocationOnScreen(location);
////        window.setAnimationStyle(R.style.mypopwindow_anim_style);  //设置动画
////这里就可自定义在上方和下方了 ，这种方式是为了确定在某个位置，某个控件的左边，右边，上边，下边都可以
//        window.showAtLocation(relView, Gravity.NO_GRAVITY, (location[0] + relView.getWidth() / 2) - popupWidth / 2, location[1] - popupHeight + 10);
    }

    /**
     * 关闭窗口
     */
    public void closePopupWindow(Activity activity) {
//        WindowManager.LayoutParams params = activity.getWindow().getAttributes();
//        params.alpha = 1f;
//        activity.getWindow().setAttributes(params);
//
//        if (window != null) {
//            window.dismiss();
//            window = null;
//            System.out.println("popWindow消失 ...");
//
//        }
        if(dialog!=null) dialog.dismiss();
    }

    public interface CallBackPayWindow {

        void handleCallBackPayWindowFromStop(String recorddir);
        void handleCallBackPayWindowFromStart(String recorddir);

//        void handleCallBackPayWindowFromLib();

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

