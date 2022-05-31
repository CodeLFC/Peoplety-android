package gaozhi.online.peoplety.ui.activity.record;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import gaozhi.online.base.net.http.DataHelper;
import gaozhi.online.peoplety.R;
import gaozhi.online.peoplety.entity.Comment;
import gaozhi.online.peoplety.entity.IPInfo;
import gaozhi.online.peoplety.entity.Record;
import gaozhi.online.peoplety.entity.Token;
import gaozhi.online.peoplety.entity.UserInfo;
import gaozhi.online.peoplety.entity.dto.UserDTO;
import gaozhi.online.peoplety.service.constant.GetIPInfoService;
import gaozhi.online.peoplety.service.user.GetUserInfoService;
import gaozhi.online.peoplety.ui.util.WebActivity;
import gaozhi.online.peoplety.ui.widget.NoAnimatorRecyclerView;
import gaozhi.online.peoplety.util.DateTimeUtil;
import gaozhi.online.peoplety.util.GlideUtil;
import gaozhi.online.peoplety.util.StringUtil;

/**
 * 评论适配器
 */
public class CommentAdapter extends NoAnimatorRecyclerView.BaseAdapter<CommentAdapter.CommentViewHolder,
        Comment> {
    private final Token token;

    public CommentAdapter(Token token) {
        this.token = token;
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CommentViewHolder(layoutInflate(parent, R.layout.item_recycler_comment), token);
    }

    public static class CommentViewHolder extends NoAnimatorRecyclerView.BaseViewHolder<Comment> {
        private final Context context;
        private final ImageView imageHead;
        private final TextView textName;
        private final ImageView imageGender;
        private final TextView textRemark;
        private final TextView textContent;
        private final TextView textUrl;
        private final TextView textIP;
        private final TextView textTime;
        private final ImageView imageDelete;
        private final TextView textStatus;
        private final TextView textFloor;
        private final Token token;
        //service
        private final GetUserInfoService getUserInfoService = new GetUserInfoService(new DataHelper.OnDataListener<>() {

            @Override
            public void handle(int id, UserDTO data, boolean local) {
                Log.i(getClass().getName(), local + ":" + (data != null ? "" + data : "null"));
                if (data == null || data.getUserInfo() == null) {
                    return;
                }
                textName.setText(data.getUserInfo().getNick());
                GlideUtil.loadRoundRectangleImage(context, data.getUserInfo().getHeadUrl(), imageHead);
                textRemark.setText(data.getUserInfo().getRemark());
                UserInfo.Gender gender = UserInfo.Gender.getGender(data.getUserInfo().getGender());
                switch (gender) {
                    case FEMALE:
                        imageGender.setImageResource(R.drawable.female);
                        break;
                    case MALE:
                        imageGender.setImageResource(R.drawable.male);
                        break;
                    case OTHER:
                        imageGender.setImageResource(R.drawable.other_gender);
                        break;
                }
                if (data.getUserInfo().getId() == token.getUserid()) {
                    imageDelete.setVisibility(View.VISIBLE);
                } else {
                    imageDelete.setVisibility(View.GONE);
                }
                if (data.getStatus() == null) {
                    textStatus.setVisibility(View.GONE);
                } else {
                    textStatus.setVisibility(View.VISIBLE);
                    textStatus.setText(data.getStatus().getName());
                }
            }
        });
        //获取位置信息
        private final GetIPInfoService getIPInfoService = new GetIPInfoService(new DataHelper.OnDataListener<>() {
            @Override
            public void handle(int id, IPInfo data, boolean local) {
                textIP.setVisibility(View.VISIBLE);
                textIP.setText(data.getShowArea());
            }
        });

        public CommentViewHolder(@NonNull View itemView, Token token) {
            super(itemView);
            context = itemView.getContext();
            this.token = token;
            imageHead = itemView.findViewById(R.id.item_recycler_comment_image_head);
            textName = itemView.findViewById(R.id.item_recycler_comment_text_name);
            imageGender = itemView.findViewById(R.id.item_recycler_comment_image_gender);
            textRemark = itemView.findViewById(R.id.item_recycler_comment_text_remark);
            textContent = itemView.findViewById(R.id.item_recycler_comment_text_content);
            textUrl = itemView.findViewById(R.id.item_recycler_comment_text_url);
            textIP = itemView.findViewById(R.id.item_recycler_comment_text_ip);
            textTime = itemView.findViewById(R.id.item_recycler_comment_text_time);
            imageDelete = itemView.findViewById(R.id.item_recycler_comment_image_delete);
            textStatus = itemView.findViewById(R.id.item_recycler_comment_text_status);
            textFloor = itemView.findViewById(R.id.item_recycler_comment_text_floor);

        }

        @Override
        public void bindView(Comment item) {
            textContent.setText(item.getContent());
            textTime.setText(DateTimeUtil.getRecordTime(item.getTime()));
            if (!StringUtil.isEmpty(item.getUrl())) {
                textUrl.setVisibility(View.VISIBLE);
                textUrl.setOnClickListener(v -> WebActivity.startActivity(context, item.getUrl(), item.getContent()));
            } else {
                textUrl.setVisibility(View.GONE);
            }
            getIPInfoService.request(item.getIp());
            getUserInfoService.request(token, item.getUserid());
            textFloor.setText(item.getId() + context.getString(R.string.floor));
        }
    }
}
