package gaozhi.online.peoplety.service.record;

import com.github.pagehelper.PageInfo;
import com.google.gson.reflect.TypeToken;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import gaozhi.online.base.net.Result;
import gaozhi.online.base.net.http.DataHelper;
import gaozhi.online.peoplety.entity.Comment;
import gaozhi.online.peoplety.entity.Token;
import gaozhi.online.peoplety.service.BaseApiRequest;
import gaozhi.online.peoplety.service.NetConfig;
import io.realm.Realm;
import io.realm.RealmResults;

/**
 * 获取评论
 */
public class GetCommentByRecordIdService extends BaseApiRequest<PageInfo<Comment>> {
    private static final int PAGE_SIZE = 20;

    public GetCommentByRecordIdService(DataHelper.OnDataListener<PageInfo<Comment>> resultHandler) {
        super(NetConfig.recordBaseURL, Type.GET);
        setDataListener(resultHandler);
    }

    public void request(Token token, long recordId, int pageNum) {
        Map<String, String> headers = new HashMap<>();
        headers.put("token", getGson().toJson(token));
        Map<String, String> params = new HashMap<>();
        params.put("recordId", "" + recordId);
        params.put("pageNum", "" + pageNum);
        params.put("pageSize", "" + PAGE_SIZE);
        request("get/record/comment", headers, params);
    }

    @Override
    public PageInfo<Comment> initLocalData(Map<String, String> headers, Map<String, String> params, Object body) {
        int pageNum = Integer.parseInt(params.get("pageNum"));
        if (pageNum > 1) {
            return null;
        }
        long recordId = Long.parseLong(params.get("recordId"));
        RealmResults<Comment> result = getRealm().where(Comment.class).equalTo("recordId", recordId).findAll();
        return new PageInfo<>(result);
    }

    @Override
    public void getNetData(Result result, Consumer<PageInfo<Comment>> consumer) {
        PageInfo<Comment> commentPageInfo = getGson().fromJson(result.getData(), new TypeToken<PageInfo<Comment>>() {
        }.getType());
        consumer.accept(commentPageInfo);
        if (commentPageInfo.getPageNum() > 1) {
            return;
        }
        getRealm().executeTransactionAsync(realm -> {
            if (commentPageInfo.getList().size() > 0) {
                long recordId = commentPageInfo.getList().get(0).getRecordId();
                RealmResults<Comment> old = realm.where(Comment.class).equalTo("recordId", recordId).findAll();
                for (Comment comment : old) {
                    comment.deleteFromRealm();
                }
            }
            realm.copyToRealmOrUpdate(commentPageInfo.getList());
        });
    }
}
