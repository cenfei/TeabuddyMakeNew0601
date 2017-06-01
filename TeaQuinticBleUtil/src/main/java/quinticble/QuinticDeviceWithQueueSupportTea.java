package quinticble;

import android.bluetooth.BluetoothDevice;

import java.util.List;

class QuinticDeviceWithQueueSupportTea implements QuinticDeviceTea {

    private static final String LOCK_QUINTICDEVICE_QUEUE = "QuiticDevice_Queue";
    private static final String LOCK_AUTORECONNECT = "AutoReonnect";

    private QuinticDeviceTea device;

    private Timeout autoReconnectTimeout;

    private boolean isTimeoutBusy;

    public QuinticDeviceWithQueueSupportTea(QuinticDeviceTea device) {
        this.device = device;
        this.autoReconnectTimeout = new Timeout(5000, new Timeout.TimerEvent() {
            @Override
            public void onTimeout() {
                isTimeoutBusy = true;
                reconnect(new QuinticCallbackTea<Void>() {
                    @Override
                    public void onComplete(Void result) {
                        super.onComplete(result);
                        isTimeoutBusy = false;
                        autoReconnectTimeout.restart();
                    }

                    @Override
                    public void oadUpdate(Void result) {

                        disconnect();
                        super.oadUpdate(result);
                    }

                    @Override
                    public void onError(QuinticException ex) {
                        super.onError(ex);
                        isTimeoutBusy = false;
                        autoReconnectTimeout.restart();
                    }
                });
            }
        });
        this.autoReconnectTimeout.start();
    }

    @Override
    public String getAddress() {
        return device.getAddress();
    }

    @Override
    public BluetoothDevice getBluetoothDeviceTea() {
        return device.getBluetoothDeviceTea();
    }

    @Override
    public void reconnect(final QuinticCallbackTea<Void> callback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                aquire();
                device.reconnect(new QCallback<>(callback));
            }
        }).start();
    }


    @Override
    public void sendCommonCode(final String code,final QuinticCallbackTea<String> callback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                aquire();
                device.sendCommonCode(code,new QCallback<String>(callback));
            }
        }).start();
    }

    @Override
    public void sendListMp3Code(final List<byte[]> code,final QuinticCallbackTea<String> callback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                aquire();
                device.sendListMp3Code(code,new QCallback<String>(callback));
            }
        }).start();
    }

    @Override
    public void disconnect() {
        aquire();
        device.disconnect();
        LockUtil.getInstance().releaseLock(LOCK_QUINTICDEVICE_QUEUE);
    }



    private void aquire() {
        LockUtil.getInstance().aquireLock(LOCK_QUINTICDEVICE_QUEUE);
        autoReconnectTimeout.cancel();
    }

    class QCallback<T> extends QuinticCallbackTea<T> {

        private QuinticCallbackTea<T> cb;

        public QCallback(QuinticCallbackTea<T> cb) {
            this.cb = cb;
        }

        @Override
        public void onComplete(T result) {
            super.onComplete(result);
            LockUtil.getInstance().releaseLock(LOCK_QUINTICDEVICE_QUEUE);
            if (!isTimeoutBusy) {
                autoReconnectTimeout.restart();
            }
            cb.onComplete(result);
        }

        @Override
        public void oadUpdate(T result) {
            super.oadUpdate(result);
            disconnect();
            cb.oadUpdate(result);
        }

        @Override
        public void onProgress(int progress) {
            super.onProgress(progress);
            cb.onProgress(progress);
        }

        @Override
        public void onError(QuinticException ex) {
            super.onError(ex);
            LockUtil.getInstance().releaseLock(LOCK_QUINTICDEVICE_QUEUE);
            if (!isTimeoutBusy) {
                autoReconnectTimeout.restart();
            }
            cb.onError(ex);
        }
    }

}
