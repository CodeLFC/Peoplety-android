package gaozhi.online.peoplety.ui.activity.login;


import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.function.BiConsumer;

import gaozhi.online.base.net.Result;
import gaozhi.online.base.net.http.DataHelper;
import gaozhi.online.base.ui.BaseActivity;
import gaozhi.online.peoplety.R;
import gaozhi.online.peoplety.entity.Area;
import gaozhi.online.peoplety.entity.Token;
import gaozhi.online.peoplety.entity.UserAuth;
import gaozhi.online.peoplety.entity.dto.UserDTO;
import gaozhi.online.peoplety.service.constant.ResourceRequester;
import gaozhi.online.peoplety.service.user.LoginService;
import gaozhi.online.peoplety.ui.activity.home.MainActivity;
import gaozhi.online.peoplety.ui.base.DBBaseActivity;
import gaozhi.online.peoplety.ui.util.WebActivity;
import gaozhi.online.peoplety.ui.util.pop.TipPopWindow;
import gaozhi.online.peoplety.util.PatternUtil;
import gaozhi.online.peoplety.util.ResourceUtil;
import gaozhi.online.peoplety.util.StringUtil;
import gaozhi.online.peoplety.util.ToastUtil;
import io.realm.Realm;
import io.realm.RealmResults;

public class LoginActivity extends DBBaseActivity implements DataHelper.OnDataListener<UserDTO>, BiConsumer<Integer, Boolean> {

    //登陆保护时间
    private static final long LOGIN_PROTECTED_TIME = 1000 * 60 * 60 * 2;
    // service
    private final LoginService loginService = new LoginService(this);
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
    //资源请求进度
    private TextView textProcess;
    //intent
    private static final String INTENT_TAG_AUTO_LOGIN = "INTENT_TAG_AUTO_LOGIN";
    private boolean auto_login;
    //entity
    private String account;
    private String pass;
    //db
    private UserDTO loginUser;
    //资源请求服务
    private ResourceRequester resourceRequester;

    @Override
    protected void doBusiness(Realm realm) {
        loginUser = realm.where(UserDTO.class).equalTo("current", true).findFirst();
        if (loginUser != null)
            loginUser = realm.copyFromRealm(loginUser);
    }

    @Override
    protected void initParams(Intent intent) {
        //取消沉浸状态栏
        setSteepStatusBar(false);
        auto_login = intent.getBooleanExtra(INTENT_TAG_AUTO_LOGIN, true);
        resourceRequester = new ResourceRequester(getRealm(), this, this);
    }

    @Override
    protected int bindLayout() {
        return R.layout.activity_login;
    }

    @Override
    protected void initView(View view) {
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
        textProcess = $(R.id.login_activity_text_process);
    }


    @Override
    protected void doBusiness(Context mContext) {
        if (loginUser == null) {//没有用户信息
            showLoginView();
            return;
        }

        edit_id.setText(loginUser.getAccount());
        edit_pass.setText(loginUser.getPass());

        checkBox_agree_privacy.setChecked(true);

        if (!auto_login) {//显示登陆部分
            showLoginView();
            return;
        }
        //token有效期快过了，开始自动登陆
        if (loginUser.getToken().getValidateTime() < System.currentTimeMillis() + LOGIN_PROTECTED_TIME) {
            login();
            return;
        }
        //常量有效期没过
        if (loginUser.getResourceValidateTime() > System.currentTimeMillis()) {
            enterMainWindow();
            return;
        }
        //更新常量
        resourceRequester.refreshResource(loginUser);
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
            showLoginView();
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
    public void handle(int id, UserDTO data) {
        getRealm().executeTransactionAsync(realm -> {
            Area area = loginUser == null ? null : loginUser.getArea();
            loginUser = data;
            loginUser.setAccount(account);
            loginUser.setPass(pass);
            //记忆地址
            loginUser.setArea(area);

            RealmResults<UserDTO> allUser = realm.where(UserDTO.class).findAll();
            for (UserDTO userDTO : allUser) {
                userDTO.setCurrent(false);
            }
            loginUser.setCurrent(true);
            realm.copyToRealmOrUpdate(loginUser);
        }, () -> {//success 登陆成功
            if (System.currentTimeMillis() > loginUser.getResourceValidateTime()) {
                textProcess.setText(R.string.request_ing);
                //请求资源
                resourceRequester.refreshResource(loginUser);
            } else {
                enterMainWindow();
            }
        });
    }



    @Override
    public void error(int id, int code, String message, String data) {
        btn_login.setText(R.string.login);
        btn_login.setEnabled(true);
        showLoginView();
        new TipPopWindow(this, true).setMessage(message + data).showPopupWindow(this);
    }
    /**
     * 进入主页面
     */
    private void enterMainWindow() {
        MainActivity.startActivity(LoginActivity.this);
        finish();
    }

    //显示登陆界面
    private void showLoginView() {
        layout_bottom.setVisibility(View.INVISIBLE);
        layout_top.setVisibility(View.VISIBLE);
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

    @Override
    public void accept(Integer integer, Boolean aBoolean) {
        textProcess.setText(getString(R.string.request_ing) + integer + "/" + resourceRequester.getResourceSize());
        if (aBoolean) {//资源请求完成
            enterMainWindow();
        }
    }
}