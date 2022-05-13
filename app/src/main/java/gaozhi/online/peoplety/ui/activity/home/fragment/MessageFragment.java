package gaozhi.online.peoplety.ui.activity.home.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import gaozhi.online.base.ui.BaseFragment;
import gaozhi.online.peoplety.R;
import gaozhi.online.peoplety.ui.base.DBBaseFragment;
import gaozhi.online.peoplety.util.ToastUtil;
import io.realm.Realm;

/**
 * create an instance of this fragment.
 * 消息页
 */
public class MessageFragment extends DBBaseFragment {
    private TextView title;
    private View commentView;
    private View friendView;

    @Override
    public int bindLayout() {
        return R.layout.fragment_message;
    }

    @Override
    public void initView(View view) {
        title = view.findViewById(R.id.title_text);
        title.setText(R.string.bottom_message);
        commentView = view.findViewById(R.id.fragment_message_view_comment);
        commentView.setOnClickListener(this);
        friendView = view.findViewById(R.id.fragment_message_view_friends);
        friendView.setOnClickListener(this);
    }

    @Override
    public void initParams(Bundle bundle) {

    }

    @Override
    public void doBusiness() {

    }

    @Override
    public void onPageScrolled(float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected() {
        ToastUtil.showToastShort("只在应用内刷新消息");
    }

    @Override
    protected void doBusiness(Realm realm) {

    }

    @Override
    public void onClick(View v) {

    }
}