package gaozhi.online.peoplety.ui.base;

import android.os.Bundle;

import gaozhi.online.base.ui.BaseActivity;
import gaozhi.online.peoplety.PeopletyApplication;
import gaozhi.online.peoplety.entity.dto.UserDTO;
import gaozhi.online.peoplety.ui.activity.home.MainActivity;
import io.realm.Realm;

public abstract class DBBaseActivity extends BaseActivity {
    private Realm realm;

    @Override
    protected void initLocalData() {
        doBusiness(getRealm());
    }

    protected abstract void doBusiness(Realm realm);

    public Realm getRealm() {
        return realm;
    }

    public UserDTO getLoginUser() {
        return getApplication(PeopletyApplication.class).getLoginUser();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        realm = getApplication(PeopletyApplication.class).getRealm();
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }

    @Override
    public void startLaunchActivity() {
        if (this instanceof MainActivity) {
            return;
        }
        MainActivity.startActivity(this);
    }
}
