package gaozhi.online.peoplety.ui.activity.personal;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.github.pagehelper.PageInfo;

import gaozhi.online.base.net.http.DataHelper;
import gaozhi.online.peoplety.R;
import gaozhi.online.peoplety.entity.Record;
import gaozhi.online.peoplety.entity.dto.UserDTO;
import gaozhi.online.peoplety.service.record.GetRecordByUserIdService;
import gaozhi.online.peoplety.service.user.GetUserInfoService;
import gaozhi.online.peoplety.ui.activity.record.RecordAdapter;
import gaozhi.online.peoplety.ui.activity.record.RecordDetailActivity;
import gaozhi.online.peoplety.ui.base.DBBaseActivity;
import gaozhi.online.peoplety.ui.widget.NoAnimatorRecyclerView;
import gaozhi.online.peoplety.util.ToastUtil;
import io.realm.Realm;

/**
 * 显示用户发布的所有卷宗列表
 */
public class UserRecordActivity extends DBBaseActivity implements SwipeRefreshLayout.OnRefreshListener, DataHelper.OnDataListener<PageInfo<Record>>, NoAnimatorRecyclerView.OnLoadListener {
    private static final String INTENT_USER_ID = "userid";

    public static void startActivity(Context context, long userid) {
        Intent intent = new Intent(context, UserRecordActivity.class);
        intent.putExtra(INTENT_USER_ID, userid);
        context.startActivity(intent);
    }

    //params
    private static final int PAGE_SIZE = 10;
    private long userid;
    //db
    private UserDTO loginUser;
    //ui
    private TextView textTitle;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecordAdapter recordAdapter;
    private NoAnimatorRecyclerView recyclerView;
    //service
    private PageInfo<Record> recordPageInfo;
    private final GetRecordByUserIdService getRecordByUserIdService = new GetRecordByUserIdService(this);
    private final GetUserInfoService getUserInfoService = new GetUserInfoService(new DataHelper.OnDataListener<UserDTO>() {
        @Override
        public void handle(int id, UserDTO data, boolean local) {
            if (data != null) {
                textTitle.setText(data.getUserInfo().getNick());
            }
        }
    });

    @Override
    protected void initParams(Intent intent) {
        userid = intent.getLongExtra(INTENT_USER_ID, 0);
    }

    @Override
    protected int bindLayout() {
        return R.layout.activity_user_record;
    }

    @Override
    protected void initView(View view) {
        swipeRefreshLayout = $(R.id.user_record_activity_swipe);
        recyclerView = $(R.id.user_record_activity_recycler_record);
        recyclerView.setLayoutManager(new NoAnimatorRecyclerView.BaseAdapter.DefaultLinearLayoutManager(this));
        recordAdapter = new RecordAdapter(loginUser.getToken(), new NoAnimatorRecyclerView.BaseAdapter.BaseSortedListAdapterCallback<>() {
            @Override
            public int compare(Record o1, Record o2) {
                if ((o1.isTop() && o2.isTop()) || (!o1.isTop() && !o2.isTop())) {
                    return (int) (o2.getId() - o1.getId());
                }
                return o1.isTop() ? -1 : 1;
            }
        });
        //点击条目打开详情
        recordAdapter.setOnItemClickedListener(record -> RecordDetailActivity.startActivity(this, record.getId()));
        recyclerView.setAdapter(recordAdapter);
        swipeRefreshLayout.setOnRefreshListener(this);
        recyclerView.setOnLoadListener(this);
        textTitle = $(R.id.title_text);
    }

    @Override
    protected void doBusiness(Context mContext) {
        onRefresh();
    }

    @Override
    protected void doBusiness(Realm realm) {
        loginUser = getLoginUser();
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onRefresh() {
        getRecordByUserIdService.request(loginUser.getToken(), userid, 1, PAGE_SIZE);
        getUserInfoService.request(loginUser.getToken(), userid);
    }

    @Override
    public void onLoad() {
        if (recordPageInfo != null && recordPageInfo.isHasNextPage())
            getRecordByUserIdService.request(loginUser.getToken(), userid, recordPageInfo.getNextPage(), PAGE_SIZE);
    }

    @Override
    public void handle(int id, PageInfo<Record> data, boolean local) {
        if (data == null) return;
        recordPageInfo = data;
        if (recordPageInfo.getPageNum() <= 1) {
            recordAdapter.clear();
        }
        recordAdapter.add(data.getList());
        if (!local) {
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