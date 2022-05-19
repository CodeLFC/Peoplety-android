package gaozhi.online.peoplety.service.record;

import java.util.HashMap;
import java.util.Map;

import gaozhi.online.base.net.Result;
import gaozhi.online.base.net.http.ApiRequest;
import gaozhi.online.peoplety.entity.Record;
import gaozhi.online.peoplety.entity.Token;
import gaozhi.online.peoplety.service.NetConfig;

/**
 * 发布卷宗
 */
public class PublishRecordService extends ApiRequest<Result> {
    public PublishRecordService( OnDataListener<Result> resultHandler) {
        super(NetConfig.recordBaseURL, Type.POST);
        setDataListener(resultHandler);
    }

    public void request(Token token, Record record){
        Map<String, String> headers = new HashMap<>();
        headers.put("token", getGson().toJson(token));
        request("post/record",headers,null,record);
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
