package gaozhi.online.peoplety.ui.activity.chat.conversation;

import android.content.Context;
import gaozhi.online.peoplety.entity.client.Conversation;
import gaozhi.online.base.ui.recycler.BaseCell;

/**
 * 会话的BaseCell
 */
public class ConversationCell extends BaseCell<Conversation> {
    public ConversationCell(Conversation conversation) {
        super(conversation);
    }

    /**
     * 获取viewType
     *
     * @return 返回类型
     */
    @Override
    public int getItemType() {
        return 0;
    }

    /**
     * 获取item的id
     *
     * @return
     */
    @Override
    public long getItemId() {
        return data.getId();
    }
}
