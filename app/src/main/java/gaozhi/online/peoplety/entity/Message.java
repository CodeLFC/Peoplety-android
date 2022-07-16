package gaozhi.online.peoplety.entity;

import io.realm.RealmObject;
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
public class Message  extends RealmObject {
    public static final long SERVER = -1;
    private int type;
    //消息来源
    private long fromId;
    //消息去向
    private long toId;
    //消息内容
    private String msg;
    //消息时间
    private long time;

    //消息类型
    public enum Type {
        UNKNOWN(-1),
        SYSTEM(0),
        NEW_COMMENT(1),
        NEW_FANS(2);
        private final int type;

        Type(int type) {
            this.type = type;
        }

        public int getType() {
            return type;
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
}
