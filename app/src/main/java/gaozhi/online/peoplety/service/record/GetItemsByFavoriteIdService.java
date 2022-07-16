package gaozhi.online.peoplety.service.record;

import com.github.pagehelper.PageInfo;
import com.google.gson.reflect.TypeToken;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import gaozhi.online.base.net.Result;
import gaozhi.online.peoplety.entity.Item;
import gaozhi.online.peoplety.entity.Token;
import gaozhi.online.peoplety.service.BaseApiRequest;
import gaozhi.online.peoplety.service.NetConfig;
import io.realm.Realm;

/**
 * 获取收藏夹中的收藏内容
 */
public class GetItemsByFavoriteIdService extends BaseApiRequest<PageInfo<Item>> {
    public GetItemsByFavoriteIdService(OnDataListener<PageInfo<Item>> onDataListener) {
        super(NetConfig.favoriteBaseURL, Type.GET);
        setDataListener(onDataListener);
    }

    public void request(Token token, long favoriteId, int pageNum, int pageSize) {
        Map<String, String> headers = new HashMap<>();
        headers.put("token", getGson().toJson(token));
        Map<String, String> params = new HashMap<>();
        params.put("favoriteId", "" + favoriteId);
        params.put("pageNum", "" + pageNum);
        params.put("pageSize", "" + pageSize);
        request("get/favorite/items", headers, params);
    }

    @Override
    public PageInfo<Item> initLocalData(Map<String, String> headers, Map<String, String> params, Object body) {
        long favoriteId = Long.parseLong(params.get("favoriteId"));
        int pageNum = Integer.parseInt(params.get("pageNum"));
        if (pageNum <= 1) {
            return new PageInfo<>(getRealm().where(Item.class).equalTo("favoriteId", favoriteId).findAll());
        }
        return null;
    }

    @Override
    public void getNetData(Result result, Consumer<PageInfo<Item>> consumer) {
        PageInfo<Item> pageInfo = getGson().fromJson(result.getData(), new TypeToken<PageInfo<Item>>() {
        }.getType());
        consumer.accept(pageInfo);
        if (pageInfo.getPageNum() > 1) {
            return;
        }
        getRealm().executeTransactionAsync(realm -> {
            //删除过期缓存
            realm.where(Item.class).lessThan("time", System.currentTimeMillis() - cathePeriod).findAll().deleteAllFromRealm();
            realm.copyToRealmOrUpdate(pageInfo.getList());
        });
    }
}
