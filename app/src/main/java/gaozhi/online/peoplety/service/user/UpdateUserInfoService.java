package gaozhi.online.peoplety.service.user;


import com.google.gson.Gson;
import gaozhi.online.base.net.http.ApiRequest;
import gaozhi.online.peoplety.entity.Token;
import gaozhi.online.peoplety.entity.UserInfo;
import gaozhi.online.peoplety.service.NetConfig;
import java.util.HashMap;
import java.util.Map;

/**
 * 更新用户资料
 * "set head_url=#{head_url},nick=#{nick},remark=#{remark},gender=#{gender},birth=#{birth},gps=#{gps},cell_phone=#{cellPhone},wechat=#{wechat},qq=#{qq},visible=#{visible},email=#{email},update_time=#{updateTime} " +
 */
public class UpdateUserInfoService extends ApiRequest {
    public UpdateUserInfoService(ResultHandler resultHandler) {
        super(NetConfig.userBaseURL, Type.PUT, resultHandler);
    }

    public void request(Token token, UserInfo userInfo) {
        Map<String, String> headers = new HashMap<>();
        headers.put("token", new Gson().toJson(token));
        request("put/user_info",headers,null,userInfo);
    }
}
