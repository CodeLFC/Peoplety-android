package gaozhi.online.peoplety.service.record;


import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import gaozhi.online.base.net.Result;
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

    public void request(Token token, long recordId) {
        request(token, recordId, 1, 1);
    }

    public void request(Token token, long recordId, Integer childPage, Integer commentPage) {
        Map<String, String> headers = new HashMap<>();
        headers.put("token", getGson().toJson(token));
        Map<String, String> params = new HashMap<>();
        params.put("recordId", "" + recordId);
        request("get/record", headers, params);
    }

    @Override
    public RecordDTO initLocalData(Map<String, String> headers, Map<String, String> params, Object body) {
        long recordId = Long.parseLong(params.get("recordId"));
        RecordDTO recordDTO = copyFromRealm(getRealm().where(RecordDTO.class).equalTo("id", recordId).findFirst());
        if (recordDTO == null) return null;
        recordDTO.setRecord(copyFromRealm(recordDTO.getRecord()));
        return recordDTO;
    }

    @Override
    public void getNetData(Result result, Consumer<RecordDTO> consumer) {
        final RecordDTO recordDTO = getGson().fromJson(result.getData(), RecordDTO.class);
        recordDTO.setTime(recordDTO.getRecord() == null ? 0 : recordDTO.getRecord().getTime());
        getRealm().executeTransaction(realm -> {
            if (recordDTO.getRecord() != null && !recordDTO.getRecord().isEnable()) {
                //删除
                realm.where(Comment.class).equalTo("recordId", recordDTO.getRecord().getId()).findAll().deleteAllFromRealm();
                realm.where(Record.class).equalTo("id", recordDTO.getRecord().getId()).findAll().deleteAllFromRealm();
                realm.where(RecordDTO.class).equalTo("id",recordDTO.getId()).findAll().deleteAllFromRealm();
            } else {
                realm.copyToRealmOrUpdate(recordDTO);
            }
            //删除过期缓存
            realm.where(RecordDTO.class).lessThan("time", System.currentTimeMillis() - cathePeriod).findAll().deleteAllFromRealm();
        });
        RecordDTO record = copyFromRealm(recordDTO);
        record.setRecord(copyFromRealm(record.getRecord()));
        consumer.accept(record);
    }
}
