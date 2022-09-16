package gaozhi.online.peoplety.service.msg;

import java.util.Map;
import java.util.function.Consumer;

import gaozhi.online.base.net.Result;
import gaozhi.online.base.net.http.ApiRequest;
import gaozhi.online.peoplety.service.NetConfig;

/**
 * 获取消息ID
 */
public class GetMessageIdService extends ApiRequest<Result> {
    public GetMessageIdService(OnDataListener<Result> resultHandler) {
        super(NetConfig.messageIMURL, Type.GET);
        setDataListener(resultHandler);
    }

    public void request() {
        request("get/msgId", null);
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
