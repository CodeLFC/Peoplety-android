package gaozhi.online.peoplety.service.friend;

import gaozhi.online.base.net.Result;
import gaozhi.online.base.net.http.ApiRequest;
import gaozhi.online.peoplety.entity.Friend;
import gaozhi.online.peoplety.entity.Token;
import gaozhi.online.peoplety.service.BaseApiRequest;
import gaozhi.online.peoplety.service.NetConfig;
import io.realm.Realm;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * @author LiFucheng
 * @version 1.0
 * @description: TODO 获取指定的朋友关系
 * @date 2022/4/13 16:25
 */
public class GetFriendService extends BaseApiRequest<Friend> {
    public GetFriendService(OnDataListener<Friend> resultHandler) {
        super(NetConfig.friendBaseURL, Type.GET);
        setDataListener(resultHandler);
    }

    public void request(Token token, long friendId) {
        Map<String, String> headers = new HashMap<>();
        headers.put("token", getGson().toJson(token));
        Map<String, String> params = new HashMap<>();
        params.put("friendId", "" + friendId);
        params.put("userId", "" + token.getUserid());
        request("get/friend", headers, params);
    }


    @Override
    public Friend initLocalData(Map<String, String> headers, Map<String, String> params, Object body) {
        long friendId = Long.parseLong(params.get("friendId"));
        long userId = Long.parseLong(params.get("userId"));
        return getRealm().where(Friend.class).equalTo("userid", userId).equalTo("friendId", friendId).findFirst();
    }

    @Override
    public void getNetData(Result result, Consumer<Friend> consumer) {
        Friend friend = getGson().fromJson(result.getData(), Friend.class);
        if (friend == null) {
            return;
        }
        getRealm().executeTransaction(realm -> realm.copyToRealmOrUpdate(friend));
        consumer.accept(friend);
    }
}
