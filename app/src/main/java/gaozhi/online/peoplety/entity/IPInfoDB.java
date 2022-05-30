package gaozhi.online.peoplety.entity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.LinkedList;
import java.util.List;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import lombok.Data;

@Data
public class IPInfoDB extends RealmObject {
    private String ret;
    @PrimaryKey
    private String ip;
    private String data;
    private String msg;

    public IPInfoDB setIPInfo(IPInfo ipInfo) {
        if (ipInfo == null) return this;
        ret = ipInfo.getRet();
        ip = ipInfo.getIp();
        data = new Gson().toJson(ipInfo.getData());
        msg = ipInfo.getMsg();
        return this;
    }

    public IPInfo getIPInfo() {
        IPInfo ipInfo = new IPInfo();
        ipInfo.setIp(ip);
        ipInfo.setMsg(msg);
        ipInfo.setRet(ret);
        ipInfo.setData(new Gson().fromJson(data, new TypeToken<List<String>>() {
        }.getType()));
        if(ipInfo.getData() == null){
            ipInfo.setData(new LinkedList<>());
        }
        return ipInfo;
    }
}