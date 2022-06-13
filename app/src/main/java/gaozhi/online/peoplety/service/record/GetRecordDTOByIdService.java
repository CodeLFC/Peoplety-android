package gaozhi.online.peoplety.service.record;

import com.github.pagehelper.PageInfo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

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
    private long recordId;

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
        recordId = Long.parseLong(params.get("recordId"));
        return getRealm().where(RecordDTO.class).equalTo("id", recordId).findFirst();
    }

    @Override
    public void getNetData(Result result, Consumer<RecordDTO> consumer) {
        final RecordDTO recordDTO = getGson().fromJson(result.getData(), RecordDTO.class);
        consumer.accept(recordDTO);
        getRealm().executeTransactionAsync(realm -> {
            if (recordDTO.getRecord() == null) {
                RecordDTO temp = realm.where(RecordDTO.class).equalTo("id", recordId).findFirst();
                if (temp != null)
                    temp.deleteFromRealm();
            }
            realm.copyToRealmOrUpdate(recordDTO);
        });
    }
}
