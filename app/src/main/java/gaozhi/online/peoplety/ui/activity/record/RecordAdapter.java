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

import gaozhi.online.base.net.http.DataHelper;
import gaozhi.online.peoplety.R;
import gaozhi.online.peoplety.entity.Area;
import gaozhi.online.peoplety.entity.IPInfo;
import gaozhi.online.peoplety.entity.Record;
import gaozhi.online.peoplety.entity.RecordType;
import gaozhi.online.peoplety.entity.Token;
import gaozhi.online.peoplety.entity.UserInfo;
import gaozhi.online.peoplety.entity.client.ImageModel;
import gaozhi.online.peoplety.entity.dto.RecordDTO;
import gaozhi.online.peoplety.entity.dto.UserDTO;
import gaozhi.online.peoplety.service.constant.GetIPInfoService;
import gaozhi.online.peoplety.service.record.GetRecordDTOByIdService;
import gaozhi.online.peoplety.service.user.GetUserInfoService;
import gaozhi.online.peoplety.ui.activity.PublishRecordActivity;
import gaozhi.online.peoplety.ui.util.WebActivity;
import gaozhi.online.peoplety.ui.util.image.ShowImageActivity;
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
        private final TextView textArea;
        private final TextView textDescription;
        private final TextView textIp;
        private final TextView textTime;
        private final ImageAdapter imageAdapter;
        private final TextView textUrl;
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
        //父子
        private final TextView textParent;
        //评论框
        //  private final EditText editComment;
        private final TextView textContent;
        private final RecordCommentPopWindow editTextPopWindow;
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
        //获取详情
        private final GetRecordDTOByIdService getRecordDTOByIdService = new GetRecordDTOByIdService(this);
        //获取位置信息
        private final GetIPInfoService getIPInfoService = new GetIPInfoService(new DataHelper.OnDataListener<>() {
            @Override
            public void handle(int id, IPInfo data, boolean local) {
                textIp.setVisibility(View.VISIBLE);
                textIp.setText(data.getShowArea());
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

            textUrl = itemView.findViewById(R.id.item_recycler_record_text_url);
            imageAdapter.setOnItemClickedListener(this);

            imageHead = itemView.findViewById(R.id.item_recycler_record_image_head);
            textName = itemView.findViewById(R.id.item_recycler_record_text_name);
            textRemark = itemView.findViewById(R.id.item_recycler_record_text_remark);
            textComment = itemView.findViewById(R.id.item_recycler_record_text_comment_num);
            textFavorite = itemView.findViewById(R.id.item_recycler_record_text_favorite_num);
            imageDelete = itemView.findViewById(R.id.item_recycler_record_image_delete);
            textStatus = itemView.findViewById(R.id.item_recycler_record_text_status);
            imageGender = itemView.findViewById(R.id.item_recycler_record_image_gender);

            imageFavorite = itemView.findViewById(R.id.item_recycler_record_image_favorite);
            imageComment = itemView.findViewById(R.id.item_recycler_record_image_comment);
            //  editComment = itemView.findViewById(R.id.item_recycler_record_edit_comment);
            textContent = itemView.findViewById(R.id.item_recycler_record_text_content);

            editTextPopWindow = new RecordCommentPopWindow(context);

            textParent = itemView.findViewById(R.id.item_recycler_record_text_parent);

            imageFork = itemView.findViewById(R.id.item_recycler_record_image_fork);
            textFloor = itemView.findViewById(R.id.item_recycler_record_text_floor);
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
                imageFavorite.setOnClickListener(v -> {
                    ToastUtil.showToastShort("收藏到某些收藏夹");
                });
                imageComment.setOnClickListener(v -> {
                    //  editComment.requestFocus();
                    editTextPopWindow.showPopupWindow(imageComment, token, record);
                });
            } else {
                textDescription.setMaxLines(2);
                textContent.setMaxLines(5);
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
                imageAdapter.add(new ImageModel(0, img, ""));
            }
            textUrl.setVisibility(PatternUtil.matchUrl(item.getUrl()) ? View.VISIBLE : View.GONE);
            textUrl.setOnClickListener(v -> WebActivity.startActivity(context, item.getUrl(), item.getTitle()));
            textParent.setText(item.getParentId() == 0 ? R.string.parent_record : R.string.child_record);
            //派生分支
            imageFork.setOnClickListener((v) -> {
                PublishRecordActivity.startActivity(context, recordType, record);
            });
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
            if (data == null) return;

            //刷新数据
            refreshData(data.getRecord());
            //评论数量
            textComment.setText(StringUtil.numLong2Str(data.getCommentPageInfo().getTotal()));
            //收藏数量
            textFavorite.setText(StringUtil.numLong2Str(data.getFavoriteNum()));
            //是否 收藏
            imageFavorite.setImageResource(data.isFavorite() ? R.drawable.favorited : R.drawable.favorite);
        }

        @Override
        public void error(int id, int code, String message, String data) {

        }
    }
}
