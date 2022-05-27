package gaozhi.online.peoplety.ui.activity.record;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import gaozhi.online.base.net.http.DataHelper;
import gaozhi.online.peoplety.R;
import gaozhi.online.peoplety.entity.Record;
import gaozhi.online.peoplety.entity.dto.RecordDTO;
import gaozhi.online.peoplety.entity.dto.UserDTO;
import gaozhi.online.peoplety.service.record.GetRecordDTOByIdService;
import gaozhi.online.peoplety.ui.base.DBBaseActivity;
import gaozhi.online.peoplety.ui.widget.NoAnimatorRecyclerView;
import gaozhi.online.peoplety.util.ToastUtil;
import io.realm.Realm;

public class RecordDetailActivity extends DBBaseActivity implements DataHelper.OnDataListener<RecordDTO> {
    private static final String INTENT_RECORD_ID = "record_id";
    private long recordId;

    public static void startActivity(Context context, long recordId) {
        Intent intent = new Intent(context, RecordDetailActivity.class);
        intent.putExtra(INTENT_RECORD_ID, recordId);
        Log.i(RecordDetailActivity.class.getName(), recordId + "卷宗编号");
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
    //子-横向排列
    private NoAnimatorRecyclerView recyclerViewRecordParentChild;
    private ChildRecordAdapter childRecordAdapter;
    //评论 - 纵向排列
    private NoAnimatorRecyclerView recyclerViewComment;
    private CommentAdapter commentAdapter;
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
        recyclerViewRecordParentChild = $(R.id.record_detail_activity_recycler_parent_child);

        recyclerViewRecordParentChild.setLayoutManager(new LinearLayoutManager(this));

        recyclerViewComment = $(R.id.record_detail_activity_recycler_comment);
        recyclerViewComment.setLayoutManager(new LinearLayoutManager(this));

    }

    @Override
    protected void doBusiness(Context mContext) {
        childRecordAdapter = new ChildRecordAdapter();
        recyclerViewRecordParentChild.setAdapter(childRecordAdapter);

        commentAdapter = new CommentAdapter(loginUser.getToken());
        recyclerViewComment.setAdapter(commentAdapter);

        recordViewHolder.bindView(record);
        getRecordDTOByIdService.request(loginUser.getToken(), recordId);
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
        //本地数据非第一页会返回null
        if (data == null) return;
        title.setText(data.getRecord().getTitle());
        recordViewHolder.bindView(data);
        recordDTO = data;
        //添加卷宗
        if (data.getChildPageInfo().getPageNum() <= 1) {
            childRecordAdapter.clear();
            if (data.getParent() != null) {//添加父卷宗
                childRecordAdapter.add(data.getParent());
            }
        }
        childRecordAdapter.add(recordDTO.getChildPageInfo().getList());

        //添加评论
        if (data.getCommentPageInfo().getPageNum() <= 1) {
            commentAdapter.clear();
        }
        commentAdapter.add(recordDTO.getCommentPageInfo().getList());
    }

    @Override
    public void error(int id, int code, String message, String data) {
        ToastUtil.showToastLong(message + data);
    }
}