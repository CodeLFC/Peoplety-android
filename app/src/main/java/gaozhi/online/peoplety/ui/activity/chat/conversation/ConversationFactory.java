package gaozhi.online.peoplety.ui.activity.chat.conversation;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;


import com.bumptech.glide.Glide;

import gaozhi.online.base.net.http.DataHelper;
import gaozhi.online.base.ui.recycler.ViewHolderFactory;
import gaozhi.online.peoplety.PeopletyApplication;
import gaozhi.online.peoplety.R;
import gaozhi.online.peoplety.entity.Friend;
import gaozhi.online.peoplety.entity.client.Conversation;
import gaozhi.online.peoplety.entity.dto.UserDTO;
import gaozhi.online.peoplety.service.friend.GetFriendService;
import gaozhi.online.peoplety.service.user.GetUserInfoService;
import gaozhi.online.peoplety.ui.activity.chat.ChatActivity;
import gaozhi.online.peoplety.util.DateTimeUtil;
import gaozhi.online.peoplety.util.GlideUtil;
import io.realm.Realm;

/**
 * 会话工厂
 */
public class ConversationFactory extends ViewHolderFactory<Conversation, ConversationCell> {
    private UserDTO loginUser;

    public ConversationFactory(Context context) {
        super(context);
        loginUser = ((PeopletyApplication) context.getApplicationContext()).getLoginUser();
    }

    @Override
    public RecyclerView.ViewHolder createViewHolder(ViewGroup parent, int viewType) {
        return new FriendConversationViewHolder(inflateLayout(parent, R.layout.item_conversation_friend), loginUser);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, ConversationCell cell) {
        Conversation conversation = cell.getData();
        FriendConversationViewHolder conversationViewHolder = (FriendConversationViewHolder) holder;
        conversationViewHolder.bindView(conversation);
    }

    /**
     * 朋友会话 缓存
     */
    static class FriendConversationViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        private final Context context;
        ImageView img_left;
        TextView name;
        TextView remark;
        TextView time;
        TextView unread;
        Conversation conversation;
        private final GetFriendService getFriendService = new GetFriendService(new DataHelper.OnDataListener<Friend>() {
            /**
             * 是否是本地数据
             *
             * @param id
             * @param data
             * @param local
             */
            @Override
            public void handle(int id, Friend data, boolean local) {
                if (data == null) return;
                //显示昵称
                name.setText(data.getRemark());
                getUserInfoService.request(loginUser.getToken(), data.getFriendId());
            }
        });
        private final GetUserInfoService getUserInfoService = new GetUserInfoService(new DataHelper.OnDataListener<UserDTO>() {
            /**
             * 是否是本地数据
             *
             * @param id
             * @param data
             * @param local
             */
            @Override
            public void handle(int id, UserDTO data, boolean local) {
                if (data == null || data.getUserInfo() == null) {
                    return;
                }
                GlideUtil.loadRoundRectangleImage(context, data.getUserInfo().getHeadUrl(), img_left);
                if (name.getText().toString().isEmpty()) {
                    name.setText(data.getUserInfo().getNick());
                }
            }
        });
        private final UserDTO loginUser;

        public FriendConversationViewHolder(View itemView, UserDTO loginUser) {
            super(itemView);
            this.context = itemView.getContext();
            itemView.setOnClickListener(this);
            //绑定控件
            img_left = itemView.findViewById(R.id.conversation_friend_head);
            name = itemView.findViewById(R.id.conversation_friend_name);
            remark = itemView.findViewById(R.id.conversation_friend_remark);
            time = itemView.findViewById(R.id.conversation_friend_time);
            unread = itemView.findViewById(R.id.conversation_friend_unread);
            this.loginUser = loginUser;
            //长按删除
            itemView.setOnLongClickListener(this);
        }

        public void bindView(Conversation conversation) {
            this.conversation = conversation;
            getFriendService.request(loginUser.getToken(), conversation.getFriend());
            //显示备注
            remark.setText(conversation.getRemark());
            //显示时间
            time.setText(DateTimeUtil.getChatTime(conversation.getTime()));
            //显示未读消息数量
            unread.setText(Integer.toString(conversation.getUnread()));
            unread.setVisibility(conversation.getUnread() == 0 ? View.GONE : View.VISIBLE);
        }

        /**
         * Called when a view has been clicked.
         *
         * @param v The view that was clicked.
         */
        @Override
        public void onClick(View v) {
            if (conversation == null) return;
            //启动聊天界面
            ChatActivity.startActivity(context, conversation.getFriend());
        }

        /**
         * Called when a view has been clicked and held.
         *
         * @param v The view that was clicked and held.
         * @return true if the callback consumed the long click, false otherwise.
         */
        @Override
        public boolean onLongClick(View v) {
            ((ConversationRecyclerAdapter) getBindingAdapter()).remove(getBindingAdapterPosition());
            Realm realm = ((PeopletyApplication) context.getApplicationContext()).getRealm();
            realm.executeTransaction(realm1 -> {
                Conversation c = realm1.where(Conversation.class).equalTo("id", conversation.getId()).findFirst();
                if (c == null) {
                    return;
                }
                c.deleteFromRealm();
            });
            return true;
        }
    }
}
