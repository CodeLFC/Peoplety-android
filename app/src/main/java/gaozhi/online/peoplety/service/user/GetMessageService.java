package gaozhi.online.peoplety.service.user;

import android.util.Log;

import com.google.gson.reflect.TypeToken;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import gaozhi.online.base.net.Result;
import gaozhi.online.peoplety.entity.Message;
import gaozhi.online.peoplety.entity.Token;
import gaozhi.online.peoplety.service.BaseApiRequest;
import gaozhi.online.peoplety.service.NetConfig;
import io.realm.RealmResults;
import io.realm.Sort;

/**
 * 获取消息
 */
public class GetMessageService extends BaseApiRequest<List<Message>> {

    public GetMessageService(OnDataListener<List<Message>> onDataListener) {
        super(NetConfig.userBaseURL, Type.GET);
        setDataListener(onDataListener);
    }

    public void request(Token token) {
        Map<String, String> headers = new HashMap<>();
        headers.put("token", getGson().toJson(token));
        Map<String, String> params = new HashMap<>();
        params.put("toId", "" + token.getUserid());
        request("get/messages", headers, params);
    }

    public void read(Message message) {
        getRealm().executeTransactionAsync(realm -> {
            message.setRead(true);
            realm.copyToRealmOrUpdate(message);
        });
    }

    @Override
    public List<Message> initLocalData(Map<String, String> headers, Map<String, String> params, Object body) {
        return getRealm().where(Message.class).equalTo("toId", Long.parseLong(params.get("toId"))).sort("time", Sort.DESCENDING).findAll();
    }

    @Override
    public void getNetData(Result result, Consumer<List<Message>> consumer) {
        List<Message> messages = getGson().fromJson(result.getData(), new TypeToken<List<Message>>() {
        }.getType());
        getRealm().executeTransaction(realm -> {
            //删除过期缓存
            RealmResults<Message> results = realm.where(Message.class).lessThan("time", System.currentTimeMillis() - cathePeriod).findAll();
            Log.i(getClass().getName(), "delete message: " + results.asJSON());
            results.deleteAllFromRealm();
            realm.copyToRealmOrUpdate(messages);
        });
        consumer.accept(messages);
    }


}
