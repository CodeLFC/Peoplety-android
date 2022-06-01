package gaozhi.online.peoplety.ui.activity.record;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.github.pagehelper.PageInfo;

import java.util.function.Consumer;

import gaozhi.online.base.net.http.DataHelper;
import gaozhi.online.peoplety.R;
import gaozhi.online.peoplety.entity.Record;
import gaozhi.online.peoplety.entity.dto.RecordDTO;
import gaozhi.online.peoplety.entity.dto.UserDTO;
import gaozhi.online.peoplety.service.record.GetChildByRecordIdService;
import gaozhi.online.peoplety.service.record.GetRecordDTOByIdService;
import gaozhi.online.peoplety.ui.activity.PublishRecordActivity;
import gaozhi.online.peoplety.ui.base.DBBasePopWindow;
import gaozhi.online.peoplety.ui.widget.NoAnimatorRecyclerView;
import gaozhi.online.peoplety.util.ToastUtil;

/**
 * 查看相关卷宗
 */
public class ChildRecordPopWindow extends DBBasePopWindow implements View.OnClickListener, DataHelper.OnDataListener<PageInfo<Record>>, SwipeRefreshLayout.OnRefreshListener, Consumer<Record> {
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecordAdapter childRecordAdapter;
    private Button btnPublish;
    //data
    private UserDTO loginUser;
    private Record record;
    //service
    private final GetRecordDTOByIdService getRecordDTOByIdService = new GetRecordDTOByIdService(new DataHelper.OnDataListener<RecordDTO>() {
        @Override
        public void handle(int id, RecordDTO data, boolean local) {
            if (data == null) return;
            childRecordAdapter.add(data.getRecord());
        }
    });
    private final GetChildByRecordIdService getChildByRecordIdService = new GetChildByRecordIdService(this);

    public ChildRecordPopWindow(Context context) {
        super(context, R.layout.pop_window_child_record, true);
    }

    @Override
    protected void initView(View rootView) {
        loginUser = getRealm().where(UserDTO.class).equalTo("current", true).findFirst();
        loginUser = getRealm().copyFromRealm(loginUser);

        swipeRefreshLayout = rootView.findViewById(R.id.pop_window_child_record_swipe);
        swipeRefreshLayout.setOnRefreshListener(this);
        NoAnimatorRecyclerView recyclerView = rootView.findViewById(R.id.pop_window_child_record_recycler);
        recyclerView.setLayoutManager(new NoAnimatorRecyclerView.BaseAdapter.DefaultLinearLayoutManager(rootView.getContext()));
        childRecordAdapter = new RecordAdapter(loginUser.getToken());
        childRecordAdapter.setOnItemClickedListener(this);
        recyclerView.setAdapter(childRecordAdapter);
        btnPublish = rootView.findViewById(R.id.pop_window_child_record_btn_publish);
        btnPublish.setOnClickListener(this);
    }

    @Override
    protected void doBusiness(Context context) {

    }

    public void showPopupWindow(ImageView parent, Record record) {
        this.record = record;
        showPopupWindow(parent);
        if (record.getParentId() != 0) {
            getRecordDTOByIdService.request(loginUser.getToken(), record.getParentId());
        }
        onRefresh();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == btnPublish.getId()) {
            PublishRecordActivity.startActivity(v.getContext(), record);
            dismiss();
            return;
        }
    }

    @Override
    public void onRefresh() {
        getChildByRecordIdService.request(loginUser.getToken(), record.getId(), 1);
    }

    @Override
    public void handle(int id, PageInfo<Record> data, boolean local) {
        if (!local) {
            swipeRefreshLayout.setRefreshing(false);
        }
        if (data == null) return;
        childRecordAdapter.add(data.getList());
    }

    @Override
    public void error(int id, int code, String message, String data) {
        swipeRefreshLayout.setRefreshing(false);
        ToastUtil.showToastShort(message + data);
    }

    @Override
    public void accept(Record record) {
        RecordDetailActivity.startActivity(getContentView().getContext(), record.getId());
    }
}
