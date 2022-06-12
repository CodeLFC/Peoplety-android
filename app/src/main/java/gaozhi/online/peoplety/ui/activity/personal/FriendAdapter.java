package gaozhi.online.peoplety.ui.activity.personal;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import gaozhi.online.base.net.Result;
import gaozhi.online.base.net.http.DataHelper;
import gaozhi.online.peoplety.R;
import gaozhi.online.peoplety.entity.Friend;
import gaozhi.online.peoplety.entity.Token;
import gaozhi.online.peoplety.entity.UserInfo;
import gaozhi.online.peoplety.entity.dto.UserDTO;
import gaozhi.online.peoplety.service.friend.AddAttentionService;
import gaozhi.online.peoplety.service.friend.GetFriendService;
import gaozhi.online.peoplety.service.user.GetUserInfoService;
import gaozhi.online.peoplety.ui.widget.NoAnimatorRecyclerView;
import gaozhi.online.peoplety.util.GlideUtil;
import gaozhi.online.peoplety.util.StringUtil;
import gaozhi.online.peoplety.util.ToastUtil;

public class FriendAdapter extends NoAnimatorRecyclerView.BaseAdapter<FriendAdapter.FriendViewHolder, Friend> {
    private final Token token;

    public FriendAdapter(Token token) {
        super(Friend.class);
        this.token = token;
    }

    @NonNull
    @Override
    public FriendViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new FriendViewHolder(layoutInflate(parent, R.layout.item_recycler_friend), token);
    }

    public static class FriendViewHolder extends NoAnimatorRecyclerView.BaseViewHolder<Friend> implements DataHelper.OnDataListener<UserDTO>, View.OnClickListener {
        private final Token token;
        private boolean showAttention;
        private long friendId;
        //ui
        private final Context context;
        private final ImageView imageHead;
        private final ImageView imageGender;
        private final TextView textName;
        private final TextView textRemark;
        private final TextView textAttention;
        private final TextView textStatus;
        //service
        private final GetUserInfoService getUserInfoService = new GetUserInfoService(this);
        private final GetFriendService getFriendService = new GetFriendService(new DataHelper.OnDataListener<>() {
            @Override
            public void handle(int id, Friend data, boolean local) {
                if (data == null) {
                    if (showAttention && !local)
                        textAttention.setVisibility(View.VISIBLE);
                    return;
                }
                textName.setText(data.getRemark());
                textAttention.setVisibility(View.GONE);
            }
        });
        //关注
        private final AddAttentionService addAttentionService = new AddAttentionService(new DataHelper.OnDataListener<>() {
            @Override
            public void handle(int id, Result data) {
                ToastUtil.showToastShort(R.string.tip_attention_success);
                textAttention.setVisibility(View.GONE);
            }

            @Override
            public void error(int id, int code, String message, String data) {
                ToastUtil.showToastShort(message + data);
            }
        });

        public FriendViewHolder(@NonNull View itemView, Token token) {
            super(itemView);
            context = itemView.getContext();
            this.token = token;
            imageHead = itemView.findViewById(R.id.item_recycler_friend_image_head);
            imageGender = itemView.findViewById(R.id.item_recycler_friend_image_gender);
            textName = itemView.findViewById(R.id.item_recycler_friend_text_name);
            textRemark = itemView.findViewById(R.id.item_recycler_friend_text_remark);
            textAttention = itemView.findViewById(R.id.item_recycler_friend_text_attention);
            textStatus = itemView.findViewById(R.id.item_recycler_friend_text_status);
            textAttention.setOnClickListener(this);
            itemView.setOnClickListener(this);
            showAttention(true);
        }

        @Override
        public void bindView(Friend item) {
            long anotherId = item.getUserid() == token.getUserid() ? item.getFriendId() : item.getUserid();
            if (item.getUserid() == token.getUserid()) {
                textName.setText(item.getRemark());
            }
            //获取用户信息
            getUserInfoService.request(token, anotherId);
            friendId = anotherId;
        }

        public void bindView(long friendId) {
            this.friendId = friendId;
            //获取用户信息
            getUserInfoService.request(token, friendId);
        }

        public void showAttention(boolean show) {
            showAttention = show;
        }

        @Override
        public void handle(int id, UserDTO data) {
            if (data == null || data.getUserInfo() == null) return;
            GlideUtil.loadRoundRectangleImage(context, data.getUserInfo().getHeadUrl(), imageHead);
            textName.setText(data.getUserInfo().getNick());
            if (data.getStatus() != null) {
                textStatus.setText(data.getStatus().getName());
            }
            textRemark.setText(data.getUserInfo().getRemark());
            UserInfo.Gender gender = UserInfo.Gender.getGender(data.getUserInfo().getGender());
            imageGender.setImageResource(gender == UserInfo.Gender.MALE ? R.drawable.male : gender == UserInfo.Gender.FEMALE ? R.drawable.female : R.drawable.other_gender);
            if (token.getUserid() != data.getUserInfo().getId()) {
                getFriendService.request(token, data.getUserInfo().getId());
            }
        }

        @Override
        public void error(int id, int code, String message, String data) {
            ToastUtil.showToastShort(message + data);
        }

        @Override
        public void onClick(View v) {
            if (v.getId() == textAttention.getId()) {
                addAttentionService.request(token, friendId);
                return;
            }
            if (v.getId() == itemView.getId()) {
                PersonalActivity.startActivity(context, friendId);
            }
        }
    }
}