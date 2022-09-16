package gaozhi.online.peoplety.ui.activity.home.fragment;

import android.Manifest;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import gaozhi.online.base.im.core.LocalDataSender;
import gaozhi.online.base.net.http.DataHelper;
import gaozhi.online.base.ui.BaseActivity;
import gaozhi.online.peoplety.R;
import gaozhi.online.peoplety.entity.Status;
import gaozhi.online.peoplety.entity.UserInfo;
import gaozhi.online.peoplety.entity.UserRecordCount;
import gaozhi.online.peoplety.entity.dto.UserDTO;
import gaozhi.online.peoplety.im.IMClient;
import gaozhi.online.peoplety.service.record.GetRecordCountByUseridService;
import gaozhi.online.peoplety.ui.activity.AboutActivity;
import gaozhi.online.peoplety.ui.activity.personal.FavoriteActivity;
import gaozhi.online.peoplety.ui.util.scan.ScanActivity;
import gaozhi.online.peoplety.ui.activity.SettingsActivity;
import gaozhi.online.peoplety.ui.activity.personal.FriendsActivity;
import gaozhi.online.peoplety.ui.activity.personal.PersonalActivity;
import gaozhi.online.peoplety.ui.activity.personal.UserRecordActivity;
import gaozhi.online.peoplety.ui.activity.userinfo.QRCodeActivity;
import gaozhi.online.peoplety.ui.base.DBBaseFragment;
import gaozhi.online.peoplety.ui.activity.login.LoginActivity;
import gaozhi.online.peoplety.util.GlideUtil;
import gaozhi.online.peoplety.util.PermissionUtil;
import gaozhi.online.peoplety.util.StringUtil;
import gaozhi.online.peoplety.util.ToastUtil;
import io.realm.Realm;

/**
 * 我
 */
public class MeFragment extends DBBaseFragment {
    private TextView title;
    private ImageView title_right;
    private UserDTO loginUser;
    private Status status;
    //ui
    private ImageView imageHead;
    private TextView textName;
    private ImageView imageGender;
    private TextView textStatus;
    private TextView textId;
    private TextView textVip;

    private TextView textAttentionNum;
    private View viewAttentionNum;
    private TextView textFansNum;
    private View viewFansNum;
    private TextView textRecordNum;
    private View viewRecordNum;
    private TextView textFavoriteNum;
    private View viewFavoriteNum;
    //ui views
    private View viewUserInfo;
    private View viewQRCode;
    private View viewChangeAccount;
    private View viewAbout;
    private View viewSettings;

    //permission
    private final String[] authorities = new String[]{
            Manifest.permission.CAMERA
    };
    private PermissionUtil permissionUtil;

    //service
    private final GetRecordCountByUseridService getRecordCountByUseridService = new GetRecordCountByUseridService(new DataHelper.OnDataListener<>() {
        @Override
        public void handle(int id, UserRecordCount data, boolean local) {
            if (data == null) return;
            textRecordNum.setText("" + data.getRecordNum());
            textFavoriteNum.setText("" + data.getFavoriteNum());
        }
    });

    @Override
    protected void doBusiness(Realm realm) {
        loginUser = realm.where(UserDTO.class).equalTo("current", true).findFirst();
        loginUser = realm.copyFromRealm(loginUser);
        status = realm.where(Status.class).equalTo("id", loginUser.getUserInfo().getStatus()).findFirst();
    }

    @Override
    public int bindLayout() {
        return R.layout.fragment_me;
    }

    @Override
    public void initView(View view) {
        title = view.findViewById(R.id.title_text);
        title.setText(R.string.bottom_me);
        title_right = view.findViewById(R.id.title_image_right);
        title_right.setImageResource(R.drawable.scan);
        title_right.setOnClickListener(this);
        imageHead = view.findViewById(R.id.fragment_me_image_head);
        textName = view.findViewById(R.id.fragment_me_text_name);
        imageGender = view.findViewById(R.id.fragment_me_image_gender);
        textStatus = view.findViewById(R.id.fragment_me_text_status);
        textId = view.findViewById(R.id.fragment_me_text_id);
        textVip = view.findViewById(R.id.fragment_me_text_vip);

        textAttentionNum = view.findViewById(R.id.fragment_me_text_attention_num);
        textAttentionNum.setOnClickListener(this);
        viewAttentionNum = view.findViewById(R.id.fragment_me_view_attention);
        viewAttentionNum.setOnClickListener(this);
        textFansNum = view.findViewById(R.id.fragment_me_text_fans_num);
        textFansNum.setOnClickListener(this);
        viewFansNum = view.findViewById(R.id.fragment_me_view_fans);
        viewFansNum.setOnClickListener(this);
        textRecordNum = view.findViewById(R.id.fragment_me_text_record_num);
        textRecordNum.setOnClickListener(this);
        viewRecordNum = view.findViewById(R.id.fragment_me_view_record);
        viewRecordNum.setOnClickListener(this);
        textFavoriteNum = view.findViewById(R.id.fragment_me_text_favorite_num);
        textFavoriteNum.setOnClickListener(this);
        viewFavoriteNum = view.findViewById(R.id.fragment_me_view_favorite);
        viewFavoriteNum.setOnClickListener(this);

        viewUserInfo = view.findViewById(R.id.fragment_me_view_account);
        viewUserInfo.setOnClickListener(this);

        viewQRCode = view.findViewById(R.id.fragment_me_view_qr_code);
        viewQRCode.setOnClickListener(this);

        viewChangeAccount = view.findViewById(R.id.fragment_me_view_change_account);
        viewChangeAccount.setOnClickListener(this);

        viewAbout = view.findViewById(R.id.fragment_me_view_about);
        viewAbout.setOnClickListener(this);

        viewSettings = view.findViewById(R.id.fragment_me_view_settings);
        viewSettings.setOnClickListener(this);

    }

    @Override
    public void initParams(Bundle bundle) {
        permissionUtil = new PermissionUtil((BaseActivity) getActivity(), 100);
        permissionUtil.setPermissionListener(new PermissionUtil.PermissionListener() {
            @Override
            public void agreed() {
                //打开扫码
                ScanActivity.startActivityForResult(getActivity(), 0);
            }

            @Override
            public void denied() {
                ToastUtil.showToastLong(R.string.not_permission);
            }
        });
    }

    @Override
    public void doBusiness() {

    }

    private void refreshData() {
        getRecordCountByUseridService.request(loginUser.getToken(), loginUser.getUserInfo().getId());
        GlideUtil.loadRoundRectangleImage(getContext(), loginUser.getUserInfo().getHeadUrl(), R.drawable.app_logo, imageHead);
        textName.setText(loginUser.getUserInfo().getNick());
        textStatus.setText(status.getName());
        textId.setText(getString(R.string.id) + loginUser.getUserInfo().getId());
        textVip.setText(getString(R.string.vip) + loginUser.getUserInfo().getVip());
        switch (UserInfo.Gender.getGender(loginUser.getUserInfo().getGender())) {
            case MALE:
                imageGender.setImageResource(R.drawable.male);
                break;
            case FEMALE:
                imageGender.setImageResource(R.drawable.female);
                break;
            case OTHER:
                imageGender.setImageResource(R.drawable.other_gender);
        }
        textAttentionNum.setText(StringUtil.numLong2Str(loginUser.getAttentionNum()));
        textFansNum.setText(StringUtil.numLong2Str(loginUser.getFanNum()));
    }

    @Override
    public void onPageScrolled(float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected() {

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == viewQRCode.getId()) {
            QRCodeActivity.startActivity(getContext());
            return;
        }
        if (v.getId() == viewUserInfo.getId()) {
            PersonalActivity.startActivity(getContext(), loginUser.getUserInfo().getId());
            return;
        }
        if (v.getId() == viewChangeAccount.getId()) {
            //退出登录
            IMClient.getInstance(getContext()).release();
            LoginActivity.startActivity(getContext());
            getActivity().finish();
            return;
        }
        if (v.getId() == viewAbout.getId()) {
            AboutActivity.startActivity(getContext());
            return;
        }
        if (v.getId() == viewSettings.getId()) {
            SettingsActivity.startActivity(getContext());
            return;
        }
        if (v.getId() == textRecordNum.getId() || v.getId() == viewRecordNum.getId()) {
            UserRecordActivity.startActivity(getContext(), loginUser.getUserInfo().getId());
            return;
        }
        if (v.getId() == textFavoriteNum.getId() || v.getId() == viewFavoriteNum.getId()) {
            FavoriteActivity.startActivity(getContext(), loginUser.getUserInfo().getId());
            return;
        }
        if (v.getId() == textAttentionNum.getId() || v.getId() == viewAttentionNum.getId()) {
            FriendsActivity.startActivityForAttention(getContext(), loginUser.getUserInfo().getId());
            return;
        }
        if (v.getId() == textFansNum.getId() || v.getId() == viewFansNum.getId()) {
            FriendsActivity.startActivityForFan(getContext(), loginUser.getUserInfo().getId());
            return;
        }
        if (v.getId() == title_right.getId()) {
            permissionUtil.requestPermission(authorities);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        doBusiness(getRealm());
        refreshData();
    }
}