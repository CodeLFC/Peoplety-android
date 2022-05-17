package gaozhi.online.peoplety.service.record;

import java.util.HashMap;
import java.util.Map;

import gaozhi.online.base.net.http.ApiRequest;
import gaozhi.online.peoplety.entity.Record;
import gaozhi.online.peoplety.entity.Token;
import gaozhi.online.peoplety.service.NetConfig;

/**
 * 发布卷宗
 */
public class PublishRecordService extends ApiRequest {
    public PublishRecordService( ResultHandler resultHandler) {
        super(NetConfig.recordBaseURL, Type.POST, resultHandler);
    }

    public void request(Token token, Record record){
        Map<String, String> headers = new HashMap<>();
        headers.put("token", getGson().toJson(token));
        request("post/record",headers,null,record);
    }
}
