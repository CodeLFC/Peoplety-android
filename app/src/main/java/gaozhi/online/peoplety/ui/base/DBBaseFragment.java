package gaozhi.online.peoplety.ui.base;

import android.os.Bundle;

import gaozhi.online.base.ui.BaseFragment;
import io.realm.Realm;

public abstract class DBBaseFragment extends BaseFragment {
    //db
    private Realm realm;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        doBusiness(getRealm());
        super.onCreate(savedInstanceState);

    }
    protected abstract void doBusiness(Realm realm);

    public Realm getRealm() {
        return Realm.getInstance(Realm.getDefaultConfiguration());
    }
}
