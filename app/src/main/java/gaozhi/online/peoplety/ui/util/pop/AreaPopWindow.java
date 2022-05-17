package gaozhi.online.peoplety.ui.util.pop;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.function.Consumer;

import gaozhi.online.peoplety.R;
import gaozhi.online.peoplety.entity.Area;
import gaozhi.online.peoplety.ui.base.DBBasePopWindow;
import gaozhi.online.peoplety.ui.widget.NoAnimatorRecyclerView;

/**
 * 地区选择
 */
public class AreaPopWindow extends DBBasePopWindow implements View.OnClickListener {
    //ui
    private RecyclerView recyclerView;
    private AreaAdapter areaAdapter;
    private ImageView imagePreStep;

    public AreaPopWindow(Context context, boolean fullScreen) {
        super(context, R.layout.pop_window_area, fullScreen);
    }

    @Override
    protected void initView(View rootView) {
        recyclerView = rootView.findViewById(R.id.pop_window_recycler_area);
        recyclerView.setLayoutManager(new LinearLayoutManager(rootView.getContext()));
        areaAdapter = new AreaAdapter();
        recyclerView.setAdapter(areaAdapter);
        List<Area> zero = getRealm().where(Area.class).equalTo("parentId", 0).findAll();
        areaAdapter.add(zero);
        imagePreStep = rootView.findViewById(R.id.pop_window_image_pre_step);
        imagePreStep.setOnClickListener(this);
    }

    @Override
    protected void doBusiness(Context context) {

    }

    public void setOnAreaClickedListener(Consumer<Area> onItemClickedListener) {
        areaAdapter.setOnItemClickedListener(area -> {
            List<Area> areas = getRealm().where(Area.class).equalTo("parentId", area.getId()).findAll();
            if (areas.size()>0) {
                areaAdapter.clear();
                areaAdapter.add(areas);
            }else {
                dismiss();
                if (onItemClickedListener != null) {
                    onItemClickedListener.accept(area);
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        int parentId = areaAdapter.getParentId();
        Area parentArea = getRealm().where(Area.class).equalTo("id", parentId).findFirst();
        if (parentArea == null) {
            dismiss();
            return;
        }
        List<Area> parents = getRealm().where(Area.class).equalTo("parentId", parentArea.getParentId()).findAll();
        if (parents.size() > 0) {
            areaAdapter.clear();
            areaAdapter.add(parents);
        }
    }

    //适配器
    private static class AreaAdapter extends NoAnimatorRecyclerView.BaseAdapter<AreaAdapter.AreaViewHolder, Area> {
        @NonNull
        @Override
        public AreaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new AreaViewHolder(layoutInflate(parent, R.layout.item_recycler_text));
        }

        public int getParentId() {
            if (getItemCount() == 0) {
                return 0;
            }
            return getItem(0).getParentId();
        }

        //viewHolder
        private static class AreaViewHolder extends NoAnimatorRecyclerView.BaseViewHolder<Area> {
            private final TextView textView;

            public AreaViewHolder(@NonNull View itemView) {
                super(itemView);
                textView = itemView.findViewById(R.id.recycler_adapter_text);
            }

            public void bindView(Area area) {
                textView.setText(area.getName());
            }
        }
    }
}
