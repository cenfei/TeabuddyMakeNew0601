package quinticble;

/**
 * Quintic扫描回调
 */
public interface QuinticScanCallback {

    /**
     * 扫描到设备时
     * @param scanResult 扫描结果
     */
    void onScan(QuinticScanResult scanResult);

    /**
     * 扫描停止时
     */
    void onStop();

    /**
     * 发生错误时
     * @param ex 错误
     */
    void onError(QuinticException ex);

    void onStart();
}
