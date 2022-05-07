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
 * @description: TODO
 * @date 2022/4/13 18:19
 */
public class DeleteFriendService extends ApiRequest {
    public DeleteFriendService(ResultHandler resultHandler) {
        super(NetConfig.friendBaseURL, Type.DELETE, resultHandler);
    }

    public void request(Token token, long id) {
        Map<String, String> headers = new HashMap<>();
        headers.put("token", new Gson().toJson(token));
        Map<String, String> params = new HashMap<>();
        params.put("id",""+id);
        request("delete/attention", headers, params);
    }
}
