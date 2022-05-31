package gaozhi.online.peoplety.ui.activity.record;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import gaozhi.online.base.net.http.DataHelper;
import gaozhi.online.peoplety.R;
import gaozhi.online.peoplety.entity.Record;
import gaozhi.online.peoplety.entity.dto.RecordDTO;
import gaozhi.online.peoplety.entity.dto.UserDTO;
import gaozhi.online.peoplety.service.record.GetRecordDTOByIdService;
import gaozhi.online.peoplety.ui.base.DBBaseActivity;
import gaozhi.online.peoplety.util.ToastUtil;
import io.realm.Realm;

public class RecordDetailActivity extends DBBaseActivity implements DataHelper.OnDataListener<RecordDTO>, SwipeRefreshLayout.OnRefreshListener {
    private static final String INTENT_RECORD_ID = "record_id";
    private long recordId;

    public static void startActivity(Context context, long recordId) {
        Intent intent = new Intent(context, RecordDetailActivity.class);
        intent.putExtra(INTENT_RECORD_ID, recordId);
        context.startActivity(intent);
    }

    @Override
    protected void initParams(Intent intent) {
        recordId = intent.getLongExtra(INTENT_RECORD_ID, 0);
    }

    @Override
    protected int bindLayout() {
        return R.layout.activity_record_detail;
    }

    //ui
    private RecordAdapter.RecordViewHolder recordViewHolder;
    private TextView title;
    private SwipeRefreshLayout swipeRefreshLayout;

    //data
    private UserDTO loginUser;
    //db
    private Record record;
    //service
    private final GetRecordDTOByIdService getRecordDTOByIdService = new GetRecordDTOByIdService(this);
    private RecordDTO recordDTO;

    @Override
    protected void initView(View view) {
        recordViewHolder = new RecordAdapter.RecordViewHolder($(R.id.record_detail_activity_view_record_item), loginUser.getToken());
        recordViewHolder.setShowDetails(true);
        title = $(R.id.title_text);

        swipeRefreshLayout = $(R.id.record_detail_activity_swipe_refresh);
        swipeRefreshLayout.setOnRefreshListener(this);
    }

    @Override
    protected void doBusiness(Context mContext) {
        recordViewHolder.bindView(record);
        onRefresh();
    }

    @Override
    protected void doBusiness(Realm realm) {
        loginUser = realm.where(UserDTO.class).equalTo("current", true).findFirst();
        loginUser = realm.copyFromRealm(loginUser);
        record = getRealm().where(Record.class).equalTo("id", recordId).findFirst();
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void start(int id) {

    }

    @Override
    public void handle(int id, RecordDTO data, boolean local) {
        if (!local) {
            swipeRefreshLayout.setRefreshing(false);
        }
        //本地数据非可能返回null
        if (data == null) return;
        if (!local) {
            recordDTO = data;
        } else {
            recordDTO = getRealm().copyFromRealm(data);
        }
        title.setText(recordDTO.getRecord().getTitle());
        recordViewHolder.bindView(recordDTO);
        Log.d(TAG, "" + recordDTO);
    }

    @Override
    public void error(int id, int code, String message, String data) {
        ToastUtil.showToastLong(message + data);
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onRefresh() {
        getRecordDTOByIdService.request(loginUser.getToken(), recordId);
    }
}