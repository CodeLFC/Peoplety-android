package gaozhi.online.peoplety.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

/**
 * 带有上拉加载的下拉刷新框
 */
/**
 * 自定义View继承SwipeRefreshLayout，添加上拉加载更多的属性
 */
public class RecycleSwipeRefreshView extends SwipeRefreshLayout{
    private OnLoadListener mOnLoadListener;
    /**
     * 正在加载状态
     */
    private boolean isLoading;

    public RecycleSwipeRefreshView(Context context, AttributeSet attrs) {
        super(context, attrs);
        View view = getChildAt(0);
        if(view instanceof RecyclerView){
            RecyclerView recyclerView = (RecyclerView) view;
            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                    if(!isLoading&&!recyclerView.canScrollVertically(SCROLL_AXIS_VERTICAL)){
                        loadData();
                    }
                }
            });
        }else{
            Log.i(getClass().getName(),"子控件："+view.getClass().getName());
        }
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
}
