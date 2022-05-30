package gaozhi.online.peoplety.service.constant;

import java.util.HashMap;
import java.util.Map;

import gaozhi.online.base.net.Result;
import gaozhi.online.peoplety.entity.IPInfo;
import gaozhi.online.peoplety.entity.IPInfoDB;
import gaozhi.online.peoplety.service.BaseApiRequest;
import gaozhi.online.peoplety.service.NetConfig;

/**
 * 获取IP信息
 */
public class GetIPInfoService extends BaseApiRequest<IPInfo> {
    public GetIPInfoService(OnDataListener<IPInfo> onDataListener) {
        super(NetConfig.recordConstantBaseURL, Type.GET);
        setDataListener(onDataListener);
    }

    public void request(String ip) {
        Map<String, String> params = new HashMap<>();
        params.put("ip", ip);
        request("get/ip_info", params);
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
    public IPInfo getNetData(Result result) {
        IPInfo ipInfo = getGson().fromJson(result.getData(), IPInfo.class);
        getRealm().executeTransactionAsync(realm -> realm.copyToRealmOrUpdate(new IPInfoDB().setIPInfo(ipInfo)));
        return ipInfo;
    }
}
