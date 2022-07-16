package gaozhi.online.peoplety.service.record;


import com.github.pagehelper.PageInfo;
import com.google.gson.reflect.TypeToken;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import gaozhi.online.base.net.Result;
import gaozhi.online.peoplety.entity.Friend;
import gaozhi.online.peoplety.entity.Record;
import gaozhi.online.peoplety.entity.Token;
import gaozhi.online.peoplety.service.BaseApiRequest;
import gaozhi.online.peoplety.service.NetConfig;
import io.realm.Realm;

/**
 * 获取关注的人发布的内容 - 未完成
 */
public class GetAttentionRecordByUseridService extends BaseApiRequest<PageInfo<Record>> {
    private long userid;

    public GetAttentionRecordByUseridService(OnDataListener<PageInfo<Record>> onDataListener) {
        super(NetConfig.recordBaseURL, Type.GET);
        setDataListener(onDataListener);
    }

    public void request(Token token, int pageNum, int pageSize) {
        Map<String, String> headers = new HashMap<>();
        headers.put("token", getGson().toJson(token));
        Map<String, String> params = new HashMap<>();
        params.put("userid_local", "" + token.getUserid());
        params.put("pageNum", "" + pageNum);
        params.put("pageSize", "" + pageSize);
        request("get/attention/records", headers, params);
    }

    @Override
    public PageInfo<Record> initLocalData(Map<String, String> headers, Map<String, String> params, Object body) {
        //联表查询
        userid = Long.parseLong(params.get("userid_local"));
        List<Friend> friends = getRealm().where(Friend.class).equalTo("userid", userid).findAll();
        List<Long> friendIds = new LinkedList<>();
        for (Friend friend : friends) {
            friendIds.add(friend.getFriendId());
        }
        List<Record> records = getRealm().where(Record.class).in("userid", friendIds.toArray(new Long[]{})).findAll();
        return new PageInfo<>(records);
    }

    @Override
    public void getNetData(Result result, Consumer<PageInfo<Record>> consumer) {
        PageInfo<Record> pageInfo = getGson().fromJson(result.getData(), new TypeToken<PageInfo<Record>>() {
        }.getType());
        if (pageInfo.getPageNum() > 1) {
            consumer.accept(pageInfo);
            return;
        }
        //装入数据库
        getRealm().executeTransaction(realm -> {
            //删除过期缓存
            realm.where(Record.class).lessThan("time", System.currentTimeMillis() - cathePeriod).findAll().deleteAllFromRealm();
            List<Record> records = pageInfo.getList();
            records = realm.copyToRealmOrUpdate(records);
            pageInfo.setList(copyFromRealm(realm,records));
        });
        consumer.accept(pageInfo);
    }
}
