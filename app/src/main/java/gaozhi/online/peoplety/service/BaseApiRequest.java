package gaozhi.online.peoplety.service;

import gaozhi.online.base.net.http.ApiRequest;
import io.realm.Realm;

public abstract class BaseApiRequest<T> extends ApiRequest<T> {
    //一年缓存期
    protected static final long cathePeriod = 1000*60*60*24*365;

    protected Realm getRealm() {
        return Realm.getDefaultInstance();
    }

    public BaseApiRequest(String baseURL, Type type) {
        super(baseURL, type);
    }
}
