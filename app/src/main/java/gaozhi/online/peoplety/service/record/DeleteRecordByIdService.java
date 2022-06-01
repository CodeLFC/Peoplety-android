package gaozhi.online.peoplety.service.record;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import gaozhi.online.base.net.Result;
import gaozhi.online.base.net.http.ApiRequest;
import gaozhi.online.peoplety.entity.Token;
import gaozhi.online.peoplety.service.NetConfig;

/**
 * 删除卷宗
 */
public class DeleteRecordByIdService extends ApiRequest<Result> {

    public DeleteRecordByIdService(OnDataListener<Result> onDataListener) {
        super(NetConfig.recordBaseURL, Type.DELETE);
        setDataListener(onDataListener);
    }

    public void request(Token token, long recordId) {
        Map<String, String> headers = new HashMap<>();
        headers.put("token", getGson().toJson(token));
        Map<String, String> params = new HashMap<>();
        params.put("id", "" + recordId);
        request("delete/record", headers, params);
    }

    @Override
    public Result initLocalData(Map<String, String> headers, Map<String, String> params, Object body) {
        return null;
    }

    @Override
    public void getNetData(Result result, Consumer<Result> consumer) {
        consumer.accept(result);
    }
}
