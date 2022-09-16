package gaozhi.online.peoplety.ui.base;

import static android.content.Context.NOTIFICATION_SERVICE;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;

import java.util.function.Consumer;

import gaozhi.online.peoplety.R;
import gaozhi.online.peoplety.util.GlideUtil;

/**
 * 通知
 */
public class NotificationBuilder {
    public enum NChannel {
        ALIVE_SERVICE_CHANNEL("1", "keep_alive", NotificationManager.IMPORTANCE_HIGH),
        IM_CHANNEL("2", "im", NotificationManager.IMPORTANCE_MAX);
        private final String id;
        private final String name;
        private final int important;

        NChannel(String id, String name, int important) {
            this.id = id;
            this.name = name;
            this.important = important;
        }
    }

    private final Context context;
    private final NotificationManager manager;
    private final String channelId;
    private Notification notification;

    public NotificationBuilder(Context context, NChannel nChannel) {
        this.context = context;
        channelId = nChannel.id;
        manager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        // 以下代码，解决在Android 8及以上代码中，无法正常显示Notification或报"Bad notification for startForeground"等问题
        NotificationChannel notificationChannel = new NotificationChannel(nChannel.id, nChannel.name, nChannel.important);
        notificationChannel.enableLights(true);

        notificationChannel.setLightColor(Color.RED);
        notificationChannel.setShowBadge(true);
        notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
        manager.createNotificationChannel(notificationChannel);
    }

    public Notification buildMessageNotification(String title, String content) {
        return buildMessageNotification(title, content, System.currentTimeMillis());
    }

    public Notification buildMessageNotification(String title, String content, PendingIntent intent) {
        return buildMessageNotification(title, content, System.currentTimeMillis(), intent);
    }

    public Notification buildMessageNotification(String title, String content, long time) {
        return buildMessageNotification(title, content, time, null);
    }

    public void buildMessageNotification(String title, String content, long time, PendingIntent intent, String imageUrl, Consumer<Notification> notificationConsumer) {
        GlideUtil.loadBitmap(context, imageUrl, R.drawable.app_logo, new CustomTarget<>() {
            /**
             * The method that will be called when the resource load has finished.
             *
             * @param resource   the loaded resource.
             * @param transition
             */
            @Override
            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                if (notificationConsumer == null) return;
                notificationConsumer.accept(buildMessageNotification(title, content, time, intent, resource));
            }

            @Override
            public void onLoadCleared(@Nullable Drawable placeholder) {

            }
        });
    }

    public Notification buildMessageNotification(String title, String content, long time, PendingIntent intent, Bitmap icon) {

        Notification notification = new NotificationCompat.Builder(context, channelId)
                .setContentTitle(title)  //设置标题
                .setContentText(content) //设置内容
                .setWhen(time)  //设置时间
                .setSmallIcon(R.drawable.app_logo)  //设置小图标
                .setAutoCancel(true)
                .setContentIntent(intent)
                // 通知的显示等级（Android5.0开始，通知可以显示在锁屏上）：
                // - VISIBILITY_PRIVATE : 显示基本信息，如通知的图标，但隐藏通知的全部内容
                // - VISIBILITY_PUBLIC : 显示通知的全部内容
                // - VISIBILITY_SECRET : 不显示任何内容，包括图标
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setLargeIcon(icon)
                .build();
        return notification;
    }

    public Notification buildMessageNotification(String title, String content, long time, PendingIntent intent) {

        Notification notification = new NotificationCompat.Builder(context, channelId)
                .setContentTitle(title)  //设置标题
                .setContentText(content) //设置内容
                .setWhen(time)  //设置时间
                .setSmallIcon(R.drawable.app_logo)  //设置小图标
                .setAutoCancel(true)
                .setContentIntent(intent)
                // 通知的显示等级（Android5.0开始，通知可以显示在锁屏上）：
                // - VISIBILITY_PRIVATE : 显示基本信息，如通知的图标，但隐藏通知的全部内容
                // - VISIBILITY_PUBLIC : 显示通知的全部内容
                // - VISIBILITY_SECRET : 不显示任何内容，包括图标
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .build();
        return notification;
    }

    public <T extends Activity> PendingIntent buildPendingIntent(Class<T> activityClass) {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.setClass(context, activityClass);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        return PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE);
    }

    public void notify(int id, Notification notification) {
        manager.notify(id, notification);
    }
}
