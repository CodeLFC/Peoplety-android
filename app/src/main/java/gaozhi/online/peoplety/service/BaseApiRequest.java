package gaozhi.online.peoplety.service;

import gaozhi.online.base.net.http.ApiRequest;
import io.realm.Realm;

public abstract class BaseApiRequest<T> extends ApiRequest<T> {
    protected Realm getRealm() {
        return Realm.getDefaultInstance();
    }

    public BaseApiRequest(String baseURL, Type type) {
        super(baseURL, type);
    }
}
