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
 * 添加收藏内容
 */
public class PublishFavoriteItemService extends BaseApiRequest<Item> {
    public PublishFavoriteItemService(OnDataListener<Item> onDataListener) {
        super(NetConfig.favoriteBaseURL, Type.POST);
        setDataListener(onDataListener);
    }

    public void request(Token token, Item item) {
        Map<String, String> headers = new HashMap<>();
        headers.put("token", getGson().toJson(token));
        request("post/favorite/item", headers, null, item);
    }

    @Override
    public Item initLocalData(Map<String, String> headers, Map<String, String> params, Object body) {
        return null;
    }

    @Override
    public void getNetData(Result result, Consumer<Item> consumer) {
        Item item = getGson().fromJson(result.getData(), Item.class);
        getRealm().executeTransaction(realm -> realm.copyToRealmOrUpdate(item));
        consumer.accept(copyFromRealm(item));
    }
}
