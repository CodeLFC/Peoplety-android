package gaozhi.online.peoplety.ui.main.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import gaozhi.online.base.ui.BaseFragment;
import gaozhi.online.peoplety.R;
import gaozhi.online.peoplety.ui.base.DBBaseFragment;
import io.realm.Realm;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class PublishFragment extends DBBaseFragment {
    private TextView title;
    @Override
    protected void doBusiness(Realm realm) {

    }
    @Override
    public int bindLayout() {
        return R.layout.fragment_publish;
    }

    @Override
    public void initView(View view) {
        title = view.findViewById(R.id.title_text);
        title.setText(R.string.bottom_publish);
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

    }

    @Override
    public void onClick(View v) {

    }
}