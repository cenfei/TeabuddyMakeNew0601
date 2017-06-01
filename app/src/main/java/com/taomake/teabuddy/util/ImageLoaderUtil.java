package com.taomake.teabuddy.util;

import android.content.Context;
import android.graphics.Bitmap;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.taomake.teabuddy.R;


/**
 * Created by Sam on 2015/8/4.
 */
public class ImageLoaderUtil {
    private static DisplayImageOptions avatarOptions = null;
    private static DisplayImageOptions bannerOptions = null;
    private static DisplayImageOptions personalBannerOptions = null;
    private static DisplayImageOptions wechatOptions = null;
    private static DisplayImageOptions serverOptions = null;
    private static DisplayImageOptions runAdOptions = null;
    private static DisplayImageOptions medalOptions = null;

    public static void initAdLoader(Context context){

        ImageLoaderConfiguration loaderConfig = new ImageLoaderConfiguration.Builder(context)
                .imageDownloader(new BaseImageDownloader(context,2000,3000))
                .build();
        ImageLoader.getInstance().init(loaderConfig);
    }

    public static void initDefaulLoader(Context context){
        ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(context));
    }

    public static DisplayImageOptions getAvatarOptionsInstance(){
        if(avatarOptions == null){
            avatarOptions = new DisplayImageOptions.Builder()
            .showImageOnLoading(R.drawable.rounded_gray_wallet_img) //设置图片在下载期间显示的图片
            .showImageForEmptyUri(R.drawable.rounded_gray_wallet_img)//设置图片Uri为空或是错误的时候显示的图片
            .showImageOnFail(R.drawable.rounded_gray_wallet_img)  //设置图片加载/解码过程中错误时候显示的图片
            .cacheInMemory(false)//设置下载的图片是否缓存在内存中
            .cacheOnDisk(true)//设置下载的图片是否缓存在SD卡中
//                .imageScaleType(ImageScaleType.IN_SAMPLE_INT)//设置图片以如何的编码方式显示
            .bitmapConfig(Bitmap.Config.RGB_565)//设置图片的解码类型
            .displayer(new SimpleBitmapDisplayer())
            .build();//构建完成
        }
        return  avatarOptions;
    }

    public static DisplayImageOptions getBannerOptionsInstance(){
        if(bannerOptions == null){
            bannerOptions = new DisplayImageOptions.Builder()
                    .showImageOnLoading(R.drawable.rounded_gray_wallet_img) //设置图片在下载期间显示的图片
                    .showImageForEmptyUri(R.drawable.rounded_gray_wallet_img)//设置图片Uri为空或是错误的时候显示的图片
                    .showImageOnFail(R.drawable.rounded_gray_wallet_img)  //设置图片加载/解码过程中错误时候显示的图片
                    .cacheInMemory(true)//设置下载的图片是否缓存在内存中
                    .cacheOnDisk(true)//设置下载的图片是否缓存在SD卡中
//                .imageScaleType(ImageScaleType.IN_SAMPLE_INT)//设置图片以如何的编码方式显示
                    .bitmapConfig(Bitmap.Config.RGB_565)//设置图片的解码类型
                    .displayer(new SimpleBitmapDisplayer())
                    .build();//构建完成
        }
        return  bannerOptions;
    }

    public static DisplayImageOptions getPersonalBannerOptionsInstance(){
        if(personalBannerOptions == null){
            personalBannerOptions = new DisplayImageOptions.Builder()
                    .showImageOnLoading(R.drawable.rounded_gray_wallet_img) //设置图片在下载期间显示的图片
                    .showImageForEmptyUri(R.drawable.rounded_gray_wallet_img)//设置图片Uri为空或是错误的时候显示的图片
                    .showImageOnFail(R.drawable.rounded_gray_wallet_img)  //设置图片加载/解码过程中错误时候显示的图片
                    .cacheInMemory(true)//设置下载的图片是否缓存在内存中
                    .cacheOnDisk(true)//设置下载的图片是否缓存在SD卡中
//                .imageScaleType(ImageScaleType.IN_SAMPLE_INT)//设置图片以如何的编码方式显示
                    .bitmapConfig(Bitmap.Config.RGB_565)//设置图片的解码类型
                    .displayer(new SimpleBitmapDisplayer())
                    .build();//构建完成
        }
        return  personalBannerOptions;
    }

    public static DisplayImageOptions getRoundedOptionsInstance(int roundedNum){
        if(wechatOptions == null){
            wechatOptions = new DisplayImageOptions.Builder()
                    .showImageOnLoading(R.drawable.rounded_gray_wallet_img) //设置图片在下载期间显示的图片
                    .showImageForEmptyUri(R.drawable.rounded_gray_wallet_img)//设置图片Uri为空或是错误的时候显示的图片
                    .showImageOnFail(R.drawable.rounded_gray_wallet_img)  //设置图片加载/解码过程中错误时候显示的图片
                    .cacheInMemory(true)//设置下载的图片是否缓存在内存中
                    .cacheOnDisk(true)//设置下载的图片是否缓存在SD卡中
//                .imageScaleType(ImageScaleType.IN_SAMPLE_INT)//设置图片以如何的编码方式显示
                    .bitmapConfig(Bitmap.Config.RGB_565)//设置图片的解码类型
                    .displayer(new RoundedBitmapDisplayer(roundedNum))
                    .build();//构建完成
        }
        return  wechatOptions;
    }

    public static DisplayImageOptions getServerrOptionsInstance(){
        if(serverOptions == null){
            serverOptions = new DisplayImageOptions.Builder()
                    .showImageOnLoading(R.drawable.rounded_gray_wallet_img) //设置图片在下载期间显示的图片
                    .showImageForEmptyUri(R.drawable.rounded_gray_wallet_img)//设置图片Uri为空或是错误的时候显示的图片
                    .showImageOnFail(R.drawable.rounded_gray_wallet_img)  //设置图片加载/解码过程中错误时候显示的图片
                    .cacheInMemory(true)//设置下载的图片是否缓存在内存中
                    .cacheOnDisk(true)//设置下载的图片是否缓存在SD卡中
//                .imageScaleType(ImageScaleType.IN_SAMPLE_INT)//设置图片以如何的编码方式显示
                    .bitmapConfig(Bitmap.Config.RGB_565)//设置图片的解码类型
                    .displayer(new SimpleBitmapDisplayer())
                    .build();//构建完成
        }
        return  serverOptions;
    }

    public static DisplayImageOptions getRunAdOptionsInstance(){
        if(runAdOptions == null){
            runAdOptions = new DisplayImageOptions.Builder()
                    .showImageOnLoading(R.drawable.rounded_gray_wallet_img) //设置图片在下载期间显示的图片
                    .showImageForEmptyUri(R.drawable.rounded_gray_wallet_img)//设置图片Uri为空或是错误的时候显示的图片
                    .showImageOnFail(R.drawable.rounded_gray_wallet_img)  //设置图片加载/解码过程中错误时候显示的图片
                    .cacheInMemory(true)//设置下载的图片是否缓存在内存中
                    .cacheOnDisk(true)//设置下载的图片是否缓存在SD卡中
//                .imageScaleType(ImageScaleType.IN_SAMPLE_INT)//设置图片以如何的编码方式显示
                    .bitmapConfig(Bitmap.Config.RGB_565)//设置图片的解码类型
                    .displayer(new SimpleBitmapDisplayer())
                    .build();//构建完成
        }
        return  runAdOptions;
    }

    public static DisplayImageOptions getMedalOptionsInstance(){
        if(medalOptions == null){
            medalOptions = new DisplayImageOptions.Builder()
                    .showImageOnLoading(R.drawable.rounded_gray_wallet_img) //设置图片在下载期间显示的图片
                    .showImageForEmptyUri(R.drawable.rounded_gray_wallet_img)//设置图片Uri为空或是错误的时候显示的图片
                    .showImageOnFail(R.drawable.rounded_gray_wallet_img)  //设置图片加载/解码过程中错误时候显示的图片
                    .cacheInMemory(true)//设置下载的图片是否缓存在内存中
                    .cacheOnDisk(true)//设置下载的图片是否缓存在SD卡中
//                .imageScaleType(ImageScaleType.IN_SAMPLE_INT)//设置图片以如何的编码方式显示
                    .bitmapConfig(Bitmap.Config.RGB_565)//设置图片的解码类型
                    .displayer(new SimpleBitmapDisplayer())
                    .build();//构建完成
        }
        return  medalOptions;
    }
}
