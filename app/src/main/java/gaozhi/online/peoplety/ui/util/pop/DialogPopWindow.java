package gaozhi.online.peoplety.ui.util.pop;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import gaozhi.online.base.ui.BasePopupWindow;
import gaozhi.online.peoplety.R;

/**
 * 双按钮的popWindow 没有完成
 */
public class DialogPopWindow extends BasePopupWindow {
    private TextView message;
    private Button btnLeft;
    private Button btnRight;

    public DialogPopWindow(Context context) {
        super(context, R.layout.pop_window_dialog, true);
    }

    @Override
    protected void initView(View rootView) {
        message = rootView.findViewById(R.id.pop_window_dialog_message);
        btnLeft = rootView.findViewById(R.id.pop_window_dialog_btn_cancel);
        btnRight = rootView.findViewById(R.id.pop_window_dialog_btn_ok);
        btnLeft.setOnClickListener(v->dismiss());
    }

    @Override
    protected void doBusiness(Context context) {

    }

    public TextView getMessage() {
        return message;
    }

    public Button getBtnLeft() {
        return btnLeft;
    }

    public Button getBtnRight() {
        return btnRight;
    }
}
