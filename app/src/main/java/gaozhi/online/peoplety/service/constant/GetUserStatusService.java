package gaozhi.online.peoplety.service.constant;

import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

import gaozhi.online.base.net.http.ApiRequest;
import gaozhi.online.peoplety.entity.Token;
import gaozhi.online.peoplety.service.NetConfig;

/**
 * 用户身份常量
 */
public class GetUserStatusService extends ApiRequest {
    public GetUserStatusService(ResultHandler resultHandler) {
        super(NetConfig.userConstantBaseURL, Type.GET, resultHandler);
    }

    public void request(Token token) {
        Map<String, String> headers = new HashMap<>();
        headers.put("token", getGson().toJson(token));
        request("get/status", headers, new HashMap<>());
    }
}
