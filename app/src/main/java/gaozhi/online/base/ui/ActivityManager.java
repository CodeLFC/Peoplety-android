package gaozhi.online.base.ui;

import android.app.Activity;
import android.content.res.Resources;
import android.preference.PreferenceManager;

import java.util.HashMap;
import java.util.Map;

import gaozhi.online.peoplety.PeopletyApplication;

/**
 * Activity manager
 */
public class ActivityManager {
    private final Map<String, BaseActivity> baseActivityMap = new HashMap<>();

    private static final ActivityManager instance = new ActivityManager();

    private ActivityManager() {

    }

    public static ActivityManager getInstance() {
        return instance;
    }

    public void push(BaseActivity baseActivity) {
        baseActivityMap.put(baseActivity.getClass().getName(), baseActivity);
    }

    public <T extends BaseActivity> T get(Class<T> klass) {
        return (T) baseActivityMap.get(klass.getName());
    }

    public void remove(BaseActivity baseActivity) {
        baseActivityMap.remove(baseActivity.getClass().getName());
    }

    public void exit() {
        for (Map.Entry<String, BaseActivity> e : baseActivityMap.entrySet()) {
            e.getValue().finish();
        }
    }

    public void setAppFontSize(float fontScale) {
        for (Activity activity : baseActivityMap.values()) {
            Resources resources = activity.getResources();
            if (resources != null) {
                android.content.res.Configuration configuration = resources.getConfiguration();
                configuration.fontScale = fontScale;
                activity.createConfigurationContext(configuration);
                activity.recreate();
            }
        }
        PreferenceManager.getDefaultSharedPreferences(PeopletyApplication.getContext()).edit().putFloat("fontScale", fontScale).apply();
    }
}
