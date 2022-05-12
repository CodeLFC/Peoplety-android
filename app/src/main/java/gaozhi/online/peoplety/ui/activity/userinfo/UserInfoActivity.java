package gaozhi.online.peoplety.ui.activity.userinfo;


import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.gson.Gson;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import gaozhi.online.base.net.Result;
import gaozhi.online.base.net.http.ApiRequest;
import gaozhi.online.peoplety.R;
import gaozhi.online.peoplety.entity.Status;
import gaozhi.online.peoplety.entity.UserInfo;
import gaozhi.online.peoplety.entity.dto.UserDTO;
import gaozhi.online.peoplety.service.user.UpdateUserInfoService;
import gaozhi.online.peoplety.ui.base.DBBaseActivity;
import gaozhi.online.peoplety.ui.util.WebActivity;
import gaozhi.online.peoplety.ui.util.image.ShowImageActivity;
import gaozhi.online.peoplety.util.DateTimeUtil;
import gaozhi.online.peoplety.util.GlideUtil;
import gaozhi.online.peoplety.util.PatternUtil;
import gaozhi.online.peoplety.util.StringUtil;
import gaozhi.online.peoplety.util.SystemUtil;
import gaozhi.online.peoplety.util.ToastUtil;
import io.realm.Realm;

/**
 * 个人信息
 */
public class UserInfoActivity extends DBBaseActivity implements ApiRequest.ResultHandler {
    private static final int REQUEST_CODE_CHOOSE_PHOTO = 1000;
    private UserDTO loginUser;
    private Status status;
    //service
    private final UpdateUserInfoService updateUserInfoService = new UpdateUserInfoService(this);

    //ui
    private View headView;
    private ImageView imageHead;
    private View qrCodeView;
    private TextView textId;
    private View ipView;
    private TextView textIp;
    private TextView textPhone;
    private TextView textStatus;
    private EditText editName;
    private EditText editRemark;
    private EditText editEmail;
    private EditText editWechat;
    private EditText editQQ;
    private Spinner spinnerGender;
    private TextView textBirth;
    private Button btnSubmit;
    //picker
    private DatePickerDialog datePickerDialog;
    private Calendar calendar;
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
        //获得当前时间 DatePicker默认显示
        calendar = Calendar.getInstance();
    }

    @Override
    protected int bindLayout() {
        return R.layout.activity_user_info;
    }

    @Override
    protected void initView(View view) {
        TextView title = $(R.id.title_text);
        title.setText(R.string.user_info);
        headView = $(R.id.userinfo_activity_view_head);
        headView.setOnClickListener(this);
        imageHead = $(R.id.userinfo_activity_image_head);
        imageHead.setOnClickListener(this);
        qrCodeView = $(R.id.userinfo_activity_view_qr_code);
        qrCodeView.setOnClickListener(this);
        textId = $(R.id.userinfo_activity_text_id);
        ipView = $(R.id.userinfo_activity_view_ip);
        ipView.setOnClickListener(this);
        textIp = $(R.id.userinfo_activity_text_ip);
        textPhone = $(R.id.userinfo_activity_text_phone);
        textStatus = $(R.id.userinfo_activity_text_status);
        editName = $(R.id.userinfo_activity_edit_name);
        editRemark = $(R.id.userinfo_activity_edit_remark);
        editEmail = $(R.id.userinfo_activity_edit_email);
        editWechat = $(R.id.userinfo_activity_edit_wechat);
        editQQ = $(R.id.userinfo_activity_edit_qq);
        spinnerGender = $(R.id.userinfo_activity_spinner_gender);

        textBirth = $(R.id.userinfo_activity_text_birth);
        textBirth.setOnClickListener(this);
        btnSubmit = $(R.id.userinfo_activity_btn_submit);
        btnSubmit.setOnClickListener(this);
        datePickerDialog = new DatePickerDialog(this, null, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
    }

    @Override
    protected void doBusiness(Context mContext) {
        UserInfo userInfo = loginUser.getUserInfo();
        GlideUtil.loadRoundRectangleImage(this, userInfo.getHeadUrl(), R.drawable.default_head, imageHead);
        textId.setText(Long.toString(userInfo.getId()));
        textIp.setText(userInfo.getIp());
        textPhone.setText(userInfo.getPhone());
        textStatus.setText(status.getName());
        editName.setText(userInfo.getNick());
        editRemark.setText(userInfo.getRemark());
        editEmail.setText(userInfo.getEmail());
        editWechat.setText(userInfo.getWechat());
        editQQ.setText(userInfo.getQq());
        //性别选择
        GenderSpinnerAdapter genderSpinnerAdapter = new GenderSpinnerAdapter(this, UserInfo.Gender.values());
        spinnerGender.setAdapter(genderSpinnerAdapter);
        spinnerGender.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                userInfo.setGender(genderSpinnerAdapter.getItem(position).getKey());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        //设置选中性别
        spinnerGender.setSelection(genderSpinnerAdapter.getPosition(UserInfo.Gender.getGender(userInfo.getGender())));

        textBirth.setText(DateTimeUtil.getBirthTime(userInfo.getBirth()));
        //生日选择
        datePickerDialog.setOnDateSetListener((view, year, month, dayOfMonth) -> {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            userInfo.setBirth(calendar.getTimeInMillis());
            textBirth.setText(DateTimeUtil.getBirthTime(userInfo.getBirth()));
        });
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
            if (!StringUtil.isEmpty(userInfo.getWechat()) && !PatternUtil.matchWechat(userInfo.getWechat())) {
                ToastUtil.showToastShort(R.string.tip_please_check_wechat);
                return;
            }
            if (!StringUtil.isEmpty(userInfo.getQq()) && !PatternUtil.matchQQ(userInfo.getQq())) {
                ToastUtil.showToastShort(R.string.tip_please_check_qq);
                return;
            }
            if (!StringUtil.isEmpty(userInfo.getEmail()) && !PatternUtil.matchEmail(userInfo.getEmail())) {
                ToastUtil.showToastShort(R.string.tip_please_check_email);
                return;
            }
            updateUserInfoService.request(loginUser.getToken(), userInfo);
            return;
        }
        if (v.getId() == textBirth.getId()) {
            datePickerDialog.show();
            return;
        }
        if (v.getId() == qrCodeView.getId()) {
            QRCodeActivity.startActivity(this);
            return;
        }

        if (v.getId() == headView.getId()) {
            SystemUtil.openAlbum(this, REQUEST_CODE_CHOOSE_PHOTO);
            return;
        }
        if (v.getId() == imageHead.getId()) {
            ShowImageActivity.startActivity(this, loginUser.getUserInfo().getHeadUrl());
            return;
        }
        if(v.getId() == ipView.getId()){
            WebActivity.startActivity(this,getString(R.string.url_ip),loginUser.getUserInfo().getIp());
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        doBusiness(getRealm());
        doBusiness(this);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CODE_CHOOSE_PHOTO:
                if (resultCode == RESULT_OK) {
                    ShowUpdateHeadActivity.startActivity(this, data.getData());
                }
        }
    }

    /**
     * 性别选择器
     */
    public static class GenderSpinnerAdapter extends ArrayAdapter<UserInfo.Gender> {

        public GenderSpinnerAdapter(@NonNull Context context, @NonNull UserInfo.Gender[] objects) {
            super(context, R.layout.user_info_gender_item, objects);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            TextView text = (TextView) super.getView(position, convertView, parent);
            text.setText(getItem(position).getDescription());
            return text;
        }

        public int getPosition(UserInfo.Gender gender) {
            for (int i = 0; i < getCount(); i++) {
                if (gender == getItem(i)) {
                    return i;
                }
            }
            return 0;
        }
    }
}