package quinticble;

/**
 * 蓝牙通讯数据观察者
 */
public interface BleDataObserver {

    /**
     * 监测向手环设备发送数据
     * @param data 监测到的数据
     */
    void onDataWrite(byte[] data);

    /**
     * 监测从手环设备收到数据
     * @param data 监测到的数据
     */
    void onDataNotify(byte[] data);
}
