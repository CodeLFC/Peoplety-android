package gaozhi.online.peoplety.util;

import java.nio.charset.StandardCharsets;

/**
 * @description(字符串工具)
 * @author: gaozhi.online
 * @createDate: 2021/2/5 0005
 * @version: 1.0
 */
public class StringUtil {
    /**
     * @description(Returns true if, and only if, length() is 0 or if is null.)
     * @return boolean
     * @author gaozhi.online
     * @date 2021/2/5 0005
     */
    public static boolean isEmpty(String arg){
        if(arg!=null){
            return arg.isEmpty();
        }
        return true;
    }

    public static boolean equals(String arg1,String arg2){
        if(arg1!=null){
            return arg1.equals(arg2);
        }
        return arg2 == null;
    }

    /**
     * 生成随机数
     * @param len 随机数的长度
     * @return
     */
    public static String random(int len){
         int min= (int) Math.pow(10,len-1);
        int max= (int) Math.pow(10,len);
         int random= (int) (Math.random()*max);
         if(random<min){
             random*=10;
         }else if(random>=max){
             random/=10;
         }
         return String.valueOf(random);
    }

    /**
     * 字节数组到String的转换.
     */
    public static String bytesToString(byte[] str) {
        return new String(str, StandardCharsets.UTF_8);
    }

    public static String num2Str(int num){
        if(num<1000){
            return Integer.toString(num);
        }
        if(num<10000){
            return String.format("%.1fk",num/1000.0);
        }
        return String.format("%.1fw",num/10000.0);
    }

    public static String numLong2Str(long num){
        if(num<1000){
            return Long.toString(num);
        }
        if(num<10000){
            return String.format("%.1fk",num/1000.0);
        }
        return String.format("%.1fw",num/10000.0);
    }
}
