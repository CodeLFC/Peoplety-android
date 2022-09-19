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
        doBusiness(getRealm());
        super.onCreate(savedInstanceState);

    }

    protected abstract void doBusiness(Realm realm);

    public Realm getRealm() {
        return getApplication(PeopletyApplication.class).getRealm();
    }

    public UserDTO getLoginUser() {
        return getApplication(PeopletyApplication.class).getLoginUser();
    }
}
