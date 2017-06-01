package com.taomake.teabuddy.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.webkit.DownloadListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.taomake.teabuddy.R;


public class WebviewCommonActivity extends Activity implements
        OnClickListener {

    private String url, title = null;

    private TextView title_text;

    private WebView wb;

    private LinearLayout title_left;

    private class MyWebViewDownLoadListener implements DownloadListener {

        @Override
        public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype,
                                    long contentLength) {
            Log.i("tag", "url=" + url);
            Log.i("tag", "userAgent=" + userAgent);
            Log.i("tag", "contentDisposition=" + contentDisposition);
            Log.i("tag", "mimetype=" + mimetype);
            Log.i("tag", "contentLength=" + contentLength);
            Uri uri = Uri.parse(url);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        this.requestWindowFeature(Window.FEATURE_NO_TITLE);// 去标题栏
//        setFullScreen();

//        quitFullScreen();

        setContentView(R.layout.webview_3d);


        findview();
    }


    String accessToken = null;
    String downLoad = null;

    RelativeLayout main_title_id;
    private void findview() {
        if (getIntent() != null) {
            title = getIntent().getStringExtra("title");
            url = getIntent().getStringExtra("url");
            downLoad = getIntent().getStringExtra("download");

            accessToken = getIntent().getStringExtra("accessToken");

        }
        main_title_id = (RelativeLayout) findViewById(R.id.main_title_id);


        title_left = (LinearLayout) findViewById(R.id.left_title_line);
        title_left.setOnClickListener(this);

        ImageView left_title_icon = (ImageView) findViewById(R.id.left_title_icon);
        left_title_icon.setVisibility(View.GONE);

        TextView left_title = (TextView) findViewById(R.id.left_title);
        left_title.setText("关闭");
        left_title.setVisibility(View.VISIBLE);

        TextView right_title = (TextView) findViewById(R.id.right_title);
        right_title.setVisibility(View.GONE);
        right_title.setText("VR模式");
        ImageView right_title_icon = (ImageView) findViewById(R.id.right_title_icon);

        right_title_icon.setVisibility(View.GONE);
        title_text = (TextView) findViewById(R.id.title);
        if (title != null) {
            title_text.setText(title);
        }
        wb = (WebView) findViewById(R.id.webview);
        wb.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);

        if (downLoad != null && !downLoad.equals("")) {
            wb.setDownloadListener(new MyWebViewDownLoadListener());
        }
        WebSettings webSettings = wb.getSettings();


//        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);//设置js可以直接打开窗口，如window.open()，默认为false
//        displayWebview.getSettings().setJavaScriptEnabled(true);//是否允许执行js，默认为false。设置true时，会提醒可能造成XSS漏洞
//        webSettings.setSupportZoom(true);//是否可以缩放，默认true
//        displayWebview.getSettings().setBuiltInZoomControls(true);//是否显示缩放按钮，默认false
//        displayWebview.getSettings().setUseWideViewPort(true);//设置此属性，可任意比例缩放。大视图模式
//        displayWebview.getSettings().setLoadWithOverviewMode(true);//和setUseWideViewPort(true)一起解决网页自适应问题
//        webSettings.setAppCacheEnabled(true);//是否使用缓存
//        webSettings.setDomStorageEnabled(true);//DOM Storage
//        webSettings.setUserAgentString("User-Agent:Android");//设置用户代理，一般不用


        //webSettings.setTextSize(WebSettings.TextSize.SMALLEST);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setBuiltInZoomControls(true);
        // Util.setZoomControlGone(wb);
//		webSettings.set
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);
        if (Build.VERSION.SDK_INT >= 16) {
            webSettings.setAllowUniversalAccessFromFileURLs(true);
        }

        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        //webSettings.setUseWideViewPort(true);//设定支持viewport
//		webSettings.setTextSize(R.dimen.bd_wallet_contact_name_width)
//        if (accessToken != null && !accessToken.equals("")) {
//            wb.setWebViewClient(new WebViewClient() {
//                public boolean shouldOverrideUrlLoading(WebView view, String url) { // 重写此方法表明点击网页里面的连接还是在当前的webview里跳转，不跳到浏览器那边
//                    view.loadUrl(url);
//                    return true;
//                }
//            });
//            if (url != null) {
//
//                accessToken = "accessToken=" + accessToken;
//                wb.postUrl(url, accessToken.getBytes());
//            }
//        } else {

            wb.setWebViewClient(
                    new WebViewClient() {


                        public boolean shouldOverrideUrlLoading(WebView view, String url) { // 重写此方法表明点击网页里面的连接还是在当前的webview里跳转，不跳到浏览器那边


                            view.loadUrl(url);
                            return true;
                        }
                    });

//            wb.setWebViewClient(new WebViewClient());
            if (url != null) {
                wb.loadUrl(url);

                right_title.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        setFullScreen();
                        wb.loadUrl("javascript:entervr()");

                    }
                });

            }

//        }
    }

    @Override
    public void onClick(View v) {

        if (v.equals(title_left)) {
            finish();
        }


    }

    public void onResume() {
        super.onResume();
    }

    public void onPause() {
        super.onPause();
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && wb.canGoBack()) {
            wb.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 屏幕旋转时调用此方法
     */
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        //newConfig.orientation获得当前屏幕状态是横向或者竖向
        //Configuration.ORIENTATION_PORTRAIT 表示竖向
        //Configuration.ORIENTATION_LANDSCAPE 表示横屏
        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            quitFullScreen();
            if (wb != null) {
                wb.loadUrl("javascript:exitvr()");
            }
            Toast.makeText(WebviewCommonActivity.this, "请横屏观看vr效果", Toast.LENGTH_SHORT).show();
        }
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            setFullScreen();
            Toast.makeText(WebviewCommonActivity.this, "马上进入vr模式", Toast.LENGTH_SHORT).show();
            if (wb != null) {
                wb.loadUrl("javascript:entervr()");
            }
        }
    }

    private void setFullScreen() {
//        this.requestWindowFeature(Window.FEATURE_NO_TITLE);// 去标题栏
        main_title_id.setVisibility(View.GONE);
        if (Build.VERSION.SDK_INT >= 19){
            View decorView = getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_LAYOUT_STABLE  //去掉好像无影响
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION //去掉好像无影响
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
            decorView.setSystemUiVisibility(option);
        }else {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
    }
//退出全屏函数：

    private void quitFullScreen() {
//        this.requestWindowFeature(Window.FEATURE_NO_TITLE);// 去标题栏
        main_title_id.setVisibility(View.VISIBLE);
        if (Build.VERSION.SDK_INT >= 19) {
            View decorView = getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_VISIBLE;
            decorView.setSystemUiVisibility(option);

        }else{


            final WindowManager.LayoutParams attrs = getWindow().getAttributes();
            attrs.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getWindow().setAttributes(attrs);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        }
    }
}