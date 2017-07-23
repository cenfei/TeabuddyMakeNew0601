package com.taomake.teabuddy.sensoractivity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.taomake.teabuddy.R;
import com.taomake.teabuddy.base.MainApp;
import com.taomake.teabuddy.component.FoxProgressbarInterface;
import com.taomake.teabuddy.component.One_Permission_Popwindow;
import com.taomake.teabuddy.object.DeviceVersionObj;
import com.taomake.teabuddy.util.Constant;
import com.taomake.teabuddy.util.MyStringUtils;
import com.taomake.teabuddy.util.Util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


/**
 *
 */
public class DeviceActivityTea extends Activity {

    public static final String EXTRA_DEVICE = "EXTRA_DEVICE";
    private static final int FWUPDATE_ACT_REQ = 1;

    private BluetoothDevice mBluetoothDevice = null; //当前蓝牙设备


    private boolean mIsReceiving = false; // 是否注册广播
    private BluetoothLeService mBluetoothLeService = null;  //自定义的BluetoothLeService 句柄
    private boolean mBleSupported = true; //是否支持蓝牙4.0
    private boolean mScanning = false; //是否正在扫描
    //    private static BluetoothManager mBluetoothManager; //蓝牙设备管理器
    public BluetoothAdapter mBtAdapter = null; //蓝牙适配器

    private int PACKAGE_SIZE = 16;
    private static final int GATT_WRITE_TIMEOUT = 100;

//    private Button mScanButton = null; //开始搜索按钮
//    private Button mWriteButton = null; //写数据按钮
//    private Button mFwButton = null; //固件更新按钮
//    private TextView mLogText = null; //数据显示文本框，主要用来显示已接收的数据
//    private EditText m_editText = null;

    private boolean m_isConnect = false;

    public DeviceActivityTea() {

    }

    FoxProgressbarInterface foxProgressbarInterface;
    TextView tea_os_version;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.sensor_device_update_two);


         tea_os_version = (TextView) findViewById(R.id.tea_os_version);
        TextView text_db_size = (TextView) findViewById(R.id.text_db_size);

        TextView comment_content_id = (TextView) findViewById(R.id.comment_content_id);

        MainApp mainApp = (MainApp) getApplicationContext();

        DeviceVersionObj deviceVersionObj = mainApp.deviceVersionObj;
        String sysdownloadsize = deviceVersionObj.downloadsize;
        String version = deviceVersionObj.ver;
        String updatetext = MyStringUtils.decodeUnicode(deviceVersionObj.content);
        if (!TextUtils.isEmpty(version)) {
            tea_os_version.setText("Cha OS " + version);
            text_db_size.setText(sysdownloadsize);
            comment_content_id.setText(updatetext);
        } else {
            tea_os_version.setText("Cha OS 1.0");
            text_db_size.setText("0kb");
            comment_content_id.setText("传统修复");

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
                startBluetoothLeService();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } else {
            startBluetoothLeService();
            // BLE
        }


//        setContentView(com.taomake.teabuddy.layout.);
//        Intent intent = getIntent();
//
//        mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
//        mBtAdapter = mBluetoothManager.getAdapter();
//
//        if (mBtAdapter == null) {
//            Toast.makeText(this, "ble not support", Toast.LENGTH_LONG).show();
//            mBleSupported = false;
//        }
//
//        mScanButton = (Button) findViewById(R.id.scan_button);
//        mWriteButton = (Button) findViewById(R.id.write_button);
//        mFwButton = (Button) findViewById(R.id.fwupdate_button);
//        mLogText = (TextView) findViewById(R.id.log_text);
//        mWriteButton.setEnabled(false);
//        mFwButton.setEnabled(false);
//        mScanButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                if (mBleSupported) {
//
//                    if (mScanning) {
//                        scanLeDevice(false);
//                    } else {
//                        scanLeDevice(true);
//                    }
//                }
//
//            }
//        });
//
//        m_editText = (EditText) findViewById(R.id.edit_text);
//
//
//        mWriteButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                String str = m_editText.getText().toString();
//
//                if(str.length()%2!=0)
//                {
//                    str+="0";
//                }
//
//                byte [] data = hexStringToByteArray(str);
//
//                sendData(data);
//
//
//               /* byte[] data = {0x01,0x03,0x00,0x00,0x00,0x64,0x44,0x21};
//                boolean success = writeDataWithNoResponse(data);
//                if (success) {
//                    BtLog.LogOut("write success");
//                } else {
//                    BtLog.LogOut("write failed");
//                }*/
//
//            }
//        });
//
//        mFwButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                startOadActivity();
//            }
//        });
//
//        startBluetoothLeService();

    }


    //    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            final String action = intent.getAction();
//
//            if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
//                // Bluetooth adapter state change
//                switch (mBtAdapter.getState()) {
//                    case BluetoothAdapter.STATE_ON:
//                        //ConnIndex = NO_DEVICE;
//                        startBluetoothLeService();
//                        break;
//                    case BluetoothAdapter.STATE_OFF:
//                        //Toast.makeText(context, R.string.app_closing, Toast.LENGTH_LONG)
//                        //        .show();
//                        //finish();
//                        break;
//                    default:
//                        // Log.w(TAG, "Action STATE CHANGED not processed ");
//                        break;
//                }
//            }
//        }
//    };
    BluetoothManager mBluetoothManager;
    IntentFilter mFilter;

//    public void getLeservice()
//    {
//        // Use this check to determine whether BLE is supported on the device. Then
//        // you can selectively disable BLE-related features.
//        if (!getPackageManager().hasSystemFeature(
//                PackageManager.FEATURE_BLUETOOTH_LE)) {
//            Toast.makeText(this, com.example.ti.ble.sensortag.R.string.ble_not_supported, Toast.LENGTH_LONG)
//                    .show();
//            mBleSupported = false;
//        }
//
//        // Initializes a Bluetooth adapter. For API level 18 and above, get a
//        // reference to BluetoothAdapter through BluetoothManager.
//        mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
//        mBtAdapter = mBluetoothManager.getAdapter();
//
//        // Checks if Bluetooth is supported on the device.
//        if (mBtAdapter == null) {
//            Toast.makeText(this, R.string.bt_not_supported, Toast.LENGTH_LONG).show();
//            mBleSupported = false;
//            return;
//        }
//
//        mFilter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
//        registerReceiver(mReceiver, mFilter);
//
//        if (!mBtAdapter.isEnabled()) {
//            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//            enableIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            startActivity(enableIntent);
//        }
//
//        startBluetoothLeService();
//    }


    private void sendData(byte[] data) {

        int length = data.length;
        int count = 0;

        if (length % PACKAGE_SIZE == 0) {
            count = length / PACKAGE_SIZE;
        } else {
            count = length / PACKAGE_SIZE + 1;
        }

        for (int i = 0; i < count - 1; i++) {
            boolean ok = waitIdle(GATT_WRITE_TIMEOUT);

            if (ok) {
                byte[] recordData = new byte[PACKAGE_SIZE];
                arrayCopy(data, recordData, i * PACKAGE_SIZE,
                        PACKAGE_SIZE);
                writeDataWithNoResponse(recordData);
            } else {
                BtLog.LogOut("**********************error");
            }
        }

        boolean ok = waitIdle(GATT_WRITE_TIMEOUT);
        if (ok) {
            byte[] recordData = new byte[length - (count - 1) * PACKAGE_SIZE];
            arrayCopy(data, recordData, (count - 1) * PACKAGE_SIZE,
                    (length - (count - 1) * PACKAGE_SIZE));
            writeDataWithNoResponse(recordData);
        } else {
            BtLog.LogOut("**********************error");
        }
    }

    public void arrayCopy(byte[] src, byte[] des, int start, int length) {
        for (int i = start; i < start + length; i++) {
            des[i - start] = src[i];
        }
    }


    public boolean waitIdle(int timeout) {
        timeout /= 10;
        while (--timeout > 0) {

            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }

        return true;
    }


    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }


    /**
     * 写入数据不需要回复确认
     *
     * @param data 要写入的数据
     * @return 返回true 表示写入成功 返回false 表示写入失败
     */
    private boolean writeDataWithNoResponse(byte[] data) {
        if (mBluetoothLeService != null) {
            return mBluetoothLeService.writeDataWithNoResponse(data);
        }

        return false;
    }

    /**
     * 写入数据需要回复确认
     *
     * @param data 要写入的数据
     * @return 返回true 表示写入成功 返回false 表示写入失败
     */
    private boolean writeDataWithResponse(byte[] data) {
        if (mBluetoothLeService != null) {
            return mBluetoothLeService.writeDataWithResponse(data);
        }
        return false;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        unbindService(mServiceConnection);
    }

    @Override
    protected void onResume() {
        // Log.d(TAG, "onResume");
        super.onResume();
        if (!mIsReceiving) {
            registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
            mIsReceiving = true;
        }
    }

    String deviceVersionObjStr;

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public void initDevice() {
        Intent intent = getIntent();

//        mBtLeService = BluetoothLeService.getInstance();
//        mBluetoothDevice = intent.getParcelableExtra(EXTRA_DEVICE);
        mBluetoothManager = mBluetoothLeService.getBtManager();

//        String deviceName = "88:4A:EA:83:A5:62";

        String deviceName = getIntent().getStringExtra("MAC_DEVICE");

        deviceVersionObjStr = intent.getStringExtra("deviceVersionObj");
        mBluetoothDevice = mBluetoothLeService.mBtAdapter.getRemoteDevice(deviceName);

        int connState = mBluetoothManager.getConnectionState(mBluetoothDevice,
                BluetoothGatt.GATT);


        switch (connState) {
            case BluetoothGatt.STATE_CONNECTED:


                break;
            case BluetoothGatt.STATE_DISCONNECTED:
                boolean ok = mBluetoothLeService.connect(mBluetoothDevice.getAddress());
                if (!ok) {
                    setError("Connect failed");
                }


                break;
            default:
                setError("Device busy (connecting/disconnecting)");
                break;
        }


//        mServiceList = new ArrayList<BluetoothGattService>();
//
//        mIsSensorTag2 = false;
//        // Determine type of SensorTagGatt
//        if ((deviceName.equals("SensorTag2")) ||(deviceName.equals("CC2650 SensorTag"))) {
//            mIsSensorTag2 = true;
//        }
//        else mIsSensorTag2 = false;
//
//        PreferenceManager.setDefaultValues(this, com.example.ti.ble.sensortag.R.xml.preferences, false);
//        // Log.i(TAG, "Preferences for: " + deviceName);
//
//        // GUI
////        mDeviceView = new DeviceView();
////        mSectionsPagerAdapter.addSection(mDeviceView, "Sensors");
////        HelpView hw = new HelpView();
////        hw.setParameters("help_device.html", R.layout.fragment_help, R.id.webpage);
////        mSectionsPagerAdapter.addSection(hw, "Help");
//        mProfiles = new ArrayList<GenericBluetoothProfile>();
//        progressDialog = new ProgressDialog(DeviceActivityTea.this);
//        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
//        progressDialog.setIndeterminate(true);
//        progressDialog.setTitle("Discovering Services");
//        progressDialog.setMessage("");
//        progressDialog.setMax(100);
//        progressDialog.setProgress(0);
////        progressDialog.show();
//
//        // GATT database
//        Resources res = getResources();
//        XmlResourceParser xpp = res.getXml(com.example.ti.ble.sensortag.R.xml.gatt_uuid);
//        new com.example.ti.ble.common.GattInfo(xpp);


//        if (!mIsReceiving) {
//            registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
//            mIsReceiving = true;
//        }
//        for (GenericBluetoothProfile p : mProfiles) {
//            if (p.isConfigured != true) p.configureService();
//            if (p.isEnabled != true) p.enableService();
//            p.onResume();
//        }
//        this.mBtLeService.abortTimedDisconnect();

        one_text_line = (TextView) findViewById(R.id.one_text_line);

        one_text_line.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                startOadActivity();

            }
        });
        LinearLayout left_title_line = (LinearLayout) findViewById(R.id.left_title_line);

        left_title_line.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                finishRequest();

            }
        });


    }

    TextView one_text_line;

    private void startBluetoothLeService() {
        foxProgressbarInterface = new FoxProgressbarInterface();
        foxProgressbarInterface.startProgressBar(this, "初始化OAD...");

        boolean f;

        Intent bindIntent = new Intent(this, BluetoothLeService.class);
        startService(bindIntent);
        f = bindService(bindIntent, mServiceConnection, Context.BIND_AUTO_CREATE);
        if (!f) {
            Toast.makeText(getApplicationContext(), "Bind to BluetoothLeService failed", Toast
                    .LENGTH_SHORT).show();
            finish();
        }
    }


    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service)
                    .getService();
            if (!mBluetoothLeService.initialize()) {
                Toast.makeText(getApplicationContext(), "Unable to initialize BluetoothLeService", Toast.LENGTH_SHORT)
                        .show();
                finish();
                return;
            }
            final int n = mBluetoothLeService.numConnectedDevices();
//            if (n > 0) {
//                runOnUiThread(new Runnable() {
//                    public void run() {
//                        Toast.makeText(getApplicationContext(), "error", Toast.LENGTH_SHORT).show();
//                    }
//                });
//            } else {
//            }

            initDevice();
        }

        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeService = null;
        }
    };


    /**
     * 开始扫描4.0设备
     */
    private void startScan() {
        // Start device discovery
        if (mBleSupported) {
            scanLeDevice(true);
        } else {
            setError("BLE not supported on this device");
        }
    }


    /**
     * 搜索ble设备
     *
     * @param enable 为true开始搜索  enable为false 停止扫描
     * @return true 返回true表示操作成功，false操作失败
     */
    private boolean scanLeDevice(boolean enable) {
        if (enable) {
            mScanning = mBtAdapter.startLeScan(mLeScanCallback);
            mScanning = true;
        } else {
            mScanning = false;
            mBtAdapter.stopLeScan(mLeScanCallback);
        }
        return mScanning;
    }

    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {

        /**
         * 发现设备回调函数
         * @param device 找到的蓝牙设备
         * @param rssi 信号
         * @param scanRecord  advertisement 提供的远程设备
         */
        public void onLeScan(final BluetoothDevice device, final int rssi, byte[] scanRecord) {
            runOnUiThread(new Runnable() {
                public void run() {

                    Log.d("guidongli", device.getName() + " rssi = " + rssi + "  address = " + device.getAddress());

//                    mLogText.append(device.getName() + " rssi = " + rssi + "  address = " + device.getAddress());

                    //if (device.getAddress().equals("7C:EC:79:EF:BA:09")) {

                    String name = device.getName();


                    /*if (name!=null && name.contains("FPTe") ||name.contains("GK") || device
                            .getAddress().equals("7C:EC:79:EF:BA:09")) {*/

                    if (!m_isConnect) {
                        scanLeDevice(false);
                        mBluetoothDevice = device;
                        m_isConnect = !m_isConnect;

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                boolean ok = mBluetoothLeService.connect(mBluetoothDevice.getAddress());
                                if (!ok) {
                                    setError("Connect failed");
                                }
                            }
                        });
                    }

                   /* }*/
                }

            });
        }
    };

    @Override
    protected void onPause() {
        // Log.d(TAG, "onPause");
        super.onPause();
        if (mIsReceiving) {
            unregisterReceiver(mGattUpdateReceiver);
            mIsReceiving = false;
        }
    }


    /**
     * 注册需要监听的通知
     *
     * @return 返回IntentFilter 对象
     */
    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter fi = new IntentFilter();
        fi.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        fi.addAction(BluetoothLeService.ACTION_DATA_NOTIFY);
        fi.addAction(BluetoothLeService.ACTION_DATA_WRITE);
        fi.addAction(BluetoothLeService.ACTION_DATA_READ);
        fi.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        fi.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        fi.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        fi.addAction(BluetoothLeService.ACTION_GET_CHARTACTERISTIC);
        fi.addAction(BluetoothLeService.ACTION_DATA_LOG);

        return fi;
    }

    /**
     * 启动固件更新的activity
     */
    private void startOadActivity() {

        if (mBluetoothLeService.mOadService != null) {

            final Intent i = new Intent(this, FwUpdateActivityTea.class);
            startActivityForResult(i, FWUPDATE_ACT_REQ);
        } else {
            Toast.makeText(this, "OAD not available on this BLE device",
                    Toast.LENGTH_LONG).show();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        Log.d("onActivityResult", " onActivityResult " + requestCode + " " + resultCode);
        if (requestCode == FWUPDATE_ACT_REQ && resultCode == RESULT_OK) {
            Log.d("onActivityResult", " finish " );

            finish();
        }

    }

    /**
     * 显示状态异常的toast
     *
     * @param txt
     */
    private void setError(String txt) {
        Toast.makeText(this, txt, Toast.LENGTH_LONG).show();
    }


    /**
     * 显示状态的toast
     *
     * @param txt
     */
    private void setStatus(String txt) {
//        Toast.makeText(this, txt, Toast.LENGTH_SHORT).show();
        Util.Toast(this,txt,null);
    }


    /**
     *
     */
    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, final Intent intent) {
            final String action = intent.getAction();
            int status = intent.getIntExtra(BluetoothLeService.EXTRA_STATUS,
                    BluetoothGatt.GATT_SUCCESS);

            if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                if (status == BluetoothGatt.GATT_SUCCESS) {
                    if (foxProgressbarInterface != null)
                        foxProgressbarInterface.stopProgressBar();
                    setStatus("初始化成功");



                } else {
                    Toast.makeText(getApplication(), "Service discovery failed",
                            Toast.LENGTH_LONG).show();

                    new One_Permission_Popwindow().showPopwindow(DeviceActivityTea.this,  tea_os_version, "建议重启应用，以便重现升级", "取消", "停止升级", new One_Permission_Popwindow.CallBackPayWindow() {
                        @Override
                        public void handleCallBackChangeUser() {

                        }

                        @Override
                        public void handleCallBackBindDevice() {
                            System.exit(0);

                        }
                    });

                    return;
                }
            } else if (BluetoothLeService.ACTION_DATA_NOTIFY.equals(action)) {
                // Notification
                byte[] value = intent.getByteArrayExtra(BluetoothLeService.EXTRA_DATA);
                String uuidStr = intent.getStringExtra(BluetoothLeService.EXTRA_UUID);
                onCharacteristicChanged(uuidStr, value);
            } else if (BluetoothLeService.ACTION_DATA_WRITE.equals(action)) {
                String uuidStr = intent.getStringExtra(BluetoothLeService.EXTRA_UUID);
                onCharacteristicWrite(uuidStr, status);
            } else if (BluetoothLeService.ACTION_DATA_READ.equals(action)) {

                String uuidStr = intent.getStringExtra(BluetoothLeService.EXTRA_UUID);
                byte[] value = intent.getByteArrayExtra(BluetoothLeService.EXTRA_DATA);
                onCharacteristicsRead(uuidStr, value, status);
            } else if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
                switch (mBtAdapter.getState()) {
                    case BluetoothAdapter.STATE_ON:
                        Toast.makeText(context, "Bluetooth on", Toast.LENGTH_LONG)
                                .show();
                        break;
                    case BluetoothAdapter.STATE_OFF:
                        Toast.makeText(context, "Bluetooth off", Toast.LENGTH_LONG)
                                .show();
                        finish();
                        break;
                    default:
                        break;
                }

            } else if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
                int connectStatus = intent.getIntExtra(BluetoothLeService.EXTRA_STATUS,
                        BluetoothGatt.GATT_FAILURE);
                if (connectStatus == BluetoothGatt.GATT_SUCCESS) {

                } else {
                    setError("Connect failed. Status: " + status);
                }
            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {

                int disConnectStatus = intent.getIntExtra(BluetoothLeService.EXTRA_STATUS,
                        BluetoothGatt.GATT_FAILURE);
                //stopDeviceActivity();
                if (disConnectStatus == BluetoothGatt.GATT_SUCCESS) {

                } else {

                }
                mBluetoothLeService.close();
            } else if (BluetoothLeService.ACTION_GET_CHARTACTERISTIC.equals(action)) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
//                        mWriteButton.setEnabled(true);
//                        mFwButton.setEnabled(true);
                    }
                });
            } else if (BluetoothLeService.ACTION_DATA_LOG.equals(action)) {


                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        String str = intent.getStringExtra(BluetoothLeService.EXTRA_DATA);
//                        mLogText.append(str);
                    }
                });
            }


            if (status != BluetoothGatt.GATT_SUCCESS) {
                setError("GATT error code: " + status);
            }
        }
    };

    private void onCharacteristicWrite(String uuidStr, int status) {

    }


    /**
     * @param uuidStr 获取到数据Characteristic的uuid
     * @param value   获取到的值
     */
    private void onCharacteristicChanged(String uuidStr, final byte[] value) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
//                if (mLogText.getText().toString().length() > 1000) {
//                    mLogText.setText("");
//                }
//                mLogText.append(converByteToHex(value, value.length));
            }
        });

    }


    public String converByteToHex(byte[] buffer, int max) {

        String h = "";
        for (int i = 0; i < max; i++) {
            String temp = Integer.toHexString(buffer[i] & 0xFF);
            if (temp.length() == 1) {
                temp = "0" + temp;
            }
            h = h + " " + temp;
        }

        return h;
    }


    private void onCharacteristicsRead(String uuidStr, byte[] value, int status) {
        Toast.makeText(getApplicationContext(), "find oad server", Toast.LENGTH_SHORT).show();
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
                    String path = Constant.path;
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

                        startOadActivity();
//                        mBtGatt = BluetoothLeService.getBtGatt();
//                        mBtGatt.discoverServices();
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

    public void finishRequest() {

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

    }


}
