package quinticble;

import android.bluetooth.BluetoothDevice;

import java.util.List;

/**
 * 异步操作的手环设备
 */
public interface QuinticDeviceTea {

    /**
     * 获取MAC地址
     *
     * @return MAC地址
     */
    String getAddress();

    BluetoothDevice getBluetoothDeviceTea();

    /**
     * 重连接
     *
     * @param callback 重连接回调
     */
    void reconnect(QuinticCallbackTea<Void> callback);


     void sendCommonCode(String code,final QuinticCallbackTea<String> callback) ;

    void sendListMp3Code(List<byte[]> code,final QuinticCallbackTea<String> callback) ;

    /**
     * 断开连接
     */
    void disconnect();
}