package gaozhi.online.peoplety.service.record;

import com.github.pagehelper.PageInfo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import gaozhi.online.base.net.Result;
import gaozhi.online.base.net.http.ApiRequest;
import gaozhi.online.peoplety.entity.Comment;
import gaozhi.online.peoplety.entity.Record;
import gaozhi.online.peoplety.entity.Token;
import gaozhi.online.peoplety.entity.dto.RecordDTO;
import gaozhi.online.peoplety.service.BaseApiRequest;
import gaozhi.online.peoplety.service.NetConfig;
import io.realm.Realm;

/**
 * 获取卷宗详情
 */
public class GetRecordDTOByIdService extends BaseApiRequest<RecordDTO> {

    public GetRecordDTOByIdService(OnDataListener<RecordDTO> dataListener) {
        super(NetConfig.recordBaseURL, Type.GET);
        setDataListener(dataListener);
    }
    public void request(Token token,long recordId){
        request(token,recordId,1,1);
    }
    public void request(Token token,long recordId,Integer childPage,  Integer commentPage){
        Map<String, String> headers = new HashMap<>();
        headers.put("token", getGson().toJson(token));
        Map<String, String> params = new HashMap<>();
        params.put("recordId", "" + recordId);
        params.put("childPage", "" + childPage);
        params.put("commentPage", "" + commentPage);
        request("get/record", headers, params);
    }
    @Override
    public RecordDTO initLocalData(Map<String, String> headers, Map<String, String> params, Object body) {
        long recordId = Long.parseLong(params.get("recordId"));
        RecordDTO recordDTO = new RecordDTO();
        recordDTO.setRecord(getRealm().where(Record.class).equalTo("id",recordId).findFirst());
        Record record = recordDTO.getRecord();
        if(record ==null){
            return recordDTO;
        }
        recordDTO.setParent(getRealm().where(Record.class).equalTo("id",record.getParentId()).findFirst());
        recordDTO.setChildPageInfo(new PageInfo<>(getRealm().where(Record.class).equalTo("parentId",record.getId()).findAll()));
        recordDTO.setCommentPageInfo(new PageInfo<>(getRealm().where(Comment.class).equalTo("recordId",record.getId()).findAll()));
        return recordDTO;
    }

    @Override
    public RecordDTO getNetData(Result result) {
        RecordDTO recordDTO = getGson().fromJson(result.getData(), RecordDTO.class);
        getRealm().executeTransactionAsync(realm -> {
           Record record =  recordDTO.getRecord();
           Record parent = recordDTO.getParent();
           List<Record> records =recordDTO.getChildPageInfo().getList();
           List<Comment> comments =recordDTO.getCommentPageInfo().getList();
           if(record!=null)
           realm.copyToRealmOrUpdate(record);
           if(parent!=null)
           realm.copyToRealmOrUpdate(parent);
           //子
           for(Record child:records){
               if(child!=null){
                   realm.copyToRealmOrUpdate(child);
               }
           }
           //评论
           for(Comment comment:comments){
               if(comment!=null){
                   realm.copyToRealmOrUpdate(comment);
               }
           }
        });
        return recordDTO;
    }
}
