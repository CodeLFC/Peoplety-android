package gaozhi.online.peoplety.ui.activity.home.fragment.home;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.function.Consumer;

import gaozhi.online.peoplety.R;
import gaozhi.online.peoplety.entity.RecordType;
import gaozhi.online.peoplety.ui.widget.NoAnimatorRecyclerView;

/**
 * 卷宗类型适配器
 */
public class RecordTypeLabelAdapter extends NoAnimatorRecyclerView.BaseAdapter<RecordTypeLabelAdapter.LabelRecordTypeViewHolder, RecordType> {
    public static final RecordType allSelected = new RecordType();

    {
        allSelected.setId(-1);
        allSelected.setName("全选");
    }

    public RecordTypeLabelAdapter() {
        super(RecordType.class);
        add(allSelected);
    }

    @Override
    public void clear() {
        super.clear();
        add(allSelected);
    }

    @NonNull
    @Override
    public LabelRecordTypeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new LabelRecordTypeViewHolder(layoutInflate(parent, R.layout.item_recycler_label));
    }

    public static class LabelRecordTypeViewHolder extends NoAnimatorRecyclerView.BaseViewHolder<RecordType> {
        private TextView textContent;

        public LabelRecordTypeViewHolder(@NonNull View itemView) {
            super(itemView);
            textContent = itemView.findViewById(R.id.item_recycler_label_text);
        }

        @Override
        public void bindView(RecordType item) {
            textContent.setBackgroundResource(item.isSelected() ? R.drawable.tag_bg : R.drawable.tag_un_selected_bg);
            textContent.setText(item.getName());
        }
    }
}
