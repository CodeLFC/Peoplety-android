package gaozhi.online.peoplety.ui.activity.home.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import gaozhi.online.base.net.http.DataHelper;
import gaozhi.online.peoplety.R;
import gaozhi.online.peoplety.entity.Status;
import gaozhi.online.peoplety.entity.UserInfo;
import gaozhi.online.peoplety.entity.UserRecordCount;
import gaozhi.online.peoplety.entity.dto.UserDTO;
import gaozhi.online.peoplety.service.record.GetRecordCountByUseridService;
import gaozhi.online.peoplety.ui.activity.AboutActivity;
import gaozhi.online.peoplety.ui.activity.SettingsActivity;
import gaozhi.online.peoplety.ui.activity.personal.FriendsActivity;
import gaozhi.online.peoplety.ui.activity.personal.PersonalActivity;
import gaozhi.online.peoplety.ui.activity.personal.UserRecordActivity;
import gaozhi.online.peoplety.ui.activity.userinfo.QRCodeActivity;
import gaozhi.online.peoplety.ui.activity.userinfo.UserInfoActivity;
import gaozhi.online.peoplety.ui.base.DBBaseFragment;
import gaozhi.online.peoplety.ui.activity.login.LoginActivity;
import gaozhi.online.peoplety.ui.util.image.ShowImageActivity;
import gaozhi.online.peoplety.util.GlideUtil;
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
    private TextView textFansNum;
    private TextView textRecordNum;
    private TextView textFavoriteNum;
    //ui views
    private View viewUserInfo;
    private View viewQRCode;
    private View viewChangeAccount;
    private View viewAbout;
    private View viewSettings;
    //service
    private final GetRecordCountByUseridService getRecordCountByUseridService = new GetRecordCountByUseridService(new DataHelper.OnDataListener<UserRecordCount>() {
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
        // title_right.setImageResource(R.drawable.scan);
        title_right.setOnClickListener(this);
        imageHead = view.findViewById(R.id.fragment_me_image_head);
        imageHead.setOnClickListener(this);
        textName = view.findViewById(R.id.fragment_me_text_name);
        imageGender = view.findViewById(R.id.fragment_me_image_gender);
        textStatus = view.findViewById(R.id.fragment_me_text_status);
        textId = view.findViewById(R.id.fragment_me_text_id);
        textVip = view.findViewById(R.id.fragment_me_text_vip);

        textAttentionNum = view.findViewById(R.id.fragment_me_text_attention_num);
        textAttentionNum.setOnClickListener(this);
        textFansNum = view.findViewById(R.id.fragment_me_text_fans_num);
        textFansNum.setOnClickListener(this);
        textRecordNum = view.findViewById(R.id.fragment_me_text_record_num);
        textRecordNum.setOnClickListener(this);
        textFavoriteNum = view.findViewById(R.id.fragment_me_text_favorite_num);
        textFavoriteNum.setOnClickListener(this);

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

    }

    @Override
    public void doBusiness() {
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
        if (v.getId() == imageHead.getId()) {
            ShowImageActivity.startActivity(getContext(), loginUser.getUserInfo().getHeadUrl());
            return;
        }
        if (v.getId() == viewQRCode.getId()) {
            QRCodeActivity.startActivity(getContext());
            return;
        }
        if (v.getId() == viewUserInfo.getId()) {
            PersonalActivity.startActivity(getContext(), loginUser.getUserInfo().getId());
            return;
        }
        if (v.getId() == viewChangeAccount.getId()) {
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
        if (v.getId() == textRecordNum.getId()) {
            UserRecordActivity.startActivity(getContext(), loginUser.getUserInfo().getId());
            return;
        }
        if (v.getId() == textFavoriteNum.getId()) {
            ToastUtil.showToastShort("收藏夹");
            return;
        }
        if (v.getId() == textAttentionNum.getId()) {
            FriendsActivity.startActivityForAttention(getContext());
            return;
        }
        if (v.getId() == textFansNum.getId()) {
            FriendsActivity.startActivityForFan(getContext());
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        doBusiness(getRealm());
        doBusiness();
        getRecordCountByUseridService.request(loginUser.getToken(),loginUser.getUserInfo().getId());
    }
}