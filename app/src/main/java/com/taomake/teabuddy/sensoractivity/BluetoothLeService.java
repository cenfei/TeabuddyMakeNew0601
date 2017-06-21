package com.taomake.teabuddy.sensoractivity;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

// import android.util.Log;

/**
 * Service for managing connection and data communication with a GATT server
 * hosted on a given Bluetooth LE device.
 */
public class BluetoothLeService extends Service {
    static final String TAG = "BluetoothLeService";

    public final static String ACTION_GATT_CONNECTED = "android.ble.com.bleandroidframework" +
            ".ACTION_GATT_CONNECTED";
    public final static String ACTION_GET_CHARTACTERISTIC = "android.ble.com.bleandroidframework" + ".ACTION_GET_CHARTACTERISTIC";
    public final static String ACTION_GATT_DISCONNECTED = "android.ble.com.bleandroidframework.ACTION_GATT_DISCONNECTED";
    public final static String ACTION_GATT_SERVICES_DISCOVERED = "android.ble.com.bleandroidframework.ACTION_GATT_SERVICES_DISCOVERED";
    public final static String ACTION_DATA_READ = "android.ble.com.bleandroidframework.ACTION_DATA_READ";
    public final static String ACTION_DATA_NOTIFY = "android.ble.com.bleandroidframework.ACTION_DATA_NOTIFY";
    public final static String ACTION_DATA_WRITE = "android.ble.com.bleandroidframework.ACTION_DATA_WRITE";
    public final static String ACTION_DATA_LOG = "android.ble.com.bleandroidframework.LOG";
    public final static String EXTRA_DATA = "android.ble.com.bleandroidframework.EXTRA_DATA";
    public final static String EXTRA_UUID = "android.ble.com.bleandroidframework.EXTRA_UUID";
    public final static String EXTRA_STATUS = "android.ble.com.bleandroidframework.EXTRA_STATUS";
    public final static String EXTRA_ADDRESS = "android.ble.com.bleandroidframework.EXTRA_ADDRESS";

    // BLE
    private BluetoothManager mBluetoothManager = null;
    public BluetoothAdapter mBtAdapter = null;
    private BluetoothGatt mBluetoothGatt = null;
    private static BluetoothLeService mThis = null;
    private volatile boolean mBusy = false; // Write/read pending response
    private String mBluetoothDeviceAddress;
    public boolean mServicesRdy = false; //service是否已初始化完毕
    public List<BluetoothGattService> mServiceList = new ArrayList<BluetoothGattService>(); //ble
    // 提供的service列表
    public BluetoothGattCharacteristic m_writeGattCharacteristic = null;  //写入数据的Characteristic
    public BluetoothGattCharacteristic m_readGattCharacteristic = null;   //读取数据的Characteristic
    public BluetoothGattService mOadService = null; //OAD service

    /**
     * 连接回调，有关4.0的回调 都在这个接口里
     */
    private BluetoothGattCallback mGattCallbacks = new BluetoothGattCallback() {

        /**
         * 连接状态发生变化时会调用此函数
         * @param gatt 对象
         * @param status 标识是否了连接的状态
         * @param newState 新的状态是连接 还是断开
         */
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status,
                                            int newState) {
            if (mBluetoothGatt == null) {
                // Log.e(TAG, "mBluetoothGatt not created!");
                return;
            }

            BluetoothDevice device = gatt.getDevice();
            String address = device.getAddress();

            try {
                switch (newState) {
                    case BluetoothProfile.STATE_CONNECTED:
                        broadcastUpdate(ACTION_GATT_CONNECTED, address, status);

                        onInflated();

                        break;
                    case BluetoothProfile.STATE_DISCONNECTED:
                        broadcastUpdate(ACTION_GATT_DISCONNECTED, address, status);
                        break;
                    default:
                        // Log.e(TAG, "New state not processed: " + newState);
                        break;
                }
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }

        /**
         * 扫描五福回调
         * @param gatt 对象
         * @param status 状态 一般为GATT_SUCCESS
         */
        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            BluetoothDevice device = gatt.getDevice();

            broadcastUpdate(ACTION_GATT_SERVICES_DISCOVERED, device.getAddress(),
                    status);

            displayServices();
            checkOad();
        }







        /**
         * 一般收到值的时候 会回调此函数
         * @param gatt 对象
         * @param characteristic 对象
         */
        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt,
                                            BluetoothGattCharacteristic characteristic) {
            broadcastUpdate(ACTION_DATA_NOTIFY, characteristic,
                    BluetoothGatt.GATT_SUCCESS);

            //  characteristic.getValue()

            BtLog.LogOut("receive data");

        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt,
                                         BluetoothGattCharacteristic characteristic, int status) {
            broadcastUpdate(ACTION_DATA_READ, characteristic, status);
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt,
                                          BluetoothGattCharacteristic characteristic, int status) {
            broadcastUpdate(ACTION_DATA_WRITE, characteristic, status);
        }

        @Override
        public void onDescriptorRead(BluetoothGatt gatt,
                                     BluetoothGattDescriptor descriptor, int status) {
            mBusy = false;
        }

        @Override
        public void onDescriptorWrite(BluetoothGatt gatt,
                                      BluetoothGattDescriptor descriptor, int status) {
            // Log.i(TAG, "onDescriptorWrite: " + descriptor.getUuid().toString());
            mBusy = false;
        }
    };

    private void broadcastUpdate(final String action, final String address,
                                 final int status) {
        final Intent intent = new Intent(action);
        intent.putExtra(EXTRA_ADDRESS, address);
        intent.putExtra(EXTRA_STATUS, status);
        sendBroadcast(intent);
        mBusy = false;
    }

    private void broadcastUpdate(final String action) {
        final Intent intent = new Intent(action);
        sendBroadcast(intent);
        mBusy = false;
    }


    private void broadcastUpdate(final String action,
                                 final BluetoothGattCharacteristic characteristic, final int status) {
        final Intent intent = new Intent(action);
        intent.putExtra(EXTRA_UUID, characteristic.getUuid().toString());
        intent.putExtra(EXTRA_DATA, characteristic.getValue());
        intent.putExtra(EXTRA_STATUS, status);
        sendBroadcast(intent);
        mBusy = false;
    }

    private boolean checkGatt() {
        if (mBtAdapter == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return false;
        }
        if (mBluetoothGatt == null) {
             Log.w(TAG, "BluetoothGatt not initialized");
            return false;
        }

        if (mBusy) {
            Log.w(TAG, "LeService busy");
            return false;
        }
        return true;

    }


    public class LocalBinder extends Binder {
        public BluetoothLeService getService() {
            return BluetoothLeService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public boolean onUnbind(Intent intent) {

        mServicesRdy = false;
        close();
        return super.onUnbind(intent);
    }

    private final IBinder binder = new LocalBinder();


    /**
     * 初始化函数
     *
     * @return true 初始化成功 false  初始化失败
     */
    public boolean initialize() {
        // For API level 18 and above, get a reference to BluetoothAdapter through
        // BluetoothManager.
        mThis = this;
        if (mBluetoothManager == null) {
            mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
            if (mBluetoothManager == null) {
                // Log.e(TAG, "Unable to initialize BluetoothManager.");
                return false;
            }
        }

        mBtAdapter = mBluetoothManager.getAdapter();
        if (mBtAdapter == null) {
            // Log.e(TAG, "Unable to obtain a BluetoothAdapter.");
            return false;
        }
        return true;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mBluetoothGatt != null) {
            mBluetoothGatt.close();
            mBluetoothGatt = null;
        }
    }

    //
    // GATT API
    //

    /**
     * Request a read on a given {@code BluetoothGattCharacteristic}. The read
     * result is reported asynchronously through the
     * {@code BluetoothGattCallback#onCharacteristicRead(android.bluetooth.BluetoothGatt, android.bluetooth.BluetoothGattCharacteristic, int)}
     * callback.
     *
     * @param characteristic The characteristic to read from.
     */
    public void readCharacteristic(BluetoothGattCharacteristic characteristic) {
        if (!checkGatt())
            return;
        mBusy = true;
        mBluetoothGatt.readCharacteristic(characteristic);
    }

    public boolean writeCharacteristic(
            BluetoothGattCharacteristic characteristic, byte b) {
        if (!checkGatt())
            return false;

        byte[] val = new byte[1];
        val[0] = b;
        characteristic.setValue(val);

        mBusy = true;
        return mBluetoothGatt.writeCharacteristic(characteristic);
    }

    public boolean writeCharacteristic(
            BluetoothGattCharacteristic characteristic, boolean b) {
        if (!checkGatt())
            return false;

        byte[] val = new byte[1];

        val[0] = (byte) (b ? 1 : 0);
        characteristic.setValue(val);
        mBusy = true;
        return mBluetoothGatt.writeCharacteristic(characteristic);
    }

    public boolean writeCharacteristic(BluetoothGattCharacteristic characteristic) {
        if (!checkGatt())
            return false;

        mBusy = true;
        return mBluetoothGatt.writeCharacteristic(characteristic);
    }


    /**
     * 返回扫描出service的个数
     *
     * @return 返回 service的个数
     */
    public int getNumServices() {
        if (mBluetoothGatt == null)
            return 0;

        return mBluetoothGatt.getServices().size();
    }

    /**
     * 返回扫描出service的对象数组
     *
     * @return 返回 service的对象数组
     */
    public List<BluetoothGattService> getSupportedGattServices() {
        if (mBluetoothGatt == null)
            return null;

        return mBluetoothGatt.getServices();
    }


    /**
     * 启用BluetoothGattCharacteristic 的notify
     *
     * @param characteristic 对象
     * @param enable         true为启用 false为关闭
     * @return 是否设置成功
     */
    public boolean setCharacteristicNotification(
            BluetoothGattCharacteristic characteristic, boolean enable) {
        if (!checkGatt())
            return false;

        boolean ok = false;
        if (mBluetoothGatt.setCharacteristicNotification(characteristic, enable)) {

            BluetoothGattDescriptor clientConfig = characteristic
                    .getDescriptor(GattInfo.CLIENT_CHARACTERISTIC_CONFIG);
            if (clientConfig != null) {

                if (enable) {

                    ok = clientConfig
                            .setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                } else {

                    ok = clientConfig
                            .setValue(BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE);
                }

                if (ok) {
                    mBusy = true;
                    ok = mBluetoothGatt.writeDescriptor(clientConfig);
                }
            }
        }

        return ok;
    }

    /**
     * 是否启用通知
     *
     * @param characteristic 对象
     * @return
     */
    public boolean isNotificationEnabled(
            BluetoothGattCharacteristic characteristic) {
        if (!checkGatt())
            return false;

        BluetoothGattDescriptor clientConfig = characteristic
                .getDescriptor(GattInfo.CLIENT_CHARACTERISTIC_CONFIG);
        if (clientConfig == null)
            return false;

        return clientConfig.getValue() == BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE;
    }


    /**
     * 发起连接请求
     *
     * @param address 蓝牙设备地址
     * @return 是否连接成功
     */
    public boolean connect(final String address) {
        if (mBtAdapter == null || address == null) {
            // Log.w(TAG, "BluetoothAdapter not initialized or unspecified address.");
            return false;
        }
        final BluetoothDevice device = mBtAdapter.getRemoteDevice(address);
        int connectionState = mBluetoothManager.getConnectionState(device,
                BluetoothProfile.GATT);

        if (connectionState == BluetoothProfile.STATE_DISCONNECTED) {


            if (mBluetoothDeviceAddress != null
                    && address.equals(mBluetoothDeviceAddress) && mBluetoothGatt != null) {

                if (mBluetoothGatt.connect()) {
                    return true;
                } else {
                    return false;
                }
            }

            if (device == null) {
                return false;
            }

            mBluetoothGatt = device.connectGatt(this, false, mGattCallbacks);
            mBluetoothDeviceAddress = address;
        } else {

            return false;
        }
        return true;
    }


    /**
     * 断开连接
     *
     * @param address 蓝牙设备地址
     */
    public void disconnect(String address) {
        if (mBtAdapter == null) {
            // Log.w(TAG, "disconnect: BluetoothAdapter not initialized");
            return;
        }
        if (address == null || address.isEmpty())
            return;
        final BluetoothDevice device = mBtAdapter.getRemoteDevice(address);
        int connectionState = mBluetoothManager.getConnectionState(device,
                BluetoothProfile.GATT);

        if (mBluetoothGatt != null) {
            if (connectionState != BluetoothProfile.STATE_DISCONNECTED) {
                mBluetoothGatt.disconnect();
            } else {
                // Log.w(TAG, "Attempt to disconnect in state: " + connectionState);
            }
        }
    }

    /**
     * After using a given BLE device, the app must call this method to ensure
     * resources are released properly.
     */
    public void close() {
        if (mBluetoothGatt != null) {
            // Log.i(TAG, "close");
            mBluetoothGatt.close();
            mBluetoothGatt = null;
        }
    }

    public int numConnectedDevices() {
        int n = 0;

        if (mBluetoothGatt != null) {
            List<BluetoothDevice> devList;
            devList = mBluetoothManager.getConnectedDevices(BluetoothProfile.GATT);
            n = devList.size();
        }
        return n;
    }

    /**
     * 获取设备支持的service
     */
    private void displayServices() {
        mServicesRdy = true;

        try {
            mServiceList = getSupportedGattServices();


            Intent intent = new Intent();
            intent.setAction(BluetoothLeService.ACTION_DATA_LOG);
            intent.putExtra(BluetoothLeService.EXTRA_DATA, "     service count="+mServiceList.size());
            sendBroadcast(intent);


        } catch (Exception e) {
            e.printStackTrace();
            mServicesRdy = false;
        }
    }


    /**
     * 检查是否支持oad，并初始化数据的读写接口,
     * 需要支持的是，此处只有获得到我们需要的Characteristic才能认为设备已经连接成功了，系统的回调通知并不准确。一般收到系统回调过3-5秒
     * 才能获得到Characteristic，在大多数的android手机上都是这样一个结果
     */
    private void checkOad() {
        // mOadService = null;


        for (int i = 0; i < mServiceList.size()
                ; i++) {
            BluetoothGattService srv = mServiceList.get(i);


            BtLog.LogOut("uuid = " + srv.getUuid().toString());

            if (srv.getUuid().equals(GattInfo.OAD_SERVICE_UUID)) {
                mOadService = srv;
                broadcastUpdate(ACTION_GET_CHARTACTERISTIC);//FOX添加
            }


            if(i>2) {
                Intent intent = new Intent();
                intent.setAction(BluetoothLeService.ACTION_DATA_LOG);
                intent.putExtra(BluetoothLeService.EXTRA_DATA, "     UUID="+ srv.getUuid()
                        .toString());
                sendBroadcast(intent);
            }


            if (srv != null && srv.getUuid().equals(UUID.fromString(GattInfo.SERVICE_UUID))) {
                List<BluetoothGattCharacteristic> characteristicList = srv.getCharacteristics();

                for (int j = 0; j < characteristicList.size(); j++) {
                    BluetoothGattCharacteristic characteristic = characteristicList.get(j);


                    if (characteristic != null) {
                        if (characteristic.getUuid().equals(UUID.fromString(GattInfo.WRITE_CHARACTERISTIC))) {
                            m_writeGattCharacteristic = characteristic;
                        }


                        if (characteristic.getUuid().equals(UUID.fromString(GattInfo.READ_CHARACTERISTIC))) {
                            m_readGattCharacteristic = characteristic;

                            setCharacteristicNotification
                                    (m_readGattCharacteristic, true);

                        }


                        if (m_readGattCharacteristic != null && m_writeGattCharacteristic != null) {
                            broadcastUpdate(ACTION_GET_CHARTACTERISTIC);
                        }


                    }
                }
            }

            //   broadcastUpdate(ACTION_GET_CHARTACTERISTIC);
        }
    }


    void onInflated() {


        if (!mServicesRdy && mBluetoothGatt != null) {
            if (getNumServices() == 0)
                discoverServices();
            else {
                displayServices();
            }
        }
    }

    /**
     * 查找服务
     */
    private void discoverServices() {
        if (mBluetoothGatt.discoverServices()) {
            mServiceList.clear();
        }
    }

    /**
     * 写入数据不需要回复确认
     *
     * @param data 要写入的数据
     * @return 返回true 表示写入成功 返回false 表示写入失败
     */
    public boolean writeDataWithNoResponse(byte[] data) {

        m_writeGattCharacteristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE);
        m_writeGattCharacteristic.setValue(data);
        boolean success = writeCharacteristic
                (m_writeGattCharacteristic);

        return success;


    }

    /**
     * 写入数据需要回复确认
     *
     * @param data 要写入的数据
     * @return 返回true 表示写入成功 返回false 表示写入失败
     */
    public boolean writeDataWithResponse(byte[] data) {

        m_writeGattCharacteristic.setValue(data);
        boolean success = writeCharacteristic
                (m_writeGattCharacteristic);

        return success;

    }


    //
    // Utility functions
    //
    public static BluetoothGatt getBtGatt() {
        return mThis.mBluetoothGatt;
    }

    public static BluetoothManager getBtManager() {
        return mThis.mBluetoothManager;
    }

    public static BluetoothLeService getInstance() {
        return mThis;
    }

    public boolean waitIdle(int timeout) {
        timeout /= 10;
        while (--timeout > 0) {
            if (mBusy)
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            else
                break;
        }

        return timeout > 0;
    }


    public void closedisconnect() {
        if (mBtAdapter == null) {
            // Log.w(TAG, "disconnect: BluetoothAdapter not initialized");
            return;
        }
        if(mBluetoothDeviceAddress!=null) {
            final BluetoothDevice device = mBtAdapter.getRemoteDevice(mBluetoothDeviceAddress);
            int connectionState = mBluetoothManager.getConnectionState(device,
                    BluetoothProfile.GATT);

            if (mBluetoothGatt != null) {
                if (connectionState != BluetoothProfile.STATE_DISCONNECTED) {
                    mBluetoothGatt.disconnect();


                    mBluetoothGatt.close();
                } else {
                    // Log.w(TAG, "Attempt to disconnect in state: " + connectionState);
                }
            }
        }
    }
}
