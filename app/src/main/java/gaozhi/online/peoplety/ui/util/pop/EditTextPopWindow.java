package gaozhi.online.peoplety.ui.util.pop;

import android.content.Context;
import android.view.View;

import gaozhi.online.base.ui.BasePopupWindow;
import gaozhi.online.peoplety.R;

/**
 * 带编辑框的控件   ，   由于软键盘的问题 放弃使用
 */
@Deprecated
public class EditTextPopWindow extends BasePopupWindow {
    public EditTextPopWindow(Context context,boolean fullScreen) {
        super(context, R.layout.pop_window_edit, fullScreen);
    }

    @Override
    protected void initView(View rootView) {

    }

    @Override
    protected void doBusiness(Context context) {

    }
}
