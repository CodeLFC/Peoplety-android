package gaozhi.online.peoplety.service.record;

import com.github.pagehelper.PageInfo;
import com.google.gson.reflect.TypeToken;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import gaozhi.online.base.net.Result;
import gaozhi.online.peoplety.entity.Favorite;
import gaozhi.online.peoplety.entity.Token;
import gaozhi.online.peoplety.service.BaseApiRequest;
import gaozhi.online.peoplety.service.NetConfig;

/**
 * 获取收藏夹
 */
public class GetFavoritesByUseridService extends BaseApiRequest<PageInfo<Favorite>> {


    public GetFavoritesByUseridService(OnDataListener<PageInfo<Favorite>> onDataListener) {
        super(NetConfig.favoriteBaseURL, Type.GET);
        setDataListener(onDataListener);
    }

    public void request(Token token, long userid, int pageNum, int pageSize) {
        Map<String, String> headers = new HashMap<>();
        headers.put("token", getGson().toJson(token));
        Map<String, String> params = new HashMap<>();
        params.put("userid", "" + userid);
        params.put("pageNum", "" + pageNum);
        params.put("pageSize", "" + pageSize);
        request("get/user/favorites", headers, params);
    }

    @Override
    public PageInfo<Favorite> initLocalData(Map<String, String> headers, Map<String, String> params, Object body) {
        long userid = Long.parseLong(params.get("userid"));
        int pageNum = Integer.parseInt(params.get("pageNum"));
        if (pageNum <= 1) {
            return new PageInfo<>(getRealm().where(Favorite.class).equalTo("userid", userid).findAll());
        }
        return null;
    }

    @Override
    public void getNetData(Result result, Consumer<PageInfo<Favorite>> consumer) {
        PageInfo<Favorite> pageInfo = getGson().fromJson(result.getData(), new TypeToken<PageInfo<Favorite>>() {
        }.getType());

        if (pageInfo.getPageNum() > 1) {
            consumer.accept(pageInfo);
            return;
        }
        //装入数据库
        getRealm().executeTransaction(realm -> {
            //删除过期缓存
            if (realm.where(Favorite.class).findAll().size() > MIN_SIZE)
                realm.where(Favorite.class).lessThan("time", System.currentTimeMillis() - cathePeriod).findAll().deleteAllFromRealm();
            List<Favorite> favorites = pageInfo.getList();
            favorites = realm.copyToRealmOrUpdate(favorites);
            pageInfo.setList(copyFromRealm(realm, favorites));
        });
        consumer.accept(pageInfo);
    }
}
