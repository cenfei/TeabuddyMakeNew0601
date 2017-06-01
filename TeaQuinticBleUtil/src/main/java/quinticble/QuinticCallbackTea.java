package quinticble;

/**
 * 手环回调
 */
public abstract class QuinticCallbackTea<T> {

    /**
     * 当操作完成时--上报信息
     * @param result 操作结果
     */
    public void onKeepNotify(byte[] data) {
        this.onProgress(100);
    }


    /**
     * 当操作完成时
     * @param result 操作结果
     */
    public void onComplete(T result) {
        this.onProgress(100);
    }

    /**
     * 当发生异常时
     * @param ex 异常
     */
    public void onError(QuinticException ex) {
        this.onProgress(0);
    }
    public void oadUpdate(T bluetoothDevice) {
        this.onProgress(100);
    }
    /**
     * 当操作进度变更
     * @param progress 进度值（0-100）
     */
    public void onProgress(int progress) { }

}
