package gaozhi.online.peoplety.ui.util.pop;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;

import gaozhi.online.base.ui.BasePopupWindow;
import gaozhi.online.peoplety.R;
import gaozhi.online.peoplety.ui.widget.NoAnimatorRecyclerView;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 选项列表
 */
public class OptionsPopWindow extends BasePopupWindow {
    @Data
    @AllArgsConstructor
    public static class Option implements NoAnimatorRecyclerView.BaseAdapter.BaseItem {
        private int id;
        private String text;
        private boolean isSelected;
        private boolean isDismiss;

        /**
         * 兼容
         *
         * @param id
         * @param text
         */
        public Option(int id, String text) {
            this.id = id;
            this.text = text;
        }

        @Override
        public long getItemId() {
            return id;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Option option = (Option) o;
            return id == option.id && isDismiss == option.isDismiss && isSelected == option.isSelected && Objects.equals(text, option.text);
        }

        @Override
        public int hashCode() {
            return Objects.hash(id, text, isDismiss, isSelected);
        }
    }

    private RecyclerView recyclerView;
    private OptionAdapter optionAdapter;

    public OptionsPopWindow(Context context) {
        super(context, R.layout.pop_window_options, true);
        setOnItemClickedListener(null);
    }

    @Override
    protected void initView(View rootView) {
        recyclerView = rootView.findViewById(R.id.pop_window_recycler_options);
        recyclerView.setLayoutManager(new LinearLayoutManager(rootView.getContext()));
        optionAdapter = new OptionAdapter();
        recyclerView.setAdapter(optionAdapter);
    }

    public OptionsPopWindow setOptions(List<Option> options) {
        optionAdapter.clear();
        optionAdapter.add(options);
        return this;
    }

    public OptionsPopWindow setOnItemClickedListener(BiConsumer<OptionsPopWindow, Option> optionConsumer) {
        optionAdapter.setOnItemClickedListener(option -> {
            option.isSelected = !option.isSelected;
            optionAdapter.updateItem(option);
            if (option.isDismiss) {
                dismiss();
            }
            if (optionConsumer != null) {
                optionConsumer.accept(OptionsPopWindow.this, option);
            }
        });
        return this;
    }

    @Override
    protected void doBusiness(Context context) {

    }

    //适配器
    private static class OptionAdapter extends NoAnimatorRecyclerView.BaseAdapter<OptionAdapter.OptionViewHolder, Option> {
        public OptionAdapter() {
            super(Option.class);
        }

        @NonNull
        @Override
        public OptionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new OptionViewHolder(layoutInflate(parent, R.layout.item_recycler_text));
        }

        //viewHolder
        private static class OptionViewHolder extends NoAnimatorRecyclerView.BaseViewHolder<Option> {
            private final Context context;
            private final TextView textView;

            public OptionViewHolder(@NonNull View itemView) {
                super(itemView);
                context = itemView.getContext();
                textView = itemView.findViewById(R.id.recycler_adapter_text);
            }

            @Override
            public void bindView(Option item) {
                textView.setText(item.getText());
                textView.setTextColor(context.getColor(item.isSelected ? R.color.theme_color : R.color.deep_text_color));
            }

        }
    }
}
