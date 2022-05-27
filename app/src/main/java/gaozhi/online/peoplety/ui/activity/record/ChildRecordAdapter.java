package gaozhi.online.peoplety.ui.activity.record;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import gaozhi.online.peoplety.R;
import gaozhi.online.peoplety.entity.Record;
import gaozhi.online.peoplety.ui.util.WebActivity;
import gaozhi.online.peoplety.ui.widget.NoAnimatorRecyclerView;
import gaozhi.online.peoplety.util.PatternUtil;
import gaozhi.online.peoplety.util.StringUtil;

/**
 * 子卷宗适配器
 */
public class ChildRecordAdapter extends NoAnimatorRecyclerView.BaseAdapter<ChildRecordAdapter.ChildRecordViewHolder,Record> {

    @NonNull
    @Override
    public ChildRecordViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ChildRecordViewHolder(layoutInflate(parent, R.layout.item_recycler_child_record));
    }

    public static class ChildRecordViewHolder extends NoAnimatorRecyclerView.BaseViewHolder<Record>{
        private final Context context;
        private final TextView textTitle;
        private final TextView textParent;
        private final TextView textUrl;
        private final TextView textDescription;
        private final ImageView imageTop;
        private final TextView textIP;
        private final TextView textFloor;
        public ChildRecordViewHolder(@NonNull View itemView) {
            super(itemView);
            this.context = itemView.getContext();
            textTitle = itemView.findViewById(R.id.item_recycler_child_record_text_title);
            textParent = itemView.findViewById(R.id.item_recycler_child_record_text_parent);
            textUrl = itemView.findViewById(R.id.item_recycler_child_record_text_url);
            textDescription = itemView.findViewById(R.id.item_recycler_child_record_text_description);
            imageTop = itemView.findViewById(R.id.item_recycler_child_record_image_top);
            textIP = itemView.findViewById(R.id.item_recycler_child_record_text_ip);
            textFloor = itemView.findViewById(R.id.item_recycler_child_record_text_floor);
        }

        @Override
        public void bindView(Record item) {
            textTitle.setText(item.getTitle());
            textParent.setText(item.getParentId()==0?R.string.parent_record:R.string.child_record);
            textDescription.setText(item.getDescription());
            imageTop.setVisibility(item.isTop()?View.VISIBLE:View.GONE);
            textUrl.setVisibility(PatternUtil.matchUrl(item.getUrl()) ? View.VISIBLE : View.GONE);
            textUrl.setOnClickListener(v -> WebActivity.startActivity(context, item.getUrl(), item.getTitle()));
            textIP.setText(item.getIp());
            textFloor.setText(item.getId()+context.getString(R.string.floor));
        }
    }
}
