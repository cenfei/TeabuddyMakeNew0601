package quinticble;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 * 刷刷手环三代
 */
class QuinticDeviceTeaImpl implements QuinticDeviceTea {

    static int intervalTime = 30;
    private static int[] crcTable = {0x0000, 0x1021, 0x2042, 0x3063, 0x4084,
            0x50A5, 0x60C6, 0x70E7, 0x8108, 0x9129, 0xA14A, 0xB16B, 0xC18C,
            0xD1AD, 0xE1CE, 0xF1EF, 0x1231, 0x0210, 0x3273, 0x2252, 0x52B5,
            0x4294, 0x72F7, 0x62D6, 0x9339, 0x8318, 0xB37B, 0xA35A, 0xD3BD,
            0xC39C, 0xF3FF, 0xE3DE, 0x2462, 0x3443, 0x0420, 0x1401, 0x64E6,
            0x74C7, 0x44A4, 0x5485, 0xA56A, 0xB54B, 0x8528, 0x9509, 0xE5EE,
            0xF5CF, 0xC5AC, 0xD58D, 0x3653, 0x2672, 0x1611, 0x0630, 0x76D7,
            0x66F6, 0x5695, 0x46B4, 0xB75B, 0xA77A, 0x9719, 0x8738, 0xF7DF,
            0xE7FE, 0xD79D, 0xC7BC, 0x48C4, 0x58E5, 0x6886, 0x78A7, 0x0840,
            0x1861, 0x2802, 0x3823, 0xC9CC, 0xD9ED, 0xE98E, 0xF9AF, 0x8948,
            0x9969, 0xA90A, 0xB92B, 0x5AF5, 0x4AD4, 0x7AB7, 0x6A96, 0x1A71,
            0x0A50, 0x3A33, 0x2A12, 0xDBFD, 0xCBDC, 0xFBBF, 0xEB9E, 0x9B79,
            0x8B58, 0xBB3B, 0xAB1A, 0x6CA6, 0x7C87, 0x4CE4, 0x5CC5, 0x2C22,
            0x3C03, 0x0C60, 0x1C41, 0xEDAE, 0xFD8F, 0xCDEC, 0xDDCD, 0xAD2A,
            0xBD0B, 0x8D68, 0x9D49, 0x7E97, 0x6EB6, 0x5ED5, 0x4EF4, 0x3E13,
            0x2E32, 0x1E51, 0x0E70, 0xFF9F, 0xEFBE, 0xDFDD, 0xCFFC, 0xBF1B,
            0xAF3A, 0x9F59, 0x8F78, 0x9188, 0x81A9, 0xB1CA, 0xA1EB, 0xD10C,
            0xC12D, 0xF14E, 0xE16F, 0x1080, 0x00A1, 0x30C2, 0x20E3, 0x5004,
            0x4025, 0x7046, 0x6067, 0x83B9, 0x9398, 0xA3FB, 0xB3DA, 0xC33D,
            0xD31C, 0xE37F, 0xF35E, 0x02B1, 0x1290, 0x22F3, 0x32D2, 0x4235,
            0x5214, 0x6277, 0x7256, 0xB5EA, 0xA5CB, 0x95A8, 0x8589, 0xF56E,
            0xE54F, 0xD52C, 0xC50D, 0x34E2, 0x24C3, 0x14A0, 0x0481, 0x7466,
            0x6447, 0x5424, 0x4405, 0xA7DB, 0xB7FA, 0x8799, 0x97B8, 0xE75F,
            0xF77E, 0xC71D, 0xD73C, 0x26D3, 0x36F2, 0x0691, 0x16B0, 0x6657,
            0x7676, 0x4615, 0x5634, 0xD94C, 0xC96D, 0xF90E, 0xE92F, 0x99C8,
            0x89E9, 0xB98A, 0xA9AB, 0x5844, 0x4865, 0x7806, 0x6827, 0x18C0,
            0x08E1, 0x3882, 0x28A3, 0xCB7D, 0xDB5C, 0xEB3F, 0xFB1E, 0x8BF9,
            0x9BD8, 0xABBB, 0xBB9A, 0x4A75, 0x5A54, 0x6A37, 0x7A16, 0x0AF1,
            0x1AD0, 0x2AB3, 0x3A92, 0xFD2E, 0xED0F, 0xDD6C, 0xCD4D, 0xBDAA,
            0xAD8B, 0x9DE8, 0x8DC9, 0x7C26, 0x6C07, 0x5C64, 0x4C45, 0x3CA2,
            0x2C83, 0x1CE0, 0x0CC1, 0xEF1F, 0xFF3E, 0xCF5D, 0xDF7C, 0xAF9B,
            0xBFBA, 0x8FD9, 0x9FF8, 0x6E17, 0x7E36, 0x4E55, 0x5E74, 0x2E93,
            0x3EB2, 0x0ED1, 0x1EF0};
    private SimpleDateFormat yyyyMMddHHmmssFormat = new SimpleDateFormat("yyyyMMddHHmmss");

    /**
     * 应用程序上下文
     */
    private Context context;
    /**
     * 设备
     */
    public BluetoothDevice device;
    /**
     * 固件版本
     */
    private Integer firmwareVersion;
    /**
     * 版本数据
     */
    private byte[] versionData;
    /**
     * 设备编号
     */
    private String deviceSerial;
    /**
     * 协议版本号
     */
    private Integer protocolVersion;
    /**
     * 设备类型
     */
    private Long deviceType;
    public BleConnection connection;

    public  void setConnectionNull(){
        try{
        if(connection!=null) {
            connection.disconnect();
            connection = null;
        }}
        catch (Exception e){
            Log.d("setConnectionNull",""+e.getMessage());
        }
    }

    /**
     * 初始化设备
     *
     * @param context 应用程序上下文
     * @param device  设备
     */
    public QuinticDeviceTeaImpl(BleConnection connection, Context context, BluetoothDevice device) {
        this.connection = connection;
        this.context = context;
        this.device = device;
    }


    private int crc16(byte[] datas) {
        int crc = 0;
        for (int i = 0; i < datas.length; i++) {
            crc = crcTable[((crc >> 8) ^ datas[i]) & 0xff] ^ (crc << 8);
        }
        return ~crc;
    }
    @Override
    public BluetoothDevice getBluetoothDeviceTea() {

        return device;
    }
    /**
     * 获取设备MAC地址
     *
     * @return 设备MAC地址
     */
    @Override
    public String getAddress() {
        if (device != null) {
            return device.getAddress();
        } else {
            return null;
        }
    }

    @Override
    public void reconnect(final QuinticCallbackTea<Void> callback) {
        try {



            final BleConnection connection = getConnection();
            connection.connectDevice(device.getAddress(), new BleStateChangeCallback() {
                @Override
                public void onError(BleException ex) {
                    super.onError(ex);
                    connection.hold();
                    callback.onError(new QuinticException(ex));
                }

                @Override
                public void onConnected(BluetoothDevice device) {
                    super.onConnected(device);
                    connection.hold();
                    callback.onComplete(null);
                }
            });
        } catch (BleException e) {
            callback.onError(new QuinticException(e));
        }
    }


    BleConnection getConnection() throws BleException {
        if (connection == null) {
            connection = new BleConnection(this.context, QuinticUuid.SERVICE_UUID, QuinticUuid.WRITE_CHARACTERISTIC_UUID, QuinticUuid.NOTIFY_CHARACTERISTIC_UUID);
        }
        return connection;
    }

    @Override
    public void disconnect() {
        connection.disconnect();
    }


    /**
     * 获取设备号
     *
     * @param callback 回调
     */
    //@Override
    public void sendCommonCode(final String code, final QuinticCallbackTea<String> callback) {
        try {
            callback.onProgress(10);
            final BleConnection connection = getConnection();
            connection.connectDevice(device.getAddress(), new DeviceStateChangeCallback() {

                @Override
                public void onConnected(BluetoothDevice device) {
                    super.onConnected(device);

                    try {
                        connection.writeData(new BleWriteData(QuinticCommon.stringToBytes(code)));
                        callback.onProgress(40);
                    } catch (BleException ex) {
                        connection.hold();
                        callback.onError(new QuinticException(ex));
                    }
                }

                @Override
                public void onError(BleException ex) {
                    super.onError(ex);
                    connection.hold();
                    callback.onError(new QuinticException(ex));
                }

                @Override
                public void onNotify(byte[] data) {
                    super.onNotify(data);
                    if (!QuinticCommon.matchData(data, QuinticCommon.stringToBytes("ec"))) {
                        String notifydata = QuinticCommon.unsignedBytesToHexString(data, " ");

                        callback.onComplete(notifydata);
                    } else {
//                        callback.onKeepNotify(data);
//                        Log.e("onKeepNotify notify", QuinticCommon.unsignedBytesToHexString(data, 0, data.length));

//                        callback.onError(new QuinticException(QuinticException.INVALID_RESPONSE, "查询设备版本失败"));
                    }


//                    if (data[0] == 0x5B && data[1] == 0x01 && data[2] == 0x00) {
//                        connection.hold();
//                        callback.onComplete(null);
//                    } else {
//                        connection.hold();
//                        callback.onError(new QuinticException(QuinticException.INVALID_RESPONSE, "无法设置时间和目标步数"));
//                    }
                }
            });
        } catch (BleException ex) {
            callback.onError(new QuinticException(ex));
        }
    }


    /**
     * 获取设备号
     *
     * @param callback 回调
     */
    int position = 0;

    //@Override
    public void sendListMp3Code(final List<byte[]> code, final QuinticCallbackTea<String> callback) {
        try {
            callback.onProgress(1);
            final BleConnection connection = getConnection();
            connection.connectDevice(device.getAddress(), new DeviceStateChangeCallback() {

                @Override
                public void onConnected(BluetoothDevice device) {
                    super.onConnected(device);
                    position = 0;

                    convertCodeMap3(code, callback);
                }

                @Override
                public void onError(BleException ex) {
                    super.onError(ex);
                    connection.hold();
                    callback.onError(new QuinticException(ex));
                }

                @Override
                public void onNotify(byte[] data) {
                    super.onNotify(data);

//                    Intent intent = new Intent("com.pushtest.broadcast");
//                    intent.putExtra("CurrentLoading", "notify:" + QuinticCommon.unsignedBytesToHexString(data, 0, data.length));
//
//                    context.sendBroadcast(intent);
                    String trueReturnCode = "EB0C00" + QuinticCommon.unsignedIntToHexString(position + 3);

                    if (QuinticCommon.matchData(data, QuinticCommon.stringToBytes(trueReturnCode))) {
                        //假如匹配的值正确
                        if (position < 8) {
                            Log.e("MP3","MP3第"+position+"成功");

                            position++;
//                            try {
//                                Thread.sleep(60000);
//                            } catch (InterruptedException e) {
//                                e.printStackTrace();
//                            }

                            convertCodeMap3(code, callback);
                        } else {//最后一包已经发完

                            Log.e("MP3","MP3发送接收完成");
                            try {
                                Log.e("MP3","MP3开始设置当前语音组");

                                connection.writeData(new BleWriteData(QuinticCommon.stringToBytes("EB0D")));
                            } catch (BleException ex) {
                                callback.onError(new QuinticException(ex));
                            }

                        }


                    }
                    if(QuinticCommon.matchData(data, QuinticCommon.stringToBytes("EB0C0001"))) {
                        Log.e("MP3","MP3设置当前语音组成功");
                        try {
                            Log.e("MP3","MP3开始复位设备");

                            connection.writeData(new BleWriteData(QuinticCommon.stringToBytes("EB17")));
                        } catch (BleException ex) {
                            callback.onError(new QuinticException(ex));
                        }
                    }

                    if(QuinticCommon.matchData(data, QuinticCommon.stringToBytes("EB1701"))) {
                        Log.e("MP3","MP3复位设备成功");
                        callback.onComplete("EB1701");

                    }
//                    if (!QuinticCommon.matchData(data, QuinticCommon.stringToBytes("ec"))) {
//                        String notifydata=QuinticCommon.unsignedBytesToHexString(data, " ");
//
//                        callback.onComplete(notifydata);
//                    } else {
//                        callback.onKeepNotify(data);
//                        Log.e("onKeepNotify notify", QuinticCommon.unsignedBytesToHexString(data, 0, data.length));

//                        callback.onError(new QuinticException(QuinticException.INVALID_RESPONSE, "查询设备版本失败"));
//                    }


                }
            });
        } catch (BleException ex) {
            callback.onError(new QuinticException(ex));
        }
    }


//    public void convertCodeMap3(final List<byte[]> code, final QuinticCallbackTea<String> callback) {
//        try {
//            //逻辑list 中一共9个mp3的byte[]
//            /**
//             * 每个长包分，多个短包发送
//             */
////            Intent intent = new Intent("com.pushtest.broadcast");
////            intent.putExtra("CurrentLoading", "MP3 第" + position + "个Broadcast  发送进度:" + 11 * (position + 1));
////
////            context.sendBroadcast(intent);
//            byte[] mapcode0 = code.get(position);
//            //先发送 EB 0C 00 00   00     A4     34     03
//
//            Log.e("mapcode0",QuinticCommon.unsignedBytesToHexString(mapcode0,0,1000));
//
//            Log.e("mapcode0",QuinticCommon.unsignedBytesToHexString(mapcode0,1000,mapcode0.length-1000));
//
//            int packageCount = mapcode0.length / 64;
//
//            if(packageCount%64!=0){
//                packageCount=packageCount+1;
//            }
//
//            int lenth=0;
//            if(packageCount%4!=0){
//                packageCount=packageCount-packageCount%4+4;
//                lenth=packageCount*64;
//            }else{
//                lenth=mapcode0.length;
//                Log.e("lenth",lenth+"");
//                if(mapcode0.length % 64!=0){
//                    lenth=mapcode0.length-mapcode0.length % 64+64;
//                    Log.e("lenth2",lenth+"");
//
//                }
//
//            }
//
//
//
////            byte[]  mapcode=new byte[lenth];
////            for(int m=0;m<mapcode.length;m++){
////                mapcode[m]=0;
////            }
////
////            for(int m=0;m<mapcode0.length;m++){
////                mapcode[m]=mapcode0[m];
////            }
////            System.arraycopy(mapcode0, 0, mapcode, 0, mapcode0.length);
////           byte  aa= mapcode[mapcode0.length];
////
////            mapcode[mapcode0.length]=0;
////            byte  bb= mapcode[mapcode0.length];
//
////            Log.e("mapcode",QuinticCommon.unsignedBytesToHexString(mapcode,0,mapcode.length));
//
//
//
//            String packageCountH = QuinticCommon.unsignedIntToHexString(packageCount / 256);
//            String packageCountL = QuinticCommon.unsignedIntToHexString(packageCount % 256);
//
//            int otherNum = 64;
//            String otherNumHex = QuinticCommon.unsignedIntToHexString(otherNum);
//
//            String firstCode = "EB0C0000" + packageCountH + packageCountL + otherNumHex + QuinticCommon.unsignedIntToHexString(position + 3);
//            connection.writeData(new BleWriteData(QuinticCommon.stringToBytes(firstCode)));
//
//
//            for (int i = 0; i < packageCount; i++) {
//
//                callback.onProgress(11 * (position + 1)+i*10/packageCount);
//
//
//                String iHexH = QuinticCommon.unsignedIntToHexString((i + 1) / 256);
//                String iHexL = QuinticCommon.unsignedIntToHexString((i + 1) % 256);
//                StringBuffer everyCode =new StringBuffer();
//
////                everyCode.append("EB0C" + iHexH + iHexL + "40");
////                everyCode.append(QuinticCommon.unsignedBytesToHexString(mapcode, 64 * i, 64));
//                int length=mapcode0.length;
//                int codeImportNum=length/64;
//                if(codeImportNum%64==0){
//                    if(i<=codeImportNum-1){
//                        everyCode.append("EB0C" + iHexH + iHexL + "40");
//                        everyCode.append(QuinticCommon.unsignedBytesToHexString(mapcode0, 64 * i, 64));
//
//                    }else{
//                        everyCode.append("EB0C" + iHexH + iHexL + "40");
//                        for(int j=0;j<64;j++){
//                            everyCode.append("00");
//                        }
//                    }
//
//
//                }else{
//                    if(i<=codeImportNum-1){
//                        everyCode.append("EB0C" + iHexH + iHexL + "40");
//                        everyCode.append(QuinticCommon.unsignedBytesToHexString(mapcode0, 64 * i, 64));
//
//                    }
//                    else if(i==codeImportNum){
//                        everyCode.append("EB0C" + iHexH + iHexL + "40");
//                        everyCode.append(QuinticCommon.unsignedBytesToHexString(mapcode0, 64 * i, codeImportNum%64));
//
//
//                        for(int j=0;j<64-codeImportNum%64;j++){
//                            everyCode.append("00");
//                        }
//
//                    }
//
//                    else{
//                        everyCode.append("EB0C" + iHexH + iHexL + "40");
//                        for(int j=0;j<64;j++){
//                            everyCode.append("00");
//                        }
//                    }
//
//
//                }
//
//
//
//
//
//                Log.e("bytes:",everyCode.toString());
//                connection.writeData(new BleWriteData(QuinticCommon.stringToBytes(everyCode.toString())));
//
////                Log.i("MP3 "+position+"Broadcast  Send", everyCode);
////                intent.putExtra("CurrentLoading", "MP3 第" + position + "个Broadcast  发送 短包进度:第" + i + "个");
////
////                context.sendBroadcast(intent);
//
//            }
//
//            String finalCode = "EB0CFFFF" + QuinticCommon.unsignedIntToHexString(position + 3);
//
//
//            connection.writeData(new BleWriteData(QuinticCommon.stringToBytes(finalCode)));
//
//
//           // callback.onProgress(11 * (position + 1));
//        } catch (BleException ex) {
//            connection.hold();
//            callback.onError(new QuinticException(ex));
//        }
//    }
//}
public void convertCodeMap3(final List<byte[]> code, final QuinticCallbackTea<String> callback) {
    try {
        //逻辑list 中一共9个mp3的byte[]
        /**
         * 每个长包分，多个短包发送
         */
//        Intent intent = new Intent("com.pushtest.broadcast");
//        intent.putExtra("CurrentLoading", "MP3 第" + position + "个Broadcast  发送进度:" + 11 * (position + 1));
//
//        context.sendBroadcast(intent);
        byte[] mapcode0 = code.get(position);
        //先发送 EB 0C 00 00   00     A4     34     03


        Log.e("mapcode0",QuinticCommon.unsignedBytesToHexString(mapcode0,0,1000));
        Log.e("mapcode0",QuinticCommon.unsignedBytesToHexString(mapcode0,1000,mapcode0.length-1000));

        int packageCount = mapcode0.length / 64;

        if(packageCount%64!=0){
            packageCount=packageCount+1;
        }

        int lenth=0;
        if(packageCount%4!=0){
            packageCount=packageCount-packageCount%4+4;
            lenth=packageCount*64;
        }else{
            lenth=mapcode0.length;
            Log.e("lenth",lenth+"");
            if(mapcode0.length % 64!=0){
                lenth=mapcode0.length-mapcode0.length % 64+64;
                Log.e("lenth2",lenth+"");

            }

        }



        byte[]  mapcode=new byte[lenth];
        for(int m=0;m<mapcode0.length;m++){
            mapcode[m]=mapcode0[m];
        }
        Log.e("mapcode",QuinticCommon.unsignedBytesToHexString(mapcode,0,mapcode.length));



        String packageCountH = QuinticCommon.unsignedIntToHexString(packageCount / 256);
        String packageCountL = QuinticCommon.unsignedIntToHexString(packageCount % 256);

        int otherNum = 64;
        String otherNumHex = QuinticCommon.unsignedIntToHexString(otherNum);

        String firstCode = "EB0C0000" + packageCountH + packageCountL + otherNumHex + QuinticCommon.unsignedIntToHexString(position + 3);
        connection.writeData(new BleWriteData(QuinticCommon.stringToBytes(firstCode)));


        for (int i = 0; i < packageCount; i++) {
            String iHexH = QuinticCommon.unsignedIntToHexString((i + 1) / 256);
            String iHexL = QuinticCommon.unsignedIntToHexString((i + 1) % 256);


            String everyCode =null;
            everyCode = "EB0C" + iHexH + iHexL + "40" + QuinticCommon.unsignedBytesToHexString(mapcode, 64 * i, 64);

            connection.writeData(new BleWriteData(QuinticCommon.stringToBytes(everyCode)));

//                Log.i("MP3 "+position+"Broadcast  Send", everyCode);
//            intent.putExtra("CurrentLoading", "MP3 第" + position + "个Broadcast  发送 短包进度:第" + i + "个");
//
//            context.sendBroadcast(intent);

        }

        String finalCode = "EB0CFFFF" + QuinticCommon.unsignedIntToHexString(position + 3);


        connection.writeData(new BleWriteData(QuinticCommon.stringToBytes(finalCode)));


        callback.onProgress(11 * (position + 1));
    } catch (BleException ex) {
        connection.hold();
        callback.onError(new QuinticException(ex));
    }
}
}
