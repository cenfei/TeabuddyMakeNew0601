/**************************************************************************************************
 Filename:       DeviceActivityTea.java

 Copyright (c) 2013 - 2014 Texas Instruments Incorporated

 All rights reserved not granted herein.
 Limited License.

 Texas Instruments Incorporated grants a world-wide, royalty-free,
 non-exclusive license under copyrights and patents it now or hereafter
 owns or controls to make, have made, use, import, offer to sell and sell ("Utilize")
 this software subject to the terms herein.  With respect to the foregoing patent
 license, such license is granted  solely to the extent that any such patent is necessary
 to Utilize the software alone.  The patent license shall not apply to any combinations which
 include this software, other than combinations with devices manufactured by or for TI ('TI Devices').
 No hardware patent is licensed hereunder.

 Redistributions must preserve existing copyright notices and reproduce this license (including the
 above copyright notice and the disclaimer and (if applicable) source code license limitations below)
 in the documentation and/or other materials provided with the distribution

 Redistribution and use in binary form, without modification, are permitted provided that the following
 conditions are met:

 * No reverse engineering, decompilation, or disassembly of this software is permitted with respect to any
 software provided in binary form.
 * any redistribution and use are licensed by TI for use only with TI Devices.
 * Nothing shall obligate TI to provide you with source code for the software licensed and provided to you in object code.

 If software source code is provided to you, modification and redistribution of the source code are permitted
 provided that the following conditions are met:

 * any redistribution and use of the source code, including any resulting derivative works, are licensed by
 TI for use only with TI Devices.
 * any redistribution and use of any object code compiled from the source code and any resulting derivative
 works, are licensed by TI for use only with TI Devices.

 Neither the name of Texas Instruments Incorporated nor the names of its suppliers may be used to endorse or
 promote products derived from this software without specific prior written permission.

 DISCLAIMER.

 THIS SOFTWARE IS PROVIDED BY TI AND TI'S LICENSORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING,
 BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 IN NO EVENT SHALL TI AND TI'S LICENSORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
 OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 POSSIBILITY OF SUCH DAMAGE.


 **************************************************************************************************/
package com.taomake.teabuddy.sensoractivity;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ti.ble.btsig.profiles.DeviceInformationServiceProfile;
import com.example.ti.ble.common.BluetoothGATTDefines;
import com.example.ti.ble.common.BluetoothLeService;
import com.example.ti.ble.common.GattInfo;
import com.example.ti.ble.common.GenericBluetoothProfile;
import com.example.ti.ble.common.IBMIoTCloudProfile;
import com.example.ti.ble.sensortag.DeviceView;
import com.example.ti.ble.sensortag.FwUpdateActivity;
import com.example.ti.ble.sensortag.FwUpdateActivity_CC26xx;
import com.example.ti.ble.sensortag.PreferencesActivity;
import com.example.ti.ble.sensortag.PreferencesFragment;
import com.example.ti.ble.sensortag.SensorTagAccelerometerProfile;
import com.example.ti.ble.sensortag.SensorTagAmbientTemperatureProfile;
import com.example.ti.ble.sensortag.SensorTagBarometerProfile;
import com.example.ti.ble.sensortag.SensorTagDisplayProfile;
import com.example.ti.ble.sensortag.SensorTagHumidityProfile;
import com.example.ti.ble.sensortag.SensorTagIRTemperatureProfile;
import com.example.ti.ble.sensortag.SensorTagLuxometerProfile;
import com.example.ti.ble.sensortag.SensorTagMovementProfile;
import com.example.ti.ble.sensortag.SensorTagSimpleKeysProfile;
import com.example.ti.ble.sensortag.SensorTagTestProfile;
import com.example.ti.ble.sensortag.ViewPagerActivity;
import com.example.ti.ble.ti.profiles.TILampControlProfile;
import com.example.ti.ble.ti.profiles.TIOADProfile;
import com.example.ti.util.CustomToast;
import com.example.ti.util.PreferenceWR;
import com.google.gson.Gson;
import com.taomake.teabuddy.R;
import com.taomake.teabuddy.activity.DeviceUpdateTwoActivity;
import com.taomake.teabuddy.base.MainApp;
import com.taomake.teabuddy.component.One_Permission_Popwindow;
import com.taomake.teabuddy.object.DeviceVersionObj;
import com.taomake.teabuddy.util.Constant;
import com.taomake.teabuddy.util.MyStringUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import quinticble.QuinticBleAPISdkBase;

// import android.util.Log;

@SuppressLint("InflateParams") public class DeviceActivityTea extends ViewPagerActivity {
    // Log
    // private static String TAG = "DeviceActivityTea";

    // Activity
    public static final String EXTRA_DEVICE = "EXTRA_DEVICE";
    private static final int PREF_ACT_REQ = 0;
    private static final int FWUPDATE_ACT_REQ = 1;

    private DeviceView mDeviceView = null;

    // BLE
    private BluetoothLeService mBtLeService = null;
    private BluetoothDevice mBluetoothDevice = null;
    private BluetoothGatt mBtGatt = null;
    private List<BluetoothGattService> mServiceList = null;
    private boolean mServicesRdy = false;
    private boolean mIsReceiving = false;
    private IBMIoTCloudProfile mqttProfile;

    // SensorTagGatt
    private BluetoothGattService mOadService = null;
    private BluetoothGattService mConnControlService = null;
    private BluetoothGattService mTestService = null;
    private boolean mIsSensorTag2;
    private String mFwRev;
    public ProgressDialog progressDialog;

    //GUI
    private List<GenericBluetoothProfile> mProfiles;

    boolean mBleSupported=false;
    BluetoothAdapter mBtAdapter;
    IntentFilter mFilter;


    // Code to manage Service life cycle.
    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mBtLeService = ((BluetoothLeService.LocalBinder) service)
                    .getService();

            if (!mBtLeService.initialize()) {
                //Toast.makeText(context, "Unable to initialize BluetoothLeService", Toast.LENGTH_SHORT).show();
                //finish();
                return;
            }
//            onConnectFox();
            final int n = mBtLeService.numConnectedDevices();
            if (n > 0) {
                /*
                runOnUiThread(new Runnable() {
                    public void run() {
                        mThis.setError("Multiple connections!");
                    }
                });
                */
            } else {
                //startScan();
                // Log.i(TAG, "BluetoothLeService connected");
            }


            initDevice();




        }

        public void onServiceDisconnected(ComponentName componentName) {
            mBtLeService = null;
            // Log.i(TAG, "BluetoothLeService disconnected");
        }
    };

    String deviceVersionObjStr;
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public void  initDevice(){
        Intent intent = getIntent();

//        mBtLeService = BluetoothLeService.getInstance();
//        mBluetoothDevice = intent.getParcelableExtra(EXTRA_DEVICE);
        mBluetoothManager = mBtLeService.getBtManager();

//        String deviceName = "88:4A:EA:83:A5:62";

        String deviceName=getIntent().getStringExtra("MAC_DEVICE");

        deviceVersionObjStr=   intent.getStringExtra("deviceVersionObj");
        mBluetoothDevice = mBtLeService.mBtAdapter.getRemoteDevice(deviceName);

        int connState = mBluetoothManager.getConnectionState(mBluetoothDevice,
                BluetoothGatt.GATT);



        switch (connState) {
            case BluetoothGatt.STATE_CONNECTED:
//                mBtLeService.disconnect(null);
                mBtLeService.setGattInfo(QuinticBleAPISdkBase.getInstanceFactory(DeviceActivityTea.this).bluetoothGatt, mBluetoothDevice.getAddress());

//                    boolean ok = mBtLeService.connect(mBluetoothDevice.getAddress());
//                    if (!ok) {
//                        setError("Connect failed");
//                    }

                break;
            case BluetoothGatt.STATE_DISCONNECTED:
                boolean   ok = mBtLeService.connect(mBluetoothDevice.getAddress());
                if (!ok) {
                    setError("Connect failed");
                }


                break;
            default:
                setError("Device busy (connecting/disconnecting)");
                break;
        }



        mServiceList = new ArrayList<BluetoothGattService>();

        mIsSensorTag2 = false;
        // Determine type of SensorTagGatt
        if ((deviceName.equals("SensorTag2")) ||(deviceName.equals("CC2650 SensorTag"))) {
            mIsSensorTag2 = true;
        }
        else mIsSensorTag2 = false;

        PreferenceManager.setDefaultValues(this, com.example.ti.ble.sensortag.R.xml.preferences, false);
        // Log.i(TAG, "Preferences for: " + deviceName);

        // GUI
//        mDeviceView = new DeviceView();
//        mSectionsPagerAdapter.addSection(mDeviceView, "Sensors");
//        HelpView hw = new HelpView();
//        hw.setParameters("help_device.html", R.layout.fragment_help, R.id.webpage);
//        mSectionsPagerAdapter.addSection(hw, "Help");
        mProfiles = new ArrayList<GenericBluetoothProfile>();
        progressDialog = new ProgressDialog(DeviceActivityTea.this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setIndeterminate(true);
        progressDialog.setTitle("Discovering Services");
        progressDialog.setMessage("");
        progressDialog.setMax(100);
        progressDialog.setProgress(0);
//        progressDialog.show();

        // GATT database
        Resources res = getResources();
        XmlResourceParser xpp = res.getXml(com.example.ti.ble.sensortag.R.xml.gatt_uuid);
        new GattInfo(xpp);




        if (!mIsReceiving) {
            registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
            mIsReceiving = true;
        }
        for (GenericBluetoothProfile p : mProfiles) {
            if (p.isConfigured != true) p.configureService();
            if (p.isEnabled != true) p.enableService();
            p.onResume();
        }
        this.mBtLeService.abortTimedDisconnect();

        one_text_line=(TextView)   findViewById(com.example.ti.ble.sensortag.R.id.one_text_line);

        one_text_line.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DeviceVersionObj deviceVersionObj=new Gson().fromJson(deviceVersionObjStr, DeviceVersionObj.class);


                downloadBin(deviceVersionObj.url, Constant.path_bin_name);

            }
        });
        LinearLayout  left_title_line=(LinearLayout)   findViewById(com.example.ti.ble.sensortag.R.id.left_title_line);

        left_title_line.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

               finishRequest();

            }
        });


    }

    @Override
    public void onBackPressed() {
        finishRequest();
    }

    TextView one_text_line;

    public void finishRequest() {
        MainApp mainApp = (MainApp) getApplicationContext();

        if (mainApp.boolupdateSuccess == 1) {
            new One_Permission_Popwindow().showPopwindow(DeviceActivityTea.this, one_text_line, "确认退出吗？固件已经进入升级模式，请勿离开", "确认", "取消", new One_Permission_Popwindow.CallBackPayWindow() {
                @Override
                public void handleCallBackChangeUser() {//确定

                    MyStringUtils.closeBle();
                    finish();

                }

                @Override
                public void handleCallBackBindDevice() {

                }
            });
        } else {
            finish();
        }
    }

//下载bin

    public void downloadBin(final String urlStr, final String fileName) {

        new Thread(new Runnable() {
            @Override
            public void run() {
//                String fileName = "2.mp3";
                OutputStream output = null;
                String pathName = null;
                try {
                /*
                 * 通过URL取得HttpURLConnection
                 * 要网络连接成功，需在AndroidMainfest.xml中进行权限配置
                 * <uses-permission android:name="android.permission.INTERNET" />
                 */
                    URL url = new URL(urlStr);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    //取得inputStream，并将流中的信息写入SDCard

                /*
                 * 写前准备
                 * 1.在AndroidMainfest.xml中进行权限配置
                 * <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
                 * 取得写入SDCard的权限
                 * 2.取得SDCard的路径： Environment.getExternalStorageDirectory()
                 * 3.检查要保存的文件上是否已经存在
                 * 4.不存在，新建文件夹，新建文件
                 * 5.将input流中的信息写入SDCard
                 * 6.关闭流
                 */
                    String path=Constant.path;
                    String SDCard = Environment.getExternalStorageDirectory() + "";
                    pathName = SDCard + "/" + path + "/" + fileName;//文件存储路径

                    File file = new File(pathName);
                    InputStream input = conn.getInputStream();
                    if (file.exists()) {
                        System.out.println("exits");
//                        file.delete();
//                        return;
                    } else {
                        String dir = SDCard + "/" + path;
                        new File(dir).mkdir();//新建文件夹
                        file.createNewFile();//新建文件

                    }

                    output = new FileOutputStream(file);
                    byte[] voice_bytes = new byte[1024];
                    int len1 = -1;
                    while ((len1 = input.read(voice_bytes)) != -1) {
                        output.write(voice_bytes, 0, len1);
                        output.flush();

                    }

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        output.close();
                        System.out.println("success");
                    } catch (IOException e) {
                        System.out.println("fail");
                        e.printStackTrace();
                    }
                }


                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                                        mBtGatt = BluetoothLeService.getBtGatt();
                mBtGatt.discoverServices();
                    }
                });

//                Message msg = myHandler.obtainMessage();
//
//                Bundle bundle = new Bundle();
//                bundle.putString("pathname", pathName);
//                msg.setData(bundle);
//                myHandler.sendMessage(msg);

            }

        }).start();
        // String urlStr="http://172.17.54.91:8080/download/1.mp3";


    }






    private void startBluetoothLeService() {
        boolean f;

        Intent bindIntent = new Intent(this, BluetoothLeService.class);
        startService(bindIntent);
        f = bindService(bindIntent, mServiceConnection, Context.BIND_AUTO_CREATE);
        if (!f) {
            CustomToast.middleBottom(this, "Bind to BluetoothLeService failed");
            //finish();
        }
    }


    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
                // Bluetooth adapter state change
                switch (mBtAdapter.getState()) {
                    case BluetoothAdapter.STATE_ON:
                        //ConnIndex = NO_DEVICE;
                        startBluetoothLeService();
                        break;
                    case BluetoothAdapter.STATE_OFF:
                        //Toast.makeText(context, R.string.app_closing, Toast.LENGTH_LONG)
                        //        .show();
                        //finish();
                        break;
                    default:
                        // Log.w(TAG, "Action STATE CHANGED not processed ");
                        break;
                }
            }
        }
    };
    BluetoothManager mBluetoothManager;
    public void getLeservice()
    {
        // Use this check to determine whether BLE is supported on the device. Then
        // you can selectively disable BLE-related features.
        if (!getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, com.example.ti.ble.sensortag.R.string.ble_not_supported, Toast.LENGTH_LONG)
                    .show();
            mBleSupported = false;
        }

        // Initializes a Bluetooth adapter. For API level 18 and above, get a
        // reference to BluetoothAdapter through BluetoothManager.
        mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBtAdapter = mBluetoothManager.getAdapter();

        // Checks if Bluetooth is supported on the device.
        if (mBtAdapter == null) {
            Toast.makeText(this, com.example.ti.ble.sensortag.R.string.bt_not_supported, Toast.LENGTH_LONG).show();
            mBleSupported = false;
            return;
        }

        mFilter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(mReceiver, mFilter);

        if (!mBtAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            enableIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(enableIntent);
        }

        startBluetoothLeService();
    }



    public DeviceActivityTea() {
        mResourceFragmentPager = com.example.ti.ble.sensortag.R.layout.fragment_pager;
        mResourceIdPager = com.example.ti.ble.sensortag.R.id.pager;
        mFwRev = new String("1.5"); // Assuming all SensorTags are up to date until actual FW revision is read
    }

    public static DeviceActivityTea getInstance() {
        return (DeviceActivityTea) mThis;
    }
String upversion;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        super.onCreate(savedInstanceState);

        setContentView(com.example.ti.ble.sensortag.R.layout.sensor_device_update_two);



        TextView tea_os_version = (TextView) findViewById(R.id.tea_os_version);
        TextView text_db_size = (TextView) findViewById(R.id.text_db_size);


         upversion=getIntent().getStringExtra("upversion");
      String  sysdownloadsize=getIntent().getStringExtra("sysdownloadsize");

        if(!TextUtils.isEmpty(upversion)) {
            tea_os_version.setText("Cha OS " +upversion);
            text_db_size.setText(sysdownloadsize);
        }else{
            tea_os_version.setText("Cha OS 1.0");
            text_db_size.setText("0kb");

        }

        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter
                .getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            Toast.makeText(DeviceActivityTea.this, "本机没有找到蓝牙硬件或驱动！", Toast.LENGTH_SHORT).show();
        }
        // 如果本地蓝牙没有开启，则开启
        if (!mBluetoothAdapter.isEnabled()) {
            mBluetoothAdapter.enable();
            try {
                Thread.sleep(3000);
                getLeservice();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }else {
            getLeservice();
            // BLE
        }

    }


    //下载bin 文件





    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mqttProfile != null) {
            mqttProfile.disconnect();

        }
        if (mIsReceiving) {
            unregisterReceiver(mGattUpdateReceiver);
            mIsReceiving = false;
        }
        for (GenericBluetoothProfile p : mProfiles) {
            p.onPause();
        }
        if (!this.isEnabledByPrefs("keepAlive")) {
            this.mBtLeService.timedDisconnect();
        }
        //View should be started again from scratch
//        this.mDeviceView.first = true;
        this.mProfiles = null;
//        this.mDeviceView.removeRowsFromTable();
//        this.mDeviceView = null;
        finishActivity(PREF_ACT_REQ);
        finishActivity(FWUPDATE_ACT_REQ);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.optionsMenu = menu;
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(com.example.ti.ble.sensortag.R.menu.device_activity_actions, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        int i = item.getItemId();
        if (i == com.example.ti.ble.sensortag.R.id.opt_prefs) {
            startPreferenceActivity();

        } else if (i == com.example.ti.ble.sensortag.R.id.opt_about) {
            openAboutDialog();

        } else {
            return super.onOptionsItemSelected(item);
        }
        return true;
    }
    public boolean isEnabledByPrefs(String prefName) {
        String preferenceKeyString = "pref_"
                + prefName;

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mBtLeService);

        Boolean defaultValue = true;
        return prefs.getBoolean(preferenceKeyString, defaultValue);
    }
    @Override
    protected void onResume() {
        // Log.d(TAG, "onResume");
        super.onResume();

    }

    @Override
    protected void onPause() {
        Log.d("TAG", "onPause");
        super.onPause();
    }
    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter fi = new IntentFilter();
        fi.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        fi.addAction(BluetoothLeService.ACTION_DATA_NOTIFY);
        fi.addAction(BluetoothLeService.ACTION_DATA_WRITE);
        fi.addAction(BluetoothLeService.ACTION_DATA_READ);
        fi.addAction(DeviceInformationServiceProfile.ACTION_FW_REV_UPDATED);
        fi.addAction(TIOADProfile.ACTION_PREPARE_FOR_OAD);
        return fi;
    }

    void onViewInflated(View view) {
        // Log.d(TAG, "Gatt view ready");
        setBusy(true);

        // Set title bar to device name
        setTitle(mBluetoothDevice.getName());

        // Create GATT object
        mBtGatt = BluetoothLeService.getBtGatt();

        PreferenceWR p = new PreferenceWR(mBluetoothDevice.getAddress(),this);
        if (p.getBooleanPreference(PreferenceWR.PREFERENCEWR_NEEDS_REFRESH) == true) {
            Log.d("DeviceActivityTea", "Need to refresh device cache, calling refreshDeviceCache()");
            progressDialog.setTitle("Refreshing device cache ");
            boolean refresh = this.mBtLeService.refreshDeviceCache(this.mBtGatt);
            //We need a wait here, because this takes time ...
            if (refresh == true) {
                if (!mServicesRdy && mBtGatt != null) {
                    if (mBtLeService.getNumServices() == 0) {
                        progressDialog.setTitle("Refreshing device cache ");
                        discoverServices();
                    }
                    else {
                    }
                }
            }
            p.setBooleanPreference(PreferenceWR.PREFERENCEWR_NEEDS_REFRESH,false);
        }
        else {
            // Start service discovery
            if (!mServicesRdy && mBtGatt != null) {
                if (mBtLeService.getNumServices() == 0)
                    discoverServices();
                else {
                }
            }
        }
    }

    boolean isSensorTag2() {
        return mIsSensorTag2;
    }

    String firmwareRevision() {
        return mFwRev;
    }
    BluetoothGattService getOadService() {
        return mOadService;
    }

    BluetoothGattService getConnControlService() {
        return mConnControlService;
    }
    BluetoothGattService getTestService() {
        return mTestService;
    }

    private void startPreferenceActivity() {
        // Launch preferences
        final Intent i = new Intent(this, PreferencesActivity.class);
        i.putExtra(PreferencesActivity.EXTRA_SHOW_FRAGMENT,
                PreferencesFragment.class.getName());
        i.putExtra(PreferencesActivity.EXTRA_NO_HEADERS, true);
        i.putExtra(EXTRA_DEVICE, mBluetoothDevice);
        startActivityForResult(i, PREF_ACT_REQ);
    }

    private void discoverServices() {
        if (mBtGatt.discoverServices()) {
            mServiceList.clear();
            setBusy(true);

        } else {

        }
    }

    private void setBusy(boolean b) {
//		mDeviceView.setBusy(b);
    }
    // Activity result handling
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            default:
                break;
        }
    }

    private void setError(String txt) {
        setBusy(false);
        Toast.makeText(this, txt, Toast.LENGTH_LONG).show();
    }

    private void setStatus(String txt) {
        Toast.makeText(this, txt, Toast.LENGTH_SHORT).show();
    }


    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        List <BluetoothGattService> serviceList;
        List <BluetoothGattCharacteristic> charList = new ArrayList<BluetoothGattCharacteristic>();

        @Override
        public void onReceive(final Context context, Intent intent) {
            final String action = intent.getAction();
            final int status = intent.getIntExtra(BluetoothLeService.EXTRA_STATUS,
                    BluetoothGatt.GATT_SUCCESS);


            if (DeviceInformationServiceProfile.ACTION_FW_REV_UPDATED.equals(action)) {
                mFwRev = intent.getStringExtra(DeviceInformationServiceProfile.EXTRA_FW_REV_STRING);
                Log.d("DeviceActivityTea", "Got FW revision : " + mFwRev + " from DeviceInformationServiceProfile");
                for (GenericBluetoothProfile p :mProfiles) {
                    p.didUpdateFirmwareRevision(mFwRev);
                }
            }
            if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                if (status == BluetoothGatt.GATT_SUCCESS) {

                    serviceList = mBtLeService.getSupportedGattServices();
                    if (serviceList.size() > 0) {
                        for (int ii = 0; ii < serviceList.size(); ii++) {
                            BluetoothGattService s = serviceList.get(ii);
                            List<BluetoothGattCharacteristic> c = s.getCharacteristics();
                            if (c.size() > 0) {
                                for (int jj = 0; jj < c.size(); jj++) {
                                    charList.add(c.get(jj));
                                }
                            }
                        }
                    }




                    Log.d("DeviceActivityTea", "Total characteristics " + charList.size());
                    new Handler(getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {

                            //Iterate through the services and add GenericBluetoothServices for each service
                            int nrNotificationsOn = 0;
                            int maxNotifications;
                            int servicesDiscovered = 0;
                            int totalCharacteristics = 0;
                            //serviceList = mBtLeService.getSupportedGattServices();
                            for (BluetoothGattService s : serviceList) {
                                List<BluetoothGattCharacteristic> chars = s.getCharacteristics();
                                totalCharacteristics += chars.size();
                            }
                            //Special profile for Cloud service
                            mqttProfile = new IBMIoTCloudProfile(context, mBluetoothDevice, null, mBtLeService);
                            mProfiles.add(mqttProfile);
                            if (totalCharacteristics == 0) {
                                //Something bad happened, we have a problem
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        progressDialog.hide();
                                        progressDialog.dismiss();
                                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                                                context);
                                        alertDialogBuilder.setTitle("Error !");
                                        alertDialogBuilder.setMessage(serviceList.size() + " Services found, but no characteristics found, device will be disconnected !");
                                        alertDialogBuilder.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                mBtLeService.refreshDeviceCache(mBtGatt);
                                                //Try again
                                                discoverServices();
                                            }
                                        });
                                        alertDialogBuilder.setNegativeButton("Disconnect", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                mBtLeService.disconnect(mBluetoothDevice.getAddress());
                                            }
                                        });
                                        AlertDialog a = alertDialogBuilder.create();
                                        a.show();
                                    }
                                });
                                return;
                            }
                            final int final_totalCharacteristics = totalCharacteristics;
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    progressDialog.setIndeterminate(false);
                                    progressDialog.setTitle("Generating GUI");
                                    progressDialog.setMessage("Found a total of " + serviceList.size() + " services with a total of " + final_totalCharacteristics + " characteristics on this device");

                                }
                            });
                            if (Build.VERSION.SDK_INT > 18) maxNotifications = 7;
                            else {
                                maxNotifications = 4;
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(context, "Android version 4.3 detected, max 4 notifications enabled", Toast.LENGTH_LONG).show();
                                    }
                                });
                            }
                            for (int ii = 0; ii < serviceList.size(); ii++) {
                                BluetoothGattService s = serviceList.get(ii);
                                List<BluetoothGattCharacteristic> chars = s.getCharacteristics();
                                if (chars.size() == 0) {

                                    Log.d("DeviceActivityTea", "No characteristics found for this service !!!");
                                    return;
                                }
                                servicesDiscovered++;
                                final float serviceDiscoveredcalc = (float) servicesDiscovered;
                                final float serviceTotalcalc = (float) serviceList.size();
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        progressDialog.setProgress((int) ((serviceDiscoveredcalc / (serviceTotalcalc - 1)) * 100));
                                    }
                                });
                                Log.d("DeviceActivityTea", "Configuring service with uuid : " + s.getUuid().toString());
                                if (SensorTagHumidityProfile.isCorrectService(s)) {
                                    SensorTagHumidityProfile hum = new SensorTagHumidityProfile(context, mBluetoothDevice, s, mBtLeService);
                                    //mProfiles.add(hum);
                                    if (nrNotificationsOn < maxNotifications) {
                                        //hum.configureService();
                                        nrNotificationsOn++;
                                    } else {
                                        hum.grayOutCell(true);
                                    }
                                    Log.d("DeviceActivityTea", "Found Humidity !");
                                    //hum.periodWasUpdated(2450);
                                }
                                if (SensorTagLuxometerProfile.isCorrectService(s)) {
                                    SensorTagLuxometerProfile lux = new SensorTagLuxometerProfile(context, mBluetoothDevice, s, mBtLeService);
                                    //mProfiles.add(lux);
                                    if (nrNotificationsOn < maxNotifications) {
                                        //lux.configureService();
                                        nrNotificationsOn++;
                                    } else {
                                        lux.grayOutCell(true);
                                    }
                                    // disable service
                                    //lux.grayOutCell(true);
                                    lux.periodWasUpdated(2450);
                                    //lux.deConfigureService();
                                    //lux.disableService();
                                }
                                if (SensorTagSimpleKeysProfile.isCorrectService(s)) {
                                    SensorTagSimpleKeysProfile key = new SensorTagSimpleKeysProfile(context, mBluetoothDevice, s, mBtLeService);
                                    //mProfiles.add(key);
                                    if (nrNotificationsOn < maxNotifications) {
                                        //key.configureService();
                                        nrNotificationsOn++;
                                    } else {
                                        key.grayOutCell(true);
                                    }
                                    Log.d("DeviceActivityTea", "Found Simple Keys !");
                                    //key.grayOutCell(true);
                                    key.deConfigureService();
                                    key.disableService();

                                }
                                if (SensorTagBarometerProfile.isCorrectService(s)) {
                                    SensorTagBarometerProfile baro = new SensorTagBarometerProfile(context, mBluetoothDevice, s, mBtLeService);
                                    //mProfiles.add(baro);
                                    if (nrNotificationsOn < maxNotifications) {
                                        //baro.configureService();
                                        nrNotificationsOn++;
                                    } else {
                                        baro.grayOutCell(true);
                                    }
                                    Log.d("DeviceActivityTea", "Found Barometer !");
                                    //baro.periodWasUpdated(2450);
                                    //baro.grayOutCell(true);
                                    baro.disableService();
                                    // baro.deConfigureService();

                                }
                                if (SensorTagAmbientTemperatureProfile.isCorrectService(s)) {
                                    SensorTagAmbientTemperatureProfile irTemp = new SensorTagAmbientTemperatureProfile(context, mBluetoothDevice, s, mBtLeService);
                                    mProfiles.add(irTemp);
                                    if (nrNotificationsOn < maxNotifications) {
                                        irTemp.configureService();
                                        nrNotificationsOn++;
                                    } else {
                                        irTemp.grayOutCell(true);
                                    }
                                    Log.d("DeviceActivityTea", "Found Ambient Temperature !");
                                    irTemp.periodWasUpdated(2450);

                                }
                                if (SensorTagIRTemperatureProfile.isCorrectService(s)) {
                                    SensorTagIRTemperatureProfile irTemp1 = new SensorTagIRTemperatureProfile(context, mBluetoothDevice, s, mBtLeService);
                                    mProfiles.add(irTemp1);
                                    if (nrNotificationsOn < maxNotifications) {
                                        irTemp1.configureService();
                                    } else {
                                        irTemp1.grayOutCell(true);
                                    }
                                    //No notifications add here because it is already enabled above ..
                                    Log.d("DeviceActivityTea", "Found IR Temperature !");
                                    //irTemp1.deConfigureService();
                                    irTemp1.periodWasUpdated(2450);
                                    //irTemp1.disableService();
                                }
                                if (SensorTagMovementProfile.isCorrectService(s)) {
                                    SensorTagMovementProfile mov = new SensorTagMovementProfile(context, mBluetoothDevice, s, mBtLeService);
                                    //mProfiles.add(mov);
                                    if (nrNotificationsOn < maxNotifications) {
                                        //mov.configureService();
                                        nrNotificationsOn++;
                                    } else {
                                        mov.grayOutCell(true);
                                    }
                                    Log.d("DeviceActivityTea", "Found Motion !");
                                    //mov.periodWasUpdated(2450);
                                    // mov.deConfigureService();
                                    mov.disableService();
                                    mov.grayOutCell(true);

                                }
                                if (SensorTagAccelerometerProfile.isCorrectService(s)) {
                                    SensorTagAccelerometerProfile acc = new SensorTagAccelerometerProfile(context, mBluetoothDevice, s, mBtLeService);
                                    //mProfiles.add(acc);
                                    if (nrNotificationsOn < maxNotifications) {
                                        // acc.configureService();
                                        nrNotificationsOn++;
                                    } else {
                                        acc.grayOutCell(true);
                                    }
                                    Log.d("DeviceActivityTea", "Found Motion !");
                                    //acc.periodWasUpdated(2450);
                                    //acc.deConfigureService();
                                    acc.disableService();
                                    //acc.grayOutCell(true);

                                }
                                if (SensorTagDisplayProfile.isCorrectService(s)) {
                                    SensorTagDisplayProfile d = new SensorTagDisplayProfile(context, mBluetoothDevice, s, mBtLeService);
                                    mProfiles.add(d);
                                    d.configureService();
                                    Log.d("DeviceActivityTea", "Found Display Control Service");
                                }
                                if (TILampControlProfile.isCorrectService(s)) {
                                    TILampControlProfile lamp = new TILampControlProfile(context, mBluetoothDevice, s, mBtLeService);
                                    mProfiles.add(lamp);
                                    lamp.configureService();
                                    Log.d("DeviceActivityTea", "Found Lamp Control Service");
                                }

                                if (DeviceInformationServiceProfile.isCorrectService(s)) {
                                    DeviceInformationServiceProfile devInfo = new DeviceInformationServiceProfile(context, mBluetoothDevice, s, mBtLeService);
                                    mProfiles.add(devInfo);
                                    devInfo.configureService();
                                    Log.d("DeviceActivityTea", "Found Device Information Service");
                                }
                                if (TIOADProfile.isCorrectService(s)) {
                                    TIOADProfile oad = new TIOADProfile(context, mBluetoothDevice, s, mBtLeService);
                                    mProfiles.add(oad);
                                    oad.configureService();
                                    mOadService = s;
                                    Log.d("DeviceActivityTea", "Found TI OAD Service");
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            new firmwareUpdateStart(progressDialog, context).execute();

                                        }
                                    });
                                }
                                if (SensorTagTestProfile.isCorrectService(s)) {
                                    mTestService = s;
                                }
                                if ((s.getUuid().toString().compareTo("f000ccc0-0451-4000-b000-000000000000")) == 0) {
                                    mConnControlService = s;
                                }
                            }
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    progressDialog.setTitle("Enabling Services");
                                    progressDialog.setMax(mProfiles.size());
                                    progressDialog.setProgress(0);
                                }
                            });
                            for (final GenericBluetoothProfile p : mProfiles) {

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
//                                        mDeviceView.addRowToTable(p.getTableRow());
                                        p.enableService();
                                        progressDialog.setProgress(progressDialog.getProgress() + 1);
                                    }
                                });
                                p.onResume();
                            }
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    progressDialog.hide();
                                    progressDialog.dismiss();
                                }
                            });
                        }
                    });
//                    worker.start();
                } else {
                    Toast.makeText(getApplication(), "Service discovery failed",
                            Toast.LENGTH_LONG).show();
                    return;
                }
            } else if (BluetoothLeService.ACTION_DATA_NOTIFY.equals(action)) {
                // Notification
                byte[] value = intent.getByteArrayExtra(BluetoothLeService.EXTRA_DATA);
                String uuidStr = intent.getStringExtra(BluetoothLeService.EXTRA_UUID);
                //Log.d("DeviceActivityTea","Got Characteristic : " + uuidStr);
                for (int ii = 0; ii < charList.size(); ii++) {
                    BluetoothGattCharacteristic tempC = charList.get(ii);
                    if ((tempC.getUuid().toString().equals(uuidStr))) {
                        for (int jj = 0; jj < mProfiles.size(); jj++) {
                            GenericBluetoothProfile p = mProfiles.get(jj);
                            if (p.isDataC(tempC)) {
                                p.didUpdateValueForCharacteristic(tempC);
                                //Do MQTT
                                Map<String,String> map = p.getMQTTMap();
                                if (map != null) {
                                    for (Map.Entry<String, String> e : map.entrySet()) {
                                        if (mqttProfile != null)
                                            mqttProfile.addSensorValueToPendingMessage(e);
                                    }
                                }
                            }
                        }
                        //Log.d("DeviceActivityTea","Got Characteristic : " + tempC.getUuid().toString());
                        break;
                    }
                }

                //onCharacteristicChanged(uuidStr, value);
            } else if (BluetoothLeService.ACTION_DATA_WRITE.equals(action)) {
                // Data written
                byte[] value = intent.getByteArrayExtra(BluetoothLeService.EXTRA_DATA);
                String uuidStr = intent.getStringExtra(BluetoothLeService.EXTRA_UUID);
                for (int ii = 0; ii < charList.size(); ii++) {
                    BluetoothGattCharacteristic tempC = charList.get(ii);
                    if ((tempC.getUuid().toString().equals(uuidStr))) {
                        for (int jj = 0; jj < mProfiles.size(); jj++) {
                            GenericBluetoothProfile p = mProfiles.get(jj);
                            p.didWriteValueForCharacteristic(tempC);
                        }
                        //Log.d("DeviceActivityTea","Got Characteristic : " + tempC.getUuid().toString());
                        break;
                    }
                }
            } else if (BluetoothLeService.ACTION_DATA_READ.equals(action)) {
                // Data read
                byte[] value = intent.getByteArrayExtra(BluetoothLeService.EXTRA_DATA);
                String uuidStr = intent.getStringExtra(BluetoothLeService.EXTRA_UUID);
                for (int ii = 0; ii < charList.size(); ii++) {
                    BluetoothGattCharacteristic tempC = charList.get(ii);
                    if ((tempC.getUuid().toString().equals(uuidStr))) {
                        for (int jj = 0; jj < mProfiles.size(); jj++) {
                            GenericBluetoothProfile p = mProfiles.get(jj);
                            p.didReadValueForCharacteristic(tempC);
                        }
                        //Log.d("DeviceActivityTea","Got Characteristic : " + tempC.getUuid().toString());
                        break;
                    }
                }
            }
            else {
                if (TIOADProfile.ACTION_PREPARE_FOR_OAD.equals(action)) {
                    new firmwareUpdateStart(progressDialog,context).execute();
                }
            }
            if (status != BluetoothGatt.GATT_SUCCESS) {
                try {
                    Log.d("DeviceActivityTea", "Failed UUID was " + intent.getStringExtra(BluetoothLeService.EXTRA_UUID));
                    setError("GATT error code: " + BluetoothGATTDefines.gattErrorCodeStrings.get(status));
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    };
    class firmwareUpdateStart extends AsyncTask<String, Integer, Void> {
        ProgressDialog pd;
        Context con;

        public firmwareUpdateStart(ProgressDialog p,Context c) {
            this.pd = p;
            this.con = c;
        }

        @Override
        protected void onPreExecute() {
            this.pd = new ProgressDialog(DeviceActivityTea.this);
            this.pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            this.pd.setIndeterminate(false);
            this.pd.setTitle("Starting firmware update");
            this.pd.setMessage("");
            this.pd.setMax(mProfiles.size());
            this.pd.show();
            super.onPreExecute();
        }
        @Override
        protected Void doInBackground(String... params) {
            Integer ii = 1;
            for (GenericBluetoothProfile p : mProfiles) {

                p.disableService();
                p.deConfigureService();
                publishProgress(ii);
                ii = ii + 1;
            }

            if (isSensorTag2()) {
                final Intent i = new Intent(this.con, FwUpdateActivity_CC26xx.class);
                startActivityForResult(i, FWUPDATE_ACT_REQ);
            }
            else {
                final Intent i = new Intent(this.con,FwUpdateActivityTea.class);
                i.putExtra("upversion",upversion);
                startActivityForResult(i, FWUPDATE_ACT_REQ);
            }
            return null;
        }
        @Override
        protected void onProgressUpdate(Integer... values) {
            this.pd.setProgress(values[0]);
        }
        @Override
        protected void onPostExecute(Void result) {
            this.pd.dismiss();
            super.onPostExecute(result);
        }

    }
}
