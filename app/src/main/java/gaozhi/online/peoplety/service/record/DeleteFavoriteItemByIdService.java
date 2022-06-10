package gaozhi.online.peoplety.service.record;

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
 * 删除收藏条目
 */
public class DeleteFavoriteItemByIdService extends BaseApiRequest<Result> {
    private long favoriteItemId;

    public DeleteFavoriteItemByIdService(OnDataListener<Result> onDataListener) {
        super(NetConfig.favoriteBaseURL, Type.DELETE);
        setDataListener(onDataListener);
    }

    public void request(Token token, long favoriteItemId) {
        this.favoriteItemId = favoriteItemId;
        Map<String, String> headers = new HashMap<>();
        headers.put("token", getGson().toJson(token));
        Map<String, String> params = new HashMap<>();
        params.put("id", "" + favoriteItemId);
        request("delete/favorite/item", headers, params);
    }

    @Override
    public Result initLocalData(Map<String, String> headers, Map<String, String> params, Object body) {
        return null;
    }

    @Override
    public void getNetData(Result result, Consumer<Result> consumer) {
        consumer.accept(result);
        getRealm().executeTransactionAsync(realm -> {
            Item item = getRealm().where(Item.class).equalTo("id", favoriteItemId).findFirst();
            if (item == null) return;
            item.deleteFromRealm();
        });
    }
}
