package gaozhi.online.peoplety.ui.util.image;

import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import gaozhi.online.base.ui.BaseActivity;
import gaozhi.online.base.ui.FragmentAdapter;
import gaozhi.online.peoplety.R;

public class ShowImageActivity extends BaseActivity {
    //intent
    private static final String INTENT_IMG_URL = "urls";
    private static final String INTENT_IMG_CUR_LOC = "location";
    //ui
    private ViewPager viewPager;
    private ImageShowFragment[] imageShowFragments;
    //vary
    private List<String> urls;
    private int current;

    @Override
    protected void initParams(Intent intent) {
        setSteepStatusBar(false);
        urls = intent.getStringArrayListExtra(INTENT_IMG_URL);
        current = intent.getIntExtra(INTENT_IMG_CUR_LOC, 0);
        imageShowFragments = new ImageShowFragment[urls.size()];
    }

    @Override
    protected int bindLayout() {
        return R.layout.activity_show_image;
    }

    @Override
    protected void initView(View view) {
        for (int i = 0; i < urls.size(); i++) {
            Bundle urlBundle = new Bundle();
            urlBundle.putString(ImageShowFragment.BUNDLE_URL, urls.get(i));
            urlBundle.putInt(ImageShowFragment.BUNDLE_POS, i);
            urlBundle.putInt(ImageShowFragment.BUNDLE_SIZE, urls.size());
            ImageShowFragment fragment = new ImageShowFragment();
            fragment.setArguments(urlBundle);
            imageShowFragments[i] = fragment;
        }
        viewPager = $(R.id.image_show_view_pager);
        viewPager.setAdapter(new FragmentAdapter(getSupportFragmentManager(), imageShowFragments));
    }

    @Override
    protected void doBusiness(Context mContext) {
        //当前显示内容
        viewPager.setCurrentItem(current);
    }

    @Override
    public void onClick(View v) {

    }

    public static void startActivity(Context context, ArrayList<String> urls) {
        startActivity(context, urls, 0);
    }

    public static void startActivity(Context context, ArrayList<String> urls, int current) {
        Intent intent = new Intent(context, ShowImageActivity.class);
        intent.putExtra(INTENT_IMG_URL, urls);
        intent.putExtra(INTENT_IMG_CUR_LOC, current);
        context.startActivity(intent);
    }

    public static void startActivity(Context context, String url) {
        Intent intent = new Intent(context, ShowImageActivity.class);
        intent.putExtra(INTENT_IMG_URL, new ArrayList<>(Collections.singletonList(url)));
        context.startActivity(intent);
    }
}