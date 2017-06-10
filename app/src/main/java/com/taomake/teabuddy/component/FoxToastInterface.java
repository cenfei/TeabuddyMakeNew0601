package com.taomake.teabuddy.component;

import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.text.TextUtils;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.taomake.teabuddy.R;


/**
 * Created by foxcen on 15/8/28.
 */
public class FoxToastInterface {

    final Handler writehandler = new Handler();


    int process = 1;
    protected Runnable runnable = new Runnable() {
        @Override
        public void run() {
            // TODO Auto-generated method stub
            //要做的事情
            if (process <= 1) {



                writehandler.postDelayed(this, 1000);
                process = process + 1;
            } else {
                process = 0;

                stopProgressBar();

                writehandler.removeCallbacks(runnable);
                if(foxToastCallback!=null)
                foxToastCallback.toastCloseCallbak();
            }
        }
    };


    public interface   FoxToastCallback{

        public void toastCloseCallbak();

    }
    FoxToastCallback  foxToastCallback ;

    public void startProgressBar(Context context,String comment,FoxToastCallback foxToastCallback) {
        dialog = new Dialog(context, R.style.myprocessstyle);
this.foxToastCallback=foxToastCallback;
        writehandler.post(runnable);

// 加载popuwindow 菊花
        dialog.setContentView(R.layout.popupwindow_toast);

        dialog.setCanceledOnTouchOutside(true);
        dialog.setCancelable(true);
        try {
            dialog.show();
        } catch (Exception e) {

        }

        // wallet_bind_line = (LinearLayout) dialog
        // .findViewById(R.id.wallet_bind_line);
        wallet_bind_image = (ImageView) dialog
                .findViewById(R.id.wallet_bind_image);
if(!TextUtils.isEmpty(comment)&&(comment.contains("成功")||comment.contains("完成"))){
    wallet_bind_image.setImageDrawable(context.getResources().getDrawable(R.drawable.rc_apply));
}else {
    wallet_bind_image.setImageDrawable(context.getResources().getDrawable(R.drawable.pc_delete));

}
        TextView wallet_sycn_comment = (TextView) dialog
                .findViewById(R.id.wallet_sycn_comment);
        wallet_sycn_comment.setText(comment);
//        startWaiting();


    }

    public void stopProgressBar() {
//        stopWaiting();
        if(dialog!=null&&dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    private Dialog dialog;
    private RotateAnimation rotateAnimation;
    private boolean isRotating = false;
    private ImageView wallet_bind_image;

    private void startWaiting() {

        if (rotateAnimation == null) {
            rotateAnimation = new RotateAnimation(0f, 360f,
                    Animation.RELATIVE_TO_SELF, 0.5f,
                    Animation.RELATIVE_TO_SELF, 0.5f);

            rotateAnimation.setDuration(1000);
            rotateAnimation.setRepeatCount(-1);
            rotateAnimation.setInterpolator(new LinearInterpolator());

            rotateAnimation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    isRotating = true;
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    isRotating = false;
                }
            });
        }

        if (!isRotating) {
            wallet_bind_image.startAnimation(rotateAnimation);
        }

    }

    private void stopWaiting() {
        if (rotateAnimation != null) {
            rotateAnimation.cancel();
        }

    }


//    public interface CallBackDialog {
//        void handleDialogResultOk(DialogInterface dialog, int which);
//        void handleDialogResultCancle(DialogInterface dialog, int which);
//
//    }


    private void initDots() {
//		LinearLayout ll = (LinearLayout) findViewById(R.id.ll);
//		dots = new ImageView[pics.length];
//		// 循环取得小点图片
//		for (int i = 0; i < pics.length; i++) {
//			dots[i] = (ImageView) ll.getChildAt(i);
//			dots[i].setEnabled(true);// 都设为灰色
//			dots[i].setOnClickListener(this);
//			dots[i].setTag(i);// 设置位置tag，方便取出与当前位置对应
//		}
//		currentIndex = 0;
//		dots[currentIndex].setEnabled(false);// 设置为白色，即选中状态
    }




    /**
     * 这只当前引导小点的选中
     */
    private void setCurDot(int positon) {
//		if (positon < 0 || positon > pics.length - 1 || currentIndex == positon) {
//			return;
//		}
//		dots[positon].setEnabled(false);
//		dots[currentIndex].setEnabled(true);
//		currentIndex = positon;
    }

}
