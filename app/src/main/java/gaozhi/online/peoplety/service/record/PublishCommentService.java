package gaozhi.online.peoplety.service.record;

import java.util.HashMap;
import java.util.Map;

import gaozhi.online.base.net.Result;
import gaozhi.online.peoplety.entity.Comment;
import gaozhi.online.peoplety.entity.Token;
import gaozhi.online.peoplety.service.BaseApiRequest;
import gaozhi.online.peoplety.service.NetConfig;

/**
 * 发布评论
 */
public class PublishCommentService extends BaseApiRequest<Comment> {
    public PublishCommentService(OnDataListener<Comment> onDataListener) {
        super(NetConfig.recordBaseURL, Type.POST);
        setDataListener(onDataListener);
    }

    @Override
    public Comment initLocalData(Map<String, String> headers, Map<String, String> params, Object body) {

        return null;
    }

    @Override
    public Comment getNetData(Result result) {
        Comment comment = getGson().fromJson(result.getData(),Comment.class);
        if(comment == null)return null;
        getRealm().executeTransactionAsync(realm -> realm.copyToRealmOrUpdate(comment));
        return comment;
    }

    public void request(Token token,Comment comment){
        Map<String, String> headers = new HashMap<>();
        headers.put("token", getGson().toJson(token));
        request("post/comment",headers,null,comment);
    }
}
