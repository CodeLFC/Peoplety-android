package gaozhi.online.peoplety.service.constant;

import java.util.HashMap;
import java.util.Map;

import gaozhi.online.base.net.Result;
import gaozhi.online.base.net.http.ApiRequest;
import gaozhi.online.peoplety.entity.Token;
import gaozhi.online.peoplety.service.NetConfig;

/**
 * 获取所有记录
 */
public class GetRecordTypeService extends ApiRequest<Result> {
    public GetRecordTypeService(OnDataListener<Result> resultHandler) {
        super(NetConfig.recordConstantBaseURL, Type.GET);
        setDataListener(resultHandler);
    }

    public void request(Token token) {
        Map<String, String> headers = new HashMap<>();
        headers.put("token", getGson().toJson(token));
        Map<String, String> params = new HashMap<>();
        request("get/record_type", headers, params);
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
