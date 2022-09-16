/*
 * Copyright (C) 2022  即时通讯网(52im.net) & Jack Jiang.
 * The MobileIMSDK_TCP (MobileIMSDK v6.x TCP版) Project.
 * All rights reserved.
 *
 * > Github地址：https://github.com/JackJiang2011/MobileIMSDK
 * > 文档地址：  http://www.52im.net/forum-89-1.html
 * > 技术社区：  http://www.52im.net/
 * > 技术交流群：185926912 (http://www.52im.net/topic-qqgroup.html)
 * > 作者公众号：“即时通讯技术圈】”，欢迎关注！
 * > 联系作者：  http://www.52im.net/thread-2792-1-1.html
 *
 * "即时通讯网(52im.net) - 即时通讯开发者社区!" 推荐开源工程。
 *
 * GeniusService.java at 2022-7-28 17:17:23, code by Jack Jiang.
 */
package gaozhi.online.peoplety.ui.service;

import android.app.Application;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import gaozhi.online.peoplety.R;
import gaozhi.online.peoplety.ui.activity.home.MainActivity;
import gaozhi.online.peoplety.ui.activity.login.LoginActivity;
import gaozhi.online.peoplety.ui.base.NotificationBuilder;

/**
 * 一个用于演示的前台服务实现类（本类代码，来自于RainbowChat产品：http://www.52im.net/thread-19-1-1.html）。
 * <p>
 * 目前的唯一作用是：作为前台服务，提升Demo的运行优先级，确保在高版本Andriod系统上进程保活和网络保活.
 * <p>
 * 注意：该服务与MobileIMSDK本身无关，也不是必须的！
 *
 * @author Jack Jiang(http://www.52im.net/space-uid-1.html)
 * @version 1.0
 */
public class GeniusService extends Service {

    final static String TAG = GeniusService.class.getSimpleName();
    private NotificationBuilder notificationBuilder;

    /**
     * Class for clients to access. Because we know this service always runs in
     * the same process as its clients, we don't need to deal with IPC.
     */
    public class LocalBinder extends Binder {
        public GeniusService getService() {
            return GeniusService.this;
        }
    }

    @Override
    public void onCreate() {
        notificationBuilder = new NotificationBuilder(this, NotificationBuilder.NChannel.ALIVE_SERVICE_CHANNEL);
        // Display a notification about us starting. We put an icon in the status bar.
        showNotification(getString(R.string.app_name), getString(R.string.tip_app_slogan));
    }

    @Override
    public void onDestroy() {
        //将service从前台移除，并允许随时被系统回收
        this.stopForeground(true);
        // Tell the user we stopped.
        Log.d(TAG, "服务Destroy了哦！");
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    // This is the object that receives interactions from clients. See RemoteService for a more complete example.
    private final IBinder mBinder = new LocalBinder();

    /**
     * Show a notification while this service is running.
     */
    public void showNotification(String title, String content) {
        Notification notification = notificationBuilder.buildMessageNotification(title, content, notificationBuilder.buildPendingIntent(MainActivity.class));
        // 让service在前台执行
        this.startForeground(999, notification);
    }
}