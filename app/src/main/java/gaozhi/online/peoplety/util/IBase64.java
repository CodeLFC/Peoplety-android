package gaozhi.online.peoplety.util;

import java.util.Base64;

/**
 * @author LiFucheng
 * @version 1.0
 * @description: TODO Base64
 * @date 2022/7/30 15:37
 */
public class IBase64 {

    /**
     * base64加密
     *
     * @param content 待加密内容
     * @return byte[]
     */
    public static byte[] Encrypt(final String content) {
        return Base64.getEncoder().encode(content.getBytes());
    }

    public static String base64Encrypt(final String content) {
        byte[] encrypt = Encrypt(content);
        return HexUtil.byteToHex(encrypt);
    }

    /**
     * base64解密
     *
     * @param encoderContent 已加密内容
     * @return byte[]
     */
    public static byte[] Decrypt(final byte[] encoderContent) {
        return Base64.getDecoder().decode(encoderContent);
    }

    public static String base64Decrypt(final String encoderContent) {
        byte[] encrypt = HexUtil.hexToByte(encoderContent);
        return new String(Decrypt(encrypt));
    }

    public static final class HexUtil {
        private final static String HEX = "0123456789abcdef";


        /**
         * 16进制字符串转字节数组
         *
         * @param hexString 16进制字符串
         * @return 转化后的字节数组
         */
        public static byte[] hexToByte(String hexString) {
            int len = hexString.length() / 2;
            byte[] result = new byte[len];
            for (int i = 0; i < len; i++) {
                result[i] = Integer.valueOf(hexString.substring(2 * i, 2 * i + 2), 16).byteValue();
            }
            return result;
        }

        /**
         * 字节数组转16进制字符串
         *
         * @param bytes 字节数组
         * @return 转化后的字符串
         */
        public static String byteToHex(byte[] bytes) {
            if (bytes == null) {
                return "";
            }
            StringBuilder result = new StringBuilder(2 * bytes.length);
            for (byte b : bytes) {
                result.append(HEX.charAt((b >> 4) & 0x0f)).append(HEX.charAt(b & 0x0f));
            }
            return result.toString();
        }
    }
}
