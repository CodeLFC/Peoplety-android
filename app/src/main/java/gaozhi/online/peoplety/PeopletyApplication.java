package gaozhi.online.peoplety;

import android.app.Activity;
import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import gaozhi.online.base.asynchronization.GlobalExecutor;
import gaozhi.online.peoplety.entity.*;
import gaozhi.online.peoplety.im.IMClient;
import gaozhi.online.peoplety.ui.service.GeniusService;
import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * 程序入口
 */
public class PeopletyApplication extends Application implements Application.ActivityLifecycleCallbacks {
    //软件版本
    public static final Version version = Version._1_1;
    private static Context context;
    private static PeopletyApplication application;
    private float fontScale;
    private SharedPreferences preferences;

    //数据库
    private final String RDB_NAME = "peoplety.realm";
    private final long RDB_VERSION = 1;
    //线程
    private static GlobalExecutor globalExecutor;

    @Override
    public void onCreate() {
        super.onCreate();
        application = this;
        context = getApplicationContext();
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        fontScale = getFontScale();
        registerActivityLifecycleCallbacks(this);

        Realm.init(this);
        RealmConfiguration config = new RealmConfiguration.Builder().name(RDB_NAME).schemaVersion(RDB_VERSION).deleteRealmIfMigrationNeeded().allowWritesOnUiThread(true).build();
        Realm.setDefaultConfiguration(config);

        globalExecutor = new GlobalExecutor();
        //绑定服务
        doBindService();
        // 确保MobileIMSDK被初始化哦（整个APP生生命周期中只需调用一次哦）
        // 提示：在不退出APP的情况下退出登陆后再重新登陆时，请确保调用本方法一次，不然会报code=203错误哦！
        IMClient.getInstance(this).initMobileIMSDK();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();

        IMClient.getInstance(this).release();
        //解绑服务
        doUnbindService();
    }

    public static Context getContext() {
        return context;
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
            resources.updateConfiguration(configuration, resources.getDisplayMetrics());
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

    public static GlobalExecutor getGlobalExecutor() {
        return globalExecutor;
    }

    //--------------------------------------------------------------- 前台服务相关代码 START
    public static void showNotification(String title, String content) {
        if (boundService != null) {
            boundService.showNotification(title, content);
        }
    }

    /**
     * 前台服务对象（绑定MobileIMSDK的Demo后，确保Demo能常驻内存，因为Andriod高版本对于进程保活、网络保活现在限制非常严格）
     */
    private static GeniusService boundService;

    /**
     * 绑定时需要使用的连接对象
     */
    private final ServiceConnection serviceConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            boundService = ((GeniusService.LocalBinder) service).getService();
        }

        public void onServiceDisconnected(ComponentName className) {
            boundService = null;
        }
    };

    /**
     * 将本activity与后台服务绑定起来.
     */
    protected void doBindService() {
        bindService(new Intent(this.getApplicationContext(), GeniusService.class), serviceConnection, Context.BIND_AUTO_CREATE);
    }

    /**
     * 解绑服务（服务将失去功能，随时会被系统回收）.
     */
    protected void doUnbindService() {
        try {
            unbindService(serviceConnection);
        } catch (Exception e) {
            Log.w(getClass().getName(), e);
        }
    }
}
