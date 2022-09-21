package gaozhi.online.peoplety.ui.activity.chat.chat;

import android.content.Context;


import gaozhi.online.base.ui.recycler.BaseRecyclerAdapter;
import gaozhi.online.peoplety.entity.Message;
import gaozhi.online.peoplety.entity.UserInfo;
import gaozhi.online.peoplety.entity.dto.UserDTO;

/**
 * 消息的列表
 */
public class ChatMsgRecyclerAdapter extends BaseRecyclerAdapter<Message, ChatCell> {
    public ChatMsgRecyclerAdapter(Context context,long friendId) {
        super(ChatCell.class, new ChatMessageViewHolderFactory(context,friendId));
    }
}
