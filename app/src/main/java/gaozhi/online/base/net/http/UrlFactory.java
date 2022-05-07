package gaozhi.online.base.net.http;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 创建url
 */
public class UrlFactory {
    /**
     *
     * @param url
     * @param params 追加一组参数
     * @return
     */
    public static String appendParams(String url, Map<String, String> params){
        if(isBlank(url)){
            return "";
        }else if(isEmptyMap(params)){
            return url.trim();
        }else{
            StringBuffer sb = new StringBuffer("");
            Set<String> keys = params.keySet();
            for (String key : keys) {
                sb.append(key).append("=").append(RestfulHTTP.encode_utf8(params.get(key))).append("&");
            }
            sb.deleteCharAt(sb.length() - 1);
            url = url.trim();
            int length = url.length();
            int index = url.indexOf("?");
            if(index > -1){//url说明有问号
                if((length - 1) == index){//url最后一个符号为？，如：http://wwww.baidu.com?
                    url += sb.toString();
                }else{//情况为：http://wwww.baidu.com?aa=11
                    url += "&" + sb.toString();
                }
            }else{//url后面没有问号，如：http://wwww.baidu.com
                url += "?" + sb.toString();
            }
            return url;
        }
    }

    /**
     *
     * @param url
     * @param name 追加一个参数
     * @param value
     * @return
     */
    public static String appendParam(String url, String name, String value){
        if(isBlank(url)){
            return "";
        }else if(isBlank(name)){
            return url.trim();
        }else{
            Map<String, String> params = new HashMap<String, String>();
            params.put(name, value);
            return appendParams(url, params);
        }
    }

    /**
     *
     * @param str
     * @return 是不是空串
     */
    public static boolean isBlank(String str) {
        int strLen;
        if (str == null || (strLen = str.length()) == 0) {
            return true;
        }
        for (int i = 0; i < strLen; i++) {
            if ((Character.isWhitespace(str.charAt(i)) == false)) {
                return false;
            }
        }
        return true;
    }

    /**
     *
     * @param map
     * @param <K>
     * @param <V>
     * @return  是不是空Map
     */
    public static <K, V> boolean isEmptyMap(Map<K, V> map) {
        return (map == null || map.size() < 1);
    }
}
