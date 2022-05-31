package gaozhi.online.peoplety.service.user;


import gaozhi.online.base.net.Result;
import gaozhi.online.base.net.http.ApiRequest;
import gaozhi.online.peoplety.service.NetConfig;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * 注册新用户服务
 */
public class RegisterService extends ApiRequest<Result> {

    public RegisterService(OnDataListener<Result> resultHandler) {
        super(NetConfig.userBaseURL, Type.POST);
        setDataListener(resultHandler);
    }

    public void request(String cell_phone, String verify_code, String pass) {
        Map<String, String> params = new HashMap<>();
        params.put("phone", cell_phone);
        params.put("verify_code", verify_code);
        params.put("pass", pass);
        request("post/register", params);
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
