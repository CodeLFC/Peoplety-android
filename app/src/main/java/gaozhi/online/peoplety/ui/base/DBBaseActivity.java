package gaozhi.online.peoplety.ui.base;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import gaozhi.online.base.ui.BaseActivity;
import io.realm.Realm;
import io.realm.RealmChangeListener;

public abstract class DBBaseActivity extends BaseActivity {
    //db
    private Realm realm;

    @Override
    protected void initLocalData() {
        realm = Realm.getDefaultInstance();
        doBusiness(realm);
    }

    protected abstract void doBusiness(Realm realm);

    public Realm getRealm() {
        return realm;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }
}
