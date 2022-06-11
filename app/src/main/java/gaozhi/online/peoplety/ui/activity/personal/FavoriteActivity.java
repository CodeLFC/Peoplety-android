package gaozhi.online.peoplety.ui.activity.personal;


import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.github.pagehelper.PageInfo;

import java.util.function.Consumer;

import gaozhi.online.base.net.http.DataHelper;
import gaozhi.online.peoplety.R;
import gaozhi.online.peoplety.entity.Favorite;
import gaozhi.online.peoplety.entity.dto.UserDTO;
import gaozhi.online.peoplety.service.record.GetFavoritesByUseridService;
import gaozhi.online.peoplety.ui.base.DBBaseActivity;
import gaozhi.online.peoplety.ui.widget.NoAnimatorRecyclerView;
import gaozhi.online.peoplety.util.ToastUtil;
import io.realm.Realm;

/**
 * 喜欢的内容
 */
public class FavoriteActivity extends DBBaseActivity implements DataHelper.OnDataListener<PageInfo<Favorite>>, SwipeRefreshLayout.OnRefreshListener, NoAnimatorRecyclerView.OnLoadListener, Consumer<Favorite> {

    private static final String INTENT_USERID = "userid";
    private static final int PAGE_SIZE = 20;

    /**
     * @param context
     */
    public static void startActivity(Context context, long userid) {
        Intent intent = new Intent(context, FavoriteActivity.class);
        intent.putExtra(INTENT_USERID, userid);
        context.startActivity(intent);
    }

    //ui
    private TextView textTitle;
    private TextView textRight;
    private SwipeRefreshLayout swipeRefreshLayout;
    private FavoriteAdapter favoriteAdapter;
    //intent
    private long userid;
    //service
    private final GetFavoritesByUseridService getFavoritesByUseridService = new GetFavoritesByUseridService(this);
    //db
    private UserDTO loginUser;
    //data
    private PageInfo<Favorite> currentPageInfo;

    @Override
    protected void initParams(Intent intent) {
        userid = intent.getLongExtra(INTENT_USERID, 0);
    }

    @Override
    protected int bindLayout() {
        return R.layout.activity_favorite;
    }

    @Override
    protected void initView(View view) {
        textTitle = $(R.id.title_text);
        textTitle.setText(R.string.favorite);
        textRight = $(R.id.title_text_right);
        textRight.setText(R.string.add);
        textRight.setOnClickListener(this);
        swipeRefreshLayout = $(R.id.favorite_activity_swipe);
        swipeRefreshLayout.setOnRefreshListener(this);
        NoAnimatorRecyclerView recyclerViewFavorite = $(R.id.favorite_activity_recycler_favorite);
        recyclerViewFavorite.setLayoutManager(new NoAnimatorRecyclerView.BaseAdapter.DefaultLinearLayoutManager(this));
        recyclerViewFavorite.setOnLoadListener(this);
        favoriteAdapter = new FavoriteAdapter();
        recyclerViewFavorite.setAdapter(favoriteAdapter);
        favoriteAdapter.setOnItemClickedListener(this);
    }

    @Override
    protected void doBusiness(Context mContext) {
        if(userid!=loginUser.getUserInfo().getId()){
            textRight.setVisibility(View.GONE);
        }
        onRefresh();
    }

    @Override
    protected void doBusiness(Realm realm) {
        loginUser = realm.where(UserDTO.class).equalTo("current", true).findFirst();
        loginUser = realm.copyFromRealm(loginUser);
    }

    @Override
    public void onClick(View v) {
       if(v.getId() == textRight.getId()){
           FavoriteManageActivity.startActivity(this);
       }
    }

    @Override
    public void onRefresh() {
        getFavoritesByUseridService.request(loginUser.getToken(), userid, 1, PAGE_SIZE);
    }

    @Override
    public void onLoad() {
        if (currentPageInfo != null && currentPageInfo.isHasNextPage()) {
            getFavoritesByUseridService.request(loginUser.getToken(), userid, currentPageInfo.getNextPage(), PAGE_SIZE);
        }
    }

    @Override
    public void handle(int id, PageInfo<Favorite> data, boolean local) {
        if (data == null) return;
        currentPageInfo = data;
        if (currentPageInfo.getPageNum() <= 1) {
            favoriteAdapter.clear();
        }
        favoriteAdapter.add(data.getList());
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void error(int id, int code, String message, String data) {
        ToastUtil.showToastShort(message + data);
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void accept(Favorite favorite) {
        FavoriteItemActivity.startActivity(this, favorite);
    }
}