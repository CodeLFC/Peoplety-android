package gaozhi.online.peoplety.ui.activity.home.fragment.message;

import android.os.Bundle;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.List;
import java.util.stream.Stream;

import gaozhi.online.base.net.http.DataHelper;
import gaozhi.online.peoplety.PeopletyApplication;
import gaozhi.online.peoplety.R;
import gaozhi.online.peoplety.entity.Message;
import gaozhi.online.peoplety.entity.client.Conversation;
import gaozhi.online.peoplety.entity.dto.UserDTO;
import gaozhi.online.peoplety.im.IMClient;
import gaozhi.online.peoplety.im.io.IMReceiver;
import gaozhi.online.peoplety.service.user.GetMessageService;
import gaozhi.online.peoplety.ui.activity.chat.conversation.ConversationCell;
import gaozhi.online.peoplety.ui.activity.chat.conversation.ConversationRecyclerAdapter;
import gaozhi.online.peoplety.ui.base.DBBaseFragment;
import gaozhi.online.peoplety.ui.widget.NoAnimatorRecyclerView;
import gaozhi.online.peoplety.util.ToastUtil;
import io.realm.Realm;
import io.realm.Sort;

/**
 * create an instance of this fragment.
 * 消息页
 */
public class MessageFragment extends DBBaseFragment implements IMReceiver {
    //    private View commentView;
//    private View friendView;
//    private ImageView redDotComment;
//    private ImageView redDotFriend;
    //会话列表
    private ConversationRecyclerAdapter conversationRecyclerAdapter;
    private NoAnimatorRecyclerView noAnimatorRecyclerView;
    //当前登陆用户
    private UserDTO loginUser;
    //会话
    private List<Conversation> conversations;

    @Override
    public int bindLayout() {
        return R.layout.fragment_message;
    }

    @Override
    public void initView(View view) {
        TextView title = view.findViewById(R.id.title_text);
        title.setText(R.string.bottom_message);
//        commentView = view.findViewById(R.id.fragment_message_view_comment);
//        commentView.setOnClickListener(this);
//        friendView = view.findViewById(R.id.fragment_message_view_friends);
//        friendView.setOnClickListener(this);
//        redDotFriend = view.findViewById(R.id.fragment_message_view_friends_new_friend_point);
//        redDotComment = view.findViewById(R.id.fragment_message_view_friends_new_comment_point);
        noAnimatorRecyclerView = view.findViewById(R.id.fragment_message_view_conversation_recycler);
        noAnimatorRecyclerView.setLayoutManager(new NoAnimatorRecyclerView.BaseAdapter.DefaultLinearLayoutManager(getContext()));
        conversationRecyclerAdapter = new ConversationRecyclerAdapter(getContext());
        noAnimatorRecyclerView.setAdapter(conversationRecyclerAdapter);
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
        loginUser = getLoginUser();
    }

    @Override
    public void onClick(View v) {
//        if (v.getId() == friendView.getId()) {
//            MessageActivity.startActivity(getContext(), new int[]{Message.Type.NEW_FANS.getType()});
//            return;
//        }
//        if (v.getId() == commentView.getId()) {
//            MessageActivity.startActivity(getContext(), new int[]{Message.Type.NEW_COMMENT.getType(), Message.Type.NEW_EXTEND.getType(), Message.Type.NEW_FAVORITE.getType()});
//            return;
//        }
    }

    @Override
    public void onResume() {
        super.onResume();
        //  List<Message> unread = getRealm().where(Message.class).equalTo("toId", loginUser.getUserInfo().getId()).equalTo("read", false).sort("time", Sort.DESCENDING).findAll();
        //     handle(unread);
        refreshConversation();
    }

    /**
     * Called when the Fragment is visible to the user.  This is generally
     * tied to {link Activity#onStart() Activity.onStart} of the containing
     * Activity's lifecycle.
     */
    @Override
    public void onStart() {
        super.onStart();
        //绑定
        IMClient.getInstance(getContext()).addIMReceiver(this);
    }

    /**
     * Called when the fragment is no longer in use.  This is called
     * after {@link #onStop()} and before {@link #onDetach()}.
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        //删除
        IMClient.getInstance(getContext()).removeIMReceiver(this);
    }

    public void handle(List<Message> data) {
//        Stream<Message> newFan = Message.filter(data, Message.Type.NEW_FANS, true);
//        Stream<Message> newComment = Message.filter(data, new Message.Type[]{Message.Type.NEW_COMMENT, Message.Type.NEW_FAVORITE, Message.Type.NEW_EXTEND}, true);
//        redDotFriend.setVisibility(newFan.count() > 0 ? View.VISIBLE : View.INVISIBLE);
//        redDotComment.setVisibility(newComment.count() > 0 ? View.VISIBLE : View.INVISIBLE);
    }

    /**
     * 加载所有会话
     */
    private void refreshConversation() {
        conversations = getRealm().where(Conversation.class)
                .equalTo("self", loginUser.getUserInfo().getId())
                .findAll();
        conversations = getRealm().copyFromRealm(conversations);
        for (Conversation conversation : conversations) {
            conversationRecyclerAdapter.add(new ConversationCell(conversation));
        }
    }

    /**
     * 优先级 ，处理的顺序
     *
     * @return
     */
    @Override
    public int order() {
        return 1;
    }

    /**
     * 收到消息
     *
     * @param message
     */
    @Override
    public boolean onReceive(Message message) {
        Realm realm = getRealm();
        Conversation conversation = realm.where(Conversation.class)
                .equalTo("self", message.getToId())
                .equalTo("friend", message.getFromId())
                .findFirst();
        if (conversation == null) return false;
        conversation = realm.copyFromRealm(conversation);
        conversationRecyclerAdapter.add(new ConversationCell(conversation));
        return false;
    }
}