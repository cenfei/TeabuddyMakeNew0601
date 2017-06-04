package com.taomake.teabuddy.activity;

/**
 * Created by foxcen on 17/6/3.
 */

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.taomake.teabuddy.R;
import com.taomake.teabuddy.util.PermissionUtil;

/**
 * Created by qianxiaoai on 2016/7/8.
 */
public class PermissionActivity extends FragmentActivity implements ActivityCompat.OnRequestPermissionsResultCallback{
    private static final String TAG = PermissionActivity.class.getSimpleName();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permission);
//        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//        PermissionsFragment fragment = new PermissionsFragment();
//        transaction.replace(R.id.content_fragment, fragment);
//        transaction.commit();

    }

    /**
     * Called when the 'show camera' button is clicked.
     * Callback is defined in resource layout definition.
     */
    public void showCamera(View view) {
        Log.i(TAG, "Show camera button pressed. Checking permission.");
        PermissionUtil.requestPermission(this, PermissionUtil.CODE_CAMERA, mPermissionGrant);
    }

    public void getAccounts(View view) {
        PermissionUtil.requestPermission(this, PermissionUtil.CODE_GET_ACCOUNTS, mPermissionGrant);
    }

    public void callPhone(View view) {
        PermissionUtil.requestPermission(this, PermissionUtil.CODE_CALL_PHONE, mPermissionGrant);
    }

    public void readPhoneState(View view) {
        PermissionUtil.requestPermission(this, PermissionUtil.CODE_READ_PHONE_STATE, mPermissionGrant);
    }

    public void accessFineLocation(View view) {
        PermissionUtil.requestPermission(this, PermissionUtil.CODE_ACCESS_FINE_LOCATION, mPermissionGrant);
    }

    public void accessCoarseLocation(View view) {
        PermissionUtil.requestPermission(this, PermissionUtil.CODE_ACCESS_COARSE_LOCATION, mPermissionGrant);
    }

    public void readExternalStorage(View view) {
        PermissionUtil.requestPermission(this, PermissionUtil.CODE_READ_EXTERNAL_STORAGE, mPermissionGrant);
    }

    public void writeExternalStorage(View view) {
        PermissionUtil.requestPermission(this, PermissionUtil.CODE_WRITE_EXTERNAL_STORAGE, mPermissionGrant);
    }

    public void recordAudio(View view) {
        PermissionUtil.requestPermission(this, PermissionUtil.CODE_RECORD_AUDIO, mPermissionGrant);
    }


    private PermissionUtil.PermissionGrant mPermissionGrant = new PermissionUtil.PermissionGrant() {
        @Override
        public void onPermissionGranted(int requestCode) {
            switch (requestCode) {
                case PermissionUtil.CODE_RECORD_AUDIO:
                    Toast.makeText(PermissionActivity.this, "Result Permission Grant CODE_RECORD_AUDIO", Toast.LENGTH_SHORT).show();
                    break;
                case PermissionUtil.CODE_GET_ACCOUNTS:
                    Toast.makeText(PermissionActivity.this, "Result Permission Grant CODE_GET_ACCOUNTS", Toast.LENGTH_SHORT).show();
                    break;
                case PermissionUtil.CODE_READ_PHONE_STATE:
                    Toast.makeText(PermissionActivity.this, "Result Permission Grant CODE_READ_PHONE_STATE", Toast.LENGTH_SHORT).show();
                    break;
                case PermissionUtil.CODE_CALL_PHONE:
                    Toast.makeText(PermissionActivity.this, "Result Permission Grant CODE_CALL_PHONE", Toast.LENGTH_SHORT).show();
                    break;
                case PermissionUtil.CODE_CAMERA:
                    Toast.makeText(PermissionActivity.this, "Result Permission Grant CODE_CAMERA", Toast.LENGTH_SHORT).show();
                    break;
                case PermissionUtil.CODE_ACCESS_FINE_LOCATION:
                    Toast.makeText(PermissionActivity.this, "Result Permission Grant CODE_ACCESS_FINE_LOCATION", Toast.LENGTH_SHORT).show();
                    break;
                case PermissionUtil.CODE_ACCESS_COARSE_LOCATION:
                    Toast.makeText(PermissionActivity.this, "Result Permission Grant CODE_ACCESS_COARSE_LOCATION", Toast.LENGTH_SHORT).show();
                    break;
                case PermissionUtil.CODE_READ_EXTERNAL_STORAGE:
                    Toast.makeText(PermissionActivity.this, "Result Permission Grant CODE_READ_EXTERNAL_STORAGE", Toast.LENGTH_SHORT).show();
                    break;
                case PermissionUtil.CODE_WRITE_EXTERNAL_STORAGE:
                    Toast.makeText(PermissionActivity.this, "Result Permission Grant CODE_WRITE_EXTERNAL_STORAGE", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(final int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        PermissionUtil.requestPermissionsResult(this, requestCode, permissions, grantResults, mPermissionGrant);
    }
}
