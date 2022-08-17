package gaozhi.online.peoplety.ui.activity.personal;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.github.pagehelper.PageInfo;

import java.util.function.Function;

import gaozhi.online.base.net.http.DataHelper;
import gaozhi.online.peoplety.R;
import gaozhi.online.peoplety.entity.Favorite;
import gaozhi.online.peoplety.entity.Item;
import gaozhi.online.peoplety.entity.Record;
import gaozhi.online.peoplety.entity.dto.RecordDTO;
import gaozhi.online.peoplety.entity.dto.UserDTO;
import gaozhi.online.peoplety.service.record.GetItemsByFavoriteIdService;
import gaozhi.online.peoplety.ui.activity.record.RecordAdapter;
import gaozhi.online.peoplety.ui.base.DBBaseActivity;
import gaozhi.online.peoplety.ui.widget.NoAnimatorRecyclerView;
import gaozhi.online.peoplety.util.ToastUtil;
import io.realm.Realm;

/**
 * 收藏内容
 */
public class FavoriteItemActivity extends DBBaseActivity implements DataHelper.OnDataListener<PageInfo<Item>>, SwipeRefreshLayout.OnRefreshListener, NoAnimatorRecyclerView.OnLoadListener {


    public static final String INTENT_FAVORITE = "favorite";

    public static void startActivity(Context context, Favorite favorite) {
        Intent intent = new Intent(context, FavoriteItemActivity.class);
        intent.putExtra(INTENT_FAVORITE, favorite);
        context.startActivity(intent);
    }

    //db
    private UserDTO loginUser;
    //data
    private Favorite favorite;
    private PageInfo<Item> itemPageInfo;
    //service
    private static final int PAGE_SIZE = 10;
    private final GetItemsByFavoriteIdService getItemsByFavoriteIdService = new GetItemsByFavoriteIdService(this);

    //ui
    private TextView textTitle;
    private TextView textRight;
    private SwipeRefreshLayout swipeRefreshLayout;
    private FavoriteItemAdapter favoriteItemAdapter;
    private NoAnimatorRecyclerView recyclerView;
    @Override
    protected void initParams(Intent intent) {
        favorite = intent.getParcelableExtra(INTENT_FAVORITE);
    }

    @Override
    protected int bindLayout() {
        return R.layout.activity_favorite_item;
    }

    @Override
    protected void initView(View view) {
        swipeRefreshLayout = $(R.id.favorite_item_activity_swipe);
        recyclerView = $(R.id.favorite_item_activity_recycler_record);
        recyclerView.setLayoutManager(new NoAnimatorRecyclerView.BaseAdapter.DefaultLinearLayoutManager(this));
        favoriteItemAdapter = new FavoriteItemAdapter(loginUser.getToken(), getRealm());
        recyclerView.setAdapter(favoriteItemAdapter);
        swipeRefreshLayout.setOnRefreshListener(this);
        recyclerView.setOnLoadListener(this);
        textTitle = $(R.id.title_text);
        textRight = $(R.id.title_text_right);
        textRight.setText(R.string.manage);
        textRight.setOnClickListener(this);
    }

    @Override
    protected void doBusiness(Context mContext) {
        textTitle.setText(favorite.getName());
    }

    @Override
    protected void doBusiness(Realm realm) {
        loginUser = realm.where(UserDTO.class).equalTo("current", true).findFirst();
        loginUser = realm.copyFromRealm(loginUser);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == textRight.getId()) {
            FavoriteManageActivity.startActivity(this,favorite);
        }
    }

    @Override
    public void onRefresh() {
        getItemsByFavoriteIdService.request(loginUser.getToken(), favorite.getId(), 1, PAGE_SIZE);
    }

    @Override
    public void handle(int id, PageInfo<Item> data, boolean local) {
        if (data == null) return;
        itemPageInfo = data;
        if (itemPageInfo.getPageNum() <= 1) {
            favoriteItemAdapter.clear();
        }
        favoriteItemAdapter.addItem(itemPageInfo.getList());
        if(!local) {
            swipeRefreshLayout.setRefreshing(false);
            recyclerView.setLoading(false);
        }
    }

    @Override
    public void error(int id, int code, String message, String data) {
        ToastUtil.showToastShort(message + data);
        swipeRefreshLayout.setRefreshing(false);
        recyclerView.setLoading(false);
    }

    @Override
    public void onLoad() {
        if (itemPageInfo != null && itemPageInfo.isHasNextPage())
            getItemsByFavoriteIdService.request(loginUser.getToken(), favorite.getId(), itemPageInfo.getNextPage(), PAGE_SIZE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        onRefresh();
    }
}