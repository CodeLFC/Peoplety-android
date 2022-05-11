package gaozhi.online.peoplety.service.friend;

import com.google.gson.Gson;
import gaozhi.online.base.net.http.ApiRequest;
import gaozhi.online.peoplety.entity.Friend;
import gaozhi.online.peoplety.entity.Token;
import gaozhi.online.peoplety.service.NetConfig;
import java.util.HashMap;
import java.util.Map;

/**
 * @author LiFucheng
 * @version 1.0
 * @description: TODO 修改备注
 * @date 2022/4/13 16:53
 */
public class UpdateFriendService extends ApiRequest {
    public UpdateFriendService(ResultHandler resultHandler) {
        super(NetConfig.friendBaseURL, Type.PUT, resultHandler);
    }

    public void request(Token token, long friendShipId, String remark) {
        Map<String, String> headers = new HashMap<>();
        headers.put("token", getGson().toJson(token));
        Map<String, String> params = new HashMap<>();
        Friend friend = new Friend();
        friend.setId(friendShipId);
        friend.setRemark(remark);
        request("put/attention", headers, params,friend);
    }
}
