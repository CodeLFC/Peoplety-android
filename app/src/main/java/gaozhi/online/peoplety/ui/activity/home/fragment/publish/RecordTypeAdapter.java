package gaozhi.online.peoplety.ui.activity.home.fragment.publish;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import gaozhi.online.peoplety.R;
import gaozhi.online.peoplety.entity.RecordType;
import gaozhi.online.peoplety.ui.widget.NoAnimatorRecyclerView;

/**
 * 卷宗类型
 */
public class RecordTypeAdapter extends NoAnimatorRecyclerView.BaseAdapter<RecordTypeAdapter.RecordTypeViewHolder,RecordType> {

    @NonNull
    @Override
    public RecordTypeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RecordTypeViewHolder(layoutInflate(parent,R.layout.recycler_adapter_record_type_item_view));
    }
    public int getParentId() {
        if (getItemCount() == 0) {
            return 0;
        }
        return getItem(0).getParentId();
    }
    /**
     * 卷宗类型视图缓存
     */
    public static class RecordTypeViewHolder extends NoAnimatorRecyclerView.BaseViewHolder<RecordType> {
        private final TextView textName;
        private final TextView textDescription;
        public RecordTypeViewHolder(@NonNull View itemView) {
            super(itemView);
            textName = itemView.findViewById(R.id.record_type_item_text_name);
            textDescription = itemView.findViewById(R.id.record_type_item_text_description);
        }
        public void bindView(RecordType recordType){
            textName.setText(recordType.getName());
            textDescription.setText(recordType.getDescription());
        }

    }
}
