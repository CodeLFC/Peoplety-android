package gaozhi.online.peoplety.ui.activity.chat.conversation;

import android.content.Context;


import gaozhi.online.base.ui.recycler.BaseRecyclerAdapter;
import gaozhi.online.peoplety.entity.client.Conversation;

/**
 * 会话列表适配器
 */
public class ConversationRecyclerAdapter extends BaseRecyclerAdapter<Conversation, ConversationCell> {

    public ConversationRecyclerAdapter(Context context) {
        super(ConversationCell.class, new ConversationFactory(context));
    }
}
