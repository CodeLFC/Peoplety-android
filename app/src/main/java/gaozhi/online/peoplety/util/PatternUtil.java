package gaozhi.online.peoplety.util;

import java.util.regex.Pattern;

/**
 * @author LiFucheng
 * @version 1.0
 * @description: TODO 常用正则表达式
 * @date 2022/5/2 18:19
 */
public class PatternUtil {
    private static final Pattern email = Pattern.compile("\\w[-\\w.+]*@([A-Za-z0-9][-A-Za-z0-9]+\\.)+[A-Za-z]{2,14}");
    private static final Pattern url = Pattern.compile("[a-zA-z]+://[^\\s]*");
    private static final Pattern english = Pattern.compile("^[A-Za-z0-9]+$");
    private static final Pattern chinese = Pattern.compile("^[\\u4e00-\\u9fa5]{0,}$");
    private static final Pattern phone = Pattern.compile("^(13[0-9]|14[5|7]|15[0|1|2|3|4|5|6|7|8|9]|18[0|1|2|3|5|6|7|8|9])\\d{8}$");
    private static final Pattern qq = Pattern.compile("^[1-9][0-9]{4,10}$");
    private static final Pattern wechat = Pattern.compile("^[a-zA-Z]([-_a-zA-Z0-9]{5,19})+$");
    private static final Pattern ipv4 = Pattern.compile("^((25[0-5]|2[0-4]\\d|1\\d{2}|[1-9]?\\d)\\.){3}(25[0-5]|2[0-4]\\d|1\\d{2}|[1-9]?\\d)$");
    private static final Pattern ipv6 = Pattern.compile( "^([0-9a-fA-F]{1,4}:){7}[0-9a-fA-F]{1,4}$");

    public static boolean matchEmail(String src) {
        if (src == null) return false;
        return email.matcher(src).matches();
    }

    public static boolean matchUrl(String src) {
        if (src == null) return false;
        return url.matcher(src).matches();
    }

    public static boolean matchEnglish(String src) {
        if (src == null) return false;
        return english.matcher(src).matches();
    }

    public static boolean matchChinese(String src) {
        if (src == null) return false;
        return chinese.matcher(src).matches();
    }

    public static boolean matchPhone(String src) {
        if (src == null) return false;
        return phone.matcher(src).matches();
    }

    public static boolean matchQQ(String src) {
        if (src == null) return false;
        return qq.matcher(src).matches();
    }

    public static boolean matchWechat(String src) {
        if (src == null) return false;
        return wechat.matcher(src).matches() || matchPhone(src);
    }

    public static boolean matchIPV4(String src) {
        if (src == null) return false;
        return ipv4.matcher(src).matches();
    }

    public static boolean matchIPV6(String src) {
        if (src == null) return false;
        return ipv6.matcher(src).matches();
    }
}
