package gaozhi.online.peoplety.ui.activity.userinfo;


import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;

import gaozhi.online.base.net.Result;
import gaozhi.online.base.net.http.ApiRequest;
import gaozhi.online.peoplety.R;
import gaozhi.online.peoplety.entity.Status;
import gaozhi.online.peoplety.entity.UserInfo;
import gaozhi.online.peoplety.entity.dto.UserDTO;
import gaozhi.online.peoplety.service.user.UpdateUserInfoService;
import gaozhi.online.peoplety.ui.base.DBBaseActivity;
import gaozhi.online.peoplety.util.DateTimeUtil;
import gaozhi.online.peoplety.util.GlideUtil;
import gaozhi.online.peoplety.util.PatternUtil;
import gaozhi.online.peoplety.util.StringUtil;
import gaozhi.online.peoplety.util.ToastUtil;
import io.realm.Realm;

/**
 * 个人信息
 */
public class UserInfoActivity extends DBBaseActivity implements ApiRequest.ResultHandler {
    private TextView title;
    private UserDTO loginUser;
    private Status status;
    //service
    private final UpdateUserInfoService updateUserInfoService = new UpdateUserInfoService(this);

    //ui
    private ImageView imageHead;
    private ImageView imageQRCode;
    private TextView textId;
    private TextView textVip;
    private TextView textPhone;
    private TextView textStatus;
    private EditText editName;
    private EditText editRemark;
    private EditText editEmail;
    private EditText editWechat;
    private EditText editQQ;
    private TextView textGender;
    private TextView textBirth;
    private Button btnSubmit;
    //util
    private final Gson gson = new Gson();

    @Override
    protected void doBusiness(Realm realm) {
        loginUser = realm.where(UserDTO.class).equalTo("current", true).findFirst();
        //build一个没有Realm绑定的副本
        loginUser = realm.copyFromRealm(loginUser);
        status = realm.where(Status.class).equalTo("id", loginUser.getUserInfo().getStatus()).findFirst();
    }

    @Override
    protected void initParams(Intent intent) {
    }

    @Override
    protected int bindLayout() {
        return R.layout.activity_user_info;
    }

    @Override
    protected void initView(View view) {
        title = $(R.id.title_text);
        title.setText(R.string.user_info);
        imageHead = $(R.id.userinfo_activity_image_head);
        imageHead.setOnClickListener(this);
        imageQRCode = $(R.id.userinfo_activity_image_qr_code);
        imageQRCode.setOnClickListener(this);
        textId = $(R.id.userinfo_activity_text_id);
        textVip = $(R.id.userinfo_activity_text_vip);
        textPhone = $(R.id.userinfo_activity_text_phone);
        textStatus = $(R.id.userinfo_activity_text_status);
        editName = $(R.id.userinfo_activity_edit_name);
        editRemark = $(R.id.userinfo_activity_edit_remark);
        editEmail = $(R.id.userinfo_activity_edit_email);
        editWechat = $(R.id.userinfo_activity_edit_wechat);
        editQQ = $(R.id.userinfo_activity_edit_qq);
        textGender = $(R.id.userinfo_activity_text_gender);
        textGender.setOnClickListener(this);
        textBirth = $(R.id.userinfo_activity_text_birth);
        textBirth.setOnClickListener(this);
        btnSubmit = $(R.id.userinfo_activity_btn_submit);
        btnSubmit.setOnClickListener(this);
    }

    @Override
    protected void doBusiness(Context mContext) {
        UserInfo userInfo = loginUser.getUserInfo();
        GlideUtil.loadRoundRectangleImage(this, userInfo.getHeadUrl(), R.drawable.default_head, imageHead);
        textId.setText(Long.toString(userInfo.getId()));
        textVip.setText(Integer.toString(userInfo.getVip()));
        textPhone.setText(userInfo.getPhone());
        textStatus.setText(status.getName());
        editName.setText(userInfo.getNick());
        editRemark.setText(userInfo.getRemark());
        editEmail.setText(userInfo.getEmail());
        editWechat.setText(userInfo.getWechat());
        editQQ.setText(userInfo.getQq());
        textGender.setText(UserInfo.Gender.getGender(userInfo.getGender()).getDescription());
        textBirth.setText(DateTimeUtil.getBirthTime(userInfo.getBirth()));
        // 检查
        editEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!PatternUtil.matchEmail(s.toString())) {
                    editEmail.setTextColor(getColor(R.color.red));
                } else {
                    editEmail.setTextColor(getColor(R.color.deep_text_color));
                }
            }
        });
        editQQ.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!PatternUtil.matchQQ(s.toString())) {
                    editQQ.setTextColor(getColor(R.color.red));
                } else {
                    editQQ.setTextColor(getColor(R.color.deep_text_color));
                }
            }
        });
        editWechat.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!PatternUtil.matchWechat(s.toString())) {
                    editWechat.setTextColor(getColor(R.color.red));
                } else {
                    editWechat.setTextColor(getColor(R.color.deep_text_color));
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == btnSubmit.getId()) {
            UserInfo userInfo = loginUser.getUserInfo();
            userInfo.setNick(editName.getText().toString());
            userInfo.setRemark(editRemark.getText().toString());
            userInfo.setEmail(editEmail.getText().toString());
            userInfo.setWechat(editWechat.getText().toString());
            userInfo.setQq(editQQ.getText().toString());
            if(!StringUtil.isEmpty(userInfo.getWechat()) &&!PatternUtil.matchWechat(userInfo.getWechat())){
                ToastUtil.showToastShort(R.string.tip_please_check_wechat);
                return;
            }
            if(!StringUtil.isEmpty(userInfo.getQq())&&!PatternUtil.matchQQ(userInfo.getQq())){
                ToastUtil.showToastShort(R.string.tip_please_check_qq);
                return;
            }
            if(!StringUtil.isEmpty(userInfo.getEmail())&&!PatternUtil.matchEmail(userInfo.getEmail())){
                ToastUtil.showToastShort(R.string.tip_please_check_email);
                return;
            }
            updateUserInfoService.request(loginUser.getToken(), userInfo);
        }
    }

    public static void startActivity(Context context) {
        Intent intent = new Intent(context, UserInfoActivity.class);
        context.startActivity(intent);
    }

    @Override
    public void start(int id) {
        btnSubmit.setText(R.string.request_ing);
        btnSubmit.setEnabled(false);
    }

    @Override
    public void handle(int id, Result result) {
        UserInfo userInfo = gson.fromJson(result.getData(), UserInfo.class);
        loginUser.setUserInfo(userInfo);
        //修改数据库中的数据
        getRealm().executeTransactionAsync(realm -> realm.copyToRealmOrUpdate(loginUser));
        btnSubmit.setText(R.string.update_info);
        btnSubmit.setEnabled(true);
        ToastUtil.showToastLong(R.string.tip_update_success);
    }

    @Override
    public void error(int id, int code, String message, String data) {
        ToastUtil.showToastLong(message + data);
        btnSubmit.setText(R.string.update_info);
        btnSubmit.setEnabled(true);
    }
}