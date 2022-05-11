package gaozhi.online.peoplety.ui.activity.home.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import gaozhi.online.peoplety.R;
import gaozhi.online.peoplety.entity.Status;
import gaozhi.online.peoplety.entity.dto.UserDTO;
import gaozhi.online.peoplety.ui.activity.AboutActivity;
import gaozhi.online.peoplety.ui.activity.SettingsActivity;
import gaozhi.online.peoplety.ui.activity.userinfo.UserInfoActivity;
import gaozhi.online.peoplety.ui.base.DBBaseFragment;
import gaozhi.online.peoplety.ui.activity.login.LoginActivity;
import gaozhi.online.peoplety.util.GlideUtil;
import io.realm.Realm;

/**
 * 我
 */
public class MeFragment extends DBBaseFragment {
    private TextView title;
    private UserDTO loginUser;
    private Status status;
    //ui
    private ImageView imageHead;
    private TextView textName;
    private TextView textStatus;
    private TextView textId;
    private TextView textVip;
    //ui views
    private View viewUserInfo;
    private View viewChangeAccount;
    private View viewAbout;
    private View viewSettings;

    @Override
    protected void doBusiness(Realm realm) {
          loginUser = realm.where(UserDTO.class).equalTo("current",true).findFirst();
          status = realm.where(Status.class).equalTo("id",loginUser.getUserInfo().getStatus()).findFirst();
    }
    @Override
    public int bindLayout() {
        return R.layout.fragment_me;
    }

    @Override
    public void initView(View view) {
         title = view.findViewById(R.id.title_text);
         title.setText(R.string.bottom_me);
         imageHead = view.findViewById(R.id.fragment_me_image_head);
         textName = view.findViewById(R.id.fragment_me_text_name);
         textStatus = view.findViewById(R.id.fragment_me_text_status);
         textId = view.findViewById(R.id.fragment_me_text_id);
         textVip = view.findViewById(R.id.fragment_me_text_vip);

         viewUserInfo = view.findViewById(R.id.fragment_me_view_account);
         viewUserInfo.setOnClickListener(this);

         viewChangeAccount = view.findViewById(R.id.fragment_me_view_change_account);
         viewChangeAccount.setOnClickListener(this);

         viewAbout = view.findViewById(R.id.fragment_me_view_about);
         viewAbout.setOnClickListener(this);

         viewSettings =view.findViewById(R.id.fragment_me_view_settings);
         viewSettings.setOnClickListener(this);

    }

    @Override
    public void initParams(Bundle bundle) {

    }

    @Override
    public void doBusiness() {
        GlideUtil.loadRoundRectangleImage(getContext(),loginUser.getUserInfo().getHeadUrl(),R.drawable.app_logo,imageHead);
        textName.setText(loginUser.getUserInfo().getNick());
        textStatus.setText(status.getName());
        textId.setText(getString(R.string.id)+loginUser.getUserInfo().getId());
        textVip.setText(getString(R.string.vip)+loginUser.getUserInfo().getVip());

    }

    @Override
    public void onPageScrolled(float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected() {

    }

    @Override
    public void onClick(View v) {
          switch (v.getId()){
              case R.id.fragment_me_view_account:
                  UserInfoActivity.startActivity(getContext());
                  break;
              case R.id.fragment_me_view_change_account:
                  LoginActivity.startActivity(getContext());
                  getActivity().finish();
                  break;
              case R.id.fragment_me_view_about:
                  AboutActivity.startActivity(getContext());
                  break;
              case R.id.fragment_me_view_settings:
                  SettingsActivity.startActivity(getContext());
                  break;
          }
    }

}