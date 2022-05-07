package gaozhi.online.peoplety.service;

/**
 * 网络配置
 */
public class NetConfig {
    //用户
    private static final String baseURL = "http://gaozhi.online";
    //权限
    public static final String userBaseURL = baseURL + ":8101/v1/user/";
    //朋友
    public static final String friendBaseURL = baseURL + ":8101/v1/friend/";
    //即时通信模块
    public static final String imBaseURL = baseURL + ":8102/v1/server/";

    //官方网站
    public static final String officialURL = "http://gaozhi.online?userid=";
}
