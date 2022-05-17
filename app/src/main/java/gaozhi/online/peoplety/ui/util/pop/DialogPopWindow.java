package gaozhi.online.peoplety.ui.util.pop;

import android.content.Context;
import android.view.View;

import gaozhi.online.base.ui.BasePopupWindow;
import gaozhi.online.peoplety.R;

/**
 * 双按钮的popWindow 没有完成
 */
@Deprecated
public class DialogPopWindow extends BasePopupWindow {

    public DialogPopWindow(Context context,boolean fullScreen) {
        super(context, R.layout.pop_window_dialog, fullScreen);
    }

    @Override
    protected void initView(View rootView) {

    }

    @Override
    protected void doBusiness(Context context) {

    }
}
