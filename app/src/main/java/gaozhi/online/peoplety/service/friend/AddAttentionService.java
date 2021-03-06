package gaozhi.online.peoplety.service.friend;


import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import gaozhi.online.base.net.Result;
import gaozhi.online.base.net.http.ApiRequest;
import gaozhi.online.peoplety.entity.Friend;
import gaozhi.online.peoplety.entity.Token;
import gaozhi.online.peoplety.service.NetConfig;

/**
 * @author LiFucheng
 * @version 1.0
 * @description: TODO 添加关注关系
 * @date 2022/4/13 16:38
 */
public class AddAttentionService extends ApiRequest<Friend> {
    public AddAttentionService(OnDataListener<Friend> onDataListener) {
        super(NetConfig.friendBaseURL, Type.POST);
        setDataListener(onDataListener);
    }

    public void request(Token token, long friendId) {
        Map<String, String> headers = new HashMap<>();
        headers.put("token", getGson().toJson(token));
        Map<String, String> params = new HashMap<>();
        Friend friend = new Friend();
        friend.setFriendId(friendId);
        request("post/attention", headers, params, friend);
    }

    @Override
    public Friend initLocalData(Map<String, String> headers, Map<String, String> params, Object body) {
        return null;
    }

    @Override
    public void getNetData(Result result, Consumer<Friend> consumer) {
        Friend friend = getGson().fromJson(result.getData(),Friend.class);
        consumer.accept(friend);
    }
}
