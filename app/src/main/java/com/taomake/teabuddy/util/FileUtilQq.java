package com.taomake.teabuddy.util;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Environment;
import android.view.View;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by fei_cen on 2017/6/5.
 */

public class FileUtilQq {

    public static Bitmap loadBitmapFromView(View v) {
        if (v == null) {
            return null;
        }
        v.setDrawingCacheEnabled(true);
        Bitmap screenshot;
        v.measure(View.MeasureSpec.makeMeasureSpec(v.getWidth(),
                View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(
                v.getHeight(), View.MeasureSpec.UNSPECIFIED));
        v.layout(0, 0, v.getMeasuredWidth(), v.getMeasuredHeight());
        v.buildDrawingCache();
        screenshot = v.getDrawingCache();
        if (screenshot == null) {
            v.setDrawingCacheEnabled(true);
            screenshot = Bitmap.createBitmap(v.getWidth(), v.getHeight(),
                    Bitmap.Config.ARGB_8888);
            Canvas c = new Canvas(screenshot);
            c.translate(-v.getScrollX(), -v.getScrollY());
            v.draw(c);
            return screenshot;
        }
        return screenshot;
    }


    /**
     * 保存图片到本地
     * */
    public static void saveBitmap(Bitmap bm) {
//        isHaveSDCard();
        File  filePath=null;
        if (isHaveSDCard()) {
            filePath = Environment.getExternalStorageDirectory();
        } else {
            filePath = Environment.getDataDirectory();
        }
        File file = new File(filePath.getPath() + "/teabuddymake/data/");
        if (!file.isDirectory()) {
            file.delete();
            file.mkdirs();
        }
        if (!file.exists()) {
            file.mkdirs();
        }
        String picName="qq_share.jpg";
//       Bitmap bm= loadBitmapFromView(view);
        writeBitmap(file.getPath(), picName, bm);

        return;
    }

    public static String   existQQshareIcon(){
        File  filePath=null;
        if (isHaveSDCard()) {
            filePath = Environment.getExternalStorageDirectory();
        } else {
            filePath = Environment.getDataDirectory();
        }
        File file = new File(filePath.getPath() + "/teabuddymake/data/qq_share.jpg");

        if (!file.exists()) {
            return null;
        }
        return filePath.getPath() + "/teabuddymake/data/qq_share.jpg";
    }


    /**
     * 保存图片
     *
     * @param path
     * @param name
     * @param bitmap
     */
    public static void writeBitmap(String path, String name, Bitmap bitmap) {
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }

        File _file = new File(path + name);
        if (_file.exists()) {
            _file.delete();
        }
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(_file);
            if (name != null && !"".equals(name)) {
                int index = name.lastIndexOf(".");
                if (index != -1 && (index + 1) < name.length()) {
                    String extension = name.substring(index + 1).toLowerCase();
                    if ("png".equals(extension)) {
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                    } else if ("jpg".equals(extension)
                            || "jpeg".equals(extension)) {
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 75, fos);
                    }
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static boolean isHaveSDCard() {
        String SDState = android.os.Environment.getExternalStorageState();
        if (SDState.equals(android.os.Environment.MEDIA_MOUNTED)) {
            return true;
        }
        return false;
    }
}
