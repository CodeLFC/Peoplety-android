package gaozhi.online.peoplety.ui.activity.record;

import android.content.Context;
import android.view.View;

import java.util.function.Consumer;

import gaozhi.online.base.net.http.DataHelper;
import gaozhi.online.peoplety.R;
import gaozhi.online.peoplety.entity.Comment;
import gaozhi.online.peoplety.entity.Record;
import gaozhi.online.peoplety.entity.Token;
import gaozhi.online.peoplety.service.record.PublishCommentService;
import gaozhi.online.peoplety.ui.util.pop.EditTextPopWindow;
import gaozhi.online.peoplety.util.PatternUtil;
import gaozhi.online.peoplety.util.StringUtil;
import gaozhi.online.peoplety.util.ToastUtil;

/**
 * 评论输入
 */
public class RecordCommentPopWindow extends EditTextPopWindow implements DataHelper.OnDataListener<Comment>, View.OnClickListener {

    private final PublishCommentService publishCommentService = new PublishCommentService(this);
    private Token token;
    private Record record;
    private Consumer<Comment> commentConsumer;

    public RecordCommentPopWindow(Context context) {
        super(context, true);
        getBtnSend().setOnClickListener(this);
    }

    public void showPopupWindow(View parent, Token token, Record record) {
        super.showPopupWindow(parent);
        this.token = token;
        this.record = record;
    }

    @Override
    public void start(int id) {

    }

    public void setCommentConsumer(Consumer<Comment> commentConsumer) {
        this.commentConsumer = commentConsumer;
    }

    @Override
    public void handle(int id, Comment data) {
        ToastUtil.showToastShort(R.string.tip_publish_success);
        dismiss();
        getEditContent().setText("");
        getEditUrl().setText("");
        if (commentConsumer != null) {
            commentConsumer.accept(data);
        }
    }

    @Override
    public void error(int id, int code, String message, String data) {
        ToastUtil.showToastLong(message + data);
    }

    @Override
    public void onClick(View v) {
        String content = getEditContent().getText().toString();
        if (StringUtil.isEmpty(content)) {
            ToastUtil.showToastShort(R.string.tip_content_is_empty);
            return;
        }
        String url = getEditUrl().getText().toString();
        if (!StringUtil.isEmpty(url) && !PatternUtil.matchUrl(url)) {
            ToastUtil.showToastShort(R.string.tip_please_enter_right_url);
            return;
        }
        Comment comment = new Comment();
        comment.setRecordId(record.getId());
        comment.setContent(content);
        comment.setUrl(url);
        publishCommentService.request(token, comment);
    }
}
