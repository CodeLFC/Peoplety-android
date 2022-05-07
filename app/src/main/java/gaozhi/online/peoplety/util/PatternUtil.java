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

    public static boolean matchEmail(String src) {
        return email.matcher(src).matches();
    }

    public static boolean matchUrl(String src) {
        return url.matcher(src).matches();
    }

    public static boolean matchEnglish(String src) {
        return english.matcher(src).matches();
    }

    public static boolean matchChinese(String src) {
        return chinese.matcher(src).matches();
    }

}
