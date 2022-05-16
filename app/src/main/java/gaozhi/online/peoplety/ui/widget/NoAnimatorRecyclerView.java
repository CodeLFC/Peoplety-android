package gaozhi.online.peoplety.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

public class NoAnimatorRecyclerView extends RecyclerView {
    public NoAnimatorRecyclerView(@NonNull Context context) {
        super(context);
        init(context);
    }

    public NoAnimatorRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public NoAnimatorRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    /**
     * 关闭默认局部刷新动画
     */
    public void init(Context context) {
        setLayoutManager(new LinearLayoutManager(context));
        this.getItemAnimator().setAddDuration(0);
        this.getItemAnimator().setChangeDuration(0);
        this.getItemAnimator().setMoveDuration(0);
        this.getItemAnimator().setRemoveDuration(0);
        ((SimpleItemAnimator) this.getItemAnimator()).setSupportsChangeAnimations(false);
    }

    /**
     * 基础适配器
     *
     * @param <T>
     * @param <V>
     */
    public abstract static class BaseAdapter<T extends BaseViewHolder<V>, V> extends RecyclerView.Adapter<T> implements Consumer<Integer> {
        private Consumer<V> onItemClickedListener;
        private final List<V> itemList = new LinkedList<>();

        protected View layoutInflate(ViewGroup parent, @LayoutRes int layoutId) {
            return LayoutInflater.from(parent.getContext()).inflate(layoutId, parent, false);
        }

        @Override
        public void onBindViewHolder(@NonNull T holder, int position) {
            holder.setOnAreaSelectedListener(this);
            holder.bindView(itemList.get(position));
        }

        @Override
        public int getItemCount() {
            return itemList.size();
        }

        protected V getItem(int index) {
            return itemList.get(index);
        }

        public void add(V item) {
            int loc = itemList.size();
            itemList.add(item);
            notifyItemInserted(loc);
        }

        public void add(List<V> items) {
            int start = itemList.size() - 1;
            itemList.addAll(items);
            notifyItemRangeInserted(start, items.size());
        }

        public void remove(int index) {
            itemList.remove(index);
            notifyItemRemoved(index);
        }

        public void clear() {
            int len = itemList.size();
            itemList.clear();
            notifyItemRangeRemoved(0, len);
        }

        public void setOnItemClickedListener(Consumer<V> onItemClickedListener) {
            this.onItemClickedListener = onItemClickedListener;
        }

        @Override
        public void accept(Integer position) {
            if (onItemClickedListener != null) {
                onItemClickedListener.accept(itemList.get(position));
            }
        }
    }

    /**
     * 可点击的视图缓存
     */
    public static abstract class BaseViewHolder<V> extends ViewHolder implements View.OnClickListener {
        private Consumer<Integer> onAreaSelectedListener;

        public BaseViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
        }

        public void setOnAreaSelectedListener(Consumer<Integer> onAreaSelectedListener) {
            this.onAreaSelectedListener = onAreaSelectedListener;
        }

        public abstract void bindView(V item);

        @Override
        public void onClick(View v) {
            if (onAreaSelectedListener != null) {
                onAreaSelectedListener.accept(getAdapterPosition());
            }
        }
    }
}
