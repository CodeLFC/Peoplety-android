package gaozhi.online.peoplety.ui.activity.personal;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import gaozhi.online.base.net.http.DataHelper;
import gaozhi.online.peoplety.R;
import gaozhi.online.peoplety.entity.Friend;
import gaozhi.online.peoplety.entity.Token;
import gaozhi.online.peoplety.entity.UserInfo;
import gaozhi.online.peoplety.entity.dto.UserDTO;
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

    public static class FriendViewHolder extends NoAnimatorRecyclerView.BaseViewHolder<Friend> implements DataHelper.OnDataListener<UserDTO> {
        private final Token token;
        private boolean showAttention;
        //ui
        private final Context context;
        private final ImageView imageHead;
        private final ImageView imageGender;
        private final TextView textName;
        private final TextView textRemark;
        private final TextView textAttention;

        //service
        private final GetUserInfoService getUserInfoService = new GetUserInfoService(this);
        private final GetFriendService getFriendService = new GetFriendService(new DataHelper.OnDataListener<Friend>() {
            @Override
            public void handle(int id, Friend data, boolean local) {
                if (data == null) {
                    if (showAttention)
                        textAttention.setVisibility(View.VISIBLE);
                    return;
                }
                //设置备注
                textName.setText(data.getRemark());
                textAttention.setVisibility(View.GONE);
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
            showAttention(true);
        }

        @Override
        public void bindView(Friend item) {
            long anotherId = item.getUserid() == token.getUserid() ? item.getFriendId() : item.getUserid();
            if (item.getUserid() == token.getUserid()) {
                textName.setText(item.getRemark());
            } else {
                //只有在粉丝页面才获取关注信息,否则就是已关注状态
                getFriendService.request(token, anotherId);
            }
            //获取用户信息
            getUserInfoService.request(token, anotherId);
        }

        public void bindView(long friendId) {
            if (token.getUserid() != friendId) {
                getFriendService.request(token, friendId);
            }
            //获取用户信息
            getUserInfoService.request(token, friendId);
        }

        public void showAttention(boolean show) {
            showAttention = show;
            textAttention.setVisibility(show ? View.VISIBLE : View.GONE);
        }

        @Override
        public void handle(int id, UserDTO data) {
            GlideUtil.loadRoundRectangleImage(context, data.getUserInfo().getHeadUrl(), imageHead);
            if (StringUtil.isEmpty(textName.getText().toString())) {
                textName.setText(data.getUserInfo().getNick());
            }
            textRemark.setText(data.getUserInfo().getRemark());
            UserInfo.Gender gender = UserInfo.Gender.getGender(data.getUserInfo().getGender());
            imageGender.setImageResource(gender == UserInfo.Gender.MALE ? R.drawable.male : gender == UserInfo.Gender.FEMALE ? R.drawable.female : R.drawable.other_gender);
        }

        @Override
        public void error(int id, int code, String message, String data) {
            ToastUtil.showToastShort(message + data);
        }
    }
}