package quinticble;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.ParcelUuid;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 刷刷手环扫描器
 */
class QuinticScannerUpdate {

    private static QuinticScannerUpdate instance;

   // private BluetoothManager bluetoothManager;

    private BluetoothAdapter bluetoothAdapter;

    private boolean isScanning;

    private BluetoothAdapter.LeScanCallback leCallback = new BluetoothAdapter.LeScanCallback() {

        @Override
        public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
            boolean matchUuid = false;
            List<UUID> uuidList = UUIDParser.parseUUIDs(scanRecord);
            for (UUID uuid : uuidList) {
                Log.d("uuid",uuid.toString().toLowerCase());
                if (QuinticUuid.SERVICE_UUID.toLowerCase().equals(uuid.toString().toLowerCase())) {
                    matchUuid = true;
                    break;
                }
            }
            if (!matchUuid && !"Quintic BLE".equals(device.getName()) && !"iShuaShua".equals(device.getName())) {
                return;
            }

            if (quinticScanCallback != null) {
                QuinticScanResult result = new QuinticScanResult();
                result.setAdvertiseData(scanRecord);
                result.setDeviceAddress(device.getAddress());
                result.setRssi(rssi);
                quinticScanCallback.onScan(result);
            }
        }
    };

    private ScanCallback scanCallback =new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
        }

        @Override
        public void onScanFailed(int errorCode) {
            super.onScanFailed(errorCode);
        }
    };


    private QuinticScanCallback quinticScanCallback;
private BluetoothLeScanner mBluetoothLeScanner;
    private Context context;

    private QuinticScannerUpdate(Context context) throws BleException {
        this.context=context;
        if (!checkSupperBluetoothLE()) {
            throw new BleException(BleException.BLE_NOT_SUPPORTED, "设备不支持ble");
        }
       // bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = getAdapter();

        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
            throw new BleException(BleException.BLUETOOTH_NOT_OPENED, "设备未打开蓝牙");
        }
    }

    public static QuinticScannerUpdate getInstance(Context context) throws BleException {
        if (instance == null) {
            instance = new QuinticScannerUpdate(context);
        }
        return instance;
    }

    public synchronized void start() {
        if (!isScanning) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

                startScanningApi21(scanCallback);

            }else {
                if (quinticScanCallback != null) {
                    quinticScanCallback.onStart();
                }
                bluetoothAdapter.startLeScan(leCallback);
            }


            isScanning = true;
        }


    }



    public synchronized void stop() {
        if (isScanning) {
            if (bluetoothAdapter != null) {
                try {
                    bluetoothAdapter.stopLeScan(leCallback);
                } catch (Exception ex) {

                }
            }
            isScanning = false;
            if (quinticScanCallback != null) {
                quinticScanCallback.onStop();
            }
        }
    }

    public void setQuinticScanCallback(QuinticScanCallback quinticScanCallback) {
        this.quinticScanCallback = quinticScanCallback;
    }

    public boolean isScanning() {
        return isScanning;
    }




    /***
     * 获取BluetoothAdapter
     *
     * @return BluetoothAdapter
     * @hide
     */
    private BluetoothAdapter getAdapter() {
        BluetoothAdapter mBluetoothAdapter;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            mBluetoothAdapter = ((BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE)).getAdapter();
        } else {
            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        }
        return mBluetoothAdapter;
    }



    /***
     * 是否支持蓝牙低功耗广播（4.3+）
     *
     * @return boolean
     * @hide
     */
    @Deprecated
    public boolean isSupperBluetoothLE() {

        return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE);
    }


    /**
     * 判断是否支持LE
     *
     * @return boolean
     * @hide
     */
    @Deprecated
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public boolean isMultipleAdvertisementSupported() {
        return bluetoothAdapter.isMultipleAdvertisementSupported();
    }

    /**
     * 检测是否支持蓝牙低功耗，检查方式走版本分之
     *
     * @return boolean
     */
    public boolean checkSupperBluetoothLE() {
        final int version = Build.VERSION.SDK_INT;
        if (version >= Build.VERSION_CODES.LOLLIPOP) {
            return isMultipleAdvertisementSupported();
        } else {
            return isSupperBluetoothLE();
        }
    }

    /**
     * 基于APi21的扫描蓝牙
     * @param mScanCallback
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void startScanningApi21(ScanCallback mScanCallback){
        bluetoothAdapter.getBluetoothLeScanner().startScan(buildScanFilters(), buildScanSettings(),mScanCallback);
    }
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private List<ScanFilter> buildScanFilters() {
        List<ScanFilter> scanFilters = new ArrayList<>();

        ScanFilter.Builder builder = new ScanFilter.Builder();
        // 注释掉下面的扫描过滤规则，才能扫描到（uuid不匹配没法扫描到的）
         builder.setServiceUuid(ParcelUuid.fromString(QuinticUuid.SERVICE_UUID));
        scanFilters.add(builder.build());

        return scanFilters;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private ScanSettings buildScanSettings() {
        ScanSettings.Builder builder = new ScanSettings.Builder();
        builder.setScanMode(ScanSettings.SCAN_MODE_LOW_POWER);
        return builder.build();
    }
}
