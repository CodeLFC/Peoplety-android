package gaozhi.online.peoplety.service.record;

import com.github.pagehelper.PageInfo;
import com.google.gson.reflect.TypeToken;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import gaozhi.online.base.net.Result;
import gaozhi.online.base.net.http.DataHelper;
import gaozhi.online.peoplety.entity.Record;
import gaozhi.online.peoplety.entity.Token;
import gaozhi.online.peoplety.service.BaseApiRequest;
import gaozhi.online.peoplety.service.NetConfig;
import io.realm.RealmResults;

/**
 * 获取子卷宗列表
 */
public class GetChildByRecordIdService extends BaseApiRequest<PageInfo<Record>> {

    public GetChildByRecordIdService(DataHelper.OnDataListener<PageInfo<Record>> resultHandler) {
        super(NetConfig.recordBaseURL, Type.GET);
        setDataListener(resultHandler);
    }

    public void request(Token token, long recordId, int pageNum) {
        Map<String, String> headers = new HashMap<>();
        headers.put("token", getGson().toJson(token));
        Map<String, String> params = new HashMap<>();
        params.put("recordId", "" + recordId);
        params.put("pageNum", "" + pageNum);
        params.put("pageSize", "" + 10);
        request("get/record/child", headers, params);
    }

    @Override
    public PageInfo<Record> initLocalData(Map<String, String> headers, Map<String, String> params, Object body) {
        int recordId = Integer.parseInt(params.get("recordId"));
        int pageNum = Integer.parseInt(params.get("pageNum"));
        if (pageNum <= 1)
            return new PageInfo<>(getRealm().where(Record.class).equalTo("parentId", recordId).equalTo("enable", true).findAll());
        return null;
    }

    @Override
    public void getNetData(Result result, Consumer<PageInfo<Record>> consumer) {
        PageInfo<Record> pageInfo = getGson().fromJson(result.getData(), new TypeToken<PageInfo<Record>>() {
        }.getType());
        consumer.accept(pageInfo);
        //装入数据库
        if (pageInfo.getPageNum() > 1) {
            return;
        }
        getRealm().executeTransactionAsync(realm -> {
            if (pageInfo.getList().size() > 0) {//删除这一类的第一页
                long parentId = pageInfo.getList().get(0).getParentId();
                RealmResults<Record> old = getRealm().where(Record.class).equalTo("parentId", parentId).findAll();
                for (Record record : old) {
                    record.deleteFromRealm();
                }
            }
            List<Record> records = pageInfo.getList();
            realm.copyToRealmOrUpdate(records);
        });
    }
}
