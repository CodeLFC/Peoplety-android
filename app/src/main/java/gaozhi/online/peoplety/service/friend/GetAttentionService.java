package gaozhi.online.peoplety.service.friend;

import com.github.pagehelper.PageInfo;
import com.google.gson.reflect.TypeToken;

import gaozhi.online.base.net.Result;
import gaozhi.online.peoplety.entity.Friend;
import gaozhi.online.peoplety.entity.Token;
import gaozhi.online.peoplety.service.BaseApiRequest;
import gaozhi.online.peoplety.service.NetConfig;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * @author LiFucheng
 * @version 1.0
 * @description: TODO 获取关注关系
 * @date 2022/4/13 10:22
 */
public class GetAttentionService extends BaseApiRequest<PageInfo<Friend>> {
    public GetAttentionService(OnDataListener<PageInfo<Friend>> resultHandler) {
        super(NetConfig.friendBaseURL, Type.GET);
        setDataListener(resultHandler);
    }

    public void request(Token token, long userid, int pageNum, int pageSize) {
        Map<String, String> headers = new HashMap<>();
        headers.put("token", getGson().toJson(token));
        Map<String, String> params = new HashMap<>();
        params.put("userid", "" + userid);
        params.put("pageNum", "" + pageNum);
        params.put("pageSize", "" + pageSize);
        request("get/attentions", headers, params);
    }

    @Override
    public PageInfo<Friend> initLocalData(Map<String, String> headers, Map<String, String> params, Object body) {
        long userid = Long.parseLong(params.get("userid"));
        int pageNum = Integer.parseInt(params.get("pageNum"));
        if (pageNum <= 1) {
            return new PageInfo<>(getRealm().where(Friend.class).equalTo("userid", userid).findAll());
        }
        return null;
    }

    @Override
    public void getNetData(Result result, Consumer<PageInfo<Friend>> consumer) {
        PageInfo<Friend> pageInfo = getGson().fromJson(result.getData(), new TypeToken<PageInfo<Friend>>() {
        }.getType());
        if (pageInfo.getPageNum() > 1) {
            consumer.accept(pageInfo);
            return;
        }

        //装入数据库
        getRealm().executeTransaction(realm -> {
            //删除过期缓存
            if (realm.where(Friend.class).findAll().size() > MIN_SIZE)
                realm.where(Friend.class).lessThan("time", System.currentTimeMillis() - cathePeriod).findAll().deleteAllFromRealm();
            List<Friend> friends = pageInfo.getList();
            realm.copyToRealmOrUpdate(friends);
        });
        consumer.accept(pageInfo);
    }
}
