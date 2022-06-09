package gaozhi.online.peoplety.service.friend;

import com.github.pagehelper.PageInfo;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import gaozhi.online.base.net.Result;
import gaozhi.online.base.net.http.ApiRequest;
import gaozhi.online.peoplety.entity.Friend;
import gaozhi.online.peoplety.entity.Token;
import gaozhi.online.peoplety.service.BaseApiRequest;
import gaozhi.online.peoplety.service.NetConfig;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * @author LiFucheng
 * @version 1.0
 * @description: TODO 获取粉丝
 * @date 2022/4/13 13:07
 */
public class GetFanService extends BaseApiRequest<PageInfo<Friend>> {
    public GetFanService(OnDataListener<PageInfo<Friend>> resultHandler) {
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
        request("get/fans", headers, params);
    }

    @Override
    public PageInfo<Friend> initLocalData(Map<String, String> headers, Map<String, String> params, Object body) {
        long userid = Long.parseLong(params.get("userid"));
        int pageNum = Integer.parseInt(params.get("pageNum"));
        if (pageNum <= 1) {
            return new PageInfo<>(getRealm().where(Friend.class).equalTo("friendId", userid).findAll());
        }
        return null;
    }

    @Override
    public void getNetData(Result result, Consumer<PageInfo<Friend>> consumer) {
        PageInfo<Friend> pageInfo = getGson().fromJson(result.getData(), new TypeToken<PageInfo<Friend>>() {
        }.getType());
        consumer.accept(pageInfo);
        if (pageInfo.getPageNum() > 1) {
            return;
        }
        //装入数据库
        getRealm().executeTransactionAsync(realm -> {
            if (pageInfo.getList().size() > 0) {//删除这一类的第一页
                realm.delete(Friend.class);
            }
            List<Friend> friends = pageInfo.getList();
            for (Friend friend : friends) {
                realm.copyToRealmOrUpdate(friend);
            }
        });
    }
}
