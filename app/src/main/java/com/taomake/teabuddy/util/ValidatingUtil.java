package com.taomake.teabuddy.util;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;

import com.taomake.teabuddy.R;
import com.taomake.teabuddy.component.CustomDialog;

import java.util.regex.Matcher;
import java.util.regex.Pattern;



/**
 * Created by xiaoganghu on 15/6/29.
 */
public class ValidatingUtil {

    public static boolean isEmpty(String string) {
        return string == null || string.length() == 0;
    }

    // 判断手机号格式是否正确
    public static boolean isMobileNO(String mobiles) {
        if (isEmpty(mobiles)) {
            return false;
        }

//        Pattern p = Pattern
//                .compile("^((13[0-9])|(14[5])|(14[7])|(17[^1,\\D])|(15[^4,\\D])|(18[0,0-9]))\\d{8}$");
//        Matcher m = p.matcher(mobiles);

        if (mobiles.length() == 11) {
            return true;
        }

        return false;
    }

    public static boolean isValidPasswd(String passwd) {
        if (isEmpty(passwd)) {
            return false;
        }

        Pattern p = Pattern.compile("[a-zA-Z0-9]{6,16}");
        Matcher m = p.matcher(passwd);
        return m.matches();
    }

    // 判断email格式是否正确
    public static boolean isEmail(String email) {
        String str = "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
        Pattern p = Pattern.compile(str);
        Matcher m = p.matcher(email);

        return m.matches();
    }

    public static String mobileByPasswd(String mobile) {
        return mobile.substring(0, 3) + " **** " + mobile.substring(7, 11);
    }

//    public static String getStringRes(Context context,int res_id) {
//        return context.getString(res_id);
//    }


    private static CustomDialog cDialog;

    private static boolean isDialogCanceled = false;
    private static Object dialogState = new Object();

    /**
     * 显示进度条  调用dialog.show()方法
     *
     * @param context
     * @param str
     * @return
     * @author Administrator
     */
    public static CustomDialog showDialog(final Context context, final String str) {
        try {
            if (context == null || !(context instanceof Activity)) {
                return null;
            }
            cancelDialog();
            isDialogCanceled = false;

            new Handler(context.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    synchronized (dialogState) {

                        if (context == null || !(context instanceof Activity)) {
                            return;
                        }

                        if (cDialog == null) {
                            synchronized (ValidatingUtil.class) {
                                if (cDialog == null) {
                                    cDialog = new CustomDialog(context, R.layout.progressbar_layout, R.style.loadingDialogStyle);// Dialog使用默认大小
                                }
                            }
                        }
                        TextView mMessage = (TextView) cDialog.findViewById(R.id.progress_loading_tv);
                        if (!TextUtils.isEmpty(str)) {
                            mMessage.setText(str);
                        }
                        if (cDialog != null && !cDialog.isShowing() && !((Activity) context).isFinishing() && !isDialogCanceled) {
                            try {
                                Log.i("ValidatingUtil", "showDialog");

                                cDialog.setCanceledOnTouchOutside(false);
                                cDialog.show();
                            } catch (Exception e) {
                                e.printStackTrace();
                                cancelDialog();
                            }
                        }
                    }
                }
            }, 3000);

            return cDialog;
        } catch (Exception e) {
            cancelDialog();
            try {
            } catch (Exception e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
        }
        return null;
    }

    public static void cancelDialog() {
        try {
            synchronized (dialogState) {
                isDialogCanceled = true;
                if (cDialog != null && cDialog.isShowing()) {
                    cDialog.dismiss();
                    Log.i("ValidatingUtil", "dialog is canceled");
                }
                cDialog = null;
            }
        } catch (Exception e) {
            cDialog = null;
            e.printStackTrace();
        }
    }
}
