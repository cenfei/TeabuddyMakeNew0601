package quinticble;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 基础工具类
 */
public class QuinticCommon {

    /**
     * 获取二进制数据第n位的值
     * @param data 字节数据
     * @param bitPos 位
     * @return 0或者1
     */
    public static boolean getBitValue(byte data, int bitPos) {
        int i = unsignedByteToInt(data);
        return (i << (31 - bitPos) >>> 31) == 1;
    }

    /**
     * 十六进制字符串转字节数组
     * @param in 十六进制字符串
     * @return 字节数组
     */
    public static byte[] stringToBytes(String in) {

        int length = in.length() / 2;

        byte[] b = new byte[length];

        for (int i = 0; i < length; i++) {
            b[i] = Integer.valueOf(in.substring(i * 2, i * 2 + 2), 16).byteValue();
        }

        return b;
    }

    /**
     * 无符号字节转整形
     * @param b 无符号字节
     * @return 整形数值
     */
    public static int unsignedByteToInt(byte b) {
        return ((int) b) & 0xff;
    }

    /**
     * 无符号字节数组转整形
     * @param b 无符号字节数组
     * @param offset 数组的开始位置
     * @param length 从开始位置计数的长度
     * @return 整形值
     */
    public static int unsignedBytesToInt(byte[] b, int offset, int length) {
        int result = 0;
        for (int i = offset; i < offset + length; i++) {
            result += unsignedByteToInt(b[i]) << ((offset + length - i - 1) * 8);
        }
        return result;
    }

    /**
     * 字节数组转整形
     * @param b 字节数组
     * @param offset 数组的开始位置
     * @param length 从开始位置计数的长度
     * @return 整形值
     */
    public static int bytesToInt(byte[] b, int offset, int length) {
        String hex = "";
        for (int i = offset; i < offset + length; i++) {
            hex += unsignedByteToHexString(b[i]);
        }
        return Long.valueOf(hex, 16).intValue();
    }

    /**
     * 无符号字节转长整形
     * @param b 无符号字节
     * @return 长整形值
     */
    public static long unsignedByteToLong(byte b) {
        return ((long) b) & 0xff;
    }

    /**
     * 测试字节数组的起始n字节是否与给定的字节匹配
     * @param data 待测试字节数组
     * @param match 给定的待匹配字节
     * @return 匹配为true,否则为false
     */
    public static boolean matchData(byte[] data, byte... match) {
        if (data.length < match.length) {
            return false;
        }

        for (int i = 0; i < match.length; i++) {


            if (data[i] != match[i]) {
                return false;
            }
        }

        return true;
    }

    /**
     * 无符号字节转十六进制字符串
     * @param b 无符号字节
     * @return 十六进制字符串
     */
    public static String unsignedByteToHexString(byte b) {
        String hex = Integer.toHexString(unsignedByteToInt(b));
        for (int i = 0; i < 2 - hex.length(); i++) {
            hex = "0" + hex;
        }
        return hex;
    }public static String unsignedIntToHexString(int b) {
        String hex = Integer.toHexString(b);
        for (int i = 0; i < 2 - hex.length(); i++) {
            hex = "0" + hex;
        }
        return hex;
    }

    public static void main(String[] args){
        String hex = Integer.toHexString(33);
        for (int i = 0; i < 2 - hex.length(); i++) {
            hex = "0" + hex;
        }
        if(hex.length()%2==1){
            hex = "0" + hex;
        }


        System.out.print(hex);

    }

    /**
     * 无符号字节数组转十六进制字符串
     * @param b 无符号字节数组
     * @param splitter 每个字节之间的分隔符号
     * @return 十六进制字符串
     */
    public static String unsignedBytesToHexString(byte[] b, String splitter) {
        String hex = "";
        for (int i = 0; i < b.length; i++) {
            if (hex.length() > 0) {
                hex += splitter;
            }
            hex += QuinticCommon.unsignedByteToHexString(b[i]);
        }
        return hex;
    }

    /**
     * 无符号字节数组转十六进制字符串
     * @param b 无符号字节数组
     * @param offset 数组的起始位置
     * @param length 从起始位置开始计数的长度
     * @return 十六进制字符串
     */
    public static String unsignedBytesToHexString(byte[] b, int offset, int length) {
        String hex = "";
        for (int i = offset; i < offset + length; i++) {
            hex += QuinticCommon.unsignedByteToHexString(b[i]);
        }
        return hex;
    }

    /**
     * 合并多个字节数组
     * @param b 待合并的字节数组
     * @return 已合并的字节数组
     */
    public static byte[] joinBytes(byte[]... b) {
        int len = 0;
        for (byte[] ar : b) {
            len += ar.length;
        }
        byte[] ret = new byte[len];
        len = 0;
        for (byte[] ar : b) {
            for (byte a : ar) {
                ret[len++] = a;
            }
        }
        return ret;
    }

    /**
     * 无符号字节数组转UTF8字符串
     * @param b 无符号字节数组
     * @param offset 数组的起始位置
     * @param length 从起始位置开始计数的长度
     * @return UTF8字符串
     */
    public static String unsignedByteToUtfString(byte b[], int offset, int length) {

        byte a[] = new byte[length];
        for (int i = offset; i < offset + length; i++) {
            a[i - offset] = b[i];
        }

        String res = null;
        try {
            res = new String(a, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return res;
    }

    /**
     * 无符号字节数组转字符串
     * @param b 无符号字节数组
     * @param offset 数组的起始位置
     * @param length 从起始位置开始计数的长度
     * @return 字符串
     */
    public static String unsignedByteToString(byte b[], int offset, int length) {

        byte a[] = new byte[length];
        for (int i = offset; i < offset + length; i++) {
            a[i - offset] = b[i];
        }
        String res = null;
        res = new String(a);
        return res;
    }

    /**
     * 无符号字节数组转指定字符集的字符串
     * @param b 无符号字节数组
     * @param offset 数组的起始位置
     * @param length 从起始位置开始计数的长度
     * @param charSet 字符集
     * @return 字符串
     */
    public static String unsignedByteToString(byte b[], int offset, int length, String charSet) {


        byte a[] = new byte[length];
        for (int i = offset; i < offset + length; i++) {
            a[i - offset] = b[i];
        }
        String res = null;
        try {
            res = new String(a, charSet);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return res;
    }

    static boolean boolTrueDate(String dateStart, Date date2, String dateEnd) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");// 小写的mm表示的是分钟

        try {


            Date sDate = sdf.parse(dateStart);
            Date eDate = sdf.parse(dateEnd);
            if (sDate.compareTo(date2) <= 0 && date2.compareTo(eDate) <= 0) {
                return true;
            }

        } catch (Exception e) {
            return false;
        }


        return false;
    }

}
