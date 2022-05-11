package gaozhi.online.peoplety.ui.activity.home;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.util.LinkedList;
import java.util.List;

import gaozhi.online.base.ui.BaseActivity;
import gaozhi.online.base.ui.BaseFragment;
import gaozhi.online.base.ui.FragmentAdapter;
import gaozhi.online.peoplety.R;
import gaozhi.online.peoplety.ui.activity.home.fragment.HomeFragment;
import gaozhi.online.peoplety.ui.activity.home.fragment.MeFragment;
import gaozhi.online.peoplety.ui.activity.home.fragment.PublishFragment;
import gaozhi.online.peoplety.util.ToastUtil;

public class MainActivity extends BaseActivity implements NavigationBarView.OnItemSelectedListener, ViewPager.OnPageChangeListener {
    //ui
    private BottomNavigationView bottomNavigationView;
    private ViewPager viewPager;
    private static final int HOME=0;
    private static final int PUBLISH=1;
    private static final int ME=2;
    private BaseFragment[] fragments;
    //permission
    //授权
    private final int PERMISSION_REQUEST_CODE = 100;
    private final String[]authorities= new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE,
//            Manifest.permission.WRITE_EXTERNAL_STORAGE,
//            Manifest.permission.ACCESS_NETWORK_STATE,
//            Manifest.permission.ACCESS_WIFI_STATE,
//            Manifest.permission.READ_PHONE_STATE,
//            Manifest.permission.ACCESS_COARSE_LOCATION,
//            Manifest.permission.ACCESS_FINE_LOCATION
    };

    /**
     * 申请权限
     */
    private void requestPermission(){
        List<String> permissionList=new LinkedList<>();
        for (String authority : authorities) {
            if (ContextCompat.checkSelfPermission(this, authority) != PackageManager.PERMISSION_GRANTED) {
                permissionList.add(authority);
            }
        }

        if(permissionList.size()>0){
            ActivityCompat.requestPermissions(this,authorities, PERMISSION_REQUEST_CODE);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode==PERMISSION_REQUEST_CODE){
            boolean hasNotGranted=false;//是否有权限没有通过
            for(int grant:grantResults){
                if(grant==PackageManager.PERMISSION_DENIED){
                    hasNotGranted=true;
                    Log.i(TAG,"deny:"+grant);
                }
            }
            if(hasNotGranted){
                ToastUtil.showToastLong(R.string.not_permission);
                finish();
            }
        }
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
        bottomNavigationView=$(R.id.main_navigation);
        bottomNavigationView.setOnItemSelectedListener(this);
        bottomNavigationView.setOnLongClickListener(v -> true);
        fragments=new BaseFragment[]{
                new HomeFragment(),
                new PublishFragment(),
                new MeFragment()
        };


        viewPager=$(R.id.main_view_pager);
        viewPager.setAdapter(new FragmentAdapter(getSupportFragmentManager(),fragments));
        viewPager.addOnPageChangeListener(this);
        //viewPager.setCurrentItem(ME);
        //请求权限
        requestPermission();
    }

    @Override
    protected void doBusiness(Context mContext) {

    }

    public static void startActivity(Context context) {
        Intent intent=new Intent(context,MainActivity.class);
        context.startActivity(intent);
    }

    @Override
    public void onClick(View v) {

    }
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        fragments[position].onPageScrolled(positionOffset,positionOffsetPixels);
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
        switch (item.getItemId()){
            case R.id.main_bottom_me:
                viewPager.setCurrentItem(ME);
                return true;
            case R.id.main_bottom_publish:
                viewPager.setCurrentItem(PUBLISH);
                return true;
            case R.id.main_bottom_home:
                viewPager.setCurrentItem(HOME);
                return true;
        }
        return false;
    }
}