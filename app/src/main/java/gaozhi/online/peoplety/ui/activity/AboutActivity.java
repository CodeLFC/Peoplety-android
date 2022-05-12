package gaozhi.online.peoplety.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import gaozhi.online.base.ui.BaseActivity;
import gaozhi.online.peoplety.PeopletyApplication;
import gaozhi.online.peoplety.R;
import gaozhi.online.peoplety.Version;
import gaozhi.online.peoplety.service.NetConfig;
import gaozhi.online.peoplety.ui.util.WebActivity;

public class AboutActivity extends BaseActivity {
    private TextView title;
    private TextView textVersion;
    private TextView textVersionName;
    private TextView textVersionDescription;
    private TextView textOfficial;

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

        textVersion = $(R.id.about_activity_text_version);
        textVersionName = $(R.id.about_activity_text_version_name);
        textVersionDescription = $(R.id.about_activity_text_version_description);
        textOfficial = $(R.id.about_activity_text_official_web);
        textOfficial.setOnClickListener(this);
    }

    @Override
    protected void doBusiness(Context mContext) {
        Version version =PeopletyApplication.version;
        textVersion.setText("V"+version.getVersion());
        textVersionName.setText(version.getVersionName());
        textVersionDescription.setText(version.getVersionDescription());
        textOfficial.setText(R.string.official_web);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == textOfficial.getId()) {
            WebActivity.startActivity(this, NetConfig.officialURL, getString(R.string.official_web));
        }
    }

    public static void startActivity(Context context) {
        Intent intent = new Intent(context, AboutActivity.class);
        context.startActivity(intent);
    }
}