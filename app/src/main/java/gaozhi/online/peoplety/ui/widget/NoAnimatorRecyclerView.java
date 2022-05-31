package gaozhi.online.peoplety.ui.widget;

import android.content.Context;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;
import androidx.recyclerview.widget.SortedList;
import androidx.recyclerview.widget.SortedListAdapterCallback;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

/**
 * 不显示动画，且带有垂直方向上的下拉刷新加载
 */
public class NoAnimatorRecyclerView extends RecyclerView {
    private OnLoadListener mOnLoadListener;
    /**
     * 正在加载状态
     */
    private boolean isLoading;
    //记录状态
    private Parcelable recyclerViewState;

    public NoAnimatorRecyclerView(@NonNull Context context) {
        super(context);
        init();
    }

    public NoAnimatorRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public NoAnimatorRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    /**
     * 关闭默认局部刷新动画
     */
    private void init() {
        RecyclerView.ItemAnimator animator = getItemAnimator();
        if (animator instanceof SimpleItemAnimator) {
            ((SimpleItemAnimator) animator).setSupportsChangeAnimations(false);
        }

        addOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                // 记录状态
                recyclerViewState = recyclerView.getLayoutManager().onSaveInstanceState();

                if (!isLoading && !recyclerView.canScrollVertically(SCROLL_AXIS_VERTICAL)) {
                    loadData();
                }
            }
        });
    }

    /**
     * 处理加载数据的逻辑
     */
    private void loadData() {
        System.out.println("加载数据...");
        if (mOnLoadListener != null) {
            // 设置加载状态，让布局显示出来
            setLoading(true);
            mOnLoadListener.onLoad();
        }

    }

    /**
     * 设置加载状态，是否加载传入boolean值进行判断
     *
     * @param loading
     */
    public void setLoading(boolean loading) {
        // 修改当前的状态
        isLoading = loading;
        //恢复状态
        getLayoutManager().onRestoreInstanceState(recyclerViewState);
    }

    /**
     * 上拉加载的接口回调
     */

    public interface OnLoadListener {
        void onLoad();
    }

    public void setOnLoadListener(OnLoadListener listener) {
        this.mOnLoadListener = listener;
    }

    /**
     * 基础适配器
     *
     * @param <T>
     * @param <V>
     */
    public abstract static class BaseAdapter<T extends BaseViewHolder<V>, V extends BaseAdapter.BaseItem> extends RecyclerView.Adapter<T> implements Consumer<Integer> {

        //item必须实现的接口
        public interface BaseItem {

            default long getItemId() {
                return -1;
            }

            /**
             * 定义item的显示顺序
             *
             * @param item
             * @return
             */
            default int compare(BaseItem item) {
                return (int) (getItemId() - item.getItemId());
            }

            default int getViewType() {
                return 0;
            }
        }

        /**
         * 默认布局管理器
         */
        public static class DefaultLinearLayoutManager extends LinearLayoutManager {

            public DefaultLinearLayoutManager(Context context) {
                super(context);
            }

            @Override
            protected void calculateExtraLayoutSpace(@NonNull State state, @NonNull int[] extraLayoutSpace) {
                Arrays.fill(extraLayoutSpace, 300);
                // super.calculateExtraLayoutSpace(state, extraLayoutSpace);
            }
        }

        private Consumer<V> onItemClickedListener;
        //列表
        private final SortedList<BaseItem> itemList = new SortedList<>(BaseItem.class, new SortedListAdapterCallback<>(this) {
            @Override
            public int compare(BaseItem o1, BaseItem o2) {
                return o1.compare(o2);
            }

            @Override
            public boolean areContentsTheSame(BaseItem oldItem, BaseItem newItem) {
                //依赖hashcode 方法
                return oldItem == newItem;
            }

            @Override
            public boolean areItemsTheSame(BaseItem item1, BaseItem item2) {
                return item1.getItemId() == item2.getItemId();
            }
        });

        public BaseAdapter() {
            //防止闪烁
            setHasStableIds(true);
        }

        protected View layoutInflate(ViewGroup parent, @LayoutRes int layoutId) {
            return LayoutInflater.from(parent.getContext()).inflate(layoutId, parent, false);
        }

        @Override
        public int getItemViewType(int position) {
            return getItem(position).getViewType();
        }

        @Override
        public void onBindViewHolder(@NonNull T holder, int position) {
            holder.setOnItemSelectedListener(this);
            holder.bindView(getItem(position));
        }

        //防止闪烁
        @Override
        public long getItemId(int position) {
            return getItem(position).getItemId();
        }

        @Override
        public int getItemCount() {
            return itemList.size();
        }

        protected V getItem(int position) {
            return (V) itemList.get(position);
        }


        public void add(V item) {
            itemList.add(item);
        }

        public void add(List<V> items) {
            for (V e : items) {
                itemList.add(e);
            }
        }

        public void remove(int index) {
            itemList.removeItemAt(index);
        }

        public void updateItem(int index, V item) {
            itemList.updateItemAt(index, item);
        }

        public void clear() {
            itemList.clear();
        }

        public void setOnItemClickedListener(Consumer<V> onItemClickedListener) {
            this.onItemClickedListener = onItemClickedListener;
        }

        @Override
        public void accept(Integer position) {
            if (onItemClickedListener != null) {
                onItemClickedListener.accept(getItem(position));
            }
        }
    }

    /**
     * 可点击的视图缓存
     */
    public static abstract class BaseViewHolder<T> extends ViewHolder implements View.OnClickListener {
        private Consumer<Integer> onItemSelectedListener;

        public BaseViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
        }

        public void setOnItemSelectedListener(Consumer<Integer> onItemSelectedListener) {
            this.onItemSelectedListener = onItemSelectedListener;
        }

        public abstract void bindView(T item);

        @Override
        public void onClick(View v) {
            if (onItemSelectedListener != null) {
                onItemSelectedListener.accept(getBindingAdapterPosition());
            }
        }
    }
}
