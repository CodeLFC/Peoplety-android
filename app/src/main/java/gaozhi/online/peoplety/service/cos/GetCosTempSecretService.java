package gaozhi.online.peoplety.service.cos;

import java.util.HashMap;
import java.util.Map;

import gaozhi.online.base.net.http.ApiRequest;
import gaozhi.online.peoplety.entity.Token;
import gaozhi.online.peoplety.service.NetConfig;

/**
 * 获取对象存储的临时密钥
 */
public class GetCosTempSecretService extends ApiRequest {
    public GetCosTempSecretService(ResultHandler resultHandler) {
        super(NetConfig.cosBaseURL, Type.GET, resultHandler);
    }
    public void request(Token token){
        Map<String, String> headers = new HashMap<>();
        headers.put("token", getGson().toJson(token));
        request("get/cos_tem_secret", headers,null);
    }
}
