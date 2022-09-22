package gaozhi.online.peoplety.ui.activity;


import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import gaozhi.online.base.ui.BaseActivity;
import gaozhi.online.peoplety.R;
import gaozhi.online.peoplety.ui.activity.home.MainActivity;

public class SettingsActivity extends BaseActivity {
    private TextView title;

    @Override
    protected void initParams(Intent intent) {

    }

    @Override
    protected int bindLayout() {
        return R.layout.activity_settings;
    }

    @Override
    protected void initView(View view) {
         title = $(R.id.title_text);
         title.setText(R.string.settings);
    }

    @Override
    protected void doBusiness(Context mContext) {

    }

    @Override
    public void startLaunchActivity() {
        MainActivity.startActivity(this);
    }

    @Override
    public void onClick(View v) {

    }


    public static void startActivity(Context context){
        Intent intent = new Intent(context,SettingsActivity.class);
        context.startActivity(intent);
    }
}