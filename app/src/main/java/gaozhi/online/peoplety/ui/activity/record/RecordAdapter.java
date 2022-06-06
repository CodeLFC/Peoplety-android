package gaozhi.online.peoplety.ui.activity.record;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import gaozhi.online.base.net.Result;
import gaozhi.online.base.net.http.DataHelper;
import gaozhi.online.peoplety.R;
import gaozhi.online.peoplety.entity.Area;
import gaozhi.online.peoplety.entity.Friend;
import gaozhi.online.peoplety.entity.IPInfo;
import gaozhi.online.peoplety.entity.Record;
import gaozhi.online.peoplety.entity.RecordType;
import gaozhi.online.peoplety.entity.Token;
import gaozhi.online.peoplety.entity.UserInfo;
import gaozhi.online.peoplety.entity.client.ImageModel;
import gaozhi.online.peoplety.entity.dto.RecordDTO;
import gaozhi.online.peoplety.entity.dto.UserDTO;
import gaozhi.online.peoplety.service.constant.GetIPInfoService;
import gaozhi.online.peoplety.service.friend.GetFriendService;
import gaozhi.online.peoplety.service.record.DeleteRecordByIdService;
import gaozhi.online.peoplety.service.record.GetRecordDTOByIdService;
import gaozhi.online.peoplety.service.user.GetUserInfoService;
import gaozhi.online.peoplety.ui.util.WebActivity;
import gaozhi.online.peoplety.ui.util.image.ShowImageActivity;
import gaozhi.online.peoplety.ui.util.pop.DialogPopWindow;
import gaozhi.online.peoplety.ui.widget.NoAnimatorRecyclerView;
import gaozhi.online.peoplety.util.DateTimeUtil;
import gaozhi.online.peoplety.util.GlideUtil;
import gaozhi.online.peoplety.util.PatternUtil;
import gaozhi.online.peoplety.util.StringUtil;
import gaozhi.online.peoplety.util.ToastUtil;
import io.realm.Realm;

/**
 * 记录
 */
public class RecordAdapter extends NoAnimatorRecyclerView.BaseAdapter<RecordAdapter.RecordViewHolder, Record> {
    //用于请求信息
    private final Token token;

    public RecordAdapter(Token token) {
        this(token, null);
    }

    public RecordAdapter(Token token, BaseSortedListAdapterCallback<Record> sortedListAdapterCallback) {
        super(Record.class, sortedListAdapterCallback);
        this.token = token;
    }

    @NonNull
    @Override
    public RecordViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RecordViewHolder(layoutInflate(parent, R.layout.item_recycler_record), token);
    }


    public static class RecordViewHolder extends NoAnimatorRecyclerView.BaseViewHolder<Record> implements Consumer<ImageModel>, DataHelper.OnDataListener<RecordDTO> {
        private final Context context;
        //卷宗编号
        private final TextView textFloor;
        private final TextView textTitle;
        private final ImageView imageTop;
        private final TextView textType;
        //屏蔽属性的显示
        private final TextView textArea;
        private final TextView textDescription;
        private final TextView textIp;
        private final TextView textTime;
        private final ImageAdapter imageAdapter;
        private final Realm realm;
        private List<String> imgList;
        private final ImageView imageHead;
        private final TextView textName;
        private final TextView textRemark;
        private final TextView textComment;
        private final TextView textFavorite;
        private final ImageView imageDelete;
        private final TextView textStatus;
        private final ImageView imageGender;
        private final ImageView imageComment;
        private final ImageView imageFavorite;
        private final ImageView imageFork;
        private final TextView textFork;
        private final ImageView imageLink;
        //父子
        private final TextView textParent;

        private final TextView textContent;
        //关注
        private final TextView textAttention;
        private final CommentPopWindow commentPopWindow;
        private final ChildRecordPopWindow childRecordPopWindow;
        //service
        private final GetUserInfoService getUserInfoService = new GetUserInfoService(new DataHelper.OnDataListener<>() {
            @Override
            public void handle(int id, UserDTO data, boolean local) {
                Log.i(getClass().getName(), local + ":" + (data != null ? "" + data : "null"));
                if (data == null || data.getUserInfo() == null) {
                    return;
                }
                textName.setText(data.getUserInfo().getNick());
                Log.d(getClass().getName(), "userinfo:" + data.getUserInfo());
                GlideUtil.loadRoundRectangleImage(context, data.getUserInfo().getHeadUrl(), R.drawable.app_logo, imageHead);
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
                if (data.getStatus() != null) {
                    textStatus.setText(data.getStatus().getName());
                }
            }
        });
        //获取详情
        private final GetRecordDTOByIdService getRecordDTOByIdService = new GetRecordDTOByIdService(this);
        //获取位置信息
        private final GetIPInfoService getIPInfoService = new GetIPInfoService(new DataHelper.OnDataListener<>() {
            @Override
            public void handle(int id, IPInfo data, boolean local) {
                textIp.setText(data.getShowArea());
            }
        });
        //删除评论
        private final DeleteRecordByIdService deleteRecordByIdService = new DeleteRecordByIdService(new DataHelper.OnDataListener<Result>() {
            @Override
            public void handle(int id, Result data) {
                if (getBindingAdapter() != null) {
                    ((RecordAdapter) getBindingAdapter()).remove(getAbsoluteAdapterPosition());
                } else {
                    ToastUtil.showToastShort(R.string.tip_delete_success);
                }
            }

            @Override
            public void error(int id, int code, String message, String data) {
                ToastUtil.showToastShort(message + data);
            }
        });
        //关注按钮
        private final GetFriendService getFriendService = new GetFriendService(new DataHelper.OnDataListener<>() {
            @Override
            public void handle(int id, Friend data, boolean local) {
                if (data == null && showDetails) {
                    textAttention.setVisibility(View.VISIBLE);
                    return;
                }
                textAttention.setVisibility(View.GONE);
            }
        });

        private final Token token;
        //是否完整显示内容
        private boolean showDetails;
        private Record record;

        public RecordViewHolder(@NonNull View itemView, Token token) {
            super(itemView);
            this.token = token;
            context = itemView.getContext();
            realm = Realm.getDefaultInstance();
            textTitle = itemView.findViewById(R.id.item_recycler_record_text_title);
            imageTop = itemView.findViewById(R.id.item_recycler_record_image_top);
            textType = itemView.findViewById(R.id.item_recycler_record_text_type);
            textArea = itemView.findViewById(R.id.item_recycler_record_text_area);
            textDescription = itemView.findViewById(R.id.item_recycler_record_text_description);
            textIp = itemView.findViewById(R.id.item_recycler_record_text_ip);
            textTime = itemView.findViewById(R.id.item_recycler_record_text_time);
            NoAnimatorRecyclerView recyclerView = itemView.findViewById(R.id.item_recycler_record_recycler_img);
            LinearLayoutManager linearLayout = new LinearLayoutManager(itemView.getContext());
            linearLayout.setOrientation(RecyclerView.HORIZONTAL);
            recyclerView.setLayoutManager(linearLayout);
            imageAdapter = new ImageAdapter();
            recyclerView.setAdapter(imageAdapter);

            imageAdapter.setOnItemClickedListener(this);

            imageHead = itemView.findViewById(R.id.item_recycler_record_image_head);
            textName = itemView.findViewById(R.id.item_recycler_record_text_name);
            textRemark = itemView.findViewById(R.id.item_recycler_record_text_remark);
            textComment = itemView.findViewById(R.id.item_recycler_record_text_comment_num);
            textFavorite = itemView.findViewById(R.id.item_recycler_record_text_favorite_num);
            imageDelete = itemView.findViewById(R.id.item_recycler_record_image_delete);
            imageDelete.setOnClickListener((v) -> {
                DialogPopWindow dialogPopWindow = new DialogPopWindow(context);
                dialogPopWindow.getMessage().setText(R.string.tip_ensure_delete);
                dialogPopWindow.getBtnRight().setOnClickListener(v1 -> {
                    deleteRecordByIdService.request(token, record.getId());
                    dialogPopWindow.dismiss();
                });
                dialogPopWindow.showPopupWindow(imageDelete);
            });
            textStatus = itemView.findViewById(R.id.item_recycler_record_text_status);
            imageGender = itemView.findViewById(R.id.item_recycler_record_image_gender);

            imageFavorite = itemView.findViewById(R.id.item_recycler_record_image_favorite);
            imageComment = itemView.findViewById(R.id.item_recycler_record_image_comment);
            commentPopWindow = new CommentPopWindow(context);
            imageComment.setOnClickListener(v -> {
                commentPopWindow.showPopupWindow(imageComment, record);
            });
            textContent = itemView.findViewById(R.id.item_recycler_record_text_content);


            textParent = itemView.findViewById(R.id.item_recycler_record_text_parent);

            imageFork = itemView.findViewById(R.id.item_recycler_record_image_fork);
            childRecordPopWindow = new ChildRecordPopWindow(context);
            //派生分支
            imageFork.setOnClickListener((v) -> {
                childRecordPopWindow.showPopupWindow(imageFork, record);
            });
            textFloor = itemView.findViewById(R.id.item_recycler_record_text_floor);

            textFork = itemView.findViewById(R.id.item_recycler_record_text_fork_num);

            textAttention = itemView.findViewById(R.id.item_recycler_record_text_attention);
            imageLink = itemView.findViewById(R.id.item_recycler_record_image_link);
        }

        @Override
        public void bindView(Record item) {
            if (item == null) return;
            this.record = item;
            //刷新数据
            refreshData(item);
            //从数据库和网络中获取用户
            getUserInfoService.request(token, item.getUserid());
            //获取详细内容
            getRecordDTOByIdService.request(token, item.getId());
            //获取地址信息
            getIPInfoService.request(item.getIp());
            //获取关注信息
            if (token.getUserid() != item.getUserid()) {
                getFriendService.request(token, item.getUserid());
            } else {
                textAttention.setVisibility(View.GONE);
            }
        }

        /**
         * 直接绑定DTO
         *
         * @param item
         */
        public void bindView(RecordDTO item) {
            if (item == null) return;
            //从数据库和网络中获取用户
            getUserInfoService.request(token, item.getRecord().getUserid());
            //绑定详细内容
            handle(-1, item, false);
        }

        public void setShowDetails(boolean showDetails) {
            this.showDetails = showDetails;
        }

        //刷新数据
        private void refreshData(Record item) {
            if (showDetails) {
                textDescription.setMaxLines(Integer.MAX_VALUE);
                textContent.setMaxLines(Integer.MAX_VALUE);
                textStatus.setVisibility(View.VISIBLE);
                //textArea.setVisibility(View.VISIBLE);
            } else {
                textDescription.setMaxLines(2);
                textContent.setMaxLines(5);
                textStatus.setVisibility(View.GONE);
                //textArea.setVisibility(View.GONE);
            }
            imageAdapter.clear();
            textTitle.setText(item.getTitle());
            imageTop.setVisibility(item.isTop() ? View.VISIBLE : View.GONE);

            RecordType recordType = realm.where(RecordType.class).equalTo("id", item.getRecordTypeId()).findFirst();
            if (recordType != null) {
                textType.setText(recordType.getName());
            }
            Area area = realm.where(Area.class).equalTo("id", item.getAreaId()).findFirst();
            if (area != null) {
                textArea.setText(area.getName());
            }
            textDescription.setText(item.getDescription());
            textContent.setText(item.getContent());

            textTime.setText(DateTimeUtil.getRecordTime(item.getTime()));
            imgList = new Gson().fromJson(item.getImgs(), new TypeToken<List<String>>() {
            }.getType());
            for (String img : imgList) {
                imageAdapter.add(new ImageModel(imageAdapter.getItemCount(), 0, img, ""));
            }
            imageLink.setVisibility(PatternUtil.matchUrl(item.getUrl()) ? View.VISIBLE : View.GONE);
            imageLink.setOnClickListener(v -> WebActivity.startActivity(context, item.getUrl(), item.getTitle()));
            textParent.setText(item.getParentId() == 0 ? R.string.parent_record : R.string.child_record);
            //卷宗编号
            textFloor.setText(item.getId() + context.getString(R.string.floor));
        }

        @Override
        public void accept(ImageModel model) {
            int position = 0;
            for (String url : imgList) {
                if (StringUtil.equals(url, model.getUrl())) {
                    break;
                }
                position++;
            }
            ShowImageActivity.startActivity(context, new ArrayList<>(imgList), position);
        }

        @Override
        public void start(int id) {

        }

        @Override
        public void handle(int id, RecordDTO data, boolean local) {
            if (data == null || data.getRecord() == null) return;
            //刷新数据
            refreshData(data.getRecord());
            //评论数量
            textComment.setText(StringUtil.numLong2Str(data.getCommentNum()));
            //收藏数量
            textFavorite.setText(StringUtil.numLong2Str(data.getFavoriteNum()));
            //是否 收藏
            imageFavorite.setImageResource(data.isFavorite() ? R.drawable.favorited : R.drawable.favorite);
            //派生数量
            textFork.setText(StringUtil.numLong2Str(data.getChildNum()));
        }

        @Override
        public void error(int id, int code, String message, String data) {

        }
    }
}
