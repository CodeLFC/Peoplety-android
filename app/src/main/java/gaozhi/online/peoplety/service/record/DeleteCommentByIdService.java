package gaozhi.online.peoplety.service.record;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import gaozhi.online.base.net.Result;
import gaozhi.online.peoplety.entity.Comment;
import gaozhi.online.peoplety.entity.Token;
import gaozhi.online.peoplety.service.BaseApiRequest;
import gaozhi.online.peoplety.service.NetConfig;
import io.realm.Realm;

/**
 * 删除评论
 */
public class DeleteCommentByIdService extends BaseApiRequest<Result> {
    public DeleteCommentByIdService(OnDataListener<Result> onDataListener) {
        super(NetConfig.recordBaseURL, Type.DELETE);
        setDataListener(onDataListener);
    }

    public void request(Token token, long recordId) {
        Map<String, String> headers = new HashMap<>();
        headers.put("token", getGson().toJson(token));
        Map<String, String> params = new HashMap<>();
        params.put("id", "" + recordId);
        request("delete/comment", headers, params);
    }

    @Override
    public Result initLocalData(Map<String, String> headers, Map<String, String> params, Object body) {
        long id = Long.parseLong(params.get("id"));
        getRealm().executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.where(Comment.class).equalTo("id", id).findAll().deleteAllFromRealm();
            }
        });
        return null;
    }

    @Override
    public void getNetData(Result result, Consumer<Result> consumer) {
        consumer.accept(result);
    }
}
