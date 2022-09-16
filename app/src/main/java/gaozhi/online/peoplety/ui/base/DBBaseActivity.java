package gaozhi.online.peoplety.ui.base;

import gaozhi.online.base.ui.BaseActivity;
import gaozhi.online.peoplety.PeopletyApplication;
import gaozhi.online.peoplety.entity.dto.UserDTO;
import io.realm.Realm;

public abstract class DBBaseActivity extends BaseActivity {
    @Override
    protected void initLocalData() {
        doBusiness(getRealm());
    }

    protected abstract void doBusiness(Realm realm);

    public Realm getRealm() {
        return getApplication(PeopletyApplication.class).getRealm();
    }

    public UserDTO getLoginUser() {
        return getApplication(PeopletyApplication.class).getLoginUser();
    }
}
