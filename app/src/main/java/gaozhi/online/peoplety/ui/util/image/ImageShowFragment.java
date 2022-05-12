package gaozhi.online.peoplety.ui.util.image;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.View;
import android.widget.TextView;

import com.github.chrisbanes.photoview.PhotoView;

import gaozhi.online.base.ui.BaseFragment;
import gaozhi.online.peoplety.R;
import gaozhi.online.peoplety.util.GlideUtil;


/**
 * A simple {@link Fragment} subclass.
 */
public class ImageShowFragment extends BaseFragment {
    //intent
    public static final String BUNDLE_URL = "url";
    public static final String BUNDLE_POS = "pos";
    public static final String BUNDLE_SIZE = "size";
    //vary
    private String url;
    private int pos;
    private int size;
    //ui
    private TextView text_title;
    private PhotoView photoView;

    @Override
    public int bindLayout() {
        return R.layout.fragment_image_show;
    }

    @Override
    public void initView(View view) {
        text_title = view.findViewById(R.id.fragment_image_show_title);
        photoView = view.findViewById(R.id.fragment_image_show_photo_view);
    }

    @Override
    public void initParams(Bundle bundle) {
        url = bundle.getString(BUNDLE_URL);
        pos = bundle.getInt(BUNDLE_POS);
        size = bundle.getInt(BUNDLE_SIZE);
    }

    @Override
    public void doBusiness() {
        text_title.setText((pos + 1) + "/" + size);
        GlideUtil.loadImage(getContext(), url,R.drawable.app_logo, photoView);
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