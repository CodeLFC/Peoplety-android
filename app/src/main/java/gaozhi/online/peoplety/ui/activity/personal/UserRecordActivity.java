package gaozhi.online.peoplety.ui.activity.personal;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
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
    //service
    private PageInfo<Record> recordPageInfo;
    private final GetRecordByUserIdService getRecordByUserIdService = new GetRecordByUserIdService(this);
    private final GetUserInfoService getUserInfoService = new GetUserInfoService(new DataHelper.OnDataListener<UserDTO>() {
        @Override
        public void handle(int id, UserDTO data, boolean local) {
            if (data != null) {
                textTitle.setText(data.getUserInfo().getNick()+getString(R.string.bottom_publish));
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
        NoAnimatorRecyclerView recyclerView = $(R.id.user_record_activity_recycler_record);
        recyclerView.setLayoutManager(new NoAnimatorRecyclerView.BaseAdapter.DefaultLinearLayoutManager(this));
        recordAdapter = new RecordAdapter(loginUser.getToken());
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
        loginUser = realm.where(UserDTO.class).equalTo("current", true).findFirst();
        loginUser = realm.copyFromRealm(loginUser);
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onRefresh() {
        getRecordByUserIdService.request(loginUser.getToken(), userid, 1, PAGE_SIZE);
        getUserInfoService.request(loginUser.getToken(),userid);
    }

    @Override
    public void onLoad() {
        if (recordPageInfo != null && recordPageInfo.isHasNextPage())
            getRecordByUserIdService.request(loginUser.getToken(), userid, recordPageInfo.getNextPage(), PAGE_SIZE);
    }

    @Override
    public void handle(int id, PageInfo<Record> data) {
        recordPageInfo = data;
        if (recordPageInfo.getPageNum() <= 1) {
            recordAdapter.clear();
        }
        recordAdapter.add(data.getList());
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void error(int id, int code, String message, String data) {
        ToastUtil.showToastShort(message + data);
        swipeRefreshLayout.setRefreshing(false);
    }
}