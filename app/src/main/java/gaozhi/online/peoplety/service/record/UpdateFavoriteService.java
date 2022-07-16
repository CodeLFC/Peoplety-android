package gaozhi.online.peoplety.service.record;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import gaozhi.online.base.net.Result;
import gaozhi.online.peoplety.entity.Favorite;
import gaozhi.online.peoplety.entity.Token;
import gaozhi.online.peoplety.service.BaseApiRequest;
import gaozhi.online.peoplety.service.NetConfig;
import io.realm.Realm;

/**
 * 修改收藏夹
 */
public class UpdateFavoriteService extends BaseApiRequest<Favorite> {
    public UpdateFavoriteService(OnDataListener<Favorite> onDataListener) {
        super(NetConfig.favoriteBaseURL, Type.PUT);
        setDataListener(onDataListener);
    }

    public void request(Token token, Favorite favorite) {
        Map<String, String> headers = new HashMap<>();
        headers.put("token", getGson().toJson(token));
        request("put/favorite", headers, new HashMap<>(), favorite);
    }

    @Override
    public Favorite initLocalData(Map<String, String> headers, Map<String, String> params, Object body) {
        return null;
    }

    @Override
    public void getNetData(Result result, Consumer<Favorite> consumer) {
        Favorite favorite = getGson().fromJson(result.getData(), Favorite.class);
        consumer.accept(favorite);
        getRealm().executeTransaction(realm -> realm.copyToRealmOrUpdate(favorite));
        consumer.accept(copyFromRealm(favorite));
    }
}
