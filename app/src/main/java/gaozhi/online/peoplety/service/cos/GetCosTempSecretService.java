package gaozhi.online.peoplety.service.cos;

import java.util.HashMap;
import java.util.Map;

import gaozhi.online.base.net.Result;
import gaozhi.online.base.net.http.ApiRequest;
import gaozhi.online.peoplety.entity.Token;
import gaozhi.online.peoplety.service.NetConfig;
import gaozhi.online.peoplety.util.TencentCOS;

/**
 * 获取对象存储的临时密钥
 */
public class GetCosTempSecretService extends ApiRequest<Result> {
    public GetCosTempSecretService(OnDataListener<Result> resultHandler) {
        super(NetConfig.cosBaseURL, Type.GET);
        setDataListener(resultHandler);
    }
    public void request(Token token){
        Map<String, String> headers = new HashMap<>();
        headers.put("token", getGson().toJson(token));
        request("get/cos_tem_secret", headers,null);
    }

    @Override
    public Result initLocalData(Map<String, String> headers, Map<String, String> params, Object body) {
        return null;
    }

    @Override
    public Result getNetData(Result result) {
        return result;
    }
}
