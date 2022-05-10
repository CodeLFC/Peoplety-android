package gaozhi.online.peoplety.ui.base;

import android.os.Bundle;

import gaozhi.online.base.ui.BaseFragment;
import io.realm.Realm;

public abstract class DBBaseFragment extends BaseFragment {
    //db
    private Realm realm;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        realm =Realm.getDefaultInstance();
        doBusiness(realm);
        super.onCreate(savedInstanceState);

    }
    protected abstract void doBusiness(Realm realm);

    public Realm getRealm() {
        return realm;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        realm.close();
    }
}
