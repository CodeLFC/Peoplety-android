package gaozhi.online.peoplety.ui.activity.login;


import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.List;

import gaozhi.online.base.net.Result;
import gaozhi.online.base.net.http.ApiRequest;
import gaozhi.online.peoplety.R;
import gaozhi.online.peoplety.entity.Status;
import gaozhi.online.peoplety.entity.Token;
import gaozhi.online.peoplety.entity.UserAuth;
import gaozhi.online.peoplety.entity.dto.UserDTO;
import gaozhi.online.peoplety.service.constant.GetUserConstantService;
import gaozhi.online.peoplety.service.user.LoginService;
import gaozhi.online.peoplety.ui.base.DBBaseActivity;
import gaozhi.online.peoplety.ui.activity.home.MainActivity;
import gaozhi.online.peoplety.ui.util.WebActivity;
import gaozhi.online.peoplety.util.PatternUtil;
import gaozhi.online.peoplety.util.ResourceUtil;
import gaozhi.online.peoplety.util.StringUtil;
import gaozhi.online.peoplety.util.ToastUtil;
import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

public class LoginActivity extends DBBaseActivity implements ApiRequest.ResultHandler {
    // service
    private final LoginService loginService = new LoginService(this);
    private final GetUserConstantService getUserConstantService = new GetUserConstantService(this);
    //ui
    private LinearLayout layout_bottom;
    private LinearLayout layout_top;

    private EditText edit_id;
    private EditText edit_pass;
    private Button btn_login;
    private TextView text_register;
    private TextView text_forget_pass;
    private CheckBox checkBox_agree_privacy;
    private TextView text_privacy;
    //util
    private final Gson gson = new Gson();
    //intent
    private static final String INTENT_TAG_AUTO_LOGIN = "INTENT_TAG_AUTO_LOGIN";
    private boolean auto_login;
    //entity
    private String account;
    private String pass;
    private UserDTO loginUser;


    @Override
    protected void initParams(Intent intent) {
        auto_login = intent.getBooleanExtra(INTENT_TAG_AUTO_LOGIN, true);
    }

    @Override
    protected int bindLayout() {
        return R.layout.activity_login;
    }

    @Override
    protected void initView(View view) {
        //取消沉浸状态栏
        setSteepStatusBar(false);
        layout_bottom = $(R.id.login_activity_start_layout_bottom);
        layout_top = $(R.id.login_activity_start_layout_top);
        edit_id = $(R.id.login_activity_edit_phone);
        edit_pass = $(R.id.login_activity_edit_pass);
        btn_login = $(R.id.login_activity_btn_login);
        btn_login.setOnClickListener(this);
        text_register = $(R.id.login_activity_text_register);
        text_register.setOnClickListener(this);
        text_forget_pass = $(R.id.login_activity_text_find_pass);
        text_forget_pass.setOnClickListener(this);
        checkBox_agree_privacy = $(R.id.login_activity_check_agree);
        text_privacy = $(R.id.login_activity_text_privacy);
        text_privacy.setOnClickListener(this);
    }

    @Override
    protected void doBusiness(Realm realm) {
        loginUser = realm.where(UserDTO.class).sort("time", Sort.DESCENDING).findFirst();
    }

    @Override
    protected void doBusiness(Context mContext) {
        if (loginUser != null) {
            edit_id.setText(loginUser.getAccount());
            edit_pass.setText(loginUser.getPass());
            checkBox_agree_privacy.setChecked(true);
        }
        if (auto_login) {
            login();
        } else {
            layout_bottom.setVisibility(View.INVISIBLE);
            layout_top.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login_activity_btn_login:
                login();
                break;
            case R.id.login_activity_text_find_pass:
                ResetPassActivity.startActivity(this);
                break;
            case R.id.login_activity_text_register:
                RegisterActivity.startActivity(this);
                break;
            case R.id.login_activity_text_privacy:
                try {
                    WebActivity.startActivity(this, ResourceUtil.readRaw(this, R.raw.privacy), getString(R.string.policy_privacy));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                // WebActivity.startActivity(this,"https://www.bilibili.com/",getString(R.string.policy_privacy));
                break;
        }
    }

    /**
     * 登陆
     */
    private void login() {
        if (!checkBox_agree_privacy.isChecked()) {
            layout_bottom.setVisibility(View.INVISIBLE);
            layout_top.setVisibility(View.VISIBLE);
            ToastUtil.showToastLong(getString(R.string.tip_please_check_privacy));
            return;
        }
        account = edit_id.getText().toString();
        pass = edit_pass.getText().toString();
        if (StringUtil.isEmpty(account) || StringUtil.isEmpty(pass)) {
            ToastUtil.showToastLong(getString(R.string.tip_account_cant_empty));
            return;
        }
        UserAuth.AccountType accountType = UserAuth.AccountType.ID;
        if (PatternUtil.matchPhone(account)) {
            accountType = UserAuth.AccountType.PHONE;
        }
        loginService.request(accountType, account, pass, Token.Device.MOBILE.getDevice());
    }

    @Override
    public void start(int id) {
        btn_login.setText(R.string.tip_login_ing);
        btn_login.setEnabled(false);
    }

    @Override
    public void handle(int id, Result result) {
        if (id == loginService.getId()) {
            getRealm().executeTransactionAsync(realm -> {
                loginUser = gson.fromJson(result.getData(), UserDTO.class);
                loginUser.setAccount(account);
                loginUser.setPass(pass);
                loginUser.setTime(System.currentTimeMillis());

                RealmResults<UserDTO> allUser = realm.where(UserDTO.class).findAll();
                for (UserDTO userDTO : allUser) {
                    userDTO.setCurrent(false);
                }
                loginUser.setCurrent(true);
                realm.copyToRealmOrUpdate(loginUser);
            }, () -> {//success
                getUserConstantService.request(loginUser.getToken());
            });
            return;
        }
        if (id == getUserConstantService.getId()) {
            getRealm().executeTransactionAsync(realm -> {
                List<Status> statuses = gson.fromJson(result.getData(), new TypeToken<List<Status>>() {
                }.getType());
                for (Status status : statuses) {
                    realm.copyToRealmOrUpdate(status);
                    Log.i(TAG,status.toString());
                }
            }, () -> {//请求资源成功
                MainActivity.startActivity(LoginActivity.this);
                finish();
            });
        }
    }

    @Override
    public void error(int id, int code, String message, String data) {
        btn_login.setText(R.string.login);
        btn_login.setEnabled(true);
        layout_bottom.setVisibility(View.INVISIBLE);
        layout_top.setVisibility(View.VISIBLE);
        ToastUtil.showToastLong(message + data);
    }

    /**
     * 其他地方调用启动登陆页面，不允许自动登陆
     *
     * @param context
     */
    public static void startActivity(Context context) {
        Intent intent = new Intent(context, LoginActivity.class);
        intent.putExtra(INTENT_TAG_AUTO_LOGIN, false);
        context.startActivity(intent);
    }

}