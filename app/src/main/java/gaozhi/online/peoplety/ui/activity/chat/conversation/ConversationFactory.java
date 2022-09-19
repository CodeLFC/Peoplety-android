package gaozhi.online.peoplety.ui.activity.chat.conversation;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;


import gaozhi.online.base.ui.recycler.BaseCell;
import gaozhi.online.base.ui.recycler.ViewHolderFactory;
import gaozhi.online.peoplety.R;
import gaozhi.online.peoplety.entity.client.Conversation;
import gaozhi.online.peoplety.util.DateTimeUtil;
import gaozhi.online.peoplety.util.GlideUtil;

/**
 * 会话工厂
 */
public class ConversationFactory extends ViewHolderFactory<Conversation> {


    public ConversationFactory(Context context) {
        super(context);
    }

    @Override
    public RecyclerView.ViewHolder createViewHolder(ViewGroup parent, int viewType) {
        return new FriendConversationViewHolder(inflateLayout(parent, R.layout.item_conversation_friend));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, BaseCell<Conversation> cell) {
        Conversation conversation = cell.getData();
        FriendConversationViewHolder conversationViewHolder = (FriendConversationViewHolder) holder;
        //头像
        GlideUtil.loadRoundImage(context, conversation.getHeadUrl(), R.drawable.default_img_head, conversationViewHolder.img_left);
        //显示昵称
        conversationViewHolder.name.setText(conversation.getName());
        //显示备注
        conversationViewHolder.remark.setText(conversation.getRemark());
        //显示时间
        conversationViewHolder.time.setText(DateTimeUtil.getChatTime(conversation.getTime()));
        //显示未读消息数量
        conversationViewHolder.unread.setText(Integer.toString(conversation.getUnread()));
        conversationViewHolder.unread.setVisibility(conversation.getUnread() == 0 ? View.GONE : View.VISIBLE);
    }

    /**
     * 朋友会话 缓存
     */
    static class FriendConversationViewHolder extends RecyclerView.ViewHolder {
        ImageView img_left;
        TextView name;
        TextView remark;
        TextView time;
        TextView unread;

        public FriendConversationViewHolder(View itemView) {
            super(itemView);
            //绑定控件
            img_left = itemView.findViewById(R.id.conversation_friend_head);
            name = itemView.findViewById(R.id.conversation_friend_name);
            remark = itemView.findViewById(R.id.conversation_friend_remark);
            time = itemView.findViewById(R.id.conversation_friend_time);
            unread = itemView.findViewById(R.id.conversation_friend_unread);
        }
    }
}
