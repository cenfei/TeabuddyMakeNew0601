package com.taomake.teabuddy.activity;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.DownloadListener;
import android.webkit.GeolocationPermissions;
import android.webkit.JsResult;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.taomake.teabuddy.R;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by zhang on 2015/8/7.
 */
@EActivity(R.layout.activity_webview)
public class WebViewActivity extends BaseActivity {

    private ValueCallback<Uri> mUploadMessage;
    private final int FILECHOOSER_RESULTCODE = 0x1124;
    private final int VIDEO_CAPTURE = 0x1125;

    Uri photoUri;

    @ViewById(R.id.left_btn)
    ImageView ivBack;

    @ViewById(R.id.title)
    TextView tvTitle;

    @ViewById(R.id.webview_main_layout)
    LinearLayout mainPage;

    @Extra
    String url;

    @Extra
    String title;

    @ViewById(R.id.shuashua_webview)
    WebView webView;

    @ViewById(R.id.progressbar_updown)
    ProgressBar progressBar;

//    @Pref
//    UserPref_ userPref;

    @AfterViews
    void init() {
        if (!TextUtils.isEmpty(title)) {
            tvTitle.setText(title);
        }

        // 设置支持JavaScript脚本
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        // 设置可以访问文件
        webSettings.setAllowFileAccess(true);
        webSettings.setBuiltInZoomControls(false);
        webSettings.setUseWideViewPort(true);//设置此属性，可任意比例缩放
        webSettings.setLoadWithOverviewMode(true);

        webSettings.setDatabaseEnabled(true);
        String dir = getApplicationContext().getDir("database", Context.MODE_PRIVATE).getPath();
        webSettings.setDatabasePath(dir);
        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);

        // 使用localStorage则必须打开
        webSettings.setDomStorageEnabled(true);
        webSettings.setAppCacheEnabled(true);

        webSettings.setGeolocationEnabled(true);

        if (Build.VERSION.SDK_INT >= 19) {
            webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        }

        if (Build.VERSION.SDK_INT >= 19) {
            webView.getSettings().setLoadsImagesAutomatically(true);
        } else {
            webView.getSettings().setLoadsImagesAutomatically(false);
        }
        // webSettings.setGeolocationDatabasePath(dir);

        webView.setWebViewClient(new WebViewClient() {

            @Override
            public void onReceivedError(WebView view, int errorCode,
                                        String description, String failingUrl) {
                //用javascript隐藏系统定义的404页面信息
                String data = "无法访问！";
                view.loadUrl("javascript:document.body.innerHTML=\"" + data + "\"");
            }


            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                try {
                    if (url.startsWith("tel:"))// 电话
                    {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                    } else if (url.startsWith("weixin:"))// 微信支付,未完成....
                    {
                        Intent intent = new Intent(Intent.ACTION_MAIN);
                        intent.addCategory(Intent.CATEGORY_LAUNCHER);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.setComponent(new ComponentName("com.tencent.mm", "com.tencent.mm.ui.LauncherUI"));
                        startActivity(intent);
                    } else
                        return false;
                } catch (ActivityNotFoundException ex) {
                    Toast.makeText(getApplicationContext(), "抱歉，没有相关应用！", Toast.LENGTH_SHORT).show();
                }
                return true;
            }

            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);

                if (!webView.getSettings().getLoadsImagesAutomatically()) {
                    webView.getSettings().setLoadsImagesAutomatically(true);
                }

                tvTitle.setText(webView.getTitle());
            }

            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }
        });

        // 设置WebChromeClient
        webView.setWebChromeClient(new WebChromeClient() {
            // 处理javascript中的alert
            public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {
                // 构建一个Builder来显示网页中的对话框
                Builder builder = new Builder(WebViewActivity.this);
                builder.setTitle("Alert");
                builder.setMessage(message);
                builder.setPositiveButton(android.R.string.ok, new AlertDialog.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        result.confirm();
                    }
                });
                builder.setCancelable(false);
                builder.create();
                builder.show();
                return true;
            }

            // 处理javascript中的confirm
            public boolean onJsConfirm(WebView view, String url, String message, final JsResult result) {
                Builder builder = new Builder(WebViewActivity.this);
                builder.setTitle("confirm");
                builder.setMessage(message);
                builder.setPositiveButton(android.R.string.ok, new AlertDialog.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        result.confirm();
                    }
                });
                builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        result.cancel();
                    }
                });
                builder.setCancelable(false);
                builder.create();
                builder.show();
                return true;
            }

            @Override
            // 设置网页加载的进度条
            public void onProgressChanged(WebView view, int newProgress) {
//                getWindow().setFeatureInt(Window.FEATURE_PROGRESS, newProgress * 1000);
                progressBar.setProgress(newProgress);
                if (newProgress == 100) {
                    progressBar.setVisibility(View.GONE);
                } else {
                    progressBar.setVisibility(View.VISIBLE);
                }
//                super.onProgressChanged(view, newProgress);
            }

            // 设置应用程序的标题title
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);

                if (!TextUtils.isEmpty(title)) {
                    tvTitle.setText(title);
                }
            }

            public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
                callback.invoke(origin, true, false);
                super.onGeolocationPermissionsShowPrompt(origin, callback);
            }

            public void openFileChooser(ValueCallback<Uri>
                                                uploadFile) {
                openFileChooser1(uploadFile, "");
            }

            public void openFileChooser(ValueCallback<Uri>
                                                uploadFile, String acceptType) {
                openFileChooser1(uploadFile, acceptType);
            }

            public void openFileChooser(ValueCallback<Uri>
                                                uploadFile, String acceptType,
                                        String capture) {
                openFileChooser1(uploadFile, acceptType);
            }

        });

        webView.setDownloadListener(new DownloadListener() {

            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype,
                                        long contentLength) {
                Uri uri = Uri.parse(url);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });

//        String postData = "accessToken=" + userPref.accessToken().get();
//        if (url.contains(".licaipu.cn/") || url.contains(".ishuashua.cn/") || url.contains(".ishuashua.com/") || url.contains(".18cai.com.cn/")) {
//            webView.postUrl(url, EncodingUtils.getBytes(postData, "base64"));
//        } else {
            webView.loadUrl(url);
//        }
    }

    public void openFileChooser1(ValueCallback<Uri>
                                         uploadFile, String type) {
        if (!TextUtils.isEmpty(type)) {
            if (type.equals("image/*")) {
                mUploadMessage = uploadFile;

                Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                SimpleDateFormat timeStampFormat = new SimpleDateFormat(
                        "yyyy_MM_dd_HH_mm_ss");
                String filename = timeStampFormat.format(new Date());
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.TITLE, filename);

                photoUri = getContentResolver().insert(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

                i.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                startActivityForResult(i, FILECHOOSER_RESULTCODE);

            } else if (type.equals("video/*")) {
                mUploadMessage = uploadFile;
                Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
                startActivityForResult(intent, VIDEO_CAPTURE);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == FILECHOOSER_RESULTCODE) {
            if (null == mUploadMessage) {
                return;
            }

            Uri result = data == null || resultCode != RESULT_OK ? null : data.getData();
            if (result == null) {
                if (photoUri != null) {
                    result = photoUri;
                }
            }
            mUploadMessage.onReceiveValue(result);
            mUploadMessage = null;
        } else if (requestCode == VIDEO_CAPTURE) {
            if (null == mUploadMessage) {
                return;
            }
            Uri result = data == null || resultCode != RESULT_OK ? null : data.getData();
            mUploadMessage.onReceiveValue(result);
            mUploadMessage = null;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Click(R.id.left_btn)
    void onLeftBtnClick() {
        finish();
    }
    @Click(R.id.close)
    void onFinish(){
        finish();
    }

    void goBack() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            finish();
        }
    }


    @Override
    protected void initActivityName() {
        activityName = WebViewActivity.class.getName() + ":" + title;
    }

    @Override
    protected void setActivityBg() {
//        if (BgTransitionUtil.bgDrawable != null) {
//            mainPage.setBackgroundDrawable(BgTransitionUtil.bgDrawable);
//        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //判断是否可以返回操作
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


}
