package com.taomake.teabuddy.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.taomake.teabuddy.R;
import com.taomake.teabuddy.util.ImageUtil;
import com.uuzuche.lib_zxing.activity.CaptureFragment;
import com.uuzuche.lib_zxing.activity.CodeUtils;

/**
 * 定制化显示扫描界面
 */
public class SecondQrCode extends AppCompatActivity {

    private CaptureFragment captureFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // 隐藏状态栏
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_second);
        captureFragment = new CaptureFragment();
        // 为二维码扫描界面设置定制化界面
        CodeUtils.setFragmentArgs(captureFragment, R.layout.my_camera);
        captureFragment.setAnalyzeCallback(analyzeCallback);
        getSupportFragmentManager().beginTransaction().replace(R.id.fl_my_container, captureFragment).commit();

        initView();

        unionid=getIntent().getStringExtra("unionid");
//            String ticket=bundle.getString("result");
//         unionid=bundle.getString("unionid");


    }
    String unionid;


    public static boolean isOpen = false;

    private void initView() {
        TextView record_first_id = (TextView) findViewById(R.id.record_first_id);
        record_first_id.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent();
                intent.setClass(SecondQrCode.this, BeginRecordActivity_.class);
                intent.putExtra("unionid", unionid);

                startActivity(intent);
                finish();

            }
        });

        ImageView right_img_text = (ImageView) findViewById(R.id.right_img_text);
        right_img_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("image/*");
                startActivityForResult(intent, REQUEST_IMAGE);

            }
        });


    }


    /**
     * 二维码解析回调函数
     */
    CodeUtils.AnalyzeCallback analyzeCallback = new CodeUtils.AnalyzeCallback() {
        @Override
        public void onAnalyzeSuccess(Bitmap mBitmap, String result) {
            Intent resultIntent = new Intent();

//            Bundle bundle = resultIntent.getExtras();
////            String ticket=bundle.getString("result");
//            String unionid=bundle.getString("unionid");

            Log.e("Recv QRcode result", result);

            //跳到手机号页面
            Intent intent = new Intent(SecondQrCode.this, LoginByphoneActivity_.class);

            intent.putExtra("ticket",result);
            intent.putExtra("unionid",unionid);

            startActivity(intent);

            finish();



//            Bundle bundle = new Bundle();
//            bundle.putInt(CodeUtils.RESULT_TYPE, CodeUtils.RESULT_SUCCESS);
//            bundle.putString(CodeUtils.RESULT_STRING, result);
//            resultIntent.putExtras(bundle);
//            SecondQrCode.this.setResult(RESULT_OK, resultIntent);
//            SecondQrCode.this.finish();
        }

        @Override
        public void onAnalyzeFailed() {
//            Intent resultIntent = new Intent();
//            Bundle bundle = new Bundle();
//            bundle.putInt(CodeUtils.RESULT_TYPE, CodeUtils.RESULT_FAILED);
//            bundle.putString(CodeUtils.RESULT_STRING, "");
//            resultIntent.putExtras(bundle);
//            SecondQrCode.this.setResult(RESULT_OK, resultIntent);
            SecondQrCode.this.finish();
        }
    };
    public static final int REQUEST_IMAGE = 112;


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        /**
         * 处理二维码扫描结果
         */


        /**
         * 选择系统图片并解析
         */
         if (requestCode == REQUEST_IMAGE) {
            if (data != null) {
                Uri uri = data.getData();
                try {
                    CodeUtils.analyzeBitmap(ImageUtil.getImageAbsolutePath(this, uri), new CodeUtils.AnalyzeCallback() {
                        @Override
                        public void onAnalyzeSuccess(Bitmap mBitmap, String result) {
                            Log.e("Recv QRcode result", result);

                            //跳到手机号页面
                            Intent intent = new Intent(SecondQrCode.this, LoginByphoneActivity_.class);

                            intent.putExtra("ticket",result);
                            intent.putExtra("unionid", unionid);

                            startActivity(intent);

                            finish();

//                            Toast.makeText(SecondQrCode.this, "解析结果:" + result, Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void onAnalyzeFailed() {
                            Toast.makeText(SecondQrCode.this, "解析二维码失败", Toast.LENGTH_LONG).show();
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }


    }


    /**
     * 请求CAMERA权限码
     */
    public static final int REQUEST_CAMERA_PERM = 101;


//    /**
//     * EsayPermissions接管权限处理逻辑
//     * @param requestCode
//     * @param permissions
//     * @param grantResults
//     */
//    @Override
//    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//
//        // Forward results to EasyPermissions
//        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
//    }
//
//
//    @AfterPermissionGranted(REQUEST_CAMERA_PERM)
//    public void cameraTask(int viewId) {
//        if (EasyPermissions.hasPermissions(this, Manifest.permission.CAMERA)) {
//            // Have permission, do the thing!
//            onClick(viewId);
//        } else {
//            // Ask for one permission
//            EasyPermissions.requestPermissions(this, "需要请求camera权限",
//                    REQUEST_CAMERA_PERM, Manifest.permission.CAMERA);
//        }
//    }
//
//    @Override
//    public void onPermissionsGranted(int requestCode, List<String> perms) {
//        Toast.makeText(this, "执行onPermissionsGranted()...", Toast.LENGTH_SHORT).show();
//    }
//
//    @Override
//    public void onPermissionsDenied(int requestCode, List<String> perms) {
//        Toast.makeText(this, "执行onPermissionsDenied()...", Toast.LENGTH_SHORT).show();
//        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
//            new AppSettingsDialog.Builder(this, "当前App需要申请camera权限,需要打开设置页面么?")
//                    .setTitle("权限申请")
//                    .setPositiveButton("确认")
//                    .setNegativeButton("取消", null /* click listener */)
//                    .setRequestCode(REQUEST_CAMERA_PERM)
//                    .build()
//                    .show();
//        }
//    }

}
