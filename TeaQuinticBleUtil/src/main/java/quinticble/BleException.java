package quinticble;

/**
 * ble异常
 */
class BleException extends Exception {

    /**
     * 一般性异常
     */
    public static final int GENERAL = 0;

    /**
     * 不支持ble
     */
    public static final int BLE_NOT_SUPPORTED = 1;

    /**
     * 蓝牙未打开
     */
    public static final int BLUETOOTH_NOT_OPENED = 2;

    /**
     * 扫描已在进行中
     */
    public static final int SCAN_ALREADY_STARTED = 3;

    /**
     * 扫描设备超时
     */
    public static final int SCAN_TIMEOUT = 4;

    /**
     * 未找到服务
     */
    public static final int SERVICE_NOT_FOUND = 5;

    /**
     * 连接超时
     */
    public static final int CONNECT_TIMEOUT = 6;

    /**
     * 设备已断开
     */
    public static final int DEVICE_DISCONNECTED = 7;
    
    /**
     * 活动连接超时
     */
    public static final int ACTIVE_TIMEOUT = 8;

    /**
     * 设备暂时无法连接
     */
    public static final int DEVICE_UNREACHABLE = 9;

    /**
     * 异常码
     */
    private int code;

    /**
     * 初始化异常
     * @param code 异常码
     * @param message 详细信息
     */
    public BleException(int code, String message) {
        super(message);
        this.code = code;
    }

    /**
     * 获取异常码
     * @return 异常码
     */
    public int getCode() {
        return code;
    }
}
