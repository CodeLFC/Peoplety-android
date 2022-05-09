package gaozhi.online.peoplety.ui.main;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;

import gaozhi.online.base.ui.BaseActivity;
import gaozhi.online.peoplety.R;
import gaozhi.online.peoplety.entity.dto.UserDTO;
import gaozhi.online.peoplety.util.ToastUtil;

public class MainActivity extends BaseActivity{
    //intent
    private static final String INTENT_TAG_LOGIN_USER="login_user";
    private UserDTO currentUser;
    //ui
    private BottomNavigationView bottomNavigationView;
    //util
    private final Gson gson=new Gson();
    @Override
    protected void initParams(Intent intent) {
        String userJson=intent.getStringExtra(INTENT_TAG_LOGIN_USER);
        currentUser=gson.fromJson(userJson,UserDTO.class);
    }

    @Override
    protected int bindLayout() {
        return R.layout.activity_main;
    }

    @Override
    protected void initView(View view) {

    }

    @Override
    protected void doBusiness(Context mContext) {
        ToastUtil.showToastLong(currentUser.toString());
    }

    public static void startActivity(Context context, UserDTO userDTO) {
        Intent intent=new Intent(context,MainActivity.class);
        intent.putExtra(INTENT_TAG_LOGIN_USER,new Gson().toJson(userDTO));
        context.startActivity(intent);
    }

    @Override
    public void onClick(View v) {

    }
}