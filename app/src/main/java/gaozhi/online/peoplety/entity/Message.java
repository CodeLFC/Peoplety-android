package gaozhi.online.peoplety.entity;
import android.util.Log;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

import gaozhi.online.peoplety.ui.widget.NoAnimatorRecyclerView;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author LiFucheng
 * @version 1.0
 * @description: TODO  消息
 * @date 2022/7/16 12:11
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Message extends RealmObject implements NoAnimatorRecyclerView.BaseAdapter.BaseItem {
    public static final long SERVER = -1;
    @PrimaryKey
    private long id;
    private int type;
    private int typeMsg;
    //消息来源
    private long fromId;
    //消息去向
    private long toId;
    //消息内容
    private String msg;
    //摘要
    private String remark;
    //消息时间
    private long time;

    private boolean read;

    //消息类型
    public enum Type {
        UNKNOWN(-1, "未知消息"),
        SYSTEM(0, "系统消息"),
        NEW_COMMENT(1, "新的评论"),
        NEW_FANS(2, "新的粉丝"),
        NEW_EXTEND(3, "新的派生"),
        NEW_FAVORITE(4, "新的收藏"),
        NEW_FRIEND_MESSAGE(5,"新朋友消息");
        private final int type;
        private final String remark;

        Type(int type, String remark) {
            this.type = type;
            this.remark = remark;
        }

        public int getType() {
            return type;
        }

        public String getRemark() {
            return remark;
        }

        public static Type getType(int type) {
            for (Type e : Type.values()) {
                if (e.getType() == type) {
                    return e;
                }
            }
            return UNKNOWN;
        }
    }

    public static Stream<Message> filter(List<Message> messages, Type type, boolean onlyUnread) {
        return filter(messages, new Type[]{type}, onlyUnread);
    }

    public static Stream<Message> filter(List<Message> messages, Type[] type, boolean onlyUnread) {
        Stream<Message> stream = messages.stream().filter(message -> {
            Log.i(Message.class.getName(), " message: " + message);
            boolean filter = false;
            for (Type e : type) {
                if (message.getType() == e.getType()) {//通过断言
                    filter = true;
                    break;
                }
            }
            return filter;
        });
        if (onlyUnread) {
            stream = stream.filter(message -> !message.isRead());
        }
        return stream;
    }

    //排序规则
    @Override
    public long getItemId() {
        return time;
    }
}