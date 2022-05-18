package gaozhi.online.peoplety.ui.widget;

import android.content.Context;
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

import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

/**
 * 不显示动画，且垂直方向上的加载
 */
public class NoAnimatorRecyclerView extends RecyclerView {
    private OnLoadListener mOnLoadListener;
    /**
     * 正在加载状态
     */
    private boolean isLoading;
    //记录上次的位置
    private int lastPosition;
    private int lastOffset;

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
    public void init() {
        RecyclerView.ItemAnimator animator = getItemAnimator();
        if (animator instanceof SimpleItemAnimator) {
            ((SimpleItemAnimator) animator).setSupportsChangeAnimations(false);
        }

//        this.getItemAnimator().setAddDuration(0);
//        this.getItemAnimator().setChangeDuration(0);
//        this.getItemAnimator().setMoveDuration(0);
//        this.getItemAnimator().setRemoveDuration(0);
//        ((SimpleItemAnimator) this.getItemAnimator()).setSupportsChangeAnimations(false);

        addOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                // 记录位置
                // 获取可视的第一个view
                View topView = getLayoutManager().getChildAt(0);
                // 获取与该view的顶部的偏移量
                lastOffset = topView.getTop();
                // 得到该View的数组位置
                lastPosition = getLayoutManager().getPosition(topView);

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
        //回到刷新前的位置
        if (!isLoading) {
            getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    // 利用线程
                    post(() -> {
                        // 这样更精确
                        ((LinearLayoutManager) getLayoutManager()).scrollToPositionWithOffset(lastPosition, lastOffset);
                    });
                    getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
            });
        }
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
    public abstract static class BaseAdapter<T extends BaseViewHolder<V>, V> extends RecyclerView.Adapter<T> implements Consumer<Integer> {
        private Consumer<V> onItemClickedListener;
        private final List<V> itemList = new LinkedList<>();

        public BaseAdapter() {
            //防止闪烁
            setHasStableIds(true);
        }

        protected View layoutInflate(ViewGroup parent, @LayoutRes int layoutId) {
            return LayoutInflater.from(parent.getContext()).inflate(layoutId, parent, false);
        }

        @Override
        public void onBindViewHolder(@NonNull T holder, int position) {
            holder.setOnItemSelectedListener(this);
            holder.bindView(itemList.get(position));
        }

        //防止闪烁
        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getItemCount() {
            return itemList.size();
        }

        protected V getItem(int index) {
            return itemList.get(index);
        }

        protected List<V> getItemList() {
            return itemList;
        }

        public void updateItem(int index, V item) {
            itemList.set(index, item);
            notifyItemChanged(index);
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
        private Consumer<Integer> onItemSelectedListener;

        public BaseViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
        }

        public void setOnItemSelectedListener(Consumer<Integer> onItemSelectedListener) {
            this.onItemSelectedListener = onItemSelectedListener;
        }

        public abstract void bindView(V item);

        @Override
        public void onClick(View v) {
            if (onItemSelectedListener != null) {
                onItemSelectedListener.accept(getBindingAdapterPosition());
            }
        }
    }
}
