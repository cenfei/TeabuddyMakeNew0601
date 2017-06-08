package com.taomake.teabuddy.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Picture;
import android.graphics.Point;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.media.ExifInterface;
import android.media.ThumbnailUtils;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.Toast;

import com.taomake.teabuddy.R;
import com.taomake.teabuddy.component.FoxToastInterface;
import com.taomake.teabuddy.prefs.ConfigPref;
import com.taomake.teabuddy.prefs.ConfigPref_;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * @author pc
 */
@SuppressLint("SimpleDateFormat")
public class Util {
    public static final int IphoneWidth = 320;
    public static final int IphoneHeight = 460;// 要480-电池蓝高（40/2）
    public static int hasBinding;

    public static Date timeStamp2Date(Long timestampString){
        Long timestamp = timestampString*1000;


//        String date = new java.text.SimpleDateFormat(formats).format(new java.util.Date(timestamp));
        return new Date(timestamp);
    }


    //0:提交待审,1:已审待设计,2:已设计待下 载,3:已下载待验收,4:已验收,5:已重新下单, -1:审核失败,-4:验收不通过待重新下单
    public static  String  getStateComment(int state){
        String comment=null;
        switch (state){

            case 0:comment="提交待审";
                break;
            case 1:comment="提交待审";
                break;
            case 2:comment="已审待设计";
                break;
            case 3:comment="已设计待下载";
                break;
            case 4:comment="已下载待验收";
                break;
            case 5:comment="已验收";
                break;
            case 6:comment="已重新下单";

                break;
            case 7:comment="等待付款";
                break;
            case 8:comment="付款处理中";

                break;
            case 9:comment="更换素材等待重新设计";
                break;
            case 10:comment="更换素材待下载";

                break;
            case -7:comment="订单关闭";
                break;
            case -8:comment="支付失败";

                break;
            case -1:comment="审核失败";
                break;
            case -4:comment="验收不通过待重新下单";
                break;

        }
        return  comment;

    }
    public static  int  getStateTextColor(int state){
        int comment=0;
        switch (state){

            case 0:comment= R.color.font_titie2;
                break;
            case 1:comment=R.color.order_result1;
                break;
            case 2:comment=R.color.order_result2;
                break;
            case 3:comment=R.color.order_result3;
                break;
            case 4:comment=R.color.order_result4;
                break;
            case 5:comment=R.color.order_result5;
                break;
            case 6:comment=R.color.order_result1;
                break;
            case 7:comment=R.color.order_result3;
                break;
            case 8:comment=R.color.order_result4;
                break;
            case 9:comment=R.color.order_result5;
                break;
            case 10:comment=R.color.order_result1;
                break;
            case -7:comment=R.color.order_result3;
                break;
            case -8:comment=R.color.order_result4;
                break;
            case -1:comment=R.color.order_result6;
                break;
            case -4:comment=R.color.order_result5;
                break;

        }
        return  comment;

    }

    public static String  getDate(Date date){
        Calendar calendar=Calendar.getInstance();
        calendar.setTime(date);

        return  calendar.get(Calendar.YEAR)+"";

    }
    public  static String  getMonthDate(Date date){
        Calendar calendar=Calendar.getInstance();
        calendar.setTime(date);
int  month=calendar.get(Calendar.MONTH);
        String monthStr=null;
        if(month<9){
            monthStr="0"+(month+1) ;
        }else{
            monthStr=(month+1)+"";

        }

        return monthStr+"-"+ calendar.get(Calendar.DATE);

    }


    public static PackageInfo getClientInfo(Context context) {

        PackageManager manager;

        PackageInfo info = null;

        manager = context.getPackageManager();

        try {

            info = manager.getPackageInfo(context.getPackageName(), 0);

        } catch (PackageManager.NameNotFoundException e) {

// TODO Auto-generated catch block

            e.printStackTrace();

        }
        return info;

    }
    public static boolean checkDeviceHasNavigationBar(Context context) {
        boolean hasNavigationBar = false;
        Resources rs = context.getResources();
        int id = rs.getIdentifier("config_showNavigationBar", "bool", "android");
        if (id > 0) {
            hasNavigationBar = rs.getBoolean(id);
        }
        try {
            Class systemPropertiesClass = Class.forName("android.os.SystemProperties");
            Method m = systemPropertiesClass.getMethod("get", String.class);
            String navBarOverride = (String) m.invoke(systemPropertiesClass, "qemu.hw.mainkeys");
            if ("1".equals(navBarOverride)) {
                hasNavigationBar = false;
            } else if ("0".equals(navBarOverride)) {
                hasNavigationBar = true;
            }
        } catch (Exception e) {

        }
        return hasNavigationBar;

    }

    public static String[] listToStringArray(List<String> lists) {

        String[] array = new String[lists.size()];
        int i = 0;
        for (String l : lists) {

            array[i] = l;
            i++;

        }
        return array;
    }

    public static String[] setsToStringArray(Set<String> lists) {

        String[] array = new String[lists.size()];
        int i = 0;
        for (String l : lists) {

            array[i] = l;
            i++;

        }
        return array;
    }

    public static byte[] Bitmap2Bytes(Bitmap bm) {
         ByteArrayOutputStream baos = new ByteArrayOutputStream();
         bm.compress(CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
         }
    public static boolean isWebchatAvaliable(Context context) {
        //检测手机上是否安装了微信
        try {
            context.getPackageManager().getPackageInfo("com.tencent.mm", PackageManager.GET_ACTIVITIES);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    /**
     * 缩放Bitmap图片
     **/
    public static Bitmap zoomBitmap(Bitmap bitmap, int width, int height) {
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        Matrix matrix = new Matrix();
        float scaleWidth = ((float) width / w);
        float scaleHeight = ((float) height / h);
        matrix.postScale(scaleWidth, scaleHeight);// 利用矩阵进行缩放不会造成内存溢出
        Bitmap newbmp = Bitmap.createBitmap(bitmap, 0, 0, w, h, matrix, true);
        return newbmp;
    }

    /**
     * Save image to the SD card
     **/
    public static void savePhotoToSDCard(Bitmap photoBitmap, String path, String photoName
    ) {
        if (android.os.Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED)) {
            File dir = new File(path);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            File photoFile = new File(path, photoName); //在指定路径下创建文件
            FileOutputStream fileOutputStream = null;
            try {
                fileOutputStream = new FileOutputStream(photoFile);
                if (photoBitmap != null) {
                    if (photoBitmap.compress(CompressFormat.PNG, 100,
                            fileOutputStream)) {
                        fileOutputStream.flush();
                    }
                }
            } catch (FileNotFoundException e) {
                photoFile.delete();
                e.printStackTrace();
            } catch (IOException e) {
                photoFile.delete();
                e.printStackTrace();
            } finally {
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    static public void startActivity(Context context, Class<?> cls) {
        Intent intent = new Intent(context, cls);
        context.startActivity(intent);
    }

    static public void startActivitySingleTop(Context context, Class<?> cls) {
        Intent intent = new Intent(context, cls);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);
    }

    static public void startActivityNewTask(Context context, Class<?> cls) {
        Intent intent = new Intent(context, cls);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    static public void outLogin(Context context,ConfigPref_ configPref){
        SharedPreferences.Editor editor =context.getSharedPreferences("dataUNIION", context.MODE_PRIVATE).edit();
        editor.putString("unionid","");
        editor.commit();
        configPref.userDeviceMac().put("");
        configPref.userUnion().put("");
        configPref.userDeviceId().put("");

    }


    static public void showToast(Context context, String msg) {
        if (context != null) {
            //当前app为刷刷才toast
            ActivityManager mActivityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            String topActivityName = mActivityManager.getRunningTasks(1).get(0).topActivity.getPackageName();
//            if (topActivityName.contains("ishuashua"))
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
        }
    }

    public static void Toast(Context context, String str) {
//        if (context != null && !TextUtils.isEmpty(str)) {
//            ActivityManager mActivityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
//            String topActivityName = mActivityManager.getRunningTasks(1).get(0).topActivity.getPackageName();
////            if (topActivityName.contains("ishuashua"))
//            Toast.makeText(context, str, Toast.LENGTH_SHORT).show();
//        }


        new FoxToastInterface().startProgressBar(context,str);


    }

    public static boolean isAppForeground(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        if (appProcesses == null) {
            return false;
        }
        final String packageName = context.getPackageName();
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND && appProcess.processName.equals(packageName)) {
                return true;
            }
        }
        return false;
    }

    public static boolean netState(Context context) {
        ConnectivityManager conn = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (conn == null) {
            return false;
        } else {
            NetworkInfo[] infos = conn.getAllNetworkInfo();
            if (infos != null) {
                for (NetworkInfo info : infos) {
                    if (info.getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static void Log(String str) {
        android.util.Log.d("zjyd", str);

    }

    public static int parseInt(String str) {
        try {
            return Integer.parseInt(str);
        } catch (Exception e) {
            return 0;
        }
    }

    public static double parseDouble(String str) {
        try {
            return Double.parseDouble(str);
        } catch (Exception e) {
            return 0;
        }

    }

//	public static void settingdilog(Context context, String[] string) {
//		final Dialog dialog = new Dialog(context);
//		dialog.setContentView(R.layout.settingdilog);
//		dialog.setTitle("请选择身高");
//		ListView view = (ListView) dialog.findViewById(R.id.listview);
//		ArrayAdapter<String> adapter = new ArrayAdapter<String>(context,
//				android.R.layout.simple_list_item_1, string);
//		view.setAdapter(adapter);
//		view.setSelection(50);
//		dialog.show();
//	}

    public static long parseLong(String str) {
        try {
            return Long.parseLong(str);
        } catch (Exception e) {
            return 0;
        }

    }

    public static float parseFloat(String str) {
        try {
            return Float.parseFloat(str);
        } catch (Exception e) {
            return 0;
        }

    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /***
     * 根据手机分辨率从dp的单位转换成px
     *
     * @param context
     * @param dpValue
     * @return
     */
    public static int dipToPx(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        int px = (int) (dpValue * scale + 0.5f);
//		int dp = px2dip(context, screenWidth);
//		if (dp > IphoneWidth) {// 如果宽转换成dp后大于320,那么要重新按比例计算下
//			px = px * dp / IphoneWidth;
//		}
        return px;
    }

    public static AlertDialog showDialog(Context context, String title,
                                         String message, String positiveStr,
                                         OnClickListener positiveListener, String negativeStr,
                                         OnClickListener negativeListener) {
        Builder builder = new Builder(context);
        if (title != null)
            builder.setTitle(title);
        if (message != null)
            builder.setMessage(message);
        builder.setPositiveButton(positiveStr, positiveListener);
        if (negativeStr != null)
            builder.setNegativeButton(negativeStr, negativeListener);
        return builder.show();
    }

    /*
     * 返回当前程序版本名
     */
    public static String getAppVersionName(Context context) {
        String versionName = "";
        try {
            // ---get the package info---
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
            versionName = pi.versionName;

            return versionName;
        } catch (Exception e) {
            // Log.e("VersionInfo", "Exception", e);
        }

        return versionName;
    }

    // 修改尺寸
    public static byte[] getIconByteArrayData(Bitmap bitmap, boolean needResize) {
        if (needResize) {
            bitmap = resizeBitmap(bitmap, 1024, Integer.MAX_VALUE);
        }
        return getIconByteArrayData(bitmap, CompressFormat.JPEG, 80);
    }

    public static byte[] getIconByteArrayData(Bitmap bitmap,
                                              CompressFormat format, int quality) {
        ByteArrayOutputStream out = null;
        try {
            out = new ByteArrayOutputStream();
            bitmap.compress(format, quality, out);
            out.close();
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return out.toByteArray();

    }

    /**
     * 保持长宽比缩小Bitmap
     *
     * @param bitmap
     * @param maxWidth
     * @param maxHeight
     * @return
     */
    public static Bitmap resizeBitmap(Bitmap bitmap, int maxWidth, int maxHeight) {
        int originWidth = bitmap.getWidth();
        int originHeight = bitmap.getHeight();
        // no need to resize
        if (originWidth < maxWidth && originHeight < maxHeight) {
            return bitmap;
        }
        int width = originWidth;
        int height = originHeight;
        // 若图片过宽, 则保持长宽比缩放图片
        if (originWidth > maxWidth) {
            width = maxWidth;
            double i = originWidth * 1.0 / maxWidth;
            height = (int) Math.floor(originHeight / i);
            try {
                bitmap = Bitmap
                        .createScaledBitmap(bitmap, width, height, false);
            } catch (OutOfMemoryError e) {
                System.gc();
                // Log.i("system", "gc");
                bitmap = Bitmap
                        .createScaledBitmap(bitmap, width, height, false);
            }

        }
        // 若图片过长, 则从上端截取
        if (height > maxHeight) {
            height = maxHeight;
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height);
        }

        return bitmap;
    }

    // 去字符
    public static String replaceStringBySpaceOrReturn(String orgStr,
                                                      boolean bSpace) {
        String replaceStr = bSpace ? " " : "\n";
        if (!orgStr.equals("") && orgStr != null) {
            if (orgStr.contains("\\\\n")) {
                orgStr = orgStr.replace("\\\\n", replaceStr);
            } else if (orgStr.contains("\\n")) {
                orgStr = orgStr.replace("\\n", replaceStr);
            } else if (orgStr.contains("\\r\\n")) {
                orgStr = orgStr.replace("\\r\\n", replaceStr);
            } else if (orgStr.contains("\r\n")) {
                orgStr = orgStr.replace("\r\n", replaceStr);
            } else if (orgStr.contains("<p>")) {
                orgStr = orgStr.replace("<p>", replaceStr);
            } else if (orgStr.contains("<br/>")) {
                orgStr = orgStr.replace("<br/>", replaceStr);
            } else if (orgStr.contains("<br />")) {
                orgStr = orgStr.replace("<br />", replaceStr);
            }

        }
        return orgStr;
    }

    // 判断手机号格式是否正确
    public static boolean isMobileNO(String mobiles) {
        Pattern p = Pattern
                .compile("^((13[0-9])|(14[57])|(15[0-35-9])|(17[06-8])|(18[0-9]))[0-9]{8}$");
        Matcher m = p.matcher(mobiles);

        return m.matches();
    }

    // 判断email格式是否正确
    public static boolean isEmail(String email) {
        String str = "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
        Pattern p = Pattern.compile(str);
        Matcher m = p.matcher(email);

        return m.matches();
    }

    public static String getTime() {
        Date date = new Date();
        String str = new SimpleDateFormat("yyyy-MM-dd").format(date);
        return str;

    }

    /**
     * 排行榜获取时间
     *
     * @param context
     * @return
     */
    public static String lbgetTime() {
        Date date = new Date();
        String str = new SimpleDateFormat("yyyyMMdd").format(date);
        return str;

    }

    /**
     * 获取登录用户token
     *
     * @param context
     * @return
     */
//	public static String getToken(Context context) {
//		try {
//			return context.getSharedPreferences(Constant.KEY_TOKEN,
//					Context.MODE_PRIVATE).getString(Constant.KEY_TOKEN, "");
//		} catch (Exception e) {
//			return "";
//		}
//	}


    /**
     * <li>功能描述：时间相减得到天数
     *
     * @param beginDateStr
     * @param endDateStr
     * @return long
     * @author Administrator
     */
    public static long getDaySub(String beginDateStr, String endDateStr) {
        long day = 0;
        SimpleDateFormat format = new SimpleDateFormat(
                "yyyyMMdd");
        Date beginDate;
        Date endDate;
        try {
            beginDate = format.parse(beginDateStr);
            endDate = format.parse(endDateStr);
            day = (endDate.getTime() - beginDate.getTime())
                    / (24 * 60 * 60 * 1000);
            // System.out.println("相隔的天数="+day);
        } catch (ParseException e) {
            // TODO 自动生成 catch 块
            e.printStackTrace();
        }
        return day;
    }

    public static byte[] getBitmapByte(Bitmap bitmap) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        bitmap.compress(CompressFormat.JPEG, 100, out);
        try {
            out.flush();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return out.toByteArray();
    }

    /**
     * @author 图片圆角工具
     */
    public static Bitmap getRoundCornerBitmap(Bitmap bitmap, float roundPX) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        Bitmap bitmap2 = Bitmap.createBitmap(width, height, Config.ARGB_4444);
        Bitmap bitmap3 = resizeBitmap(bitmap2, width, height);
        Canvas canvas = new Canvas(bitmap3);
        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, width, height);
        final RectF rectF = new RectF(rect);
        paint.setColor(color);
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        canvas.drawRoundRect(rectF, roundPX, roundPX, paint);

        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return bitmap2;
    }

    /**
     * 截屏
     *
     * @param context
     * @return
     */
    public static Bitmap captureScreen(Activity context) {
        View cv = context.getWindow().getDecorView();
        Bitmap bmp = Bitmap.createBitmap(cv.getWidth(), cv.getHeight(),
                Config.ARGB_8888);
        Canvas canvas = new Canvas(bmp);
        cv.draw(canvas);
        return bmp;
    }

    public static Bitmap takeScreenShot(Activity act) {
        if (act == null || act.isFinishing()) {
            return null;
        }

        // 获取当前视图的view
        View scrView = act.getWindow().getDecorView();
        scrView.setDrawingCacheEnabled(true);
        scrView.buildDrawingCache(true);

        // 获取状态栏高度
        Rect statuBarRect = new Rect();
        scrView.getWindowVisibleDisplayFrame(statuBarRect);
        int statusBarHeight = statuBarRect.top;
        int width = act.getWindowManager().getDefaultDisplay().getWidth();
        int height = act.getWindowManager().getDefaultDisplay().getHeight();

        Bitmap scrBmp = null;
        try {
            // 去掉标题栏的截图
            scrBmp = Bitmap.createBitmap(scrView.getDrawingCache(), 0,
                    statusBarHeight, width, height - statusBarHeight);
        } catch (IllegalArgumentException e) {
        }
        scrView.setDrawingCacheEnabled(false);
        scrView.destroyDrawingCache();
        return scrBmp;
    }

    /**
     * 截取webView快照(webView加载的整个内容的大小)
     *
     * @param webView
     * @return
     */
    public static Bitmap captureWebView(WebView webView) {
        Picture snapShot = webView.capturePicture();
        Bitmap bmp = null;
        if (snapShot != null && snapShot.getHeight() > 0 && snapShot.getWidth() > 0) {

            bmp = Bitmap.createBitmap(snapShot.getWidth(),
                    snapShot.getHeight(), Config.ARGB_8888);
            Canvas canvas = new Canvas(bmp);
            snapShot.draw(canvas);
        }
        return bmp;
    }

    public static Bitmap small(Bitmap bitmap) {
        Matrix matrix = new Matrix();
        matrix.postScale(0.3f, 0.3f); // 长和宽放大缩小的比例
        Bitmap resizeBmp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
                bitmap.getHeight(), matrix, true);
        return resizeBmp;
    }

    public static String twodb(String a) {
        BigDecimal b = new BigDecimal(Double.valueOf(a));
        double f1 = b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        return f1 + "";

    }

    /**
     * yyyyMMdd 转 yyyy-MM-dd
     */
    public static String ymd(String a) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        // 转换回时间
        Date d2 = null;
        try {
            d2 = sdf.parse(a);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }// 得到的值Fri Aug 02 00:00:00 CST 2013
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
        String s2 = sdf1.format(d2);
        return s2;

    }

    /**
     * yyyyMMdd 转 yyyy-MM-dd
     */
    public static String ymdmmxx(String a) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        // 转换回时间
        Date d2 = null;
        try {
            d2 = sdf.parse(a);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }// 得到的值Fri Aug 02 00:00:00 CST 2013
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String s2 = sdf1.format(d2);
        return s2;

    }

    /**
     * yyyyMMdd 转 MM/dd
     */
    public static String tasktime(String a) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        // 转换回时间
        Date d2 = null;
        try {
            d2 = sdf.parse(a);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }// 得到的值Fri Aug 02 00:00:00 CST 2013
        SimpleDateFormat sdf1 = new SimpleDateFormat("MM/dd");
        String s2 = sdf1.format(d2);
        return s2;

    }


    /**
     * yyyyMMdd 转 yyyy-MM-dd
     */
    public static String ymwz(String a) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        // 转换回时间
        Date d2 = null;
        try {
            d2 = sdf.parse(a);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }// 得到的值Fri Aug 02 00:00:00 CST 2013
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy年MM月dd日");
        String s2 = sdf1.format(d2);
        return s2;

    }


    /**
     * yyyyMMdd 转 yyyy-MM-dd
     */
    public static String ymdaa(String a) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        // 转换回时间
        Date d2 = null;
        try {
            d2 = sdf.parse(a);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }// 得到的值Fri Aug 02 00:00:00 CST 2013
        SimpleDateFormat sdf1 = new SimpleDateFormat("MM-dd HH:mm");
        String s2 = sdf1.format(d2);
        return s2;

    }

    /**
     * yyyyMMdd 转 MM月dd日
     */
    public static String readableMD(String a) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        // 转换回时间
        Date d2 = null;
        try {
            d2 = sdf.parse(a);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }// 得到的值Fri Aug 02 00:00:00 CST 2013
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(d2);
        return "" + (calendar.get(Calendar.MONTH) + 1) + "月" + calendar.get(Calendar.DAY_OF_MONTH) + "日";
    }

    public static String getCurrentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        Calendar calendar = Calendar.getInstance();
        Date date = calendar.getTime();
        return sdf.format(date);
    }

    /**
     * 获取某一天的前一天
     */
    public static String getBeforeDate(String d) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        Date date = null;
        try {
            date = sdf.parse(d);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        date = calendar.getTime();
        String str = sdf.format(date);
        return str;
    }

    /**
     * 获取某一天的前七天
     */
    public static String getBeforeDate_6(String d) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        Date date = null;
        try {
            date = sdf.parse(d);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_MONTH, -6);
        date = calendar.getTime();
        String str = sdf.format(date);
        return str;
    }

    /**
     * 获取某一天的前七天
     */
    public static String getBeforeDate_7(String d) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        Date date = null;
        try {
            date = sdf.parse(d);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_MONTH, -7);
        date = calendar.getTime();
        String str = sdf.format(date);
        return str;
    }

    /**
     * 获取某一天的后七天
     */
    public static String getAfterDate_7(String d) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        Date date = null;
        try {
            date = sdf.parse(d);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_MONTH, +7);
        date = calendar.getTime();
        String str = sdf.format(date);
        return str;
    }

    /**
     * 获取某一天的后七天
     */
    public static String getAfterDate_6(String d) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        Date date = null;
        try {
            date = sdf.parse(d);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_MONTH, +6);
        date = calendar.getTime();
        String str = sdf.format(date);
        return str;
    }

    /**
     * 获取某一天的后一天
     */
    public static String getAfterDate(String d) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        Date date = null;
        try {
            date = sdf.parse(d);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        date = calendar.getTime();
        String str = sdf.format(date);
        return str;
    }

    /**
     * 获取上个月
     */
    public static String getMonth_f(String d) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");
        Date date = null;
        try {
            date = sdf.parse(d);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MONTH, -1);
        date = calendar.getTime();
        String str = sdf.format(date);
        return str;
    }

    /**
     * 获取上个月
     */
    public static String getMonth_t(String d) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");
        Date date = null;
        try {
            date = sdf.parse(d);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MONTH, -1);
        date = calendar.getTime();
        String str = sdf.format(date);
        return str;
    }

    /**
     * 获取下个月
     */
    public static String getMonth_l(String d) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");
        Date date = null;
        try {
            date = sdf.parse(d);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MONTH, 1);
        date = calendar.getTime();
        String str = sdf.format(date);
        return str;
    }

    /**
     * 某日周的第一天
     */
//    public static String getweekfirst(String date) {
//        Date theDate = MyStringUtils.lbgetFormatDate(date);
//        Calendar c = Calendar.getInstance();
//        c.setTime(theDate);
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//        c.set(Calendar.DAY_OF_WEEK, 1);// 本周第一天，以星期日开始
//        return sdf.format(c.getTime());
//    }
//
//    /**
//     * 某日周的第一天（用于判断是否为本周）
//     */
//    public static String getweekfirst1(String date) {
//        Date theDate = MyStringUtils.lbgetFormatDate(date);
//        Calendar c = Calendar.getInstance();
//        c.setTime(theDate);
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
//        c.set(Calendar.DAY_OF_WEEK, 1);// 本周第一天，以星期日开始
//        return sdf.format(c.getTime());
//    }

//    /**
//     * 某日周的最后一天
//     */
//    public static String getweeklast(String date) {
//        Date theDate = MyStringUtils.lbgetFormatDate(date);
//        Calendar c = Calendar.getInstance();
//        c.setTime(theDate);
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//        c.set(Calendar.DAY_OF_WEEK, 7);// 本周最后一天
//        return sdf.format(c.getTime());
//    }
    public static Bitmap getImageThumbnail(String imagePath, int width,
                                           int height) {
        Bitmap bitmap = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Config.ARGB_4444;
        options.inPurgeable = true;
        options.inInputShareable = true;
        options.inJustDecodeBounds = true;
        // 获取这个图片的宽和高，注意此处的bitmap为null
        bitmap = BitmapFactory.decodeFile(imagePath, options);
        options.inJustDecodeBounds = false; // 设为 false
        // 计算缩放比
        int h = options.outHeight;
        int w = options.outWidth;
        int beWidth = w / width;
        int beHeight = h / height;
        int be = 1;
        if (beWidth < beHeight) {
            be = beWidth;
        } else {
            be = beHeight;
        }
        if (be <= 0) {
            be = 1;
        }
        options.inSampleSize = be;
        // 重新读入图片，读取缩放后的bitmap，注意这次要把options.inJustDecodeBounds 设为 false
        bitmap = BitmapFactory.decodeFile(imagePath, options);
        // 利用ThumbnailUtils来创建缩略图，这里要指定要缩放哪个Bitmap对象
        bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height,
                ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
        int degree = FileUtil.getExifOrientation(imagePath);
        if (degree == 90 || degree == 180 || degree == 270) {
            //Roate preview icon according to exif orientation
            Matrix matrix = new Matrix();
            matrix.postRotate(degree);
            bitmap = Bitmap.createBitmap(bitmap,
                    0, 0, width, height, matrix, true);
        } else {

        }
        return bitmap;
    }

    /**
     * 将图片的旋转角度置为0
     *
     * @param path
     * @return void
     * @Title: setPictureDegreeZero
     * @date 2012-12-10 上午10:54:46
     */
    private static void setPictureDegreeZero(String path) {
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            // 修正图片的旋转角度，设置其不旋转。这里也可以设置其旋转的角度，可以传值过去，
            // 例如旋转90度，传值ExifInterface.ORIENTATION_ROTATE_90，需要将这个值转换为String类型的
            exifInterface.setAttribute(ExifInterface.TAG_ORIENTATION, "no");
            exifInterface.saveAttributes();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public static boolean isValidNumber(String number) {
        if (number.equals("0")) {
            return false;
        }
        if (number.equals("0.0")) {
            return false;
        }
        return true;
//		int intNumber = Integer.parseInt(number);
//        return intNumber != 0;
    }

    public static int getCurrHour() {
//		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH", Locale.getDefault());
//		return Integer.parseInt(simpleDateFormat.format(new Date()));
        return Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
    }


//    public static void eventRegister(Object object) {
//        if (!EventBus.getDefault().isRegistered(object)) {
//            EventBus.getDefault().register(object);
//        }
//    }

//    public static void eventUnregister(Object object) {
//        if (EventBus.getDefault().isRegistered(object)) {
//            EventBus.getDefault().unregister(object);
//        }
//    }
//
//    public static void eventPost(Object event) {
//        EventBus.getDefault().post(event);
//    }
//
//    public static void removeUserEvent(UserEvent event) {
//        EventBus.getDefault().removeStickyEvent(event);
//    }

    public static int getScreenWidth(Context context) {
        Point point = new Point();
        WindowManager winManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        winManager.getDefaultDisplay().getSize(point);
        return point.x;
    }

    public static int getScreenHeight(Context context) {
        Point point = new Point();
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getSize(point);
        return point.y;
    }

    /**
     * 获取控件的高度，如果获取的高度为0，则重新计算尺寸后再返回高度
     *
     * @param view
     * @return
     */
    public static int getViewMeasuredHeight(View view) {
        calcViewMeasure(view);
        return view.getMeasuredHeight();
    }

    /**
     * 获取控件的宽度，如果获取的宽度为0，则重新计算尺寸后再返回宽度
     *
     * @param view
     * @return
     */
    public static int getViewMeasuredWidth(View view) {
        calcViewMeasure(view);
        return view.getMeasuredWidth();
    }

    /**
     * 测量控件的尺寸
     *
     * @param view
     */
    public static void calcViewMeasure(View view) {
        int width = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        int expandSpec = View.MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, View.MeasureSpec.AT_MOST);
        view.measure(width, expandSpec);
    }

    public final static int REQUEST_CODE_BT_SETTING = 1;


    public static void showNetworkError(Context context) {
        showToast(context, "网络错误");
    }

    /**
     *
     */
    public static String yymdh2sencod(String a) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        // 转换回时间
        Date d2 = null;
        try {
            d2 = sdf.parse(a);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }// 得到的值Fri Aug 02 00:00:00 CST 2013
        return twoDateDistance(d2.getTime());

    }

    public static String yyyyMMddFormat(String a) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        // 转换回时间
        Date d2 = null;
        try {
            d2 = sdf.parse(a);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }// 得到的值Fri Aug 02 00:00:00 CST 2013

        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        return sdf2.format(d2);

    }

    /**
     * 计算两个日期型的时间相差多少时间
     */
    public static String twoDateDistance(long c) {
        Date startDate = new Date(c);
        Date endDate = new Date(System.currentTimeMillis());
        if (startDate == null || endDate == null) {
            return null;
        }
        long timeLong = endDate.getTime() - startDate.getTime();
        if (timeLong < 60 * 1000)
            return timeLong / 1000 + "秒前";
        else if (timeLong < 60 * 60 * 1000) {
            timeLong = timeLong / 1000 / 60;
            return timeLong + "分钟前";
        } else if (timeLong < 60 * 60 * 24 * 1000) {
            timeLong = timeLong / 60 / 60 / 1000;
            return timeLong + "小时前";
        } else if (timeLong < 60 * 60 * 24 * 1000 * 7) {
            timeLong = timeLong / 1000 / 60 / 60 / 24;
            return timeLong + "天前";
        } else if (timeLong < 60 * 60 * 24 * 1000 * 7 * 4) {
            timeLong = timeLong / 1000 / 60 / 60 / 24 / 7;
            return timeLong + "周前";
        } else {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            sdf.setTimeZone(TimeZone.getTimeZone("GMT+08:00"));
            return sdf.format(startDate);
        }
    }

    /**
     * 返回时间
     */
    public static long tasktimeSystem(String a) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        // 转换回时间
        Date d2 = null;
        try {
            d2 = sdf.parse(a);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }// 得到的值Fri Aug 02 00:00:00 CST 2013

        return d2.getTime();

    }


    /**
     * yyyyMMdd 转 yyyy-MM-dd
     */
    public static String messageTime(long longtime) {
        Date date = new Date();
        String cur = new SimpleDateFormat("yyyyMMdd").format(date);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String s2 = sdf.format(longtime);
        if (s2.equals(cur)) {
            sdf = new SimpleDateFormat("HH:mm");
            s2 = sdf.format(longtime);
        } else {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.add(Calendar.DAY_OF_MONTH, -1);
            date = calendar.getTime();
            String str = sdf.format(date);
            if (str.equals(s2)) {
                s2 = "昨天";
            } else {
                sdf = new SimpleDateFormat("yyyy/MM/dd");
                s2 = sdf.format(longtime);
            }
        }
        return s2;

    }


    public static String messageDate(long longtime) {
        SimpleDateFormat sdf = new SimpleDateFormat("MM月dd日");
        String s2 = sdf.format(longtime);
        return s2;

    }

    /**
     * yyyyMMdd 转 yyyy-MM-dd
     */
    public static String ymdaaHealth(String a) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        // 转换回时间
        Date d2 = null;
        try {
            d2 = sdf.parse(a);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }// 得到的值Fri Aug 02 00:00:00 CST 2013
        SimpleDateFormat sdf1 = new SimpleDateFormat("MM-dd");
        String s2 = sdf1.format(d2);
        return s2;

    }

    public static boolean isOverTime(String time) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = null;
        try {
            date = sdf.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }// 得到的值Fri Aug 02 00:00:00 CST 2013

        Calendar calendar = Calendar.getInstance();
        Date date2 = calendar.getTime();

        return date.getTime() > date2.getTime() ? true : false;
    }

    public static String sportTime(long longtime) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(longtime);

    }

    public static String syncTime(long longtime) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        return sdf.format(longtime);
    }
}
