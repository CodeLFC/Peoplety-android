package gaozhi.online.peoplety.ui.activity.home.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.View;
import android.widget.TextView;

import java.util.function.Consumer;

import gaozhi.online.peoplety.R;
import gaozhi.online.peoplety.entity.Area;
import gaozhi.online.peoplety.entity.dto.UserDTO;
import gaozhi.online.peoplety.ui.base.DBBaseFragment;
import gaozhi.online.peoplety.ui.util.pop.AreaPopWindow;
import io.realm.Realm;

/**
 * A simple {@link Fragment} subclass. 主页
 */
public class HomeFragment extends DBBaseFragment  implements Consumer<Area> {
    private TextView title;
    private TextView titleTextRight;
    private AreaPopWindow areaPopWindow;
    //db info
    private UserDTO loginUser;

    @Override
    protected void doBusiness(Realm realm) {
        loginUser = realm.where(UserDTO.class).equalTo("current", true).findFirst();
        loginUser = realm.copyFromRealm(loginUser);
        if (loginUser.getArea() ==null){
            loginUser.setArea(realm.where(Area.class).equalTo("minimum",true).findFirst());
        }
    }

    @Override
    public int bindLayout() {
        return R.layout.fragment_home;
    }

    @Override
    public void initView(View view) {
        title = view.findViewById(R.id.title_text);
        title.setText(R.string.bottom_home);
        titleTextRight =view.findViewById(R.id.title_text_right);
        titleTextRight.setOnClickListener(this);
        areaPopWindow = new AreaPopWindow(getContext(),true);
        areaPopWindow.setOnAreaClickedListener(this);
    }

    @Override
    public void initParams(Bundle bundle) {

    }

    @Override
    public void doBusiness() {
        titleTextRight.setText(loginUser.getArea().getName());
    }

    @Override
    public void onPageScrolled(float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected() {

    }

    @Override
    public void onClick(View v) {
        if(v.getId() == titleTextRight.getId()){
            areaPopWindow.showPopupWindow(getActivity());
            return;
        }
    }
    @Override
    public void accept( Area area) {
        if(!area.isMinimum()){//不是最终节点
            return;
        }
        //绑定的对象
        final Area temp = getRealm().copyFromRealm(area);
        getRealm().executeTransactionAsync(realm -> {
            loginUser.setArea(temp);
            realm.copyToRealmOrUpdate(loginUser);
        }, () -> {
            titleTextRight.setText(area.getName());
            areaPopWindow.dismiss();
        });
    }
}