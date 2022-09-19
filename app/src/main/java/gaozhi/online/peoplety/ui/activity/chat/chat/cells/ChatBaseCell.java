package gaozhi.online.peoplety.ui.activity.chat.chat.cells;

import android.content.Context;

import gaozhi.online.base.ui.recycler.BaseCell;
import gaozhi.online.peoplety.entity.Message;
import gaozhi.online.peoplety.entity.UserInfo;
import gaozhi.online.peoplety.entity.dto.UserDTO;


/**
 * 聊天的BaseCell
 */
public abstract class ChatBaseCell extends BaseCell<Message> {
    protected Context context;
    protected UserDTO user;
    protected UserInfo friendInfo;

    public ChatBaseCell(Context context, Message message, UserDTO user, UserInfo friendInfo) {
        super(message);
        this.context = context;
        this.user = user;
        this.friendInfo = friendInfo;
    }

    public long getTime() {
        return data.getTime();
    }

    /**
     * 获取item的id
     *
     * @return
     */
    @Override
    public long getItemId() {
        return data.getItemId();
    }
}
