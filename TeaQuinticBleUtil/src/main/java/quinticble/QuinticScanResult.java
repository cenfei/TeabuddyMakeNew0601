package quinticble;

/**
 * Quintic扫描结果
 */
public class QuinticScanResult {

    /**
     * 设备地址
     */
    private String deviceAddress;

    /**
     * 信号强度
     */
    private int rssi;

    /**
     * 广播数据
     */
    private byte[] advertiseData;

    /**
     * 获取设备地址
     * @return 设备地址
     */
    public String getDeviceAddress() {
        return deviceAddress;
    }

    /**
     * 设置设备地址
     * @param deviceAddress 设备地址
     */
    public void setDeviceAddress(String deviceAddress) {
        this.deviceAddress = deviceAddress;
    }

    /**
     * 获取信号强度
     * @return 信号强度
     */
    public int getRssi() {
        return rssi;
    }

    /**
     * 设置信号强度
     * @param rssi 信号强度
     */
    public void setRssi(int rssi) {
        this.rssi = rssi;
    }

    /**
     * 获取广播数据
     * @return 广播数据
     */
    public byte[] getAdvertiseData() {
        return advertiseData;
    }

    /**
     * 设置广播数据
     * @param advertiseData 广播数据
     */
    public void setAdvertiseData(byte[] advertiseData) {
        this.advertiseData = advertiseData;
    }
}
