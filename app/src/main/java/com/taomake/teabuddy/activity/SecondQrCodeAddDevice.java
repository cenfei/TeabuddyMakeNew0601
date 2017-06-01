package com.taomake.teabuddy.activity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;

import com.google.gson.Gson;
import com.taomake.teabuddy.R;
import com.taomake.teabuddy.component.FoxProgressbarInterface;
import com.taomake.teabuddy.network.ProtocolUtil;
import com.taomake.teabuddy.network.RowMessageHandler;
import com.taomake.teabuddy.object.BindDeviceCodeJson;
import com.taomake.teabuddy.object.BindDeviceObj;
import com.taomake.teabuddy.prefs.ConfigPref_;
import com.taomake.teabuddy.util.Constant;
import com.uuzuche.lib_zxing.activity.CaptureFragment;
import com.uuzuche.lib_zxing.activity.CodeUtils;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.sharedpreferences.Pref;

/**
 * 定制化显示扫描界面
 *
 */
@EActivity(R.layout.activity_add_device)

public class SecondQrCodeAddDevice extends AppCompatActivity {

    @Pref
    ConfigPref_ configPref;


    private CaptureFragment captureFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // 隐藏状态栏
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);

//        setContentView(R.layout.activity_add_device);

    }
    @AfterViews
    void init() {

        captureFragment = new CaptureFragment();
        // 为二维码扫描界面设置定制化界面
        CodeUtils.setFragmentArgs(captureFragment, R.layout.my_camera);
        captureFragment.setAnalyzeCallback(analyzeCallback);
        getSupportFragmentManager().beginTransaction().replace(R.id.fl_my_container, captureFragment).commit();

        initView();


    }



    public static boolean isOpen = false;

    private void initView() {
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.linear1);
        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isOpen) {
                    CodeUtils.isLightEnable(true);
                    isOpen = true;
                } else {
                    CodeUtils.isLightEnable(false);
                    isOpen = false;
                }

            }
        });
    }


    /**
     * 二维码解析回调函数
     */
    CodeUtils.AnalyzeCallback analyzeCallback = new CodeUtils.AnalyzeCallback() {
        @Override
        public void onAnalyzeSuccess(Bitmap mBitmap, String result) {


            getOnlineCode(configPref.userUnion().get(),result);

//            Bundle bundle = new Bundle();
//            bundle.putInt(CodeUtils.RESULT_TYPE, CodeUtils.RESULT_SUCCESS);
//            bundle.putString(CodeUtils.RESULT_STRING, result);
//            resultIntent.putExtras(bundle);
//            SecondQrCode.this.setResult(RESULT_OK, resultIntent);
//            SecondQrCodeAddDevice.this.finish();
        }

        @Override
        public void onAnalyzeFailed() {
//            Intent resultIntent = new Intent();
//            Bundle bundle = new Bundle();
//            bundle.putInt(CodeUtils.RESULT_TYPE, CodeUtils.RESULT_FAILED);
//            bundle.putString(CodeUtils.RESULT_STRING, "");
//            resultIntent.putExtras(bundle);
//            SecondQrCode.this.setResult(RESULT_OK, resultIntent);
            SecondQrCodeAddDevice.this.finish();
        }
    };


    //**********获取筛选的后的list***************/
    FoxProgressbarInterface foxProgressbarInterface;

    public void getOnlineCode(String unionid,String ticket) {
        foxProgressbarInterface = new FoxProgressbarInterface();
        foxProgressbarInterface.startProgressBar(this, "加载中...");

        ProtocolUtil.addDeviceInfo(this, new SendBackIdeaInfoHandler(), unionid, ticket);//devno 空表示所有


    }


    private class SendBackIdeaInfoHandler extends RowMessageHandler {
        @Override
        protected void handleResp(String resp) {
            getOnlineCodeHandler(resp);
        }
    }


    public void getOnlineCodeHandler(String resp) {
        foxProgressbarInterface.stopProgressBar();
        if (resp != null && !resp.equals("")) {


            BindDeviceCodeJson baseJson = new Gson().fromJson(resp, BindDeviceCodeJson.class);
            if ((baseJson.rcode + "").equals(Constant.RES_SUCCESS)) {
                BindDeviceObj checkCode = baseJson.obj;


                if(checkCode.mac!=null&&!checkCode.mac.equals("")){
                    configPref.userDeviceMac().put(checkCode.mac);
                    configPref.userDeviceId().put(checkCode.deviceid);
                }

                finish();
            }

        }
    }

}
