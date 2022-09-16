package gaozhi.online.peoplety.ui.activity.home.fragment.message;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import gaozhi.online.base.net.http.DataHelper;
import gaozhi.online.peoplety.R;
import gaozhi.online.peoplety.entity.Message;
import gaozhi.online.peoplety.entity.dto.UserDTO;
import gaozhi.online.peoplety.service.user.GetUserInfoService;
import gaozhi.online.peoplety.ui.widget.NoAnimatorRecyclerView;
import gaozhi.online.peoplety.util.GlideUtil;

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
        private Context context;
        private TextView textType;
        private TextView textRemark;
        private ImageView imageUnread;
        private ImageView imageHead;
        private Message message;
        private GetUserInfoService getUserInfoService = new GetUserInfoService(new DataHelper.OnDataListener<UserDTO>() {
            /**
             * 是否是本地数据
             *
             * @param id
             * @param data
             * @param local
             */
            @Override
            public void handle(int id, UserDTO data, boolean local) {
                if (data == null) return;
                GlideUtil.loadRoundRectangleImage(context, data.getUserInfo().getHeadUrl(), imageHead);
                if(message == null)return;
                textType.setText(data.getDescriptionString()+Message.Type.getType(message.getType()).getRemark());
            }
        }, true);

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            this.context = itemView.getContext();
            textType = itemView.findViewById(R.id.item_recycler_msg_text_type);
            textRemark = itemView.findViewById(R.id.item_recycler_msg_text_remark);
            imageUnread = itemView.findViewById(R.id.item_recycler_msg_image_unread);
            imageHead = itemView.findViewById(R.id.item_recycler_msg_image_head);
        }

        @Override
        protected void onItemCLicked(int position) {
            super.onItemCLicked(position);
            imageUnread.setVisibility(View.INVISIBLE);
        }

        @Override
        public void bindView(Message item) {
            this.message = item;
            textType.setText(Message.Type.getType(item.getType()).getRemark());
            textRemark.setText(item.getRemark());
            imageUnread.setVisibility(item.isRead() ? View.INVISIBLE : View.VISIBLE);
            getUserInfoService.request(null, item.getFromId());
        }
    }
}
