package gaozhi.online.peoplety.ui.activity.home.fragment.home;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.View;
import android.widget.TextView;

import com.github.pagehelper.PageInfo;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

import gaozhi.online.base.net.http.DataHelper;
import gaozhi.online.peoplety.R;
import gaozhi.online.peoplety.entity.Area;
import gaozhi.online.peoplety.entity.Record;
import gaozhi.online.peoplety.entity.RecordType;
import gaozhi.online.peoplety.entity.dto.UserDTO;
import gaozhi.online.peoplety.service.record.GetRecordByAreaService;
import gaozhi.online.peoplety.ui.activity.record.RecordAdapter;
import gaozhi.online.peoplety.ui.activity.record.RecordDetailActivity;
import gaozhi.online.peoplety.ui.base.DBBaseFragment;
import gaozhi.online.peoplety.ui.util.pop.AreaPopWindow;
import gaozhi.online.peoplety.ui.util.pop.OptionsPopWindow;
import gaozhi.online.peoplety.ui.util.pop.TipPopWindow;
import gaozhi.online.peoplety.ui.widget.NoAnimatorRecyclerView;
import io.realm.Realm;

/**
 * A simple {@link Fragment} subclass. 主页
 */
public class HomeFragment extends DBBaseFragment implements Consumer<Area>, DataHelper.OnDataListener<PageInfo<Record>>, SwipeRefreshLayout.OnRefreshListener, NoAnimatorRecyclerView.OnLoadListener {
    private static final int PAGE_SIZE = 10;
    //ui
    private TextView titleTextLeft;
    private AreaPopWindow areaPopWindow;
    private TextView titleTextRight;
    private OptionsPopWindow optionsPopWindow;
    //选择
    private static final int SELECT_ALL = -1;
    private static final int OK = -2;
    //ui
    private NoAnimatorRecyclerView recordRecyclerView;
    private RecordAdapter recordAdapter;
    private SwipeRefreshLayout recyclerSwipeRefreshView;
    //db info
    private UserDTO loginUser;
    //获取内容
    private final GetRecordByAreaService getRecordByAreaService = new GetRecordByAreaService(this);
    private PageInfo<Record> currentRecordPageInfo;
    private List<Integer> selectedLabel;

    @Override
    protected void doBusiness(Realm realm) {
        loginUser = realm.where(UserDTO.class).equalTo("current", true).findFirst();
        loginUser = realm.copyFromRealm(loginUser);
    }

    @Override
    public int bindLayout() {
        return R.layout.fragment_home;
    }

    @Override
    public void initView(View view) {
        titleTextLeft = view.findViewById(R.id.title_text_left);
        titleTextLeft.setOnClickListener(this);
        areaPopWindow = new AreaPopWindow(getContext(), true);
        areaPopWindow.setOnAreaClickedListener(this);
        titleTextRight = view.findViewById(R.id.title_text_right);
        titleTextRight.setText(R.string.filter);
        titleTextRight.setOnClickListener(this);
        optionsPopWindow = new OptionsPopWindow(getContext());

        recordRecyclerView = view.findViewById(R.id.fragment_home_recycler_record);
        LinearLayoutManager linearLayoutManager = new NoAnimatorRecyclerView.BaseAdapter.DefaultLinearLayoutManager(getContext());
        recordRecyclerView.setLayoutManager(linearLayoutManager);
        recordAdapter = new RecordAdapter(loginUser.getToken());
        recordRecyclerView.setAdapter(recordAdapter);
        recordRecyclerView.setOnLoadListener(this);

        recyclerSwipeRefreshView = view.findViewById(R.id.fragment_home_swipe_record);
        recyclerSwipeRefreshView.setOnRefreshListener(this);
        //点击条目打开详情
        recordAdapter.setOnItemClickedListener(record -> RecordDetailActivity.startActivity(getContext(), record.getId()));

        optionsPopWindow.setOnItemClickedListener((optionsPopWindow, option) -> {
            //全选按钮
            if (option.getId() == SELECT_ALL) {
                List<RecordType> recordTypes = getRealm().where(RecordType.class).findAll();
                List<RecordType> temps = new LinkedList<>();
                for (RecordType recordType : recordTypes) {
                    RecordType temp = recordType.isManaged() ? getRealm().copyFromRealm(recordType) : recordType;
                    temp.setSelected(false);
                    temps.add(temp);
                }
                getRealm().executeTransactionAsync(realm -> {
                    realm.copyToRealmOrUpdate(temps);
                }, (Realm.Transaction.OnSuccess) this::refreshData);

                return;
            }
            //确认选择
            if (option.getId() == OK) {
                refreshData();
                return;
            }
            //非全选按钮，耿勋数据库
            List<RecordType> recordTypes = getRealm().where(RecordType.class).findAll();
            for (RecordType e : recordTypes) {
                if (e.getId() == option.getId()) {
                    RecordType temp = e.isManaged() ? getRealm().copyFromRealm(e) : e;
                    getRealm().executeTransactionAsync(realm -> {
                        temp.setSelected(option.isSelected());
                        realm.copyToRealmOrUpdate(temp);
                    });
                    return;
                }
            }
        });
    }

    @Override
    public void initParams(Bundle bundle) {
    }

    @Override
    public void doBusiness() {

    }

    @Override
    public void onPageScrolled(float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected() {

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == titleTextLeft.getId()) {
            areaPopWindow.showPopupWindow(getActivity());
            return;
        }
        if (v.getId() == titleTextRight.getId()) {
            optionsPopWindow.showPopupWindow(getActivity());
            return;
        }
    }

    @Override
    public void accept(Area area) {
        //绑定的对象
        final Area temp = getRealm().copyFromRealm(area);
        getRealm().executeTransactionAsync(realm -> {
            loginUser.setArea(temp);
            realm.copyToRealmOrUpdate(loginUser);
        }, () -> {
            titleTextLeft.setText(area.getName());
            //请求某个地区的资料
            refreshData();
        });

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
        new TipPopWindow(getContext(), true).setMessage(message + data).showPopupWindow(getActivity());
    }

    @Override
    public void onRefresh() {
        refreshData();
    }

    @Override
    public void onLoad() {
        if (currentRecordPageInfo == null) {
            recordRecyclerView.setLoading(false);
            return;
        }
        if (currentRecordPageInfo.isHasNextPage()) {
            //请求某个地区的资料
            getRecordByAreaService.request(loginUser.getToken(), loginUser.getArea().getId(), selectedLabel, currentRecordPageInfo.getNextPage(), PAGE_SIZE);
        } else {
            recordRecyclerView.setLoading(false);
            //提醒到底了
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (recordAdapter.getItemCount() == 0) {
            refreshData();
        }
    }

    private void refreshData() {
        if (loginUser.getArea() == null) {
            areaPopWindow.showPopupWindow(getActivity());
            return;
        }
        titleTextLeft.setText(loginUser.getArea().getName());
        selectedLabel = new LinkedList<>();

        List<RecordType> recordTypes = getRealm().where(RecordType.class).findAll();
        List<OptionsPopWindow.Option> options = new LinkedList<>();

        for (RecordType recordType : recordTypes) {
            if (recordType.isSelected()) {
                selectedLabel.add(recordType.getId());
            }
            options.add(new OptionsPopWindow.Option(recordType.getId(), recordType.getName(), recordType.isSelected(), false));
        }
        //添加全选按钮
        options.add(new OptionsPopWindow.Option(SELECT_ALL, getContext().getString(R.string.all_selected), false, true));
        //添加确认按钮
        options.add(new OptionsPopWindow.Option(OK, getContext().getString(R.string.ok), false, true));
        optionsPopWindow.setOptions(options);
        //访问内容
        if (!getRecordByAreaService.isRequesting()) {
            getRecordByAreaService.request(loginUser.getToken(), loginUser.getArea().getId(), selectedLabel, 1, PAGE_SIZE);
        }
    }
}