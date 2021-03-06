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
        if (pageInfo.getPageNum() > 1) {
            consumer.accept(pageInfo);
            return;
        }
        //装入数据库
        getRealm().executeTransactionAsync(realm -> {
            //删除过期缓存
            realm.where(Record.class).lessThan("time", System.currentTimeMillis() - cathePeriod).findAll().deleteAllFromRealm();
            List<Record> records = pageInfo.getList();
            records = realm.copyToRealmOrUpdate(records);
            pageInfo.setList(copyFromRealm(realm,records));
        }, () -> consumer.accept(pageInfo));
    }
}
