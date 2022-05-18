package gaozhi.online.peoplety.service.record;

import java.util.HashMap;
import java.util.Map;

import gaozhi.online.base.net.http.ApiRequest;
import gaozhi.online.peoplety.entity.Token;
import gaozhi.online.peoplety.service.NetConfig;

/**
 * 根据地区获取卷宗列表
 */
public class GetRecordByAreaService extends ApiRequest {


    public GetRecordByAreaService(ResultHandler resultHandler) {
        super(NetConfig.recordBaseURL, Type.GET, resultHandler);
    }

    public void request(Token token, int areaId, int pageNum, int pageSize) {
        Map<String, String> headers = new HashMap<>();
        headers.put("token", getGson().toJson(token));
        Map<String, String> params = new HashMap<>();
        params.put("areaId", "" + areaId);
        params.put("pageNum", "" + pageNum);
        params.put("pageSize", "" + pageSize);
        request("get/area/records", headers, params);
    }
}
