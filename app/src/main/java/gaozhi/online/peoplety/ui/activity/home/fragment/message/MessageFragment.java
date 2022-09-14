package gaozhi.online.peoplety.ui.activity.home.fragment.message;

import android.os.Bundle;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;
import java.util.stream.Stream;

import gaozhi.online.base.net.http.DataHelper;
import gaozhi.online.peoplety.R;
import gaozhi.online.peoplety.entity.Message;
import gaozhi.online.peoplety.entity.dto.UserDTO;
import gaozhi.online.peoplety.service.user.GetMessageService;
import gaozhi.online.peoplety.ui.base.DBBaseFragment;
import io.realm.Realm;

/**
 * create an instance of this fragment.
 * 消息页
 */
public class MessageFragment extends DBBaseFragment implements DataHelper.OnDataListener<List<Message>> {
    private View commentView;
    private View friendView;
    private ImageView redDotComment;
    private ImageView redDotFriend;

    //当前登陆用户
    private UserDTO loginUser;
    //service
    private final GetMessageService getMessageService = new GetMessageService(this);

    //
    @Override
    public int bindLayout() {
        return R.layout.fragment_message;
    }

    @Override
    public void initView(View view) {
        TextView title = view.findViewById(R.id.title_text);
        title.setText(R.string.bottom_message);
        commentView = view.findViewById(R.id.fragment_message_view_comment);
        commentView.setOnClickListener(this);
        friendView = view.findViewById(R.id.fragment_message_view_friends);
        friendView.setOnClickListener(this);
        redDotFriend = view.findViewById(R.id.fragment_message_view_friends_new_friend_point);
        redDotComment = view.findViewById(R.id.fragment_message_view_friends_new_comment_point);
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
    protected void doBusiness(Realm realm) {
        loginUser = realm.where(UserDTO.class).equalTo("current", true).findFirst();
        loginUser = realm.copyFromRealm(loginUser);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == friendView.getId()) {
            MessageActivity.startActivity(getContext(), new int[]{Message.Type.NEW_FANS.getType()});
            return;
        }
        if (v.getId() == commentView.getId()) {
            MessageActivity.startActivity(getContext(), new int[]{Message.Type.NEW_COMMENT.getType(), Message.Type.NEW_EXTEND.getType(), Message.Type.NEW_FAVORITE.getType()});
            return;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getMessageService.request(loginUser.getToken());
    }

    @Override
    public void handle(int id, List<Message> data, boolean local) {
        Stream<Message> newFan = Message.filter(data, Message.Type.NEW_FANS, true);
        Stream<Message> newComment = Message.filter(data, new Message.Type[]{Message.Type.NEW_COMMENT, Message.Type.NEW_FAVORITE, Message.Type.NEW_EXTEND}, true);
        redDotFriend.setVisibility(newFan.count() > 0 ? View.VISIBLE : View.INVISIBLE);
        redDotComment.setVisibility(newComment.count() > 0 ? View.VISIBLE : View.INVISIBLE);
    }
}