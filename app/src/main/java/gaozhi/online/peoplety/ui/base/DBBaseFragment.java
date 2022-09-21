package gaozhi.online.peoplety.ui.base;

import android.os.Bundle;

import gaozhi.online.base.ui.BaseFragment;
import gaozhi.online.peoplety.PeopletyApplication;
import gaozhi.online.peoplety.entity.dto.UserDTO;
import io.realm.Realm;

public abstract class DBBaseFragment extends BaseFragment {
    //db
    private Realm realm;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        realm = getApplication(PeopletyApplication.class).getRealm();
        doBusiness(realm);
        super.onCreate(savedInstanceState);
    }

    protected abstract void doBusiness(Realm realm);

    public Realm getRealm() {
        return realm;
    }

    public UserDTO getLoginUser() {
        return getApplication(PeopletyApplication.class).getLoginUser();
    }

    /**
     * Called when the fragment is no longer in use.  This is called
     * after {@link #onStop()} and before {@link #onDetach()}.
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        realm.close();
    }
}
