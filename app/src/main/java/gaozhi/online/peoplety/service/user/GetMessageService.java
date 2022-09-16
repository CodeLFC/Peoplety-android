package gaozhi.online.peoplety.service.user;


import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import gaozhi.online.base.net.Result;
import gaozhi.online.base.net.http.ApiRequest;
import gaozhi.online.peoplety.entity.Token;
import gaozhi.online.peoplety.service.NetConfig;

/**
 * 获取消息
 */
public class GetMessageService extends ApiRequest<Result> {

    public GetMessageService(OnDataListener<Result> onDataListener) {
        super(NetConfig.userBaseURL, Type.GET);
        setDataListener(onDataListener);
    }

    public void request(Token token) {
        Map<String, String> headers = new HashMap<>();
        headers.put("token", getGson().toJson(token));
        Map<String, String> params = new HashMap<>();
        params.put("toId", "" + token.getUserid());
        request("get/messages", headers, params);
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
