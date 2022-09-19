package gaozhi.online.base.ui.recycler;

/**
 * Recycler 元素
 *
 * @param <T>
 */
public abstract class BaseCell<T> {
    protected T data;

    public BaseCell(T t) {
        data = t;
    }

    public T getData() {
        return data;
    }

    /**
     * 获取viewType
     *
     * @return 返回类型
     */
    public abstract int getItemType();

    /**
     * 获取item的id
     *
     * @return
     */
    public abstract long getItemId();
}
