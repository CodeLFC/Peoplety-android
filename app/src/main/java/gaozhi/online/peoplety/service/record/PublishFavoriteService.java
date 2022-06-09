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
 * 新建收藏夹
 */
public class PublishFavoriteService extends BaseApiRequest<Favorite> {

    public PublishFavoriteService(OnDataListener<Favorite> onDataListener) {
        super(NetConfig.favoriteBaseURL, Type.POST);
        setDataListener(onDataListener);
    }

    public void request(Token token, Favorite favorite) {
        Map<String, String> headers = new HashMap<>();
        headers.put("token", getGson().toJson(token));
        request("post/favorite", headers, new HashMap<>(), favorite);
    }

    @Override
    public Favorite initLocalData(Map<String, String> headers, Map<String, String> params, Object body) {

        return null;
    }

    @Override
    public void getNetData(Result result, Consumer<Favorite> consumer) {
        Favorite favorite = getGson().fromJson(result.getData(), Favorite.class);
        consumer.accept(favorite);
        getRealm().executeTransactionAsync(realm -> realm.copyToRealmOrUpdate(favorite));
    }
}
