package gaozhi.online.base.ui;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import java.util.HashMap;
import java.util.Map;

/**
 * 通知
 */
@RequiresApi(api = Build.VERSION_CODES.O)
public class BaseNotification{
    public enum NotifyChannel{
        //文本
        IM_MSG("im_msg_str","消息"),
        //前台服务
        FOREGROUND_SERVICE("foreground_service","消息监听服务")
        ;
        NotifyChannel(String channelId,String channelName){
            this.channelId=channelId;
            this.channelName=channelName;
        }
        private final String channelId;
        private final String channelName;
        public String getChannelId() {
            return channelId;
        }

        public String getChannelName() {
            return channelName;
        }
    }

    protected Context context;
    private final NotificationManager manager;
    private final NotifyChannel channel;
    protected int id;
    private static final Map<Integer ,BaseNotification> notifications=new HashMap<>();
    /**
     *
     * @param context 上下文
     * @param channel 通知所属的通道
     * @param id 每个通知所指定的id，要不一样
     */
    public BaseNotification(Context context,NotifyChannel channel,int id){
        manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        //前台服务
        if(manager.getNotificationChannel(NotifyChannel.FOREGROUND_SERVICE.getChannelId())==null){
            NotificationChannel channelFOREGROUND_SERVICE=new NotificationChannel(NotifyChannel.FOREGROUND_SERVICE.getChannelId(), NotifyChannel.FOREGROUND_SERVICE.getChannelName(),NotificationManager.IMPORTANCE_LOW);
            manager.createNotificationChannel(channelFOREGROUND_SERVICE);

        }
        //消息通知
        if(manager.getNotificationChannel(NotifyChannel.IM_MSG.getChannelId())==null) {
            NotificationChannel channelIM_MSG = new NotificationChannel(NotifyChannel.IM_MSG.getChannelId(), NotifyChannel.IM_MSG.getChannelName(), NotificationManager.IMPORTANCE_HIGH);
            manager.createNotificationChannel(channelIM_MSG);
        }
        this.channel=channel;
        this.context=context;
        this.id=id;
        notifications.put(id,this);
    }

    /**
     * 获取通知的构建器
     * @return
     */
    protected NotificationCompat.Builder builder(){
        return new NotificationCompat.Builder(context,channel.getChannelId());
    }

    /**
     * 显示构建的通知
     * @param notification
     */
    protected void show(Notification notification){
        manager.notify(id,notification);
    }
    public void cancel(){
        manager.cancel(id);
    }
    //获取通知
    protected static BaseNotification getNotification(int id){
       return notifications.get(id);
    }
    //移除通知
    protected static void removeNotification(int id){
          notifications.remove(id);
    }
}
