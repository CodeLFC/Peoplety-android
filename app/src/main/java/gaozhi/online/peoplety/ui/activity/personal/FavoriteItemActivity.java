package gaozhi.online.peoplety.ui.activity.personal;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.github.pagehelper.PageInfo;

import gaozhi.online.base.net.http.DataHelper;
import gaozhi.online.peoplety.R;
import gaozhi.online.peoplety.entity.Favorite;
import gaozhi.online.peoplety.entity.Item;
import gaozhi.online.peoplety.entity.dto.UserDTO;
import gaozhi.online.peoplety.service.record.GetItemsByFavoriteIdService;
import gaozhi.online.peoplety.ui.base.DBBaseActivity;
import gaozhi.online.peoplety.util.ToastUtil;
import io.realm.Realm;

/**
 * 收藏内容
 */
public class FavoriteItemActivity extends DBBaseActivity implements DataHelper.OnDataListener<PageInfo<Item>>, SwipeRefreshLayout.OnRefreshListener {
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

    }

    @Override
    protected void doBusiness(Context mContext) {
        onRefresh();
    }

    @Override
    protected void doBusiness(Realm realm) {
        loginUser = realm.where(UserDTO.class).equalTo("current", true).findFirst();
        loginUser = realm.copyFromRealm(loginUser);
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onRefresh() {
        getItemsByFavoriteIdService.request(loginUser.getToken(), favorite.getId(), 1, PAGE_SIZE);
    }

    @Override
    public void handle(int id, PageInfo<Item> data, boolean local) {
        if(data == null)return;

        itemPageInfo = data;
        if (itemPageInfo.getPageNum() <= 1) {

        }

    }

    @Override
    public void error(int id, int code, String message, String data) {
        ToastUtil.showToastShort(message + data);
    }
}