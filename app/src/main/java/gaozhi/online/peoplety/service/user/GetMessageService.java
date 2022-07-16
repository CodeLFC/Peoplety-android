package gaozhi.online.peoplety.service.user;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import gaozhi.online.base.net.Result;
import gaozhi.online.peoplety.entity.Message;
import gaozhi.online.peoplety.entity.Token;
import gaozhi.online.peoplety.service.BaseApiRequest;
import io.realm.Realm;
import io.realm.Sort;

/**
 * 获取消息
 */
public class GetMessageService extends BaseApiRequest<List<Message>> {

    public GetMessageService(String baseURL, Type type) {
        super(baseURL, type);
    }

    public void request(Token token){
        Map<String, String> headers = new HashMap<>();
        headers.put("token", getGson().toJson(token));
        Map<String, String> params = new HashMap<>();
        params.put("toId",""+token.getUserid());
        request("get/messages", headers, params);
    }
    @Override
    public List<Message> initLocalData(Map<String, String> headers, Map<String, String> params, Object body) {
        getRealm().executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                //删除过期缓存
                realm.where(Message.class).lessThan("time",System.currentTimeMillis() - cathePeriod).findAll().deleteAllFromRealm();
            }
        });
        return getRealm().where(Message.class).equalTo("toId",Long.parseLong(params.get("toId"))).sort("time", Sort.DESCENDING).findAll();
    }

    @Override
    public void getNetData(Result result, Consumer<List<Message>> consumer) {

    }


}
