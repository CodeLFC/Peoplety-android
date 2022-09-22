package gaozhi.online.base.ui;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import gaozhi.online.peoplety.R;

public abstract class BaseActivity extends AppCompatActivity implements View.OnClickListener {
    /**
     * 是否沉浸状态栏
     **/
    private boolean isSetStatusBar = false;
    /**
     * 是否允许全屏
     **/
    private boolean mAllowFullScreen = true;
    /**
     * 是否允许旋转屏幕
     **/
    private boolean isAllowScreenRotate = false;
    /**
     * 日志输出标志
     **/
    protected final String TAG = getClass().getName();

    /**
     * 授权信息
     *
     * @param savedInstanceState
     */
    private OnRequestPermissionsResultListener onRequestPermissionsResultListener;

    public void setOnRequestPermissionsResultListener(OnRequestPermissionsResultListener onRequestPermissionsResultListener) {
        this.onRequestPermissionsResultListener = onRequestPermissionsResultListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initParams(getIntent());
        initLocalData();
        if (mAllowFullScreen) {
            requestWindowFeature(Window.FEATURE_NO_TITLE);
        }
        if (isSetStatusBar) {
            steepStatusBar();
        }
        if (!isAllowScreenRotate) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        }
        /**
         * 当前Activity渲染的视图View
         **/
        View mContextView = LayoutInflater.from(this)
                .inflate(bindLayout(), null);
        //设置软键盘输入底部不被顶起
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setContentView(mContextView);
        initView(mContextView);
        doBusiness(this);
    }

    /**
     * 初始化本地数据
     */
    protected void initLocalData() {
    }

    /**
     * [沉浸状态栏]
     */
    private void steepStatusBar() {
        // 透明状态栏
        getWindow().setStatusBarColor(getResources().getColor(R.color.theme_color, getTheme()));
        // 透明导航栏
        getWindow().setNavigationBarColor(getResources().getColor(R.color.bottom_color, getTheme()));
    }
    /**
     * [绑定控件]
     *
     * @param resId
     * @return
     */
    protected <T extends View> T $(int resId) {
        return super.findViewById(resId);
    }

    /**
     * 初始化参数
     *
     * @param intent
     */
    protected abstract void initParams(Intent intent);

    /**
     * 绑定布局资源
     *
     * @return layout 资源ID
     */
    protected abstract int bindLayout();

    /**
     * 初始化视图
     *
     * @param view
     */
    protected abstract void initView(View view);

    /**
     * [业务操作]
     *
     * @param mContext
     */
    protected abstract void doBusiness(Context mContext);

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG, "onRestart()");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart()");
        ActivityManager.getInstance().push(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume()");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause()");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop()");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy()");
        ActivityManager.getInstance().remove(this);
    }

    /**
     * [是否允许全屏]
     *
     * @param allowFullScreen
     */
    public void setAllowFullScreen(boolean allowFullScreen) {
        this.mAllowFullScreen = allowFullScreen;
    }

    /**
     * [是否设置沉浸状态栏]
     *
     * @param isSetStatusBar
     */
    public void setSteepStatusBar(boolean isSetStatusBar) {
        this.isSetStatusBar = isSetStatusBar;
    }

    /**
     * [是否允许屏幕旋转]
     *
     * @param isAllowScreenRotate
     */
    public void setScreenRotate(boolean isAllowScreenRotate) {
        this.isAllowScreenRotate = isAllowScreenRotate;
    }

//    @Override
//    public boolean dispatchTouchEvent(MotionEvent event) {//取消EditText 焦点
//        if (event.getAction() == MotionEvent.ACTION_DOWN) {
//            View v = getCurrentFocus();
//            if (v instanceof EditText) {
//                Rect outRect = new Rect();
//                v.getGlobalVisibleRect(outRect);
//                if (!outRect.contains((int) event.getRawX(), (int) event.getRawY())) {
//                    v.setFocusable(false);
//                    v.setFocusableInTouchMode(true);
//                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
//                }
//            }
//        }
//        return super.dispatchTouchEvent(event);
//    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (onRequestPermissionsResultListener != null) {
            onRequestPermissionsResultListener.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    /**
     * 响应授权
     */
    public interface OnRequestPermissionsResultListener {
        void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults);
    }

    /**
     * 获取 Application
     *
     * @param tClass
     * @param <T>
     * @return
     */
    public <T extends Application> T getApplication(Class<T> tClass) {
        return (T) getApplicationContext();
    }

    /**
     * Call this when your activity is done and should be closed.  The
     * ActivityResult is propagated back to whoever launched you via
     * onActivityResult().
     */
    @Override
    public void finish() {
        super.finish();
        if(ActivityManager.getInstance().size()<=1){
            startLaunchActivity();
        }
    }

    /**
     * 在仅剩一个Activity时救活应用
     */
    public void startLaunchActivity(){
        //TODO 救活应用
    }
}
