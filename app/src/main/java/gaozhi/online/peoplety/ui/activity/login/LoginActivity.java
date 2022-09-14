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

import net.x52im.mobileimsdk.protocal.c.PLoginInfo;

import java.io.IOException;
import java.util.function.BiConsumer;

import gaozhi.online.base.asynchronization.Handler;
import gaozhi.online.base.im.core.LocalDataSender;
import gaozhi.online.base.net.http.DataHelper;
import gaozhi.online.peoplety.PeopletyApplication;
import gaozhi.online.peoplety.R;
import gaozhi.online.peoplety.entity.Area;
import gaozhi.online.peoplety.entity.Token;
import gaozhi.online.peoplety.entity.UserAuth;
import gaozhi.online.peoplety.entity.dto.UserDTO;
import gaozhi.online.peoplety.im.IMClient;
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
    private CheckBox checkBox_agree_privacy;
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
    public LoginActivity(){
        setAllowFullScreen(true);
        setSteepStatusBar(true);
    }
    @Override
    protected void doBusiness(Realm realm) {
        loginUser = realm.where(UserDTO.class).equalTo("current", true).findFirst();
        if (loginUser != null)
            loginUser = realm.copyFromRealm(loginUser);
    }

    @Override
    protected void initParams(Intent intent) {

        auto_login = intent.getBooleanExtra(INTENT_TAG_AUTO_LOGIN, true);
    }

    @Override
    protected void initLocalData() {
        super.initLocalData();
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
        TextView text_register = $(R.id.login_activity_text_register);
        text_register.setOnClickListener(this);
        TextView text_forget_pass = $(R.id.login_activity_text_find_pass);
        text_forget_pass.setOnClickListener(this);
        checkBox_agree_privacy = $(R.id.login_activity_check_agree);
        TextView text_privacy = $(R.id.login_activity_text_privacy);
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
            connectIM();
            return;
        }
        showSloganView();
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
            //loginUser = realm.copyFromRealm(loginUser);
        }, () -> {//success 登陆成功
            if (System.currentTimeMillis() > loginUser.getResourceValidateTime()) {
                textProcess.setText(R.string.request_ing);
                //请求资源
                resourceRequester.refreshResource(loginUser);
            } else {
                connectIM();
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
    private void connectIM() {
        // 确保MobileIMSDK被初始化哦（整个APP生生命周期中只需调用一次哦）
        // 提示：在不退出APP的情况下退出登陆后再重新登陆时，请确保调用本方法一次，不然会报code=203错误哦！
        IMClient.getInstance(this).initMobileIMSDK();
        loginIM();
    }

    //登陆服务器
    private void loginIM() {
        // 无条件重置socket，防止首次登陆时用了错误的ip或域名，下次登陆时sendData中仍然使用老的ip
        // 说明：本行代码建议仅用于Demo时，生产环境下是没有意义的，因为你的APP里不可能连IP都搞错了
        // LocalSocketProvider.getInstance().closeLocalSocket();

        // * 设置好服务端反馈的登陆结果观察者（当客户端收到服务端反馈过来的登陆消息时将被通知）
        IMClient.getInstance(this).getChatBaseListener().setLoginOkForLaunchObserver((o, data) -> {
            // 服务端返回的登陆结果值
            int code = (Integer) data;
            // 登陆成功
            if (code != 0) {
                //** 提示：登陆/连接 MobileIMSDK服务器成功后的事情在此实现即可
                ToastUtil.showToastShort(getString(R.string.tip_im_login_error) + code);
            }else{
                //进入主页面
                enterMainView();
            }
        });

        String loginName = String.valueOf(loginUser.getUserInfo().getId());
        String loginToken = new Gson().toJson(loginUser.getToken());

        // 异步提交登陆id和token
        new LocalDataSender.SendLoginDataAsync(new PLoginInfo(loginName, loginToken)) {
            /**
             * 登陆信息发送完成后将调用本方法（注意：此处仅是登陆信息发送完成
             * ，真正的登陆结果要在异步回调中处理哦）。
             *
             * @param code 数据发送返回码，0 表示数据成功发出，否则是错误码
             */
            @Override
            protected void fireAfterSendLogin(int code) {
                if (code == 0) {
                    // ToastUtil.showToastShort("数据发送成功！");
                    Log.d(MainActivity.class.getSimpleName(), "登陆/连接信息已成功发出！");
                } else {
                    ToastUtil.showToastShort("数据发送失败。错误码是：" + code + "！");
                }
            }
        }.execute();
    }

    private void enterMainView() {
        Handler handler = new Handler(msg -> {
            MainActivity.startActivity(LoginActivity.this);
            finish();
        });
        //延时进入主页面
        PeopletyApplication.getGlobalExecutor().executeInBackground(() -> handler.sendEmptyMessage(0), 200);
    }

    //显示登陆界面
    private void showLoginView() {
        layout_bottom.setVisibility(View.INVISIBLE);
        layout_top.setVisibility(View.VISIBLE);
    }

    //显示slogan界面
    private void showSloganView() {
        layout_bottom.setVisibility(View.VISIBLE);
        layout_top.setVisibility(View.INVISIBLE);
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
            connectIM();
        }
    }
}