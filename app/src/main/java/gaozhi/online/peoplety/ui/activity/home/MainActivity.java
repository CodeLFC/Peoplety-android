package gaozhi.online.peoplety.ui.activity.home;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.bottomnavigation.BottomNavigationMenuView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import gaozhi.online.base.net.http.DataHelper;
import gaozhi.online.base.ui.BaseFragment;
import gaozhi.online.base.ui.FragmentAdapter;
import gaozhi.online.peoplety.R;
import gaozhi.online.peoplety.entity.dto.UserDTO;
import gaozhi.online.peoplety.service.NetConfig;
import gaozhi.online.peoplety.service.user.GetUserInfoService;
import gaozhi.online.peoplety.ui.activity.home.fragment.AttentionFragment;
import gaozhi.online.peoplety.ui.activity.home.fragment.home.HomeFragment;
import gaozhi.online.peoplety.ui.activity.home.fragment.MeFragment;
import gaozhi.online.peoplety.ui.activity.home.fragment.message.MessageFragment;
import gaozhi.online.peoplety.ui.activity.home.fragment.publish.PublishFragment;
import gaozhi.online.peoplety.ui.activity.login.LoginActivity;
import gaozhi.online.peoplety.ui.activity.personal.PersonalActivity;
import gaozhi.online.peoplety.ui.base.DBBaseActivity;
import gaozhi.online.peoplety.ui.util.WebActivity;
import gaozhi.online.peoplety.ui.util.pop.TipPopWindow;
import gaozhi.online.peoplety.ui.util.scan.ScanActivity;
import gaozhi.online.peoplety.util.PatternUtil;
import gaozhi.online.peoplety.util.PermissionUtil;
import gaozhi.online.peoplety.util.ToastUtil;
import io.realm.Realm;

public class MainActivity extends DBBaseActivity implements NavigationBarView.OnItemSelectedListener, ViewPager.OnPageChangeListener, DataHelper.OnDataListener<UserDTO> {
    //ui
    private BottomNavigationView bottomNavigationView;
    private ViewPager viewPager;
    private static final int HOME = 0;
    private static final int ATTENTION = 1;
    private static final int PUBLISH = 2;
    private static final int MESSAGE = 3;
    private static final int ME = 4;

    private BaseFragment[] fragments;
    //service
    private final GetUserInfoService getUserInfoService = new GetUserInfoService(this);
    private UserDTO loginUser;
    //util

    //permission
    //??????
    private PermissionUtil permissionUtil;
    private final String[] authorities = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.ACCESS_WIFI_STATE,
//            Manifest.permission.READ_PHONE_STATE,
//            Manifest.permission.ACCESS_COARSE_LOCATION,
//            Manifest.permission.ACCESS_FINE_LOCATION
    };

    @Override
    protected void doBusiness(Realm realm) {
        loginUser = realm.where(UserDTO.class).equalTo("current", true).findFirst();
        //build????????????Realm???????????????
        loginUser = realm.copyFromRealm(loginUser);
    }

    @Override
    protected void initParams(Intent intent) {
        permissionUtil = new PermissionUtil(this, 100);

    }

    @Override
    protected int bindLayout() {
        return R.layout.activity_main;
    }

    @Override
    protected void initView(View view) {
        bottomNavigationView = $(R.id.main_navigation);
        //????????????toast??????
        BottomNavigationMenuView bottomNavigationMenuView = (BottomNavigationMenuView) bottomNavigationView.getChildAt(0);
        for (int i = 0; i < bottomNavigationMenuView.getChildCount(); i++) {
            bottomNavigationMenuView.getChildAt(i).setOnLongClickListener(v -> true);
        }
        //????????????
        bottomNavigationView.setOnItemSelectedListener(this);
        // ???????????????
        fragments = new BaseFragment[]{
                new HomeFragment(),
                new AttentionFragment(),
                new PublishFragment(),
                new MessageFragment(),
                new MeFragment()
        };


        viewPager = $(R.id.main_view_pager);
        viewPager.setAdapter(new FragmentAdapter(getSupportFragmentManager(), fragments));
        viewPager.addOnPageChangeListener(this);
    }

    @Override
    protected void doBusiness(Context mContext) {
        permissionUtil.setPermissionListener(() -> {
            ToastUtil.showToastLong(R.string.not_permission);
        });
        //????????????
        permissionUtil.requestPermission(authorities);
    }

    public static void startActivity(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        context.startActivity(intent);
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        fragments[position].onPageScrolled(positionOffset, positionOffsetPixels);
    }

    @Override
    public void onPageSelected(int position) {
        fragments[position].onPageSelected();
        bottomNavigationView.setSelectedItemId(bottomNavigationView.getMenu().getItem(position).getItemId());
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        /**
         * ?????????????????????????????????????????????????????????
         *
         *   ???????????????0???END???,1(PRESS) , 2(UP) ?????????????????????????????????????????????????????????
         *
         *   ????????????????????????state??????1????????????????????????????????????????????????????????????????????????
         *
         *   ?????????2?????????????????????0 ????????????????????????????????????
         */
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.main_bottom_attention:
                viewPager.setCurrentItem(ATTENTION);
                return true;
            case R.id.main_bottom_me:
                viewPager.setCurrentItem(ME);
                return true;
            case R.id.main_bottom_publish:
                viewPager.setCurrentItem(PUBLISH);
                return true;
            case R.id.main_bottom_message:
                viewPager.setCurrentItem(MESSAGE);
                return true;
            case R.id.main_bottom_home:
                viewPager.setCurrentItem(HOME);
                return true;
        }
        return false;
    }

    @Override
    public void error(int id, int code, String message, String data) {
        viewPager.post(() -> new TipPopWindow(MainActivity.this, true)
                .setOkClickListener((window, v) -> {
                    window.dismiss();
                    //??????????????????????????????
                    LoginActivity.startActivity(MainActivity.this);
                    finish();
                })
                .setMessage(message + data)
                .showPopupWindow(MainActivity.this));
    }

    @Override
    protected void onResume() {
        super.onResume();
        //????????????
        getUserInfoService.request(loginUser.getToken(), loginUser.getUserInfo().getId());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == ScanActivity.QR_RESULT_CODE) {
            String qrContent = data.getStringExtra(ScanActivity.QR_CONTENT_KEY);
            if (qrContent.startsWith(NetConfig.officialURL)) {
                String userid = qrContent.substring(NetConfig.officialURL.length());
                PersonalActivity.startActivity(this, Long.parseLong(userid));
            } else if (PatternUtil.matchUrl(qrContent)) {
                WebActivity.startActivity(this, qrContent, getString(R.string.tip_scan_result));
            }
        }
    }
}