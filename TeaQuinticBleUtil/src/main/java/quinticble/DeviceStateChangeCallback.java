package quinticble;

class DeviceStateChangeCallback extends BleStateChangeCallback {

    @Override
    public void onDisconnected() {
        super.onDisconnected();
        onError(new BleException(BleException.DEVICE_DISCONNECTED, "设备连接异常断开"));
    }
}
