package gaozhi.online.peoplety.service.friend;

import com.google.gson.Gson;

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
 * @description: TODO 取消关注
 * @date 2022/4/13 18:19
 */
public class DeleteFriendService extends BaseApiRequest<Friend> {
    public DeleteFriendService(OnDataListener<Friend> resultHandler) {
        super(NetConfig.friendBaseURL, Type.DELETE);
        setDataListener(resultHandler);
    }

    public void request(Token token, long id) {
        Map<String, String> headers = new HashMap<>();
        headers.put("token", getGson().toJson(token));
        Map<String, String> params = new HashMap<>();
        params.put("id", "" + id);
        request("delete/attention", headers, params);
    }

    @Override
    public Friend initLocalData(Map<String, String> headers, Map<String, String> params, Object body) {
        return null;
    }

    @Override
    public void getNetData(Result result, Consumer<Friend> consumer) {
        Friend friend = getGson().fromJson(result.getData(), Friend.class);
        consumer.accept(friend);
        getRealm().executeTransactionAsync(realm -> {
            Friend fDB = realm.where(Friend.class).equalTo("id", friend.getId()).findFirst();
            if (fDB == null) return;
            fDB.deleteFromRealm();
        });
    }
}
