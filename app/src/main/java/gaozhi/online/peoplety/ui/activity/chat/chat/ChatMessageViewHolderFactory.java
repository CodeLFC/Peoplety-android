package gaozhi.online.peoplety.ui.activity.chat.chat;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import gaozhi.online.base.net.http.DataHelper;
import gaozhi.online.base.ui.recycler.ViewHolderFactory;
import gaozhi.online.peoplety.PeopletyApplication;
import gaozhi.online.peoplety.R;
import gaozhi.online.peoplety.entity.Message;
import gaozhi.online.peoplety.entity.Record;
import gaozhi.online.peoplety.entity.dto.UserDTO;
import gaozhi.online.peoplety.service.user.GetUserInfoService;
import gaozhi.online.peoplety.ui.activity.personal.PersonalActivity;
import gaozhi.online.peoplety.ui.activity.record.RecordDetailActivity;
import gaozhi.online.peoplety.util.DateTimeUtil;
import gaozhi.online.peoplety.util.GlideUtil;

public class ChatMessageViewHolderFactory extends ViewHolderFactory<Message, ChatCell> {
    private final UserDTO loginUser;
    private final long friendId;

    public ChatMessageViewHolderFactory(Context context, long friendId) {
        super(context);
        loginUser = ((PeopletyApplication) context.getApplicationContext()).getLoginUser();
        this.friendId = friendId;
    }


    /**
     * 根据viewType创建对应的viewHolder
     *
     * @param parent
     * @param viewType
     * @return
     */
    @Override
    public RecyclerView.ViewHolder createViewHolder(ViewGroup parent, int viewType) {
        if (viewType >= ChatCell.TYPE_OFFSET) {//特殊类型
            return new NotifyViewHolder(inflateLayout(parent, R.layout.item_chat_msg_notify), loginUser, friendId);
        }
        //消息类型
        return new TextMsgViewHolder(inflateLayout(parent, R.layout.item_chat_msg_text), loginUser, friendId);
    }

    /**
     * 把数据cell绑定到对应的holder中
     *
     * @param holder
     * @param cell
     */
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, ChatCell cell) {
        BaseViewHolder<ChatCell> baseViewHolder = (BaseViewHolder<ChatCell>) holder;
        baseViewHolder.bindView(cell);
    }

    /**
     * 通知消息
     */
    public static class NotifyViewHolder extends BaseViewHolder<ChatCell> {
        private final UserDTO loginUser;
        private UserDTO friendUser;
        private Context context;
        private ImageView imageView;
        private TextView textViewContent;
        private TextView textViewTime;
        private static final Gson gson = new Gson();

        public NotifyViewHolder(@NonNull View itemView, UserDTO loginUser, long friendId) {
            super(itemView);
            this.loginUser = loginUser;
            context = itemView.getContext();
            imageView = itemView.findViewById(R.id.chat_item_notify_img_head);
            textViewContent = itemView.findViewById(R.id.chat_item_notify_text_content);
            textViewTime = itemView.findViewById(R.id.chat_item_notify_text_time);
            new GetUserInfoService(new DataHelper.OnDataListener<UserDTO>() {
                /**
                 * 是否是本地数据
                 *
                 * @param id
                 * @param data
                 * @param local
                 */
                @Override
                public void handle(int id, UserDTO data, boolean local) {
                    friendUser = data;
                }
            }, true).request(loginUser.getToken(), friendId);
            imageView.setOnClickListener(v -> PersonalActivity.startActivity(context, friendId, true));
        }

        @Override
        public void bindView(ChatCell cell) {
            //时间
            textViewTime.setText(DateTimeUtil.getChatTime(cell.getData().getTime()));
            //内容
            Message message = cell.getData();
            Record record = gson.fromJson(message.getMsg(), Record.class);
            if (record == null) {
                textViewContent.setText(message.getRemark());
            } else {
                //点击效果
                textViewContent.setOnClickListener(v -> RecordDetailActivity.startActivity(context, record.getId()));
                textViewContent.setText(Message.Type.getType(message.getType()).getRemark() + "《" + record.getTitle() + "》");
            }
            if (friendUser != null)
                GlideUtil.loadRoundRectangleImage(context, friendUser.getUserInfo().getHeadUrl(), imageView);
        }
    }

    /**
     * 文本消息
     */
    public static class TextMsgViewHolder extends BaseViewHolder<ChatCell> {
        private final UserDTO loginUser;
        private UserDTO friendUser;
        private Context context;
        private View left;
        private ImageView imageViewLeft;
        private TextView textViewContentLeft;
        private TextView textViewTimeLeft;

        private View right;
        private ImageView imageView;
        private TextView textViewContent;
        private TextView textViewTime;


        public TextMsgViewHolder(View itemView, UserDTO loginUser, long friendId) {
            super(itemView);
            context = itemView.getContext();
            this.loginUser = loginUser;
            //绑定控件
            left = itemView.findViewById(R.id.item_chat_msg_text_view_left);
            imageViewLeft = itemView.findViewById(R.id.chat_item_left_img_head);
            textViewContentLeft = itemView.findViewById(R.id.chat_item_left_text_content);
            textViewTimeLeft = itemView.findViewById(R.id.chat_item_left_text_time);
            //绑定控件
            right = itemView.findViewById(R.id.item_chat_msg_text_view_right);
            imageView = itemView.findViewById(R.id.chat_item_right_img_head);
            textViewContent = itemView.findViewById(R.id.chat_item_right_text_content);
            textViewTime = itemView.findViewById(R.id.chat_item_right_text_time);
            new GetUserInfoService(new DataHelper.OnDataListener<UserDTO>() {
                /**
                 * 是否是本地数据
                 *
                 * @param id
                 * @param data
                 * @param local
                 */
                @Override
                public void handle(int id, UserDTO data, boolean local) {
                    friendUser = data;
                }
            }, true).request(loginUser.getToken(), friendId);
            imageViewLeft.setOnClickListener(v -> PersonalActivity.startActivity(context, friendId));
            imageView.setOnClickListener(v -> PersonalActivity.startActivity(context, loginUser.getUserInfo().getId()));
        }

        @Override
        public void bindView(ChatCell cell) {
            if (cell.getData().getFromId() == loginUser.getUserInfo().getId()) {
                //我自己发的
                left.setVisibility(View.INVISIBLE);
                right.setVisibility(View.VISIBLE);
                //时间
                textViewTime.setText(DateTimeUtil.getChatTime(cell.getData().getTime()));
                //内容
                textViewContent.setText(cell.getData().getMsg());
                GlideUtil.loadRoundRectangleImage(context, loginUser.getUserInfo().getHeadUrl(), imageView);
            } else {
                //朋友发来的
                left.setVisibility(View.VISIBLE);
                right.setVisibility(View.INVISIBLE);
                //时间
                textViewTimeLeft.setText(DateTimeUtil.getChatTime(cell.getData().getTime()));
                //内容
                textViewContentLeft.setText(cell.getData().getMsg());
                if (friendUser != null)
                    GlideUtil.loadRoundRectangleImage(context, friendUser.getUserInfo().getHeadUrl(), imageViewLeft);
            }
        }
    }

}
