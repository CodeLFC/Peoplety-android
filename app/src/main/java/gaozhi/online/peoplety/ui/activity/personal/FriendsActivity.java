package gaozhi.online.peoplety.ui.activity.personal;


import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.github.pagehelper.PageInfo;

import gaozhi.online.base.net.http.DataHelper;
import gaozhi.online.peoplety.R;
import gaozhi.online.peoplety.entity.Friend;
import gaozhi.online.peoplety.entity.dto.UserDTO;
import gaozhi.online.peoplety.service.friend.GetAttentionService;
import gaozhi.online.peoplety.service.friend.GetFanService;
import gaozhi.online.peoplety.ui.base.DBBaseActivity;
import gaozhi.online.peoplety.ui.widget.NoAnimatorRecyclerView;
import gaozhi.online.peoplety.util.ToastUtil;
import io.realm.Realm;

/**
 * 朋友列表
 */
public class FriendsActivity extends DBBaseActivity implements DataHelper.OnDataListener<PageInfo<Friend>>, SwipeRefreshLayout.OnRefreshListener, NoAnimatorRecyclerView.OnLoadListener {
    private static final String INTENT_ATTENTION = "attention";
    private static final String INTENT_USERID = "userid";
    private static final int PAGE_SIZE = 20;

    /**
     * @param context
     * @param attention 是否是关注列表
     */
    private static void startActivity(Context context, boolean attention, long userid) {
        Intent intent = new Intent(context, FriendsActivity.class);
        intent.putExtra(INTENT_ATTENTION, attention);
        intent.putExtra(INTENT_USERID, userid);
        context.startActivity(intent);
    }

    /**
     * 加载关注列表
     *
     * @param context
     */
    public static void startActivityForAttention(Context context, long userid) {
        startActivity(context, true, userid);
    }

    /**
     * 加载粉丝列表
     *
     * @param context
     */
    public static void startActivityForFan(Context context, long userid) {
        startActivity(context, false, userid);
    }

    //params
    private boolean isAttention;
    private long userid;
    //service
    private final GetAttentionService getAttentionService = new GetAttentionService(this);
    private final GetFanService getFanService = new GetFanService(this);
    private PageInfo<Friend> currentPageInfo;
    //ui
    private  NoAnimatorRecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private FriendAdapter friendAdapter;
    private TextView textTitle;
    //db
    private UserDTO loginUser;

    @Override
    protected void initParams(Intent intent) {
        isAttention = intent.getBooleanExtra(INTENT_ATTENTION, false);
        userid = intent.getLongExtra(INTENT_USERID, 0);
    }

    @Override
    protected int bindLayout() {
        return R.layout.activity_friends;
    }

    @Override
    protected void initView(View view) {
        swipeRefreshLayout = $(R.id.friends_activity_swipe);
        recyclerView = $(R.id.friends_activity_recycler_friend);
        recyclerView.setLayoutManager(new NoAnimatorRecyclerView.BaseAdapter.DefaultLinearLayoutManager(this));
        friendAdapter = new FriendAdapter(loginUser.getToken(),userid);
        recyclerView.setAdapter(friendAdapter);
        swipeRefreshLayout.setOnRefreshListener(this);
        recyclerView.setOnLoadListener(this);
        textTitle = $(R.id.title_text);
    }

    @Override
    protected void doBusiness(Context mContext) {
        if (isAttention) {
            textTitle.setText(R.string.attention);
        } else {
            textTitle.setText(R.string.fans);
        }
        onRefresh();
    }

    @Override
    protected void doBusiness(Realm realm) {
        loginUser =getLoginUser();
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onRefresh() {
        request(1);
    }

    /**
     * 请求数据
     *
     * @param pageNum
     */
    private void request(int pageNum) {
        if (isAttention) {
            getAttentionService.request(loginUser.getToken(), userid, pageNum, PAGE_SIZE);
            return;
        }
        getFanService.request(loginUser.getToken(), userid, pageNum, PAGE_SIZE);
    }

    @Override
    public void onLoad() {
        if (currentPageInfo != null && currentPageInfo.isHasNextPage()) {
            request(currentPageInfo.getNextPage());
        }
    }

    @Override
    public void handle(int id, PageInfo<Friend> data, boolean local) {
        if (data == null) return;
        currentPageInfo = data;
        if (currentPageInfo.getPageNum() <= 1) {
            friendAdapter.clear();
        }
        friendAdapter.add(data.getList());
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
}