package gaozhi.online.base.ui;

import android.app.Activity;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.PopupWindow;

import gaozhi.online.peoplety.R;


/**
 * 弹出菜单
 */
public abstract class BasePopupMenu extends PopupWindow {

    public BasePopupMenu(Context context, int resID, boolean fullScreen) {
        View rootView = LayoutInflater.from(context).inflate(resID, null);
        setContentView(rootView);

        //设置宽与高
        if (!fullScreen) {
            setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
            setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        } else {
            setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
            setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
            setClippingEnabled(false);
        }
        // 设置SelectPicPopupWindow弹出窗体可点击
        setFocusable(true);
        setOutsideTouchable(true);
        // 设置SelectPicPopupWindow弹出窗体动画效果
        this.setAnimationStyle(R.style.PopupAnimation);
        initView(rootView);
        doBusiness(context);
    }

    public abstract void initView(View rootView);

    public abstract void doBusiness(Context context);

    public void showPopupWindow(View parent) {
        showAsDropDown(parent);
    }

    public void showPopupWindow(Activity activity) {
        showAtLocation(activity.getWindow().getDecorView(), Gravity.BOTTOM, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
    }
}
