
package com.taomake.teabuddy.util;

import android.content.Context;
import android.media.ExifInterface;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.IOException;

public class FileUtil {
	 /** 
    * 检验SDcard状态 
    * @return boolean 
    */  
   public static boolean checkSDCard()  
   {  
       if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
       {  
           return true;  
       }else{  
           return false;  
       }  
   }  
   /** 
    * 保存文件文件到目录 
    * @param context 
    * @return  文件保存的目录 
    */  
   public static String setMkdir(Context context)
   {  
       String filePath;
       if(checkSDCard())  
       {  
           filePath = Environment.getExternalStorageDirectory()+ File.separator+"igotone";
       }else{  
           filePath = context.getCacheDir().getAbsolutePath()+ File.separator+"igotone";
       }  
       File file = new File(filePath);
       if(!file.exists())  
       {  
           boolean b = file.mkdirs();  
           Log.e("file", "文件不存在  创建文件    " + b);
       }else{  
           Log.e("file", "文件存在");
       }  
       return filePath;  
   }  
   
   
   public static int getExifOrientation(String filepath) {
       int degree = 0;
       ExifInterface exif = null;

       try {
           exif = new ExifInterface(filepath);
       } catch (IOException ex) {
          // MmsLog.e(ISMS_TAG, "getExifOrientation():", ex);
       }

       if (exif != null) {
           int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, -1);
           if (orientation != -1) {
               // We only recognize a subset of orientation tag values.
               switch (orientation) {
               case ExifInterface.ORIENTATION_ROTATE_90:
                   degree = 90;
                   break;

               case ExifInterface.ORIENTATION_ROTATE_180:
                   degree = 180;
                   break;

               case ExifInterface.ORIENTATION_ROTATE_270:
                   degree = 270;
                   break;
               default:
                   break;
               }
           }
       }

       return degree;
   }
   
}

