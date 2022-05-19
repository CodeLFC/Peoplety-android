package gaozhi.online.peoplety.ui.activity.record;

import android.content.Context;
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

import gaozhi.online.base.net.http.ApiRequest;
import gaozhi.online.base.net.http.DataHelper;
import gaozhi.online.peoplety.R;
import gaozhi.online.peoplety.entity.Area;
import gaozhi.online.peoplety.entity.Record;
import gaozhi.online.peoplety.entity.RecordType;
import gaozhi.online.peoplety.entity.Token;
import gaozhi.online.peoplety.entity.UserInfo;
import gaozhi.online.peoplety.entity.client.ImageModel;
import gaozhi.online.peoplety.entity.dto.UserDTO;
import gaozhi.online.peoplety.service.user.GetUserInfoService;
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

    @Override
    public void add(List<Record> items) {
        super.add(items);
        getItemList().sort((o1, o2) -> (int) (o2.getId() - o1.getId()));
        notifyItemRangeChanged(0, getItemCount());
    }

    public static class RecordViewHolder extends NoAnimatorRecyclerView.BaseViewHolder<Record> implements Consumer<ImageModel>, DataHelper.OnDataListener<UserDTO> {
        private final Context context;
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
        //service
        private final GetUserInfoService getUserInfoService = new GetUserInfoService(this);
        private final Token token;

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
        }

        @Override
        public void bindView(Record item) {
            imageAdapter.clear();
            textTitle.setText(item.getTitle());
            imageTop.setVisibility(item.isTop() ? View.VISIBLE : View.GONE);

            RecordType recordType = realm.where(RecordType.class).equalTo("id", item.getRecordTypeId()).findFirst();
            if (recordType != null) {
                textType.setText(recordType.getName());
            } else {
                //刷新资源
            }
            Area area = realm.where(Area.class).equalTo("id", item.getAreaId()).findFirst();
            if (area != null) {
                textArea.setText(area.getName());
            } else {
                //刷新资源
            }

            textDescription.setText(item.getDescription());
            textIp.setText(context.getString(R.string.ip) + item.getIp());
            textIp.setOnClickListener(v -> WebActivity.startActivity(context, context.getString(R.string.url_ip), item.getIp()));
            textTime.setText(DateTimeUtil.getRecordTime(item.getTime()));
            imgList = new Gson().fromJson(item.getImgs(), new TypeToken<List<String>>() {
            }.getType());
            for (String img : imgList) {
                imageAdapter.add(new ImageModel(0, img, ""));
            }
            textUrl.setVisibility(PatternUtil.matchUrl(item.getUrl()) ? View.VISIBLE : View.GONE);
            textUrl.setOnClickListener(v -> WebActivity.startActivity(context, item.getUrl(), item.getTitle()));

            //从数据库和网络中获取用户
            getUserInfoService.request(token, item.getUserid());
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
        public void handle(int id, UserDTO data, boolean local) {
            if (data == null) return;
            textName.setText(data.getUserInfo().getNick());
            GlideUtil.loadRoundRectangleImage(context, data.getUserInfo().getHeadUrl(), imageHead);
            textRemark.setText(data.getUserInfo().getRemark());
            if (data.getUserInfo().getId() == token.getUserid()) {
                imageDelete.setVisibility(View.VISIBLE);
            }else{
                imageDelete.setVisibility(View.GONE);
            }
        }

        @Override
        public void error(int id, int code, String message, String data) {

        }
    }
}
