package quinticble;

import android.bluetooth.BluetoothGattCharacteristic;

/**
 * ble数据
 */
class BleWriteData {

    BleWriteData(byte[] binary) {
        this.writeType = BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT;
        this.binary = binary;
    }

    BleWriteData(int writeType, byte[] binary) {
        this.writeType = writeType;
        this.binary = binary;
    }

    /**
     * 二进制数据
     */
    private byte[] binary;

    /**
     * 写类型
     */
    private int writeType;

    /**
     * @return 二进制数据
     */
    public byte[] getBinary() {
        return binary;
    }

    /**
     * @param binary 二进制数据
     */
    public void setBinary(byte[] binary) {
        this.binary = binary;
    }

    /**
     * @return 写类型
     */
    public int getWriteType() {
        return writeType;
    }

    /**
     * @param writeType 写类型
     */
    public void setWriteType(int writeType) {
        this.writeType = writeType;
    }
}
