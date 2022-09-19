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

import gaozhi.online.base.net.Result;
import gaozhi.online.base.net.http.DataHelper;
import gaozhi.online.base.ui.BaseFragment;
import gaozhi.online.base.ui.FragmentAdapter;
import gaozhi.online.peoplety.R;
import gaozhi.online.peoplety.entity.Favorite;
import gaozhi.online.peoplety.entity.Item;
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
    public static final String INTENT_PAGE = "open_page";
    public static final int HOME = 0;
    public static final int ATTENTION = 1;
    public static final int PUBLISH = 2;
    public static final int MESSAGE = 3;
    public static final int ME = 4;
    private BaseFragment[] fragments;
    //service
    private final GetUserInfoService getUserInfoService = new GetUserInfoService(this);
    private UserDTO loginUser;
    //util

    //permission
    //授权
    private PermissionUtil permissionUtil;
    private final String[] authorities = new String[]{
//            Manifest.permission.READ_EXTERNAL_STORAGE,
//            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.ACCESS_WIFI_STATE,
//            Manifest.permission.READ_PHONE_STATE,
//            Manifest.permission.ACCESS_COARSE_LOCATION,
//            Manifest.permission.ACCESS_FINE_LOCATION
    };

    @Override
    protected void doBusiness(Realm realm) {
        loginUser = getLoginUser();
    }

    @Override
    protected void initParams(Intent intent) {

    }

    @Override
    protected int bindLayout() {
        return R.layout.activity_main;
    }

    @Override
    protected void initView(View view) {
        bottomNavigationView = $(R.id.main_navigation);
        //删除长按toast响应
        BottomNavigationMenuView bottomNavigationMenuView = (BottomNavigationMenuView) bottomNavigationView.getChildAt(0);
        for (int i = 0; i < bottomNavigationMenuView.getChildCount(); i++) {
            bottomNavigationMenuView.getChildAt(i).setOnLongClickListener(v -> true);
        }
        //选中响应
        bottomNavigationView.setOnItemSelectedListener(this);
        // 初始化碎片
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
        permissionUtil = new PermissionUtil(this, 100);
        permissionUtil.setPermissionListener(() -> {
            ToastUtil.showToastLong(R.string.not_permission);
        });
        //请求权限
        permissionUtil.requestPermission(authorities);
        handleIntent(getIntent());
    }

    public static void startActivity(Context context){
        startActivity(context,HOME);
    }

    public static void startActivity(Context context, int page) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra(INTENT_PAGE, page);
        context.startActivity(intent);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Handle onNewIntent() to inform the fragment manager that the
     * state is not saved.  If you are handling new intents and may be
     * making changes to the fragment state, you want to be sure to call
     * through to the super-class here first.  Otherwise, if your state
     * is saved but the activity is not stopped, you could get an
     * onNewIntent() call which happens before onResume() and trying to
     * perform fragment operations at that point will throw IllegalStateException
     * because the fragment manager thinks the state is still saved.
     *
     * @param intent
     */
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
         handleIntent(intent);
    }
    private void handleIntent(Intent intent){
        int page =intent.getIntExtra(INTENT_PAGE,0);
        viewPager.setCurrentItem(page);
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
         * 这个方法在手指操作屏幕的时候发生变化。
         *
         *   有三个值：0（END）,1(PRESS) , 2(UP) 。当用手指滑动翻页时，手指按下去的时候
         *
         *   会触发这个方法，state值为1，手指抬起时，如果发生了滑动（即使很小），这个值
         *
         *   会变为2，然后最后变为0 。总共执行这个方法三次。
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
        if (code == Result.NET_ERROR) {
            return;
        }
        viewPager.post(() -> new TipPopWindow(MainActivity.this, true)
                .setOkClickListener((window, v) -> {
                    window.dismiss();
                    //关闭主页，进入登录页
                    LoginActivity.startActivity(MainActivity.this);
                    finish();
                })
                .setMessage(message + data)
                .showPopupWindow(MainActivity.this));
    }

    @Override
    protected void onResume() {
        super.onResume();
        //更新信息
        getUserInfoService.request(loginUser.getToken(), loginUser.getUserInfo().getId());
    }

    @Override
    protected void onStart() {
        super.onStart();
        //清除旧数据
        Realm.getDefaultInstance().executeTransaction(realm -> {
            realm.delete(Favorite.class);
            realm.delete(Item.class);
        });
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