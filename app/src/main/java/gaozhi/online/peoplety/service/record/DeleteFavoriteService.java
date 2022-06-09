package gaozhi.online.peoplety.service.record;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import gaozhi.online.base.net.Result;
import gaozhi.online.peoplety.entity.Favorite;
import gaozhi.online.peoplety.entity.Token;
import gaozhi.online.peoplety.service.BaseApiRequest;
import gaozhi.online.peoplety.service.NetConfig;

/**
 * 删除收藏夹
 */
public class DeleteFavoriteService extends BaseApiRequest<Result> {

    private long favoriteId;

    public DeleteFavoriteService(OnDataListener<Result> onDataListener) {
        super(NetConfig.favoriteBaseURL, Type.DELETE);
        setDataListener(onDataListener);
    }


    public void request(Token token, long favoriteId) {
        this.favoriteId = favoriteId;
        Map<String, String> headers = new HashMap<>();
        headers.put("token", getGson().toJson(token));
        Map<String, String> params = new HashMap<>();
        params.put("id", "" + favoriteId);
        request("delete/favorite", headers, params);
    }

    @Override
    public Result initLocalData(Map<String, String> headers, Map<String, String> params, Object body) {

        return null;
    }

    @Override
    public void getNetData(Result result, Consumer<Result> consumer) {
        consumer.accept(result);
        getRealm().executeTransactionAsync(realm -> {
            Favorite favorite = realm.where(Favorite.class).equalTo("id", favoriteId).findFirst();
            if (favorite != null) {
                favorite.deleteFromRealm();
            }
        });
    }
}
