package gaozhi.online.peoplety.ui.activity.home.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.pagehelper.PageInfo;

import gaozhi.online.base.net.http.DataHelper;
import gaozhi.online.peoplety.R;
import gaozhi.online.peoplety.entity.Record;
import gaozhi.online.peoplety.entity.dto.UserDTO;
import gaozhi.online.peoplety.service.record.GetAttentionRecordByUseridService;
import gaozhi.online.peoplety.ui.activity.record.RecordAdapter;
import gaozhi.online.peoplety.ui.activity.record.RecordDetailActivity;
import gaozhi.online.peoplety.ui.base.DBBaseFragment;
import gaozhi.online.peoplety.ui.widget.NoAnimatorRecyclerView;
import io.realm.Realm;

/**
 * 关注内容
 */
public class AttentionFragment extends DBBaseFragment implements NoAnimatorRecyclerView.OnLoadListener, SwipeRefreshLayout.OnRefreshListener, DataHelper.OnDataListener<PageInfo<Record>> {
    private static final int PAGE_SIZE = 10;
    //ui
    private TextView textTitle;
    private NoAnimatorRecyclerView recordRecyclerView;
    private RecordAdapter recordAdapter;
    private SwipeRefreshLayout recyclerSwipeRefreshView;
    //db info
    private UserDTO loginUser;
    //data
    private final GetAttentionRecordByUseridService getAttentionRecordByUseridService = new GetAttentionRecordByUseridService(this);
    private PageInfo<Record> currentRecordPageInfo;

    @Override
    public int bindLayout() {
        return R.layout.fragment_attention;
    }

    @Override
    public void initView(View view) {
        textTitle = view.findViewById(R.id.title_text);
        textTitle.setText(R.string.attention);
        recordRecyclerView = view.findViewById(R.id.fragment_attention_recycler_record);
        LinearLayoutManager linearLayoutManager = new NoAnimatorRecyclerView.BaseAdapter.DefaultLinearLayoutManager(getContext());
        recordRecyclerView.setLayoutManager(linearLayoutManager);
        recordAdapter = new RecordAdapter(loginUser.getToken());
        recordRecyclerView.setAdapter(recordAdapter);
        recordRecyclerView.setOnLoadListener(this);

        recyclerSwipeRefreshView = view.findViewById(R.id.fragment_attention_swipe_record);
        recyclerSwipeRefreshView.setOnRefreshListener(this);
        //点击条目打开详情
        recordAdapter.setOnItemClickedListener(record -> RecordDetailActivity.startActivity(getContext(), record.getId()));
    }

    @Override
    public void initParams(Bundle bundle) {

    }

    @Override
    public void doBusiness() {
        //请求某个地区的资料
        getAttentionRecordByUseridService.request(loginUser.getToken(), loginUser.getUserInfo().getId(),1, PAGE_SIZE);
    }

    @Override
    public void onPageScrolled(float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected() {

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
    public void handle(int id, PageInfo<Record> data, boolean local) {
        //本地数据非第一页会返回null
        if (data == null) return;
        if (!local) {
            //停止刷新状态
            recyclerSwipeRefreshView.setRefreshing(false);
            recordRecyclerView.setLoading(false);
        }
        currentRecordPageInfo = data;
        if (currentRecordPageInfo.getPageNum() <= 1) {
            recordAdapter.clear();
        }

        recordAdapter.add(currentRecordPageInfo.getList());
    }

    @Override
    public void error(int id, int code, String message, String data) {
        //停止刷新状态
        recyclerSwipeRefreshView.setRefreshing(false);
        recordRecyclerView.setLoading(false);
    }

    @Override
    public void onRefresh() {
       doBusiness();
    }

    @Override
    public void onLoad() {
        if (currentRecordPageInfo == null) {
            recordRecyclerView.setLoading(false);
            return;
        }
        if (currentRecordPageInfo.isHasNextPage()) {
            //请求某个地区的资料
            getAttentionRecordByUseridService.request(loginUser.getToken(), loginUser.getUserInfo().getId(), currentRecordPageInfo.getNextPage(), PAGE_SIZE);
        } else {
            recordRecyclerView.setLoading(false);
            //提醒到底了
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (recordAdapter == null || recordAdapter.getItemCount() == 0) {
            doBusiness();
        }
    }
}