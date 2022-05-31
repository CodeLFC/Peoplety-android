package gaozhi.online.peoplety.service.constant;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import gaozhi.online.base.net.Result;
import gaozhi.online.base.net.http.ApiRequest;
import gaozhi.online.peoplety.entity.Area;
import gaozhi.online.peoplety.entity.Token;
import gaozhi.online.peoplety.service.NetConfig;

/**
 * 获取所有地区
 */
public class GetRecordAreaService extends ApiRequest<Result> {

    public GetRecordAreaService(OnDataListener<Result> dataListener) {
        super(NetConfig.recordConstantBaseURL, Type.GET);
        setDataListener(dataListener);
    }

    public void request(Token token) {
        Map<String, String> headers = new HashMap<>();
        headers.put("token", getGson().toJson(token));
        Map<String, String> params = new HashMap<>();
        request("get/areas", headers, params);
    }

    @Override
    public Result initLocalData(Map<String, String> headers, Map<String, String> params, Object body) {
        return null;
    }

    @Override
    public void getNetData(Result result, Consumer<Result>consumer) {
        consumer.accept(result);
    }
}
