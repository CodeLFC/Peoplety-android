package gaozhi.online.peoplety.service;

/**
 * 网络配置
 * http://gaozhi.online:8101/user/post/login
 */
public class NetConfig {
    //用户
    private static final String baseURL = "http://gaozhi.online:8100";
    //用户微服务
    private static final String userServiceBaseURL = baseURL + "/user/general";
    //权限
    public static final String userBaseURL = userServiceBaseURL + "/user/";
    //朋友
    public static final String friendBaseURL = userServiceBaseURL + "/friend/";
    //用户常量的获取 constant/get/status
    public static final String userConstantBaseURL = userServiceBaseURL + "/constant/";
    //对象存储
    public static final String cosBaseURL = userServiceBaseURL + "/cos/";

    // 卷宗微服务
    private static final String recordServiceBaseURL = baseURL + "/record/general";
    //卷宗常量
    public static final String recordConstantBaseURL = recordServiceBaseURL + "/constant/";
    //卷宗服务
    public static final String recordBaseURL = recordServiceBaseURL + "/record/";
    //收藏夹服务
    public static final String favoriteBaseURL = recordServiceBaseURL + "/favorite/";
    //官方网站
    public static final String officialURL = "http://gaozhi.online?userid=";
}
