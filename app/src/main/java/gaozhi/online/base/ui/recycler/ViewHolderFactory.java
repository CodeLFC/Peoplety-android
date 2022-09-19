package gaozhi.online.base.ui.recycler;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.LayoutRes;
import androidx.recyclerview.widget.RecyclerView;

/**
 * ViewHolder工厂
 */
public abstract class ViewHolderFactory<T>{
    protected final Context context;

    public ViewHolderFactory(Context context) {
        this.context = context;
    }

    /**
     * 根据viewType创建对应的viewHolder
     *
     * @param parent
     * @param viewType
     * @return
     */
    public abstract RecyclerView.ViewHolder createViewHolder(ViewGroup parent, int viewType);

    /**
     * 把数据cell绑定到对应的holder中
     *
     * @param holder
     * @param cell
     */
    public abstract void onBindViewHolder(RecyclerView.ViewHolder holder, BaseCell<T> cell);

    /**
     * 根据layout ID反射出视图
     *
     * @param parent
     * @param resource
     * @return
     */
    public View inflateLayout(ViewGroup parent, @LayoutRes int resource) {
        return LayoutInflater.from(parent.getContext()).inflate(resource, parent, false);
    }

}
