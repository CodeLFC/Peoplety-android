package gaozhi.online.peoplety.service.record;

import android.util.Log;

import com.github.pagehelper.PageInfo;
import com.google.gson.reflect.TypeToken;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import gaozhi.online.base.net.Result;
import gaozhi.online.base.net.http.DataHelper;
import gaozhi.online.peoplety.entity.Comment;
import gaozhi.online.peoplety.entity.Record;
import gaozhi.online.peoplety.entity.Token;
import gaozhi.online.peoplety.service.BaseApiRequest;
import gaozhi.online.peoplety.service.NetConfig;
import io.realm.Realm;
import io.realm.RealmResults;

/**
 * 根据地区获取卷宗列表
 */
public class GetRecordByAreaService extends BaseApiRequest<PageInfo<Record>> {
    public GetRecordByAreaService(DataHelper.OnDataListener<PageInfo<Record>> resultHandler) {
        super(NetConfig.recordBaseURL, Type.GET);
        setDataListener(resultHandler);
    }

    public void request(Token token, int areaId, List<Integer> selectedLabel, int pageNum, int pageSize) {
        Map<String, String> headers = new HashMap<>();
        headers.put("token", getGson().toJson(token));
        Map<String, String> params = new HashMap<>();
        params.put("areaId", "" + areaId);
        params.put("pageNum", "" + pageNum);
        params.put("pageSize", "" + pageSize);
        params.put("selectedTypes", getGson().toJson(selectedLabel));
        request("get/area/records", headers, params);
    }

    @Override
    public PageInfo<Record> initLocalData(Map<String, String> headers, Map<String, String> params, Object body) {
        int areaId = Integer.parseInt(params.get("areaId"));
        int pageNum = Integer.parseInt(params.get("pageNum"));
        String selectedTypes = params.get("selectedTypes");
        List<Integer> selectedLabel = getGson().fromJson(selectedTypes, new TypeToken<List<Integer>>() {
        }.getType());
        if (pageNum <= 1)
            return new PageInfo<>(getRealm().where(Record.class).equalTo("areaId", areaId).in("recordTypeId", selectedLabel.toArray(new Integer[]{})).findAll());
        return null;
    }

    @Override
    public void getNetData(Result result, Consumer<PageInfo<Record>> consumer) {
        PageInfo<Record> pageInfo = getGson().fromJson(result.getData(), new TypeToken<PageInfo<Record>>() {
        }.getType());
        consumer.accept(pageInfo);

        if (pageInfo.getPageNum() > 1) {
            return;
        }
        //装入数据库
        getRealm().executeTransactionAsync(realm -> {
            if (pageInfo.getList().size() > 0) {//删除这一类的第一页
                int areaId = pageInfo.getList().get(0).getAreaId();
                RealmResults<Record> old =realm.where(Record.class).equalTo("areaId", areaId).findAll();
                for (Record record : old) {
                    Log.i(getClass().getName(),"删除卷宗内容："+record);
                    record.deleteFromRealm();
                }
            }
            List<Record> records = pageInfo.getList();
            realm.copyToRealmOrUpdate(records);
        });
    }
}
