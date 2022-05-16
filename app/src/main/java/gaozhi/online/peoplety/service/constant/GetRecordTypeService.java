package gaozhi.online.peoplety.service.constant;

import java.util.HashMap;
import java.util.Map;

import gaozhi.online.base.net.http.ApiRequest;
import gaozhi.online.peoplety.entity.Token;
import gaozhi.online.peoplety.service.NetConfig;

/**
 * 获取所有记录
 */
public class GetRecordTypeService extends ApiRequest {
    public GetRecordTypeService( ResultHandler resultHandler) {
        super(NetConfig.recordConstantBaseURL, Type.GET, resultHandler);
    }

    public void request(Token token) {
        Map<String, String> headers = new HashMap<>();
        headers.put("token", getGson().toJson(token));
        Map<String, String> params = new HashMap<>();
        request("get/record_type", headers, params);
    }
}
