package gaozhi.online.peoplety.ui.main.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import gaozhi.online.base.ui.BaseFragment;
import gaozhi.online.peoplety.R;
import gaozhi.online.peoplety.entity.dto.UserDTO;
import gaozhi.online.peoplety.ui.base.DBBaseFragment;
import io.realm.Realm;

/**
 * 我
 */
public class MeFragment extends DBBaseFragment {
    private TextView title;
    private UserDTO loginUser;
    @Override
    protected void doBusiness(Realm realm) {
          loginUser = realm.where(UserDTO.class).equalTo("current",true).findFirst();
    }
    @Override
    public int bindLayout() {
        return R.layout.fragment_me;
    }

    @Override
    public void initView(View view) {
         title = view.findViewById(R.id.title_text);
         title.setText(R.string.bottom_me);
    }

    @Override
    public void initParams(Bundle bundle) {

    }

    @Override
    public void doBusiness() {

    }

    @Override
    public void onPageScrolled(float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected() {

    }

    @Override
    public void onClick(View v) {

    }

}