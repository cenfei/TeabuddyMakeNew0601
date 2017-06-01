package quinticble;

/**
 * quintic异常
 */
public class QuinticException extends Exception {

    /**
     * 普通异常
     */
    private static final int GENERAL = 0;

    /**
     * 不支持ble
     */
    public static final int BLE_NOT_SUPPORTED = 1;

    /**
     * 蓝牙未打开
     */
    public static final int BLUETOOTH_NOT_OPENED = 2;

    /**
     * 连接超时
     */
    public static final int CONNECT_TIMEOUT = 3;

    /**
     * 不支持的功能
     */
    public static final int FUNCTION_NOT_SUPPORTED = 4;

    /**
     * 无效的响应
     */
    public static final int INVALID_RESPONSE = 5;

    /**
     * 设备断开
     */
    public static final int DEVICE_DISCONNECTED = 6;

    /**
     * 活动连接超时
     */
    public static final int ACTIVE_TIMEOUT = 7;

    /**
     * 固件版本号太低
     */
    public static final int FIRMWARE_VERSION_TOO_LOW = 8;

    /**
     * 设备暂时无法连接
     */
    public static final int DEVICE_UNREACHABLE = 9;

    /**
     * 异常代码
     */
    private int code;

    /**
     * 初始化quintic异常
     * @param message 异常消息
     */
    public QuinticException(String message) {
        super(message);
        this.code = GENERAL;
    }

    /**
     * 初始化quintic异常
     * @param code 异常码
     * @param message 信息
     */
    public QuinticException(int code, String message) {
        super(message);
        this.code = code;
    }

    /**
     * 初始化quintic异常
     * @param ex ble异常
     */
    public QuinticException(BleException ex) {
        super(ex.getMessage());
        int bleCode = ex.getCode();
        if (bleCode == BleException.BLE_NOT_SUPPORTED) {
            this.code = BLE_NOT_SUPPORTED;
        } else if (bleCode == BleException.BLUETOOTH_NOT_OPENED) {
            this.code = BLUETOOTH_NOT_OPENED;
        } else if (bleCode == BleException.SCAN_TIMEOUT || bleCode == BleException.CONNECT_TIMEOUT) {
            this.code = CONNECT_TIMEOUT;
        } else if (bleCode == BleException.DEVICE_DISCONNECTED) {
            this.code = DEVICE_DISCONNECTED;
        } else if (bleCode == BleException.ACTIVE_TIMEOUT) {
            this.code = ACTIVE_TIMEOUT;
        } else if (bleCode == BleException.DEVICE_UNREACHABLE) {
            this.code = DEVICE_UNREACHABLE;
        } else {
            this.code = GENERAL;
        }
    }

    /**
     * 初始化quintic异常
     * @param ex 普通异常
     */
    public QuinticException(Exception ex) {
        super(ex.getMessage(), ex);
        this.code = GENERAL;
    }

    /**
     * 获取异常代码
     * @return 异常代码
     */
    public int getCode() {
        return code;
    }
}
