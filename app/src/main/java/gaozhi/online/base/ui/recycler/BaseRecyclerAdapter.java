package gaozhi.online.base.ui.recycler;

import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SortedList;
import androidx.recyclerview.widget.SortedListAdapterCallback;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Recycler 适配器
 *
 * @param <T>
 */
public abstract class BaseRecyclerAdapter<T, E extends BaseCell<T>> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    protected final ViewHolderFactory<T> viewHolderFactory;
    //元素列表
    private final SortedList<E> itemList;

    public BaseRecyclerAdapter(Class<E> klass, @NonNull ViewHolderFactory<T> viewHolderFactory) {
        this.viewHolderFactory = viewHolderFactory;
        itemList = new SortedList<>(klass, createSortedListAdapterCallback());
    }

    /**
     * 创建排序适配器的钩子
     * 可以重写此回调，用于重写排序规则以及内容比较规则
     *
     * @return
     */
    public SortedListAdapterCallback<E> createSortedListAdapterCallback() {
        return new SortedListAdapterCallback<E>(this) {
            @Override
            public int compare(E o1, E o2) {
                return (int) (o1.getItemId() - o2.getItemId());
            }

            @Override
            public boolean areContentsTheSame(E oldItem, E newItem) {
                return oldItem.getData().equals(newItem.getData());
            }

            @Override
            public boolean areItemsTheSame(E item1, E item2) {
                return item1.getItemId() == item2.getItemId();
            }
        };
    }

    @Override
    public int getItemViewType(int position) {
        return getItem(position).getItemType();
    }

    /**
     * Called when RecyclerView needs a new { ViewHolder} of the given type to represent
     * an item.
     */
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return viewHolderFactory.createViewHolder(parent, viewType);
    }

    /**
     * Called by RecyclerView to display the data at the specified position. This method
     * should update the contents of the {ViewHolder#itemView} to reflect the item at
     * the given position.
     */
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        viewHolderFactory.onBindViewHolder(holder, itemList.get(position));
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

    protected E getItem(int position) {
        return itemList.get(position);
    }

    public void add(E item) {
        itemList.add(item);
    }

    public void add(List<E> items) {
        for (E e : items) {
            add(e);
        }
    }

    public <VN> void add(List<VN> items, Function<VN, E> function) {
        for (VN e : items) {
            add(function.apply(e));
        }
    }

    public void remove(int index) {
        itemList.removeItemAt(index);
    }

    public void forEach(Consumer<E> consumer) {
        forEach(consumer, null);
    }

    public void forEach(Consumer<E> consumer, Predicate<BaseCell<T>> predicate) {
        for (int index = 0; index < getItemCount(); index++) {
            E item = getItem(index);
            if (predicate == null) {
                consumer.accept(item);
                continue;
            }
            if (predicate.test(item)) {
                consumer.accept(item);
            }
        }
    }

    public void clear() {
        itemList.clear();
    }
}
