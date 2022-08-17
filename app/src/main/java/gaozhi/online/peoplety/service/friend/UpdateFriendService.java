package gaozhi.online.peoplety.service.friend;


import gaozhi.online.base.net.Result;
import gaozhi.online.peoplety.entity.Friend;
import gaozhi.online.peoplety.entity.Token;
import gaozhi.online.peoplety.service.BaseApiRequest;
import gaozhi.online.peoplety.service.NetConfig;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * @author LiFucheng
 * @version 1.0
 * @description: TODO 修改备注
 * @date 2022/4/13 16:53
 */
public class UpdateFriendService extends BaseApiRequest<Friend> {
    public UpdateFriendService(OnDataListener<Friend> resultHandler) {
        super(NetConfig.friendBaseURL, Type.PUT);
        setDataListener(resultHandler);
    }

    public void request(Token token, Friend friend) {
        Map<String, String> headers = new HashMap<>();
        headers.put("token", getGson().toJson(token));
        Map<String, String> params = new HashMap<>();
        request("put/attention", headers, params, friend);
    }

    @Override
    public Friend initLocalData(Map<String, String> headers, Map<String, String> params, Object body) {
        return null;
    }

    @Override
    public void getNetData(Result result, Consumer<Friend> consumer) {
        Friend friend = getGson().fromJson(result.getData(), Friend.class);
        getRealm().executeTransaction(realm -> realm.copyToRealmOrUpdate(friend));
        consumer.accept(friend);
    }
}
