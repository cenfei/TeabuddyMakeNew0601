package quinticble;

import android.bluetooth.BluetoothDevice;

/**
 * 状态更新回调
 */
abstract class StateChangeCallback {

    /**
     * 连接时
     */
    public void onConnected() {

    }

    /**
     * 发生错误
     * @param ex 错误异常
     */
    public void onError(BleException ex) { }

    /**
     * 找到对应的服务uuid
     */
    public void onServiceFound() { }

    /**
     * 未找到对应的服务uuid
     */
    public void onServiceNotFound() { }

    /**
     * 就绪
     * @param device
     */
    public void onReady(BluetoothDevice device) { }

    /**
     * 写入数据
     * @param data 数据
     */
    public void onWrite(byte[] data) { }

    /**
     * 读取数据
     * @param data 数据
     */
    public void onNotify(byte[] data) { }

    /**
     * 连接断开时
     */
    public void onDisconnected() { }
}