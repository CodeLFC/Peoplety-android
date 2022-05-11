package gaozhi.online.peoplety.ui.activity.login;


import android.content.Context;
import android.content.Intent;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.google.gson.Gson;

import java.util.Timer;
import java.util.TimerTask;

import gaozhi.online.base.asynchronization.Handler;
import gaozhi.online.base.net.Result;
import gaozhi.online.base.net.http.ApiRequest;
import gaozhi.online.base.ui.BaseActivity;
import gaozhi.online.peoplety.R;
import gaozhi.online.peoplety.entity.VerifyCode;
import gaozhi.online.peoplety.service.user.ResetPassService;
import gaozhi.online.peoplety.service.user.SendVerifyCodeService;
import gaozhi.online.peoplety.util.PatternUtil;
import gaozhi.online.peoplety.util.StringUtil;
import gaozhi.online.peoplety.util.ToastUtil;

public class ResetPassActivity extends BaseActivity implements Handler.Worker, ApiRequest.ResultHandler {
    //service
    private SendVerifyCodeService sendVerifyCodeService;
    private ResetPassService resetPassService;
    //ui
    private ImageView image_back;
    private EditText edit_phone;
    private EditText edit_pass;
    private EditText edit_verify_pass;
    private EditText edit_verify_code;
    private Button btn_send_verify_code;
    private Button btn_reset_pass;
    //util
    private final Gson gson=new Gson();
    private Timer timer;
    private Handler handler;
    //message
    private static final int MESSAGE_TIMER=0;
    //verify_code 验证码周期剩余时间
    private long release_time;

    @Override
    protected void initParams(Intent intent) {
        handler=new Handler(this);
        sendVerifyCodeService=new SendVerifyCodeService(this);
        resetPassService=new ResetPassService(this);
        timer=new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.sendEmptyMessage(MESSAGE_TIMER);
            }
        },0,1000);
    }

    @Override
    protected int bindLayout() {
        return R.layout.activity_reset_pass;
    }

    @Override
    protected void initView(View view) {
        image_back=$(R.id.title_image_left);
        image_back.setOnClickListener(this);
        edit_phone=$(R.id.reset_pass_activity_edit_phone);
        edit_pass=$(R.id.reset_pass_activity_edit_pass);
        edit_verify_pass=$(R.id.reset_pass_activity_edit_verify_pass);
        edit_verify_code=$(R.id.reset_pass_activity_edit_verify_code);
        btn_send_verify_code=$(R.id.reset_pass_activity_btn_send_verify_code);
        btn_send_verify_code.setOnClickListener(this);
        btn_reset_pass=$(R.id.reset_pass_activity_btn_reset_pass);
        btn_reset_pass.setOnClickListener(this);
    }

    @Override
    protected void doBusiness(Context mContext) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.title_image_left:
                finish();
                return;
        }
        String phone=edit_phone.getText().toString();
        if(!PatternUtil.matchPhone(phone)){
            ToastUtil.showToastShort(R.string.tip_phone_number_error);
            return;
        }
        switch (v.getId()){
            case R.id.reset_pass_activity_btn_send_verify_code:
                sendVerifyCodeService.request(VerifyCode.NotifyMethod.SMS,VerifyCode.CodeTemplate.FORGET_PASS,phone);
                break;
            case R.id.reset_pass_activity_btn_reset_pass:
                String pass=edit_pass.getText().toString();
                String pass_verify=edit_verify_pass.getText().toString();
                if(!StringUtil.equals(pass,pass_verify)){
                    ToastUtil.showToastShort(R.string.tip_pass_not_same);
                    return;
                }
                if(pass.length()<8||pass.length()>20||!PatternUtil.matchEnglish(pass)){
                    ToastUtil.showToastShort(R.string.tip_pass_format);
                    return;
                }
                String code=edit_verify_code.getText().toString();
                if(StringUtil.isEmpty(code)){
                    ToastUtil.showToastShort(R.string.tip_verify_code_empty);
                    return;
                }
                resetPassService.request(phone,code,pass);
                break;
        }
    }
    /**
     * 启动重置密码页面
     * @param context
     */
    public static void startActivity(Context context){
        Intent intent=new Intent(context,ResetPassActivity.class);
        context.startActivity(intent);
    }

    @Override
    public void handleMessage(@NonNull Message msg) {
        switch (msg.what){
            case MESSAGE_TIMER:
                if(release_time>0){
                    btn_send_verify_code.setText(release_time+"s");
                    btn_send_verify_code.setEnabled(false);
                    release_time--;
                }else{
                    btn_send_verify_code.setText(R.string.tip_send_verify_code);
                    btn_send_verify_code.setEnabled(true);
                }
                break;
        }
    }

    @Override
    public void start(int id) {
        if(id==sendVerifyCodeService.getId()) {
            release_time = 5;
            return;
        }
        if(id==resetPassService.getId()){
            btn_reset_pass.setText(R.string.request_ing);
            btn_reset_pass.setEnabled(false);
        }
    }

    @Override
    public void handle(int id, Result result) {
        if(id==sendVerifyCodeService.getId()){
            //开始计时-
            VerifyCode verifyCode=gson.fromJson(result.getData(),VerifyCode.class);
            release_time=(verifyCode.getValidateTime()-System.currentTimeMillis())/1000;
            return;
        }
        if(id==resetPassService.getId()){
            //修改成功。。。进入登录页
            ToastUtil.showToastLong(R.string.tip_update_success);
            Log.i(TAG,result.getData());
            finish();
        }
    }

    @Override
    public void error(int id, int code, String message,String data) {
        ToastUtil.showToastLong(message+data);
        if(id==sendVerifyCodeService.getId()) {
            release_time = 0;
            return;
        }
        if(id==resetPassService.getId()){
            btn_reset_pass.setText(R.string.reset_pass);
            btn_reset_pass.setEnabled(true);
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        timer.cancel();
    }
}