package gaozhi.online.peoplety.ui.activity.personal;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import gaozhi.online.peoplety.R;
import gaozhi.online.peoplety.ui.base.DBBaseActivity;
import io.realm.Realm;

/**
 * 个人主页
 */
public class PersonalActivity extends DBBaseActivity {
    private static final String INTENT_USER_ID = "user_id";

    public static void startActivity(Context context, long userid) {
        Intent intent = new Intent(context, PersonalActivity.class);
        intent.putExtra(INTENT_USER_ID, userid);
        context.startActivity(intent);
    }

    //intent
    private long userid;
    //ui
    private TextView textTitle;
    //service

    @Override
    protected void initParams(Intent intent) {
        userid = intent.getLongExtra(INTENT_USER_ID, 0);
    }

    @Override
    protected int bindLayout() {
        return R.layout.activity_personal;
    }

    @Override
    protected void initView(View view) {
    }

    @Override
    protected void doBusiness(Context mContext) { 
    }

    @Override
    protected void doBusiness(Realm realm) {

    }

    @Override
    public void onClick(View v) {

    }
}