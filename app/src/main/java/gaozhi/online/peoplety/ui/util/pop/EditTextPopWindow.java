package gaozhi.online.peoplety.ui.util.pop;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import gaozhi.online.base.ui.BasePopupWindow;
import gaozhi.online.peoplety.R;

/**
 * 带编辑框的控件   ，   由于软键盘的问题 放弃使用
 */
public abstract class EditTextPopWindow extends BasePopupWindow {
    //ui
    private EditText editContent;
    private EditText editUrl;
    private Button btnSend;

    public EditTextPopWindow(Context context,boolean fullScreen) {
        super(context, R.layout.pop_window_edit, fullScreen);
    }

    protected EditText getEditContent() {
        return editContent;
    }

    protected Button getBtnSend() {
        return btnSend;
    }

    public EditText getEditUrl() {
        return editUrl;
    }

    @Override
    protected void initView(View rootView) {
        editContent = rootView.findViewById(R.id.pop_window_edit_edit_content);
        btnSend = rootView.findViewById(R.id.pop_widow_edit_btn_send);
        editUrl = rootView.findViewById(R.id.pop_window_edit_edit_url);
    }

    @Override
    protected void doBusiness(Context context) {

    }
}
