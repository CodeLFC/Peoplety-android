package gaozhi.online.peoplety.service.constant;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import gaozhi.online.base.net.Result;
import gaozhi.online.peoplety.entity.IPInfo;
import gaozhi.online.peoplety.entity.IPInfoDB;
import gaozhi.online.peoplety.entity.Token;
import gaozhi.online.peoplety.service.BaseApiRequest;
import gaozhi.online.peoplety.service.NetConfig;
import io.realm.Realm;

/**
 * 获取IP信息
 */
public class GetIPInfoService extends BaseApiRequest<IPInfo> {
    public GetIPInfoService(OnDataListener<IPInfo> onDataListener) {
        super(NetConfig.recordConstantBaseURL, Type.GET);
        setDataListener(onDataListener);
    }

    public void request(Token token, String ip) {
        Map<String, String> headers = new HashMap<>();
        headers.put("token", getGson().toJson(token));
        Map<String, String> params = new HashMap<>();
        params.put("ip", ip);
        request("get/ip_info", headers,params);
    }

    @Override
    public IPInfo initLocalData(Map<String, String> headers, Map<String, String> params, Object body) {
        String ip = params.get("ip");
        IPInfoDB ipInfo = getRealm().where(IPInfoDB.class).equalTo("ip", ip).findFirst();
        if (ipInfo == null) {
            ipInfo = new IPInfoDB();
            ipInfo.setIp(ip);
        }
        return ipInfo.getIPInfo();
    }

    @Override
    public void getNetData(Result result, Consumer<IPInfo> consumer) {
        IPInfo ipInfo = getGson().fromJson(result.getData(), IPInfo.class);
        getRealm().executeTransaction(realm -> realm.copyToRealmOrUpdate(new IPInfoDB().setIPInfo(ipInfo)));
        consumer.accept(ipInfo);
    }
}
