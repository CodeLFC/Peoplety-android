package gaozhi.online.peoplety.ui.activity.home.fragment.message;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import gaozhi.online.peoplety.R;
import gaozhi.online.peoplety.entity.Message;
import gaozhi.online.peoplety.ui.widget.NoAnimatorRecyclerView;

/**
 * 消息
 */
public class MessageAdapter extends NoAnimatorRecyclerView.BaseAdapter<MessageAdapter.MessageViewHolder, Message> {
    public MessageAdapter() {
        super(Message.class);
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MessageViewHolder(layoutInflate(parent, R.layout.item_recycler_msg));
    }

    public static class MessageViewHolder extends NoAnimatorRecyclerView.BaseViewHolder<Message> {
        private TextView textType;
        private TextView textRemark;
        private ImageView imageUnread;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            textType = itemView.findViewById(R.id.item_recycler_msg_text_type);
            textRemark = itemView.findViewById(R.id.item_recycler_msg_text_remark);
            imageUnread = itemView.findViewById(R.id.item_recycler_msg_image_unread);
        }

        @Override
        public void bindView(Message item) {
            textType.setText(Message.Type.getType(item.getType()).getRemark());
            textRemark.setText(item.getRemark());
            imageUnread.setVisibility(item.isRead() ? View.INVISIBLE : View.VISIBLE);
        }
    }
}
