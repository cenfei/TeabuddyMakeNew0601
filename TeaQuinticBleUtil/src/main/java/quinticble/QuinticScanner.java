package quinticble;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.ParcelUuid;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 刷刷手环扫描器
 */
class QuinticScanner  implements ActivityCompat.OnRequestPermissionsResultCallback {

    private static QuinticScanner instance;

    // private BluetoothManager bluetoothManager;

    private BluetoothAdapter bluetoothAdapter;

    private boolean isScanning;

    private BluetoothAdapter.LeScanCallback leCallback = new BluetoothAdapter.LeScanCallback() {

        @Override
        public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
            boolean matchUuid = false;
            List<UUID> uuidList = UUIDParser.parseUUIDs(scanRecord);
            for (UUID uuid : uuidList) {
                Log.d("uuid", uuid.toString().toLowerCase());
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

    private QuinticScanCallback quinticScanCallback;
    private BluetoothLeScanner mBluetoothLeScanner;
    private Context context;

    private QuinticScanner(Context context) throws BleException {
        this.context = context;

        // bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = getAdapter();

        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
            throw new BleException(BleException.BLUETOOTH_NOT_OPENED, "设备未打开蓝牙");
        }
        if (!checkSupperBluetoothLE()) {
            throw new BleException(BleException.BLE_NOT_SUPPORTED, "设备不支持ble");
        }
    }

    public static QuinticScanner getInstance(Context context) throws BleException {
        if (instance == null) {
            instance = new QuinticScanner(context);
        }
        return instance;
    }

    public synchronized void start() {
        if (!isScanning) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ) {

                ensureDiscoverable();
//                startScanningApi21(scanCallback);

            }
//            else if (Build.VERSION.SDK_INT >= 23) {
//                if (quinticScanCallback != null) {
//                    quinticScanCallback.onStart();
//                }
//                bluetoothAdapter.startLeScan(leCallback);
//            }
            else {
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
            if (mBluetoothLeScanner != null) {

                mBluetoothLeScanner.stopScan(scanCallback);
                mBluetoothLeScanner = null;

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
     *
     * @param
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public synchronized void startScanningApi21() {
        scanCallback = new ScanCallback() {
            @Override
            public void onScanResult(int callbackType, final ScanResult result) {
                super.onScanResult(callbackType, result);
                Log.d("callbackType", "" + callbackType);
                if (quinticScanCallback != null) {
                    QuinticScanResult result0 = new QuinticScanResult();
                    result0.setAdvertiseData(result.getScanRecord().getBytes());
                    result0.setDeviceAddress(result.getDevice().getAddress());
                    result0.setRssi(result.getRssi());
                    quinticScanCallback.onScan(result0);
                }
            }

            @Override
            public void onScanFailed(int errorCode) {
                super.onScanFailed(errorCode);
            }
        };
        mBluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();
        if (quinticScanCallback != null) {
            quinticScanCallback.onStart();
        }
        mBluetoothLeScanner.startScan(buildScanFilters(), buildScanSettings(), scanCallback);
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

    private synchronized void ensureDiscoverable() {
//        if (bluetoothAdapter.getScanMode() !=
//                BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
//            Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
//            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
//            context.startActivity(discoverableIntent);
//        }
        ensureAndroidMGps();
    }

    ScanCallback scanCallback = null;

    private synchronized void ensureAndroidMGps() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (isGpsEnable(context)) {
                openGps();
            } else {
                Toast.makeText(context, "网络或者gps不可用", Toast.LENGTH_LONG).show();
            }
            return;
        } else {


            startScanningApi21();
        }
    }

    public void openGps() {
        if (bluetoothAdapter.isEnabled()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (context.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("This app needs location access");
                    builder.setMessage("Please grant location access so this app can detect Bluetooth.");
                    builder.setPositiveButton(android.R.string.ok, null);
                    builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                Activity activity = (Activity) context;
//                                activity.startActivityForResult(intent, PERMISSION_REQUEST_COARSE_LOCATION);
                                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_COARSE_LOCATION);

                                try {
                                    Thread.sleep(1000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                startScanningApi21();
//                                if (quinticScanCallback != null) {
//                                    quinticScanCallback.onStart();
//                                }
//                                bluetoothAdapter.startLeScan(leCallback);
                            }
                        }
                    });
                    builder.show();
                } else {
                    startScanningApi21();
//                    if (quinticScanCallback != null) {
//                        quinticScanCallback.onStart();
//                    }
//                    bluetoothAdapter.startLeScan(leCallback);
                }
            }
        }
    }

    // Gps是否可用
    public static final boolean isGpsEnable(final Context context) {
        boolean boolGps = true;
        LocationManager locationManager
                = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        boolean gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean network = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        if (gps || network) {
            boolGps = true;
        } else {
            boolGps = false;
        }
        return boolGps;
    }


    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 11;

    /**
     * 对授权做处理，0代表授权，-1拒绝授权
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        Log.e("onRequestPermissionsResult", "onRequestPermissionsResult");

        switch (requestCode) {
            case PERMISSION_REQUEST_COARSE_LOCATION: {
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                     Log.e("onRequestPermissionsResult", "coarse location permission granted");
                    //finish();
                } else {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("Functionality limited");
                    builder.setMessage("Since location access has not been grant app will not be able to discover beacons when in the background.");
                    builder.setPositiveButton(android.R.string.ok, null);
                    builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                        }

                    });
                    builder.show();
                }

                return;
            }
        }
    }
}
