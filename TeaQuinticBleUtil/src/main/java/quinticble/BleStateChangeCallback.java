package quinticble;

import android.bluetooth.BluetoothDevice;

/**
 * ble状态变化回调
 */
abstract class BleStateChangeCallback {

    public void onConnected(BluetoothDevice device) { }

    public void onError(BleException ex) { }
    public void onUpdate(BluetoothDevice device) { }

    public void onWrite(byte[] data) { }

    public void onNotify(byte[] data) { }

    public void onDisconnected() { }
}