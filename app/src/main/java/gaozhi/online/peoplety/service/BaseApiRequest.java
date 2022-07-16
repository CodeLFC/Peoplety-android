package gaozhi.online.peoplety.service;

import android.util.Log;

import java.util.LinkedList;
import java.util.List;

import gaozhi.online.base.net.http.ApiRequest;
import gaozhi.online.peoplety.entity.UserRecordCount;
import io.realm.Realm;
import io.realm.RealmObject;

public abstract class BaseApiRequest<T> extends ApiRequest<T> {
    //一年缓存期
    protected static final long cathePeriod = 1000L * 60 * 60 * 24 * 30;

    protected Realm getRealm() {
        return Realm.getDefaultInstance();
    }

    public BaseApiRequest(String baseURL, Type type) {
        super(baseURL, type);
    }

    protected <V extends RealmObject> List<V> copyFromRealm(List<V> value) {
        List<V> result = new LinkedList<>();
        for (V e : value) {
            result.add(copyFromRealm(getRealm(), e));
        }
        return result;
    }

    protected <V extends RealmObject> List<V> copyFromRealm(Realm realm, List<V> value) {
        List<V> result = new LinkedList<>();
        for (V e : value) {
            result.add(copyFromRealm(realm, e));
        }
        return result;
    }

    protected <V extends RealmObject> V copyFromRealm(V value) {
       return copyFromRealm(getRealm(),value);
    }

    protected <V extends RealmObject> V copyFromRealm(Realm realm, V value) {
        if(value==null){
            return null;
        }
        V newValue = value;
        if (value.isManaged()) {
            Log.i(getClass().getName(),"realm managed:"+value);
            newValue = realm.copyFromRealm(value);
        }
        return newValue;
    }
}
