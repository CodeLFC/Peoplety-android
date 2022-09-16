package gaozhi.online.peoplety.ui.activity.record;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.github.pagehelper.PageInfo;

import java.util.function.Consumer;

import gaozhi.online.base.net.http.DataHelper;
import gaozhi.online.peoplety.R;
import gaozhi.online.peoplety.entity.Comment;
import gaozhi.online.peoplety.entity.Record;
import gaozhi.online.peoplety.entity.dto.UserDTO;
import gaozhi.online.peoplety.service.record.GetCommentByRecordIdService;
import gaozhi.online.peoplety.ui.base.DBBasePopWindow;
import gaozhi.online.peoplety.ui.widget.NoAnimatorRecyclerView;
import gaozhi.online.peoplety.util.ToastUtil;

/**
 * 评论
 */
public class CommentPopWindow extends DBBasePopWindow implements View.OnClickListener, DataHelper.OnDataListener<PageInfo<Comment>>, Consumer<Comment>, NoAnimatorRecyclerView.OnLoadListener, SwipeRefreshLayout.OnRefreshListener {
    private SwipeRefreshLayout swipeRefreshLayout;
    private NoAnimatorRecyclerView recyclerView;
    private CommentAdapter commentAdapter;
    private Button btnComment;
    private RecordCommentPopWindow editTextPopWindow;
    //data
    private UserDTO loginUser;
    private Record record;

    //service
    private final GetCommentByRecordIdService getCommentByRecordIdService = new GetCommentByRecordIdService(this);
    private PageInfo<Comment> currentPageInfo = new PageInfo<>();

    public CommentPopWindow(Context context) {
        super(context, R.layout.pop_window_comment, true);
    }

    @Override
    protected void initView(View rootView) {
        loginUser = getRealm().where(UserDTO.class).equalTo("current", true).findFirst();
        loginUser = getRealm().copyFromRealm(loginUser);
        recyclerView = rootView.findViewById(R.id.pop_window_comment_recycler);
        recyclerView.setLayoutManager(new NoAnimatorRecyclerView.BaseAdapter.DefaultLinearLayoutManager(rootView.getContext()));
        commentAdapter = new CommentAdapter(loginUser.getToken());
        recyclerView.setAdapter(commentAdapter);
        btnComment = rootView.findViewById(R.id.pop_window_comment_btn_comment);
        btnComment.setOnClickListener(this);
        editTextPopWindow = new RecordCommentPopWindow(rootView.getContext());
        editTextPopWindow.setCommentConsumer(this);
        recyclerView.setOnLoadListener(this);

        swipeRefreshLayout = rootView.findViewById(R.id.pop_window_comment_swipe);
        swipeRefreshLayout.setOnRefreshListener(this);
    }

    public void showPopupWindow(View parent, Record record) {
        this.record = record;
        showPopupWindow(parent);
        onRefresh();
    }

    @Override
    protected void doBusiness(Context context) {

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == btnComment.getId()) {
            editTextPopWindow.showPopupWindow(v, loginUser, record);
        }
    }

    @Override
    public void handle(int id, PageInfo<Comment> data, boolean local) {
        if (data == null) return;
        currentPageInfo = data;

        if (currentPageInfo.getPageNum() <= 1) {
            commentAdapter.clear();
        }
        Log.i(getClass().getName(), "pageNum:" + currentPageInfo.getPageNum());
        commentAdapter.add(data.getList());
        if (local) return;
        recyclerView.setLoading(false);
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void error(int id, int code, String message, String data) {
        ToastUtil.showToastShort(message + data);
        recyclerView.setLoading(false);
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void accept(Comment comment) {
        getCommentByRecordIdService.request(loginUser.getToken(), record.getId(), 1);
    }

    @Override
    public void onLoad() {
        if (currentPageInfo.isHasNextPage()) {
            getCommentByRecordIdService.request(loginUser.getToken(), record.getId(), currentPageInfo.getNextPage());
        }
    }

    @Override
    public void onRefresh() {
        getCommentByRecordIdService.request(loginUser.getToken(), record.getId(), 1);
    }
}
