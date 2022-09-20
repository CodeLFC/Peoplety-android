package gaozhi.online.peoplety.entity.client;

import java.util.Objects;

import gaozhi.online.peoplety.entity.Message;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 会话
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Conversation extends RealmObject {
    @PrimaryKey
    private long id;
    private long self;
    private long friend;
    private String remark;
    private int unread;
    private long time;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Conversation that = (Conversation) o;
        return id == that.id && self == that.self && friend == that.friend && unread == that.unread && time == that.time && Objects.equals(remark, that.remark);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, self, friend, remark, unread, time);
    }

    public void wrapMessage(Message message, boolean isSelf){
        if(isSelf) {
            self = message.getFromId();
            friend = message.getToId();
        }else{
            self = message.getToId();
            friend = message.getFromId();
        }
        if(Message.Type.getType(message.getType()) == Message.Type.NEW_FRIEND_MESSAGE){
            Message.TypeMsg typeMsg = Message.TypeMsg.getType(message.getTypeMsg());
            switch (typeMsg){
                case STRING:
                    remark = message.getMsg();
                    break;
                default:
                    remark = typeMsg.getRemark();
            }
        }else{
            remark = Message.Type.getType(message.getType()).getRemark();
        }

        unread+=1;
        time = message.getTime();
    }
}
