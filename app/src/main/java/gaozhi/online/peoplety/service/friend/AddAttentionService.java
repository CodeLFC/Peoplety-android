package gaozhi.online.peoplety.service.friend;

import android.media.session.MediaSession;

import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

import gaozhi.online.base.net.http.ApiRequest;
import gaozhi.online.peoplety.entity.Friend;
import gaozhi.online.peoplety.service.NetConfig;

/**
 * @author LiFucheng
 * @version 1.0
 * @description: TODO 添加关注关系
 * @date 2022/4/13 16:38
 */
public class AddAttentionService extends ApiRequest {
    public AddAttentionService(ResultHandler resultHandler) {
        super(NetConfig.friendBaseURL, Type.POST, resultHandler);
    }

    public void request(MediaSession.Token token, long friendId) {
        Map<String, String> headers = new HashMap<>();
        headers.put("token", getGson().toJson(token));
        Map<String, String> params = new HashMap<>();
        Friend friend = new Friend();
        friend.setFriendId(friendId);
        request("post/attention", headers, params, friend);
    }
}
