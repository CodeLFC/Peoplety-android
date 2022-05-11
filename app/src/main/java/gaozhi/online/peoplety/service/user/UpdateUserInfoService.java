package gaozhi.online.peoplety.service.user;


import android.util.Log;

import com.google.gson.Gson;
import gaozhi.online.base.net.http.ApiRequest;
import gaozhi.online.peoplety.entity.Token;
import gaozhi.online.peoplety.entity.UserInfo;
import gaozhi.online.peoplety.service.NetConfig;
import java.util.HashMap;
import java.util.Map;

/**
 * 更新用户资料
 *
 */
public class UpdateUserInfoService extends ApiRequest {
    public UpdateUserInfoService(ResultHandler resultHandler) {
        super(NetConfig.userBaseURL, Type.PUT, resultHandler);
    }

    public void request(Token token, UserInfo userInfo) {
        Map<String, String> headers = new HashMap<>();
        Log.i(getClass().getName(),token.toString());
        headers.put("token", getGson().toJson(token));
        request("put/user_info",headers,new HashMap<>(),userInfo);
    }
}
