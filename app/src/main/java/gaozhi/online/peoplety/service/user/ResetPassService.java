package gaozhi.online.peoplety.service.user;

import gaozhi.online.base.net.Result;
import gaozhi.online.base.net.http.ApiRequest;
import gaozhi.online.peoplety.service.NetConfig;
import java.util.HashMap;
import java.util.Map;

/**
 * 重置密码
 */
public class ResetPassService extends ApiRequest<Result> {
    public ResetPassService(OnDataListener<Result> resultHandler) {
        super(NetConfig.userBaseURL, Type.PUT);
        setDataListener(resultHandler);
    }

    public void request(String cell_phone, String verify_code, String pass) {
        Map<String, String> params = new HashMap<>();
        params.put("phone", cell_phone);
        params.put("verify_code", verify_code);
        params.put("pass", pass);
        request("put/password", params);
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
