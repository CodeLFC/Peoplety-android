package gaozhi.online.peoplety.service.friend;

import com.github.pagehelper.PageInfo;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import gaozhi.online.base.net.Result;
import gaozhi.online.base.net.http.ApiRequest;
import gaozhi.online.peoplety.entity.Friend;
import gaozhi.online.peoplety.entity.Token;
import gaozhi.online.peoplety.service.NetConfig;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.function.Consumer;

/**
 * @author LiFucheng
 * @version 1.0
 * @description: TODO 获取关注关系
 * @date 2022/4/13 10:22
 */
public class GetAttentionService extends ApiRequest<PageInfo<Friend>> {
    public GetAttentionService(OnDataListener<PageInfo<Friend>> resultHandler) {
        super(NetConfig.friendBaseURL, Type.GET);
        setDataListener(resultHandler);
    }

    public void request(Token token, int pageNum, int pageSize) {
        Map<String, String> headers = new HashMap<>();
        headers.put("token", getGson().toJson(token));
        Map<String, String> params = new HashMap<>();
        params.put("pageNum", "" + pageNum);
        params.put("pageSize", "" + pageSize);
        request("get/attentions", headers, params);
    }

    @Override
    public PageInfo<Friend> initLocalData(Map<String, String> headers, Map<String, String> params, Object body) {
        return new PageInfo<>(new LinkedList<>());
    }

    @Override
    public void getNetData(Result result, Consumer<PageInfo<Friend>> consumer) {
        consumer.accept(getGson().fromJson(result.getData(), new TypeToken<PageInfo<Friend>>() {
        }.getType()));
    }
}
