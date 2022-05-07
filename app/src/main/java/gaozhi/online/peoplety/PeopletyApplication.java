package gaozhi.online.peoplety;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.orm.SugarContext;

/**
 * 程序入口
 */
public class PeopletyApplication extends Application implements Application.ActivityLifecycleCallbacks{

    private static Context context;
    private static PeopletyApplication application;
    private float fontScale;
    private SharedPreferences preferences;

    @Override
    public void onCreate() {
        super.onCreate();
        application = this;
        context = getApplicationContext();
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        fontScale = getFontScale();
        registerActivityLifecycleCallbacks(this);
        SugarContext.init(this);
    }
    @Override
    public void onTerminate() {
        super.onTerminate();
        SugarContext.terminate();
    }

    public static Context getContext(){
        return  context;
    }

    public static float getFontScale() {
        float fontScale = 1.0f;
        if (application != null) {
            fontScale = application.preferences.getFloat("fontScale", 1.0f);
        }
        return fontScale;
    }

    @Override
    public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {
        // 禁止字体大小随系统设置变化
        Resources resources = activity.getResources();
        if (resources != null && resources.getConfiguration().fontScale != fontScale) {
            android.content.res.Configuration configuration = resources.getConfiguration();
            configuration.fontScale = fontScale;
            //resources.updateConfiguration(configuration, resources.getDisplayMetrics());
            activity.createConfigurationContext(configuration);
        }
    }

    @Override
    public void onActivityStarted(@NonNull Activity activity) {

    }

    @Override
    public void onActivityResumed(@NonNull Activity activity) {

    }

    @Override
    public void onActivityPaused(@NonNull Activity activity) {

    }

    @Override
    public void onActivityStopped(@NonNull Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(@NonNull Activity activity) {

    }
}
