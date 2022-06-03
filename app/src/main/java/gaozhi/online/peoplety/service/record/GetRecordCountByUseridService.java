package gaozhi.online.peoplety.service.record;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import gaozhi.online.base.net.Result;
import gaozhi.online.peoplety.entity.Token;
import gaozhi.online.peoplety.entity.UserRecordCount;
import gaozhi.online.peoplety.service.BaseApiRequest;
import gaozhi.online.peoplety.service.NetConfig;

/**
 * 获取一些统计数据
 */
public class GetRecordCountByUseridService extends BaseApiRequest<UserRecordCount> {
    public GetRecordCountByUseridService(OnDataListener<UserRecordCount> onDataListener) {
        super(NetConfig.recordBaseURL, Type.GET);
        setDataListener(onDataListener);
    }

    public void request(Token token, long userid) {
        Map<String, String> headers = new HashMap<>();
        headers.put("token", getGson().toJson(token));
        Map<String, String> params = new HashMap<>();
        params.put("userid", "" + userid);
        request("get/count", headers, params);
    }

    @Override
    public UserRecordCount initLocalData(Map<String, String> headers, Map<String, String> params, Object body) {
        long userid = Long.parseLong(params.get("userid"));
        return getRealm().where(UserRecordCount.class).equalTo("userid", userid).findFirst();
    }

    @Override
    public void getNetData(Result result, Consumer<UserRecordCount> consumer) {
        consumer.accept(getGson().fromJson(result.getData(), UserRecordCount.class));
    }
}
