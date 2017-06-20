package com.taomake.teabuddy.sensoractivity;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.taomake.teabuddy.R;
import com.taomake.teabuddy.base.MainApp;
import com.taomake.teabuddy.object.DeviceVersionObj;
import com.taomake.teabuddy.util.Constant;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import quinticble.QuinticBleAPISdkBase;


public class FwUpdateActivityTea extends Activity {
    public final static String EXTRA_MESSAGE = "com.example.ti.ble.sensortag.MESSAGE";

    private static String TAG = "FwUpdateActivityTea";


    private static final int FILE_ACTIVITY_REQ = 0;

    private static final short OAD_CONN_INTERVAL = 12; // 15 milliseconds
    private static final short OAD_SUPERVISION_TIMEOUT = 50; // 500 milliseconds
    private static final int GATT_WRITE_TIMEOUT = 300; // Milliseconds

    private static final int FILE_BUFFER_SIZE = 0x40000;
    private static final String FW_CUSTOM_DIRECTORY = Environment.DIRECTORY_DOWNLOADS;
/*    private static final String FW_FILE_A = "SimpleBLEPeripheral_a.bin";
    private static final String FW_FILE_B = "SimpleBLEPeripheral_b.bin";*/

    private static final String FW_FILE_A = "SimpleBLEPeripheralAA.bin";
    private static final String FW_FILE_B = "SimpleBLEPeripheralBB.bin";

    private static final int OAD_BLOCK_SIZE = 16;
    private static final int HAL_FLASH_WORD_SIZE = 4;
    private static final int OAD_BUFFER_SIZE = 2 + OAD_BLOCK_SIZE;
    private static final int OAD_IMG_HDR_SIZE = 8;
    private static final long TIMER_INTERVAL = 1000;

    private static final int SEND_INTERVAL = 20; // Milliseconds (make sure this is longer than the connection interval)
    private static final int BLOCKS_PER_CONNECTION = 4; // May sent up to four blocks per connection

    // GUI
//    private TextView mTargImage;
//    private TextView mFileImage;
//    private TextView mLog;
//    private Button mBtnLoadA;
//    private Button mBtnLoadB;
//    private Button mBtnLoadC;
//    private Button mBtnStart;

    // BLE
    private BluetoothGattService mOadService;
    private List<BluetoothGattCharacteristic> mCharListOad;
    private List<BluetoothGattCharacteristic> mCharListCc;
    private BluetoothGattCharacteristic mCharIdentify = null;
    private BluetoothGattCharacteristic mCharBlock = null;
    private BluetoothGattCharacteristic mCharConnReq = null;
    private BluetoothLeService mLeService;

    // Programming
    private final byte[] mFileBuffer = new byte[FILE_BUFFER_SIZE];
    private final byte[] mOadBuffer = new byte[OAD_BUFFER_SIZE];
    private ImgHdr mFileImgHdr = new ImgHdr();
    private ImgHdr mTargImgHdr = new ImgHdr();
    private Timer mTimer = null;
    private ProgInfo mProgInfo = new ProgInfo();
    private TimerTask mTimerTask = null;

    // Housekeeping
    private boolean mServiceOk = false;
    private boolean mProgramming = false;
    private IntentFilter mIntentFilter;


    public FwUpdateActivityTea() {

        // BLE Gatt Service
        mLeService = BluetoothLeService.getInstance();

        // Service information
        mOadService = mLeService.mOadService;

        // Characteristics list
        mCharListOad = mOadService.getCharacteristics();
        mCharListCc = mOadService.getCharacteristics();

        mServiceOk = mCharListOad.size() == 2;
        if (mServiceOk) {
            mCharIdentify = mCharListOad.get(0);
            mCharBlock = mCharListOad.get(1);

            mCharBlock.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE);
            mCharConnReq = mCharListCc.get(1);
        }
    }
    ProgressBar mProgressBar;
    TextView mProgressInfo;


    TextView update_process_sucess_text;

    RelativeLayout update_process_success_rel;
    RelativeLayout update_process_rel;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.sensor_device_update_three);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);

        TextView tea_os_version = (TextView) findViewById(com.taomake.teabuddy.R.id.tea_os_version);
        tea_os_version.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startProgramming();
            }
        });

        update_process_sucess_text = (TextView) findViewById(com.taomake.teabuddy.R.id.update_process_sucess_text);

        update_process_success_rel = (RelativeLayout) findViewById(com.taomake.teabuddy.R.id.update_process_success_rel);

        update_process_rel = (RelativeLayout) findViewById(com.taomake.teabuddy.R.id.update_process_rel);
        LinearLayout left_title_line = (LinearLayout) findViewById(com.taomake.teabuddy.R.id.left_title_line);
        left_title_line.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                try {
//                    if (mDeviceActivityTea != null) {
//
//
//                        mDeviceActivityTea.finish();
//                    }
//                } catch (Exception e) {
//
//                }

                setResult(RESULT_OK);

                finish();
            }
        });
        MainApp mainApp = (MainApp) getApplicationContext();

        DeviceVersionObj deviceVersionObj=mainApp.deviceVersionObj;
        String  version =deviceVersionObj.ver;
        if(!TextUtils.isEmpty(version)) {
            tea_os_version.setText("Cha OS " +version);

        }else{
            tea_os_version.setText("Cha OS 1.0");

        }

//        String version = getIntent().getStringExtra("upversion");
//
//        if (!TextUtils.isEmpty(version)) {
//            tea_os_version.setText("Cha OS " + version);
//        } else {
//            tea_os_version.setText("Cha OS 1.0");
//
//        }


        // Icon padding
//    ImageView view = (ImageView) findViewById(android.R.id.home);
//    view.setPadding(10, 0, 20, 10);
//
//    // Context title
//    setTitle(R.string.title_oad);

        // Initialize widgets
        mProgressInfo = (TextView) findViewById(R.id.progressBarInfo);
//    mTargImage = (TextView) findViewById(R.id.tw_target);
//    mFileImage = (TextView) findViewById(R.id.tw_file);
//    mLog = (TextView) findViewById(R.id.tw_log);
//    mLog.setMovementMethod(new ScrollingMovementMethod());
//    mProgressBar = (ProgressBar) findViewById(R.id.pb_progress);
//    mBtnStart = (Button) findViewById(R.id.btn_start);
//    mBtnStart.setEnabled(false);
//    mBtnLoadA = (Button) findViewById(R.id.btn_load_a);
//    mBtnLoadB = (Button) findViewById(R.id.btn_load_b);
//    mBtnLoadC = (Button) findViewById(R.id.btn_load_c);
//
//    // Sanity check
//    mBtnLoadA.setEnabled(mServiceOk);
//    mBtnLoadB.setEnabled(mServiceOk);
//    mBtnLoadC.setEnabled(mServiceOk);
        initIntentFilter();
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy");
        super.onDestroy();
        if (mTimerTask != null)
            mTimerTask.cancel();
        mTimer = null;
    }

    @Override
    public void onBackPressed() {
        Log.d(TAG, "onBackPressed");
        if (mProgramming) {
            Toast.makeText(this, "正在升级进制退出", Toast.LENGTH_LONG).show();
        } else
            super.onBackPressed();
    }

    boolean firsttime = true;
    boolean firstStart = true;

    @Override
    protected void onResume() {
        super.onResume();
//        if (mServiceOk) {
//            registerReceiver(mGattUpdateReceiver, mIntentFilter);
//
//            getTargetImageInfo();
//            setConnectionParameters();
//        } else {
//            Toast.makeText(this, "OAD service initialisation failed", Toast.LENGTH_LONG).show();
//        }


        if (firsttime) {
            if (mServiceOk) {
                registerReceiver(mGattUpdateReceiver, mIntentFilter);

                // Read target image info
                getTargetImageInfo();

                // Connection interval is too low by default
                setConnectionParameters();

                loadFile(FW_FILE_B, false);
//                updateGui();


            } else {
                Toast.makeText(this, "OAD service initialisation failed", Toast.LENGTH_LONG).show();
            }
//            mLeService.abortTimedDisconnect();

//      try {
//        Thread.sleep(5000);
//      } catch (InterruptedException e) {
//        e.printStackTrace();
//      }
//      startProgramming();
            firsttime = false;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mGattUpdateReceiver);
    }

    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            final String action = intent.getAction();

            if (BluetoothLeService.ACTION_DATA_NOTIFY.equals(action)) {
                byte[] value = intent.getByteArrayExtra(BluetoothLeService.EXTRA_DATA);
                String uuidStr = intent.getStringExtra(BluetoothLeService.EXTRA_UUID);

                if (uuidStr.equals(mCharIdentify.getUuid().toString())) {
                    // Image info notification
                    mTargImgHdr.ver = Conversion.buildUint16(value[1], value[0]);
                    mTargImgHdr.imgType = ((mTargImgHdr.ver & 1) == 1) ? 'B' : 'A';
                    mTargImgHdr.len = Conversion.buildUint16(value[3], value[2]);
                    displayImageInfo(null, mTargImgHdr);
//                    if (firstStart) {
//                        startProgramming();
//                        firstStart = false;
//                    }


                }

                if (uuidStr.equals(mCharBlock.getUuid().toString())) {
//                    if (mProgramming == true)
//                        programBlock(((value[1] << 8) & 0xff00) + (value[0] & 0x00ff));
//                    Log.d("FwUpdateActivityTea", String.format("NB: %02x%02x", value[1], value[0]));
//                    if (firstStart) {
//                        startProgramming();
//                        firstStart = false;
//                    }
                }

            } else if (BluetoothLeService.ACTION_DATA_WRITE.equals(action)) {
                int status = intent.getIntExtra(BluetoothLeService.EXTRA_STATUS, BluetoothGatt.GATT_SUCCESS);
                if (status != BluetoothGatt.GATT_SUCCESS) {
                    Toast.makeText(context, "GATT error: status=" + status, Toast.LENGTH_SHORT).show();
                }
            }
        }
    };


    private void initIntentFilter() {
        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(BluetoothLeService.ACTION_DATA_NOTIFY);
        mIntentFilter.addAction(BluetoothLeService.ACTION_DATA_WRITE);
    }


    public void onStart(View v) {
        if (mProgramming) {
            stopProgramming();
        } else {
            startProgramming();
        }
    }


    /**
     * 加载bin文件
     * @param v 点击的是哪个控件
     */
//    public void onLoad(View v) {
//        if (v.getId() == R.id.btn_load_a)
//            loadFile(FW_FILE_A, true);
//        else
//            loadFile(FW_FILE_B, true);
//        updateGui();
//    }

    /**
     * 开始更新
     */
    private void startProgramming() {
//        mLog.append("Programming started\n");
        mProgramming = true;
        updateGui();

        // Prepare image notification
        byte[] buf = new byte[OAD_IMG_HDR_SIZE + 2 + 2];
        buf[0] = Conversion.loUint16(mFileImgHdr.ver);
        buf[1] = Conversion.hiUint16(mFileImgHdr.ver);
        buf[2] = Conversion.loUint16(mFileImgHdr.len);
        buf[3] = Conversion.hiUint16(mFileImgHdr.len);
        System.arraycopy(mFileImgHdr.uid, 0, buf, 4, 4);

        // Send image notification
        mCharIdentify.setValue(buf);
        mLeService.writeCharacteristic(mCharIdentify);

        // Initialize stats
        mProgInfo.reset();

        // Start the programming thread
        new Thread(new OadTask()).start();

        mTimer = new Timer();
        mTimerTask = new ProgTimerTask();
        mTimer.scheduleAtFixedRate(mTimerTask, 0, TIMER_INTERVAL);
    }


    /**
     * 停止更新
     */
    private void stopProgramming() {
        mTimer.cancel();
        mTimer.purge();
        mTimerTask.cancel();
        mTimerTask = null;

        mProgramming = false;
        mProgressInfo.setText("");
        mProgressBar.setProgress(0);
        updateGui();

        if (mProgInfo.iBlocks == mProgInfo.nBlocks) {
//            mLog.setText("Programming complete!\n");
        } else {
//            mLog.append("Programming cancelled\n");
        }
    }


    /**
     * 更新UI，显示更新进度等信息
     */
    private void updateGui() {
        if (mProgramming) {
            // Busy: stop label, progress bar, disabled file selector
//  		mBtnStart.setText(R.string.cancel);
//  		mBtnLoadA.setEnabled(false);
//  		mBtnLoadB.setEnabled(false);
//  		mBtnLoadC.setEnabled(false);
        } else {
            // Idle: program label, enable file selector
            mProgressBar.setProgress(0);
//  		mBtnStart.setText(R.string.start_prog);
            if (mFileImgHdr.imgType == 'A') {
//  			mBtnLoadA.setEnabled(false);
//  			mBtnLoadB.setEnabled(true);
            } else if (mFileImgHdr.imgType == 'B') {
//  			mBtnLoadA.setEnabled(true);
//  			mBtnLoadB.setEnabled(false);
            }
//  		mBtnLoadC.setEnabled(true);
        }
    }


    /**
     * 加载更新bin文件
     * @param filepath 更新的image文件路径
     * @param isAsset 是从asset目录加载还是从手机目录选择
     * @return 返回true 表示加载成功，否则加载失败
     */
    private boolean loadFile(String filepath, boolean isAsset) {
        boolean fSuccess = false;

        // Load binary file
        try {
            // Read the file raw into a buffer
            InputStream stream;
            if (isAsset) {
                stream = getAssets().open(filepath);
            } else {

                String SDCard = Environment.getExternalStorageDirectory() + "";
                filepath = SDCard + "/" + Constant.path + "/" + Constant.path_bin_name;//文件存储路径

                File f = new File(filepath);
                stream = new FileInputStream(f);
            }
            stream.read(mFileBuffer, 0, mFileBuffer.length);
            stream.close();
        } catch (IOException e) {
            // Handle exceptions here
//            mLog.setText("File open failed: " + filepath + "\n");
            return false;
        }

        // Show image info
        mFileImgHdr.ver = Conversion.buildUint16(mFileBuffer[5], mFileBuffer[4]);
        mFileImgHdr.len = Conversion.buildUint16(mFileBuffer[7], mFileBuffer[6]);



        int result=0;
        if(mFileImgHdr.len<0){
            byte hi_l=mFileBuffer[7];
            byte  lo_l=mFileBuffer[6];
            String hi_hi_str=   Conversion.toHex(hi_l);
            int h1=Integer.parseInt(hi_hi_str, 16);

            result=h1*16*16;

            String lo_hi_str=   Conversion.toHex(lo_l);
            int h2=Integer.parseInt(lo_hi_str, 16);

            result=result+h2;





        }
        mFileImgHdr.len=result;
        mFileImgHdr.imgType = ((mFileImgHdr.ver & 1) == 1) ? 'B' : 'A';
        System.arraycopy(mFileBuffer, 8, mFileImgHdr.uid, 0, 4);
        displayImageInfo(null, mFileImgHdr);

        // Verify image types
        boolean ready = mFileImgHdr.imgType != mTargImgHdr.imgType;
//        int resid = ready ? R.style.dataStyle1 : R.style.dataStyle2;
//        mFileImage.setTextAppearance(this, resid);
//
//        // Enable programming button only if image types differ
//        mBtnStart.setEnabled(ready);

        // Expected duration
        displayStats();

        // Log
//        mLog.setText("Image " + mFileImgHdr.imgType + " selected.\n");
//        mLog.append(ready ? "Ready to program device!\n" : "Incompatible image, select alternative!\n");

        updateGui();

        return fSuccess;
    }


    /**
     *显示bin文件版本
     * @param v
     * @param h
     */
    private void displayImageInfo(TextView v, ImgHdr h) {
        int imgVer = (h.ver) >> 1;
        int imgSize = h.len * 4;
        String s = String.format("Type: %c Ver.: %d Size: %d", h.imgType, imgVer, imgSize);
//        v.setText(Html.fromHtml(s));
    }


    /**
     * 显示状态
     */
    private void displayStats() {
        String txt;
        int byteRate;
        int sec = mProgInfo.iTimeElapsed / 1000;
        if (sec > 0) {
            byteRate = mProgInfo.iBytes / sec;
        } else {
            byteRate = 0;
            return;
        }
        float timeEstimate;

        timeEstimate = ((float) (mFileImgHdr.len * 4) / (float) mProgInfo.iBytes) * sec;

        int othertime = (int) ((timeEstimate - sec) / 60);
        if (othertime > 40) {
            txt = "马上开始升级";
        } else {
            if(othertime==0){
                int othertimes = (int) ((timeEstimate - sec) %60);
                txt = "还需约" + othertimes + "秒";
            }else {
                txt = "还需约" + othertime + "分钟";
            }
        }
        mProgressInfo.setText(txt);
    }


    /**
     * 获取bin文件的信息
     */
    private void getTargetImageInfo() {
        boolean ok = enableNotification(mCharIdentify, true);
        if (ok)
            ok = writeCharacteristic(mCharIdentify, (byte) 0);
        if (ok)
            ok = writeCharacteristic(mCharIdentify, (byte) 1);
        if (!ok)
            Toast.makeText(this, "Failed to get target info", Toast.LENGTH_LONG).show();
    }


    /**
     * 给ble发送数据
     * @param c 需要操作的Characteristic
     * @param v 写入的数据
     * @return 操作是否成功
     */
    private boolean writeCharacteristic(BluetoothGattCharacteristic c, byte v) {
        boolean ok = mLeService.writeCharacteristic(c, v);
        if (ok)
            ok = mLeService.waitIdle(GATT_WRITE_TIMEOUT);
        return ok;
    }


    /**
     * 启用notify接收数据
     * @param c 需要操作的Characteristic
     * @param enable true启用false停用
     * @return
     */
    private boolean enableNotification(BluetoothGattCharacteristic c, boolean enable) {
        boolean ok = mLeService.setCharacteristicNotification(c, enable);
        if (ok)
            ok = mLeService.waitIdle(GATT_WRITE_TIMEOUT);
        return ok;
    }


    /**
     * 设置连接参数
     */
    private void setConnectionParameters() {
        // Make sure connection interval is long enough for OAD (Android default connection interval is 7.5 ms)
        byte[] value = {Conversion.loUint16(OAD_CONN_INTERVAL), Conversion.hiUint16(OAD_CONN_INTERVAL), Conversion.loUint16(OAD_CONN_INTERVAL),
                Conversion.hiUint16(OAD_CONN_INTERVAL), 0, 0, Conversion.loUint16(OAD_SUPERVISION_TIMEOUT), Conversion.hiUint16(OAD_SUPERVISION_TIMEOUT)};
        mCharConnReq.setValue(value);
        mLeService.writeCharacteristic(mCharConnReq);
    }


    /**
     * 写入固件更新的参数
     */
    private void programBlock() {
        if (!mProgramming)
            return;

        if (mProgInfo.iBlocks < mProgInfo.nBlocks) {
            mProgramming = true;
            String msg = new String();

            // Prepare block
            mOadBuffer[0] = Conversion.loUint16(mProgInfo.iBlocks);
            mOadBuffer[1] = Conversion.hiUint16(mProgInfo.iBlocks);
            System.arraycopy(mFileBuffer, mProgInfo.iBytes, mOadBuffer, 2, OAD_BLOCK_SIZE);

            // Send block
            mCharBlock.setValue(mOadBuffer);
            boolean success = mLeService.writeCharacteristic(mCharBlock);

            if (success) {
                // Update stats
                mProgInfo.iBlocks++;
                mProgInfo.iBytes += OAD_BLOCK_SIZE;
                mProgressBar.setProgress((mProgInfo.iBlocks * 100) / mProgInfo.nBlocks);
                if (!mLeService.waitIdle(GATT_WRITE_TIMEOUT)) {
                    mProgramming = false;
                    success = false;
                    msg = "GATT write timeout\n";
                }

                if (mProgInfo.iBlocks == mProgInfo.nBlocks) {


                    try {
                        Thread.sleep(60000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    runOnUiThread(new Runnable() {
                        public void run() {
                            mProgramming = false;
                            update_process_sucess_text.setVisibility(View.VISIBLE);
                            update_process_rel.setVisibility(View.GONE);
                            update_process_success_rel.setVisibility(View.VISIBLE);
                            MainApp mainApp = (MainApp) getApplicationContext();
                            mainApp.boolupdateSuccess = 2;

                            Log.d("closedisconnect", "closedisconnect");
                            if (mLeService != null) {
                                mLeService.closedisconnect();
//                                mLeService.closedisconnect();
                            }
                            QuinticBleAPISdkBase.resultDevice = null;

                            closeBle();
                        }
                    });
                }

            } else {
                mProgramming = false;
                msg = "GATT writeCharacteristic failed\n";
            }
            if (!success) {
//                mLog.append(msg);
            }
        } else {
            mProgramming = false;
        }

        if (!mProgramming) {
            runOnUiThread(new Runnable() {
                public void run() {
                    displayStats();
                    stopProgramming();
                }
            });
        }
    }

    public static void closeBle() {

        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter
                .getDefaultAdapter();
        if (mBluetoothAdapter == null) {
//            Toast.makeText(context, "本机没有找到蓝牙硬件或驱动！", Toast.LENGTH_SHORT).show();
//            context.finish();
        }
        // 如果本地蓝牙没有开启，则开启
        if (mBluetoothAdapter.isEnabled()) {

            mBluetoothAdapter.disable();//关闭蓝牙
        }


    }
    /**
     * 发送数据线程
     */
    private class OadTask implements Runnable {
        @Override
        public void run() {
            while (mProgramming) {
                try {
                    Thread.sleep(SEND_INTERVAL);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                for (int i = 0; i < BLOCKS_PER_CONNECTION & mProgramming; i++) {
                    programBlock();
                }
                if ((mProgInfo.iBlocks % 100) == 0) {
                    // Display statistics each 100th block
                    runOnUiThread(new Runnable() {
                        public void run() {
                            displayStats();
                        }
                    });
                }
            }
        }
    }

    private class ProgTimerTask extends TimerTask {
        @Override
        public void run() {
            mProgInfo.iTimeElapsed += TIMER_INTERVAL;
        }
    }


    /**
     * 文件信息
     */
    private class ImgHdr {
        int ver;
        int len;
        Character imgType;
        byte[] uid = new byte[4];
    }


    /**
     * 发送文件的数据结构
     */
    private class ProgInfo {
        int iBytes = 0; // Number of bytes programmed
        short iBlocks = 0; // Number of blocks programmed
        short nBlocks = 0; // Total number of blocks
        int iTimeElapsed = 0; // Time elapsed in milliseconds

        void reset() {
            iBytes = 0;
            iBlocks = 0;
            iTimeElapsed = 0;
            nBlocks = (short) (mFileImgHdr.len / (OAD_BLOCK_SIZE / HAL_FLASH_WORD_SIZE));
        }
    }
}
