package gaozhi.online.peoplety.ui.activity.chat.chat;

import android.content.Context;

import gaozhi.online.base.ui.recycler.BaseCell;
import gaozhi.online.peoplety.entity.Message;
import gaozhi.online.peoplety.entity.UserInfo;
import gaozhi.online.peoplety.entity.dto.UserDTO;


/**
 * 聊天的BaseCell
 */
public class ChatCell extends BaseCell<Message> {
    protected Context context;
    //大类型偏移
    public static int TYPE_OFFSET = 10000;

    public ChatCell(Context context, Message message) {
        super(message);
        this.context = context;
    }

    /**
     * 获取viewType
     *
     * @return 返回类型
     */
    @Override
    public int getItemType() {
        if (getData().getType() == Message.Type.NEW_FRIEND_MESSAGE.getType()) {
            return getData().getTypeMsg();
        }
        return TYPE_OFFSET + getData().getType();
    }

    /**
     * 获取item的id
     * @return
     */
    @Override
    public long getItemId() {
        return getData().getId();
    }
}
