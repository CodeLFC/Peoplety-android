package gaozhi.online.peoplety.service.friend;

import com.google.gson.Gson;
import gaozhi.online.base.net.http.ApiRequest;
import gaozhi.online.peoplety.entity.Token;
import gaozhi.online.peoplety.service.NetConfig;
import java.util.HashMap;
import java.util.Map;

/**
 * @author LiFucheng
 * @version 1.0
 * @description: TODO 获取粉丝
 * @date 2022/4/13 13:07
 */
public class GetFanService extends ApiRequest {
    public GetFanService(ResultHandler resultHandler) {
        super(NetConfig.friendBaseURL, Type.GET, resultHandler);
    }

    public void request(Token token, int pageNum) {
        Map<String, String> headers = new HashMap<>();
        headers.put("token", new Gson().toJson(token));
        Map<String, String> params = new HashMap<>();
        params.put("pageNum", "" + pageNum);
        params.put("pageSize", "" + 100);
        request("get/fans", headers, params);
    }
}
