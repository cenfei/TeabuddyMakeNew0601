package quinticble;

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
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * ble连接
 */
public class BleConnection {

    private Context context;

    private BluetoothManager bluetoothManager;

    private BluetoothAdapter bluetoothAdapter;

    private BluetoothGattCallback gattCallback;

    private BleStateChangeCallback bleStateChangeCallback;

    private BluetoothDevice bluetoothDevice;

    private static final String LOCK_CONNECT_DEVICE = "BleConnection_ConnectDevice";

    private String serviceUuid;

    private String writeCharacteristicUuid;

    private String notifyCharacteristicUuid;

    private BluetoothGattCharacteristic notifyCharacteristic;

    private BluetoothGattCharacteristic writeCharacteristic;

    private static final String DESCRIPTOR_NOTIFY_UUID = "00002902-0000-1000-8000-00805f9b34fb";

    public BluetoothGatt gatt;

    private Queue<BleWriteData> writeDataQueue;

    private Timeout connectTimeout;

    private String deviceAddress;

    private BleStateChangeCallback blankCallback;

    private boolean isRealConnected = false;

    private boolean needMoreTimeout = false;

    private long lastcodeTime = 0;

    /**
     * 初始化ble连接
     *
     * @param context 上下文信息
     */
    public BleConnection(final Context context, final String serviceUuid, final String writeCharacteristicUuid, final String notifyCharacteristicUuid) throws BleException {
        if (!context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            throw new BleException(BleException.BLE_NOT_SUPPORTED, "设备不支持ble");
        }
        this.context = context;
        this.serviceUuid = serviceUuid;
        this.writeCharacteristicUuid = writeCharacteristicUuid;
        this.notifyCharacteristicUuid = notifyCharacteristicUuid;
        this.writeDataQueue = new ConcurrentLinkedQueue<>();
        bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();
        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
            throw new BleException(BleException.BLUETOOTH_NOT_OPENED, "设备未打开蓝牙");
        }

        connectTimeout = new Timeout(30000, new Timeout.TimerEvent() {
            @Override
            public void onTimeout() {
                errorWhenConnecting(new BleException(BleException.CONNECT_TIMEOUT, "连接设备超时"));
                try {
                    if (!isConnected()) {
                        if (gatt != null) {
                            gatt.close();
                        }
                    } else {
                        gatt.disconnect();
                    }
                } catch (Exception ex) {

                }
            }
        });

        blankCallback = new BleStateChangeCallback() {
        };

        gattCallback = new BluetoothGattCallback() {
            @Override
            public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
                super.onConnectionStateChange(gatt, status, newState);
                BleConnection.this.gatt = gatt;
                Log.d("gatt", status + "," + newState + gatt.getServices());
                if (BluetoothGatt.GATT_SUCCESS == status && newState == BluetoothProfile.STATE_CONNECTED) {
                    connectTimeout.restart(10000);
                    if (!gatt.discoverServices()) {
                        errorWhenConnecting(new BleException(BleException.SERVICE_NOT_FOUND, "无法发现服务"));
                        gatt.disconnect();
                    }
                } else {
                    try {
                        if (gatt != null) {
                            gatt.close();
                        }
                    } catch (Exception e) {
                    }
                    if (connectTimeout.isStarted()) {
                        errorWhenConnecting(new BleException(BleException.DEVICE_DISCONNECTED, "设备连接异常断开"));
                    } else {
                        bleStateChangeCallback.onDisconnected();
                    }
                    connectTimeout.cancel();
                    isRealConnected = false;
                }
            }

            @Override
            public void onServicesDiscovered(final BluetoothGatt gatt, final int status) {
                super.onServicesDiscovered(gatt, status);
                Log.d("gatt1", status + "," + gatt.getServices());

                if (BluetoothGatt.GATT_SUCCESS == status) {


                    final BluetoothGattService serviceoad = gatt.getService(UUID.fromString(QuinticUuid.OAD_UPDATE_UUID));

                    if (serviceoad != null) {
//                        bluetoothDevice = gatt.getDevice();
                        if (connectTimeout.isStarted()) {
                            connectTimeout.cancel();
                            LockUtil.getInstance().releaseLock(LOCK_CONNECT_DEVICE);
                            bleStateChangeCallback.onUpdate(null);

                        }
                        gatt.disconnect();
                        return;
                    }


                    final BluetoothGattService service = gatt.getService(UUID.fromString(serviceUuid));
                    if (service == null) {
                        errorWhenConnecting(new BleException(BleException.SERVICE_NOT_FOUND, "无法找到服务"));
                        gatt.disconnect();
                    } else {


                        notifyCharacteristic = service.getCharacteristic(UUID.fromString(notifyCharacteristicUuid));
                        writeCharacteristic = service.getCharacteristic(UUID.fromString(writeCharacteristicUuid));
//
                        if (notifyCharacteristic == null || writeCharacteristic == null) {
                            errorWhenConnecting(new BleException(BleException.DEVICE_UNREACHABLE, "无法连接设备"));
                            gatt.disconnect();
                        } else {


                            if (!gatt.setCharacteristicNotification(notifyCharacteristic, true)) {
                                errorWhenConnecting(new BleException(BleException.DEVICE_UNREACHABLE, "无法连接设备"));
                                gatt.disconnect();
                            } else {
                                Log.d("notifyCharacteristic", status + "," + gatt.getServices());

                                BluetoothGattDescriptor notifyDescriptor = notifyCharacteristic.getDescriptor(UUID.fromString(DESCRIPTOR_NOTIFY_UUID));
                                if (notifyDescriptor == null) {
                                    errorWhenConnecting(new BleException(BleException.DEVICE_UNREACHABLE, "无法连接设备"));
                                    gatt.disconnect();
                                } else {

                                    /**
                                     * BLE设备主动往手机写数据：这个功能叫做notify、indecation（用这种方式的比较少，微信BLE就是用这种方式），汉语叫可通知属性（不是所有的通道都有这个属性），要主动去开启（有些不规范的BLE设备已经默认开启，可以省略这步）。
                                     */

                                    if (0 != (notifyCharacteristic.getProperties() & BluetoothGattCharacteristic.PROPERTY_NOTIFY)) { // 查看是否带有可通知属性notify

                                        if (!notifyDescriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE)) {
                                            errorWhenConnecting(new BleException(BleException.DEVICE_UNREACHABLE, "无法连接设备"));
                                            gatt.disconnect();
                                        } else {
                                            connectTimeout.restart(10000);
                                            if (!gatt.writeDescriptor(notifyDescriptor)) {
                                                errorWhenConnecting(new BleException(BleException.DEVICE_UNREACHABLE, "无法连接设备"));
                                                gatt.disconnect();
                                            }
                                        }
                                    } else if (0 != (notifyCharacteristic.getProperties() & BluetoothGattCharacteristic.PROPERTY_INDICATE)) {  // 查看是否带有indecation属性
                                        if (!notifyDescriptor.setValue(BluetoothGattDescriptor.ENABLE_INDICATION_VALUE)) {
                                            errorWhenConnecting(new BleException(BleException.DEVICE_UNREACHABLE, "无法连接设备"));
                                            gatt.disconnect();
                                        } else {
                                            connectTimeout.restart(10000);
                                            if (!gatt.writeDescriptor(notifyDescriptor)) {
                                                errorWhenConnecting(new BleException(BleException.DEVICE_UNREACHABLE, "无法连接设备"));
                                                gatt.disconnect();
                                            }
                                        }
                                    }
                                }
                            }
                        }


                        BluetoothGattCharacteristic notifyCharacteristicIndicate = service.getCharacteristic(UUID.fromString(QuinticUuid.INDICATE_NOTIFY_CHARACTERISTIC_UUID));
                        if (notifyCharacteristicIndicate == null) {
                            errorWhenConnecting(new BleException(BleException.DEVICE_UNREACHABLE, "无法连接设备"));
                            gatt.disconnect();
                        } else {


                            if (!gatt.setCharacteristicNotification(notifyCharacteristicIndicate, true)) {
                                errorWhenConnecting(new BleException(BleException.DEVICE_UNREACHABLE, "无法连接设备"));
                                gatt.disconnect();
                            } else {
                                Log.d("notifyCharacteristic", status + "," + gatt.getServices());

                                BluetoothGattDescriptor notifyDescriptor = notifyCharacteristicIndicate.getDescriptor(UUID.fromString(DESCRIPTOR_NOTIFY_UUID));
                                if (notifyDescriptor == null) {
                                    errorWhenConnecting(new BleException(BleException.DEVICE_UNREACHABLE, "无法连接设备"));
                                    gatt.disconnect();
                                } else {

                                    /**
                                     * BLE设备主动往手机写数据：这个功能叫做notify、indecation（用这种方式的比较少，微信BLE就是用这种方式），汉语叫可通知属性（不是所有的通道都有这个属性），要主动去开启（有些不规范的BLE设备已经默认开启，可以省略这步）。
                                     */

//                                     if (0 != (notifyCharacteristicIndicate.getProperties() & BluetoothGattCharacteristic.PROPERTY_NOTIFY)) { // 查看是否带有可通知属性notify
//
//                                         if (!notifyDescriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE)) {
//                                             errorWhenConnecting(new BleException(BleException.DEVICE_UNREACHABLE, "无法连接设备"));
//                                             gatt.disconnect();
//                                         } else {
//                                             connectTimeout.restart(10000);
//                                             if (!gatt.writeDescriptor(notifyDescriptor)) {
//                                                 errorWhenConnecting(new BleException(BleException.DEVICE_UNREACHABLE, "无法连接设备"));
//                                                 gatt.disconnect();
//                                             }
//                                         }
//                                     } else if (0 != (notifyCharacteristicIndicate.getProperties() & BluetoothGattCharacteristic.PROPERTY_INDICATE)) {  // 查看是否带有indecation属性
                                    if (!notifyDescriptor.setValue(BluetoothGattDescriptor.ENABLE_INDICATION_VALUE)) {
                                        errorWhenConnecting(new BleException(BleException.DEVICE_UNREACHABLE, "无法连接设备"));
                                        gatt.disconnect();
                                    } else {
//                        connectTimeout.restart(10000);
//                        if (!gatt.writeDescriptor(notifyDescriptor)) {
//                            errorWhenConnecting(new BleException(BleException.DEVICE_UNREACHABLE, "无法连接设备"));
//                            gatt.disconnect();
//                        }
                                    }
//                                     }
                                }
                            }
                        }


                    }
                } else {
                    gatt.disconnect();
                }
            }

            @Override
            public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
                super.onCharacteristicWrite(gatt, characteristic, status);
                if (status == BluetoothGatt.GATT_SUCCESS) {
                    String writedata = QuinticCommon.unsignedBytesToHexString(characteristic.getValue(), " ");

                    Log.i("----- ble write fox-----", QuinticCommon.unsignedBytesToHexString(characteristic.getValue(), " "));

                    String trimResult = writedata.replace(" ", "");


                    if (trimResult.contains("eb0cffff")) {
                        connectTimeout.restart(600000);
                    } else {
                        connectTimeout.restart(15000);

                    }


                        for (BleDataObserver bleDataObserver : Observers.getBleDataObservers()) {
                            bleDataObserver.onDataWrite(characteristic.getValue());
                        }
                        writeDataQueue.poll();
                        doWrite();
                        bleStateChangeCallback.onWrite(characteristic.getValue());
//                    }

                    if (trimResult.equals("eb0501")) {
                        bleStateChangeCallback.onNotify(characteristic.getValue());


                    }
                    if (trimResult.equals("eb19")) {
                        bleStateChangeCallback.onNotify(characteristic.getValue());


                    }


                } else {
                    errorWhenConnecting(new BleException(BleException.DEVICE_UNREACHABLE, "写入时发生错误"));
                }
            }

            @Override
            public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
                super.onCharacteristicChanged(gatt, characteristic);
                //只要连接上----ec 数据在这里
                String notifydata = QuinticCommon.unsignedBytesToHexString(characteristic.getValue(), " ");
                Log.i("----- ble notify -----", notifydata);

                for (BleDataObserver bleDataObserver : Observers.getBleDataObservers()) {
                    bleDataObserver.onDataNotify(characteristic.getValue());
                }
                String trimResult = notifydata.replace(" ", "");

                if (trimResult.contains("eb0c00")) {
                    connectTimeout.restart(600000);
                    needMoreTimeout = true;
                }else{
                    needMoreTimeout = false;

                }
                if (trimResult.contains("eb0c0b")) {
                    connectTimeout.restart(600000);
                    needMoreTimeout = false;
                }

                if (characteristic.getValue() != null && !(characteristic.getValue().length == 2 && QuinticCommon.matchData(characteristic.getValue(), new byte[]{0, 0}))) {
                    if (needMoreTimeout) {
                        connectTimeout.restart(600000);
                    } else {
                        connectTimeout.restart(15000);

                    }
                    Log.i("----- keep notify -----", "notify");
                    bleStateChangeCallback.onNotify(characteristic.getValue());

                    if (notifydata.contains("ec")) {

                        Intent intent = new Intent("com.pushtest.broadcast");
                        Log.i("Broadcast Send", notifydata);

                        intent.putExtra("CurrentLoading", notifydata);

                        final Context context1 = context;
                        context1.sendBroadcast(intent);
                    }
                }
            }

            @Override
            public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
                super.onDescriptorWrite(gatt, descriptor, status);
                Log.d("----- ble onDescriptorWrite -----", status + "");
                if (BluetoothGatt.GATT_SUCCESS == status) {
                    if (descriptor.getUuid().toString().equals(DESCRIPTOR_NOTIFY_UUID)) {
                            connectTimeout.cancel();
                            bluetoothDevice = gatt.getDevice();
                            isRealConnected = true;
                            LockUtil.getInstance().releaseLock(LOCK_CONNECT_DEVICE);
                            bleStateChangeCallback.onConnected(bluetoothDevice);

                    }
                } else {
                    errorWhenConnecting(new BleException(BleException.DEVICE_UNREACHABLE, "无法连接设备"));
                    gatt.disconnect();
                }
            }
        };
    }

    private void doWrite() {
        try {
            BleWriteData data = writeDataQueue.peek();
            if (data != null) {
                writeCharacteristic.setValue(data.getBinary());
                writeCharacteristic.setWriteType(data.getWriteType());
                gatt.writeCharacteristic(writeCharacteristic);
                if (BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE == data.getWriteType()) {
                    Thread.sleep(20);
                }
            }
        } catch (Exception ex) {
            bleStateChangeCallback.onError(new BleException(BleException.GENERAL, ex.getMessage()));
        }
    }

    private void errorWhenConnecting(BleException ex) {
        if (connectTimeout.isStarted()) {
            connectTimeout.cancel();
            LockUtil.getInstance().releaseLock(LOCK_CONNECT_DEVICE);
            bleStateChangeCallback.onError(ex);
        }
    }

    /**
     * 连接设备
     *
     * @param deviceAddress 设备地址
     * @param callback      ble状态变化回调
     */
    public void connectDevice(final String deviceAddress, final BleStateChangeCallback callback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                LockUtil.getInstance().aquireLock(LOCK_CONNECT_DEVICE);
                BleConnection.this.deviceAddress = deviceAddress;
                BleConnection.this.bleStateChangeCallback = callback;
                if (isConnected()) {
                    Log.d("connectDevice", "isConnected---------");

                    LockUtil.getInstance().releaseLock(LOCK_CONNECT_DEVICE);
                    callback.onConnected(bluetoothDevice);
                } else {
                    if (!isBluetoothEnabled()) {
                        LockUtil.getInstance().releaseLock(LOCK_CONNECT_DEVICE);
                        callback.onError(new BleException(BleException.BLUETOOTH_NOT_OPENED, "蓝牙未打开"));
                    } else {
                        Log.d("connectDevice", "QuinticScanCallback---------");

                        try {
                            if (Build.VERSION.SDK_INT <= 18 || true) {
                                QuinticScanCallback cb = new QuinticScanCallback() {
                                    @Override
                                    public void onScan(QuinticScanResult scanResult) {

                                    }

                                    @Override
                                    public void onStop() {

                                    }

                                    @Override
                                    public void onError(QuinticException ex) {

                                    }

                                    @Override
                                    public void onStart() {

                                    }
                                };
                                QuinticScanner scanner = QuinticScanner.getInstance(context);
                                scanner.setQuinticScanCallback(cb);
                                scanner.start();
                                try {
                                    Thread.sleep(3000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                scanner.stop();
                            }
                            connectTimeout.restart(30000);
                            gatt = bluetoothAdapter.getRemoteDevice(deviceAddress).connectGatt(context, false, gattCallback);

//                            gatt = bluetoothAdapter.getRemoteDevice(deviceAddress).connectGatt(context, false, gattCallback,BluetoothDevice.TRANSPORT_LE);
                        } catch (BleException e) {
                            LockUtil.getInstance().releaseLock(LOCK_CONNECT_DEVICE);
                            callback.onError(e);
                        }
                    }
                }
            }
        }).start();
    }

    /**
     * 写入数据
     *
     * @param data 数据
     */
    public void writeData(BleWriteData data) throws BleException {
        if (!isConnected()) {
            throw new BleException(BleException.DEVICE_UNREACHABLE, "设备未连接");
        }
        if (writeDataQueue.size() == 0) {
            writeDataQueue.add(data);
            doWrite();
        } else {
            writeDataQueue.add(data);
        }
    }

    public boolean isConnected() {
        if (!isBluetoothEnabled()) {
            return false;
        } else if (bluetoothDevice == null) {
            return false;
        } else {
            int connectionState = bluetoothManager.getConnectionState(bluetoothDevice, BluetoothProfile.GATT);
            return connectionState == BluetoothGatt.STATE_CONNECTED && isRealConnected;
        }
    }

    public void finish() {
        bleStateChangeCallback = blankCallback;
        connectTimeout.restart(15000);
    }

    void abort() {
        Log.e("bleconnection abort", "abort");
        isRealConnected = false;
        LockUtil.getInstance().releaseLock(LOCK_CONNECT_DEVICE);
        connectTimeout.cancel();
        try {
            QuinticScanner.getInstance(context).stop();
        } catch (BleException e) {
            e.printStackTrace();
        }
        try {
            if (isConnected()) {
                gatt.disconnect();
            } else {
                gatt.disconnect();
                gatt.close();
            }
        } catch (Exception ex) {

        }
    }

    public void disconnect() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.d("Bleconnection", "do disconnect");

                LockUtil.getInstance().aquireLock(LOCK_CONNECT_DEVICE);
                abort();
            }
        }).start();
    }

    public void hold() {
        bleStateChangeCallback = blankCallback;
        connectTimeout.cancel();
    }

    public boolean isBluetoothEnabled() {
        return bluetoothAdapter != null && bluetoothAdapter.isEnabled();
    }
}
