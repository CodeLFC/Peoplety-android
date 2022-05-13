package gaozhi.online.peoplety.ui.util.pop;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import gaozhi.online.base.ui.BasePopupWindow;
import gaozhi.online.peoplety.R;

/**
 *
 */
public class TipPopWindow extends BasePopupWindow {
    private TextView textTitle;
    private TextView textContent;
    private Button btnOk;

    public TipPopWindow(Context context, boolean fullScreen) {
        super(context, R.layout.pop_window_tip, fullScreen);
    }

    @Override
    public void initView(View rootView) {
        textTitle = rootView.findViewById(R.id.pop_widow_tip_title_text);
        textContent = rootView.findViewById(R.id.pop_widow_tip_message);
        btnOk = rootView.findViewById(R.id.pop_widow_tip_btn_ok);
        btnOk.setOnClickListener(v -> dismiss());
    }

    @Override
    public void doBusiness(Context context) {
        setOutsideTouchable(false);
    }

    public TipPopWindow setOkClickListener(View.OnClickListener listener) {
        btnOk.setOnClickListener(listener);
        return this;
    }

    public TipPopWindow setMessage(String message) {
        textContent.setText(message);
        return this;
    }

    public TipPopWindow setTitle(String title) {
        textTitle.setText(title);
        return this;
    }
}