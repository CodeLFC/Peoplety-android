package gaozhi.online.base.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.gson.Gson;

/**
 * base {@link Fragment} class.
 */
public abstract class BaseFragment extends Fragment implements View.OnClickListener {
    protected final String TAG = getClass().getName();
    private View mContextView;

    public BaseFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            initParams(getArguments());
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //避免切换Fragment 的时候重绘UI 。失去数据
        if (mContextView == null) {
            mContextView = inflater.inflate(bindLayout(), container, false);
            initView(mContextView);
            doBusiness();
        }
        // 缓存的viewiew需要判断是否已经被加过parent，
        // 如果有parent需要从parent删除，不然会发生这个view已经有parent的错误。
        ViewGroup parent = (ViewGroup) mContextView.getParent();
        if (parent != null) {
            parent.removeView(mContextView);
        }
        return mContextView;
    }

    /**
     * @return 布局id
     */
    public abstract int bindLayout();

    /**
     * 初始化界面
     *
     * @param view
     */
    public abstract void initView(View view);

    /**
     * 初始化来自setArguments()的参数
     *
     * @param bundle 不会为空
     */
    public abstract void initParams(Bundle bundle);

    /**
     * 做业务
     */
    public abstract void doBusiness();

    public abstract void onPageScrolled(float positionOffset, int positionOffsetPixels);

    public abstract void onPageSelected();

}