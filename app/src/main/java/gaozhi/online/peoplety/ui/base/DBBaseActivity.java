package gaozhi.online.peoplety.ui.base;

import android.os.Bundle;

import gaozhi.online.base.ui.BaseActivity;
import io.realm.Realm;
import io.realm.RealmChangeListener;

public abstract class DBBaseActivity extends BaseActivity {
    //db
    private Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        realm = Realm.getDefaultInstance();
        doBusiness(realm);
        super.onCreate(savedInstanceState);
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
