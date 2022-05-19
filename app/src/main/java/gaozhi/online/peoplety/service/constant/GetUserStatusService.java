package gaozhi.online.peoplety.service.constant;

import java.util.HashMap;
import java.util.Map;

import gaozhi.online.base.net.Result;
import gaozhi.online.base.net.http.ApiRequest;
import gaozhi.online.peoplety.entity.Token;
import gaozhi.online.peoplety.service.NetConfig;

/**
 * 用户身份常量
 */
public class GetUserStatusService extends ApiRequest<Result> {
    public GetUserStatusService(OnDataListener<Result> resultHandler) {
        super(NetConfig.userConstantBaseURL, Type.GET);
        setDataListener(resultHandler);
    }

    public void request(Token token) {
        Map<String, String> headers = new HashMap<>();
        headers.put("token", getGson().toJson(token));
        request("get/status", headers, new HashMap<>());
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
