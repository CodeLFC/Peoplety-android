package gaozhi.online.peoplety.ui.base;

import gaozhi.online.base.ui.BaseActivity;
import io.realm.Realm;

public abstract class DBBaseActivity extends BaseActivity {

    @Override
    protected void initLocalData() {
        doBusiness(getRealm());
    }

    protected abstract void doBusiness(Realm realm);

    public Realm getRealm() {
        return Realm.getInstance(Realm.getDefaultConfiguration());
    }
}
