package gaozhi.online.peoplety.service.constant;

import java.util.HashMap;
import java.util.Map;

import gaozhi.online.base.net.http.ApiRequest;
import gaozhi.online.peoplety.entity.Token;
import gaozhi.online.peoplety.service.NetConfig;

/**
 * 获取所有地区
 */
public class GetRecordAreaService extends ApiRequest {

    public GetRecordAreaService(ResultHandler resultHandler) {
        super(NetConfig.recordConstantBaseURL, Type.GET, resultHandler);
    }

    public void request(Token token) {
        Map<String, String> headers = new HashMap<>();
        headers.put("token", getGson().toJson(token));
        Map<String, String> params = new HashMap<>();
        request("get/areas", headers, params);
    }
}
