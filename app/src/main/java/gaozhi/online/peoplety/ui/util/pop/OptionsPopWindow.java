package gaozhi.online.peoplety.ui.util.pop;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

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
    }

    private RecyclerView recyclerView;
    private OptionAdapter optionAdapter;

    public OptionsPopWindow(Context context, boolean fullScreen) {
        super(context, R.layout.pop_window_options, fullScreen);
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

    public OptionsPopWindow setOnItemClickedListener(BiConsumer<OptionsPopWindow,Option> optionConsumer) {
        optionAdapter.setOnItemClickedListener(option -> {
            if(optionConsumer!=null){
                optionConsumer.accept(OptionsPopWindow.this,option);
            }
        });
        return this;
    }

    @Override
    protected void doBusiness(Context context) {

    }

    //适配器
    private static class OptionAdapter extends NoAnimatorRecyclerView.BaseAdapter<OptionAdapter.OptionViewHolder, Option> {
        @NonNull
        @Override
        public OptionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new OptionViewHolder(layoutInflate(parent, R.layout.item_recycler_text));
        }

        //viewHolder
        private static class OptionViewHolder extends NoAnimatorRecyclerView.BaseViewHolder<Option> {

            private final TextView textView;

            public OptionViewHolder(@NonNull View itemView) {
                super(itemView);
                textView = itemView.findViewById(R.id.recycler_adapter_text);
            }

            @Override
            public void bindView(Option item) {
                textView.setText(item.getText());
            }

        }
    }
}
