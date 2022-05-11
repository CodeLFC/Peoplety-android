package gaozhi.online.peoplety.service;

/**
 * 网络配置
 * http://gaozhi.online:8101/user/post/login
 */
public class NetConfig {
    //用户
    private static final String baseURL = "http://gaozhi.online";
    //权限
    public static final String userBaseURL = baseURL + ":8101/user/";
    //朋友
    public static final String friendBaseURL = baseURL + ":8101/friend/";
    //用户常量的获取 constant/get/status
    public static final String userConstantBaseURL = baseURL + ":8101/constant/";
    //官方网站
    public static final String officialURL = "http://gaozhi.online?userid=";
}
