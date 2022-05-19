package gaozhi.online.peoplety.service.record;

import com.github.pagehelper.PageInfo;
import com.google.gson.reflect.TypeToken;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import gaozhi.online.base.net.Result;
import gaozhi.online.base.net.http.DataHelper;
import gaozhi.online.peoplety.entity.Comment;
import gaozhi.online.peoplety.entity.Record;
import gaozhi.online.peoplety.entity.Token;
import gaozhi.online.peoplety.service.BaseApiRequest;
import gaozhi.online.peoplety.service.NetConfig;

/**
 * 根据地区获取卷宗列表
 */
public class GetRecordByAreaService extends BaseApiRequest<PageInfo<Record>> {
    public GetRecordByAreaService(DataHelper.OnDataListener<PageInfo<Record>> resultHandler) {
        super(NetConfig.recordBaseURL, Type.GET);
        setDataListener(resultHandler);
    }

    public void request(Token token, int areaId, int pageNum, int pageSize) {
        Map<String, String> headers = new HashMap<>();
        headers.put("token", getGson().toJson(token));
        Map<String, String> params = new HashMap<>();
        params.put("areaId", "" + areaId);
        params.put("pageNum", "" + pageNum);
        params.put("pageSize", "" + pageSize);
        request("get/area/records", headers, params);
    }

    @Override
    public PageInfo<Record> initLocalData(Map<String, String> headers, Map<String, String> params, Object body) {
        int areaId = Integer.parseInt(params.get("pageSize"));
        return new PageInfo<>(getRealm().where(Record.class).equalTo("areaId", areaId).findAll());
    }

    @Override
    public PageInfo<Record> getNetData(Result result) {
        PageInfo<Record> pageInfo = getGson().fromJson(result.getData(), new TypeToken<PageInfo<Record>>() {
        }.getType());
        if (pageInfo.getPageNum() <= 1) {
            //装入数据库
            getRealm().executeTransactionAsync(realm -> {
                realm.delete(Record.class);
                realm.delete(Comment.class);
                List<Record> records = pageInfo.getList();
                for (Record record : records) {
                    realm.copyToRealmOrUpdate(record);
                }
            });
        }
        return pageInfo;
    }
}
