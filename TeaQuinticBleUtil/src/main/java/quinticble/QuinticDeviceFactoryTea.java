package quinticble;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * 手环设备工厂
 */
public class QuinticDeviceFactoryTea {

    /**
     * 查找设备锁
     */
    private static final String LOCK_FIND_DEVICE = "DeviceFactory_FindDevice";
    /**
     * 静态实例
     */
    private static QuinticDeviceFactoryTea instance;
    /**
     * 已知设备清单
     */
    public static Map<String, QuinticDeviceTea> deviceMap = new HashMap<>();
    private static QuinticScanner scanner;

    /**
     * 应用程序上下文
     */
    private Context context;

    /**
     * 初始化quintic设备工厂
     * @param context 应用程序上下文
     */
    public QuinticDeviceFactoryTea(Context context) {
        this.context = context;
    }

    /**
     * 获取工厂实例
     * @param context 应用程序上下文
     * @return 实例
     */
    public static QuinticDeviceFactoryTea getInstance(Context context) {
        if (instance == null) {
            instance = new QuinticDeviceFactoryTea(context);
        }
        return instance;
    }

    /**
     * 根据版本号创建quintic设备
     * @param device 设备
     * @return quintic设备
     */
    QuinticDeviceTea quinticDevice0;
    private QuinticDeviceTea createDeviceByVersion(BluetoothDevice device, BleConnection bleConnection) {
        QuinticDeviceTea quinticDevice;
//        if (version == 112 || version == 113 || version == 114 || version == 115 || version == 116 || version == 117) {
//            quinticDevice = new QuinticDeviceBelow117(bleConnection, this.context, device);
//        } else if (version >= 200 && version <= 299) {
//            quinticDevice = new QuinticDevice02(bleConnection, this.context, device);
//        } else if (version >= 300) {
//            quinticDevice = new QuinticDevice03(bleConnection, this.context, device);
//        } else {
//            quinticDevice = new QuinticDevice03(bleConnection, this.context, device);
//        }

        Log.d("QuinticDeviceTeaImpl","QuinticDeviceTeaImpl");
        quinticDevice = new QuinticDeviceTeaImpl(bleConnection, this.context, device);
        quinticDevice0=quinticDevice;
        Log.e("QueueSupportTea","QueueSupportTea");

        quinticDevice = new QuinticDeviceWithQueueSupportTea(quinticDevice,context);
        Log.e("QueueSupportTea","QueueSupportTea end");

        deviceMap.put(quinticDevice.getAddress(), quinticDevice);
        return quinticDevice;
    }
    public BluetoothGatt bluetoothGatt;

    public void setConnectionNull(){
        deviceMap.clear();
        quinticDevice0.setConnectionNull();
    }
    public BleConnection conn=null;

    private void leConnect(final String deviceAddress, final QuinticCallbackTea<QuinticDeviceTea> callback) {
        try {
            conn = new BleConnection(context, QuinticUuid.SERVICE_UUID, QuinticUuid.WRITE_CHARACTERISTIC_UUID, QuinticUuid.NOTIFY_CHARACTERISTIC_UUID);
            conn.connectDevice(deviceAddress, new BleStateChangeCallback() {

                private BluetoothDevice device;

                @Override
                public void onConnected(BluetoothDevice device) {
                    Log.d("onConnected","success");
                    super.onConnected(device);
                    this.device = device;
                    bluetoothGatt= conn.gatt;
                    Log.d("device_uuid", device.getAddress() + "|" + device.getName());
                    try {
                        conn.writeData(new BleWriteData(QuinticCommon.stringToBytes("EA0B")));
                    } catch (BleException e) {
                        onError(e);
                    }
                }

                @Override
                public void onUpdate(final BluetoothDevice device) {
                    super.onUpdate(device);
//                    this.device = device;
//                    bluetoothGatt= conn.gatt;
//                    QuinticDeviceTea quinticDevice = createDeviceByVersion(device, conn);

                    callback.oadUpdate(null);
                }

                @Override
                public void onError(BleException ex) {
                    super.onError(ex);
                    Log.e("onConnected", "onError");
//                    deviceMap.clear();
                    Log.e("onConnected fox", "onError clear");

                    callback.onError(new QuinticException(ex));
                }

                @Override
                public void onNotify(byte[] data) {
                    super.onNotify(data);
                    Log.d("onNotify", "onNotify");
                    if (QuinticCommon.matchData(data, QuinticCommon.stringToBytes("ea0b"))) {
//                        int version = (QuinticCommon.unsignedBytesToInt(data, 3, 2));
                        QuinticDeviceTea quinticDevice = createDeviceByVersion(device, conn);
                        callback.onComplete(quinticDevice);
                    } else {
                        callback.onKeepNotify(data);
                        Log.e("onKeepNotify notify",QuinticCommon.unsignedBytesToHexString(data,0,data.length));

//                        callback.onError(new QuinticException(QuinticException.INVALID_RESPONSE, "查询设备版本失败"));
                    }
                }

                @Override
                public void onDisconnected() {
                    Log.d("onNotify", "onDisconnected");
//deviceMap.clear();
                    Log.d("onDisconnected fox", "onDisconnected clear");

                    super.onDisconnected();
                    callback.onError(new QuinticException(QuinticException.DEVICE_DISCONNECTED, "连接异常断开"));
                }
            });
        } catch (BleException e) {
            callback.onError(new QuinticException(e));
        }
    }

    /**
     * 中断所有连接和正在进行的设备扫描
     */
    public void abort() {
        stopScanDevice();
        for (String key : deviceMap.keySet()) {
            deviceMap.get(key).disconnect();
        }
    }

    /**
     * 开始扫描设备
     * @param callback 扫描回调
     */
    public synchronized void startScanDevice(QuinticScanCallback callback) {
        if (scanner == null) {
            try {
                scanner = QuinticScanner.getInstance(context);
            } catch (BleException e) {
                callback.onError(new QuinticException(e));
                return;
            }
        }
//        scanner.stop();
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    scanner.stop();

                }
            }, 20000);
        }
        else if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP&&Build.VERSION.SDK_INT<Build.VERSION_CODES.M) {
            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    scanner.stop();

                }
            }, 6000);
        }
        else{
            scanner.stop();
        }
        scanner.setQuinticScanCallback(callback);
        scanner.start();
    }

    /**
     * 停止扫描
     */
    public synchronized void stopScanDevice() {
        if (scanner != null) {
            scanner.stop();
        }
        LockUtil.getInstance().releaseLock(LOCK_FIND_DEVICE);
    }

    /**
     * 查找设备(3s内能找到的最小RSSI)
     * @param callback 查找设备回调
     */
    public void findDevice(final QuinticCallbackTea<QuinticDeviceTea> callback) {
        findDevice(new ArrayList<String>(), callback);
    }

    /**
     * 查找设备
     * @param deviceAddress 设备地址
     * @param callback 查找设备回调
     */
    public void findDevice(final String deviceAddress, final QuinticCallbackTea<QuinticDeviceTea> callback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                LockUtil.getInstance().aquireLock(LOCK_FIND_DEVICE);
                if (deviceMap.containsKey(deviceAddress)) {
                    Log.d("findDevice", "containsKey---------");

                    LockUtil.getInstance().releaseLock(LOCK_FIND_DEVICE);
                    callback.onComplete(deviceMap.get(deviceAddress));
                } else {
                    Log.d("findDevice", "leConnect---------");

                    leConnect(deviceAddress, new QuinticCallbackTea<QuinticDeviceTea>() {
                        @Override
                        public void onComplete(QuinticDeviceTea result) {
                            super.onComplete(result);
                            LockUtil.getInstance().releaseLock(LOCK_FIND_DEVICE);
                            callback.onComplete(result);
                        }

                        @Override
                        public void oadUpdate(QuinticDeviceTea result) {
                            super.oadUpdate(result);
                            LockUtil.getInstance().releaseLock(LOCK_FIND_DEVICE);
                            callback.oadUpdate(result);

                        }

                        @Override
                        public void onError(QuinticException ex) {
                            super.onError(ex);
                            LockUtil.getInstance().releaseLock(LOCK_FIND_DEVICE);
                            callback.onError(ex);
                        }

                        @Override
                        public void onProgress(int progress) {
                            super.onProgress(progress);
                            callback.onProgress(progress);
                        }
                    });
                }
            }
        }).start();
    }

    /**
     * 查找设备(3s内能找到的最小RSSI)
     * @param skipDeviceAddresses 跳过的设备地址（即这些设备会被忽略）
     * @param callback 查找设备回调
     */
    public void findDevice(final Collection<String> skipDeviceAddresses, final QuinticCallbackTea<QuinticDeviceTea> callback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                LockUtil.getInstance().aquireLock(LOCK_FIND_DEVICE);
                final QuinticScanner scanner;
                try {
                    scanner = QuinticScanner.getInstance(context);
                } catch (BleException e) {
                    LockUtil.getInstance().releaseLock(LOCK_FIND_DEVICE);
                    callback.onError(new QuinticException(e));
                    return;
                }
                scanner.setQuinticScanCallback(new QuinticScanCallback() {

                    /**
                     * rssi -> mac地址映射
                     */
                    private SortedMap<Integer, String> rssiToMac = Collections.synchronizedSortedMap(new TreeMap<Integer, String>());

                    /**
                     * 设备超时
                     */
                    private Timeout deviceTimeout = new Timeout(10000, new Timeout.TimerEvent() {
                        @Override
                        public void onTimeout() {
                            scanner.stop();
                            if (rssiToMac.size() == 0) {
                                onError(new QuinticException(QuinticException.CONNECT_TIMEOUT, "未发现匹配的设备"));
                            } else {
                                String deviceAddress = rssiToMac.get(rssiToMac.lastKey());//key值最大
                                if (deviceMap.containsKey(deviceAddress)) {
                                    LockUtil.getInstance().releaseLock(LOCK_FIND_DEVICE);
                                    callback.onComplete(deviceMap.get(deviceAddress));
                                } else {
                                    leConnect(deviceAddress, new QuinticCallbackTea<QuinticDeviceTea>() {
                                        @Override
                                        public void onComplete(QuinticDeviceTea result) {
                                            super.onComplete(result);
                                            LockUtil.getInstance().releaseLock(LOCK_FIND_DEVICE);
                                            callback.onComplete(result);
                                        }

                                        @Override
                                        public void onError(QuinticException ex) {
                                            super.onError(ex);
                                            LockUtil.getInstance().releaseLock(LOCK_FIND_DEVICE);
                                            callback.onError(ex);
                                        }

                                        @Override
                                        public void onProgress(int progress) {
                                            super.onProgress(progress);
                                            callback.onProgress(progress);
                                        }
                                    });
                                }
                            }
                        }
                    }, true);

                    @Override
                    public synchronized void onScan(QuinticScanResult scanResult) {
                        if (rssiToMac.size() == 0) {
                            deviceTimeout.restart(3000);
                        }
                        if (!skipDeviceAddresses.contains(scanResult.getDeviceAddress())) {
                            rssiToMac.put(scanResult.getRssi(), scanResult.getDeviceAddress());
                        }
                    }

                    @Override
                    public void onStop() {

                    }

                    @Override
                    public void onError(QuinticException ex) {
                        stopScanDevice();
                        LockUtil.getInstance().releaseLock(LOCK_FIND_DEVICE);
                        callback.onError(ex);
                    }

                    @Override
                    public void onStart() {
                        deviceTimeout.start();
                    }
                });
                scanner.start();
            }
        }).start();
    }


    /**
     * 查找设备(3s内能找到的最小RSSI)
     * @return 设备实例
     * @throws QuinticException 异常
     */
//    public QuinticDevice findDeviceSync() throws QuinticException {
//        QuinticCallbackSync<QuinticDevice> cb = new QuinticCallbackSync<>();
//// ***       findDevice(cb);
//        return cb.getResult();
//    }
//
//    /**
//     * 查找设备
//     * @param deviceAddress 设备MAC地址
//     * @return 设备
//     * @throws QuinticException 异常
//     */
//    public QuinticDevice findDeviceSync(String deviceAddress) throws QuinticException {
//        QuinticCallbackSync<QuinticDevice> cb = new QuinticCallbackSync<>();
////  ***       findDevice(deviceAddress, cb);
//        return cb.getResult();
//    }
//
//    /**
//     * 查找设备
//     * @param skipDeviceAddresses 忽略的设备MAC地址
//     * @return 设备
//     * @throws QuinticException 异常
//     */
//    public QuinticDevice findDeviceSync(Collection<String> skipDeviceAddresses) throws QuinticException {
//        QuinticCallbackSync<QuinticDevice> cb = new QuinticCallbackSync<>();
////    ***     findDevice(skipDeviceAddresses, cb);
//        return cb.getResult();
//    }
}
