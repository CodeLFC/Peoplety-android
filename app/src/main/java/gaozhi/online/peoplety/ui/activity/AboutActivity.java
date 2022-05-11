package gaozhi.online.peoplety.ui.activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import gaozhi.online.base.ui.BaseActivity;
import gaozhi.online.peoplety.R;

public class AboutActivity extends BaseActivity {
    private TextView title;
    @Override
    protected void initParams(Intent intent) {

    }

    @Override
    protected int bindLayout() {
        return R.layout.activity_about;
    }

    @Override
    protected void initView(View view) {
        title = $(R.id.title_text);
        title.setText(R.string.about);
    }

    @Override
    protected void doBusiness(Context mContext) {

    }

    @Override
    public void onClick(View v) {

    }

    public static void startActivity(Context context){
        Intent intent = new Intent(context,AboutActivity.class);
        context.startActivity(intent);
    }
}