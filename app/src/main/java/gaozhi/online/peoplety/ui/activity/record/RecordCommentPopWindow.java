package gaozhi.online.peoplety.ui.activity.record;

import android.content.Context;
import android.view.View;

import com.google.gson.Gson;

import java.util.function.Consumer;

import gaozhi.online.base.net.http.DataHelper;
import gaozhi.online.peoplety.R;
import gaozhi.online.peoplety.entity.Comment;
import gaozhi.online.peoplety.entity.Message;
import gaozhi.online.peoplety.entity.Record;
import gaozhi.online.peoplety.entity.Token;
import gaozhi.online.peoplety.entity.dto.UserDTO;
import gaozhi.online.peoplety.im.io.IMSender;
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
    private UserDTO loginUser;
    private Record record;
    private Consumer<Comment> commentConsumer;

    public RecordCommentPopWindow(Context context) {
        super(context, true);
        getBtnSend().setOnClickListener(this);
    }

    public void showPopupWindow(View parent, UserDTO loginUser, Record record) {
        super.showPopupWindow(parent);
        this.loginUser = loginUser;
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
        //发送收藏消息
        Message message = new Message();
        message.setType(Message.Type.NEW_COMMENT.getType());
        message.setToId(record.getUserid());
        message.setMsg(new Gson().toJson(record));
        message.setRemark(getContext().getString(R.string.comment) + record.getId() + getContext().getString(R.string.floor) + getContext().getString(R.string.record) +":"+ record.getTitle()+data.getContent());
        new IMSender(message).send();
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
        publishCommentService.request(loginUser.getToken(), comment);
    }
}
