package gaozhi.online.peoplety.ui.activity.chat;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import gaozhi.online.base.net.http.DataHelper;
import gaozhi.online.base.ui.ActivityManager;
import gaozhi.online.peoplety.PeopletyApplication;
import gaozhi.online.peoplety.R;
import gaozhi.online.peoplety.entity.Friend;
import gaozhi.online.peoplety.entity.Message;
import gaozhi.online.peoplety.entity.client.Conversation;
import gaozhi.online.peoplety.entity.dto.UserDTO;
import gaozhi.online.peoplety.im.IMClient;
import gaozhi.online.peoplety.im.io.IMReceiver;
import gaozhi.online.peoplety.im.io.IMSender;
import gaozhi.online.peoplety.service.friend.GetFriendService;
import gaozhi.online.peoplety.service.user.GetUserInfoService;
import gaozhi.online.peoplety.ui.activity.chat.chat.ChatCell;
import gaozhi.online.peoplety.ui.activity.chat.chat.ChatMsgRecyclerAdapter;
import gaozhi.online.peoplety.ui.activity.personal.PersonalActivity;
import gaozhi.online.peoplety.ui.base.DBBaseActivity;
import gaozhi.online.peoplety.ui.widget.NoAnimatorRecyclerView;
import gaozhi.online.peoplety.util.StringUtil;
import gaozhi.online.peoplety.util.ToastUtil;
import io.realm.Realm;
import io.realm.Sort;

public class ChatActivity extends DBBaseActivity implements IMReceiver {
    public static final String INTENT_USER_ID = "user_id";

    public static void startActivity(Context context, long userid) {
        Intent intent = new Intent(context, ChatActivity.class);
        intent.putExtra(INTENT_USER_ID, userid);
        context.startActivity(intent);
    }

    private long friendId;
    private Friend friend;
    private UserDTO friendUser;
    private UserDTO loginUser;
    private Conversation conversation;
    //id
    //ui
    private TextView textName;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ChatMsgRecyclerAdapter chatMsgRecyclerAdapter;
    private NoAnimatorRecyclerView noAnimatorRecyclerView;
    private EditText editContent;
    private Button btnSend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //关闭其他的聊天界面
        ChatActivity chatActivity = ActivityManager.getInstance().get(ChatActivity.class);
        if (chatActivity != null) {
            chatActivity.finish();
        }
        //增加监听器
        IMClient.getInstance(this).addIMReceiver(this);
    }

    @Override
    protected void onDestroy() {
        //移除监听器
        IMClient.getInstance(this).removeIMReceiver(this);
        super.onDestroy();
    }

    /**
     * 初始化参数
     *
     * @param intent
     */
    @Override
    protected void initParams(Intent intent) {
        friendId = intent.getLongExtra(INTENT_USER_ID, 0);
        loginUser = getLoginUser();
        Realm realm = getRealm();
        //更新unread
        conversation = realm.where(Conversation.class)
                .equalTo("self", loginUser.getUserInfo().getId())
                .equalTo("friend", friendId)
                .findFirst();
        if (conversation == null) {
            return;
        }
        realm.executeTransaction(realm1 -> {
            conversation.setUnread(0);
            realm1.copyToRealmOrUpdate(conversation);
        });
    }

    /**
     * 刷新一些基础信息
     */
    private void refreshInfo() {
        GetFriendService getFriendService = new GetFriendService(new DataHelper.OnDataListener<>() {
            /**
             * 是否是本地数据
             *
             * @param id
             * @param data
             * @param local
             */
            @Override
            public void handle(int id, Friend data, boolean local) {
                friend = data;
                if (friend != null && !StringUtil.isEmpty(friend.getRemark())) {
                    textName.setText(friend.getRemark());
                }
            }
        });
        new GetUserInfoService(new DataHelper.OnDataListener<>() {
            /**
             * 是否是本地数据
             *
             * @param id
             * @param data
             * @param local
             */
            @Override
            public void handle(int id, UserDTO data, boolean local) {
                if (data == null) {
                    return;
                }
                friendUser = data;
                textName.setText(friendUser.getUserInfo().getNick());
                if (!local) {
                    getFriendService.request(loginUser.getToken(), friendId);
                }
            }
        }).request(loginUser.getToken(), friendId);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Handle onNewIntent() to inform the fragment manager that the
     * state is not saved.  If you are handling new intents and may be
     * making changes to the fragment state, you want to be sure to call
     * through to the super-class here first.  Otherwise, if your state
     * is saved but the activity is not stopped, you could get an
     * onNewIntent() call which happens before onResume() and trying to
     * perform fragment operations at that point will throw IllegalStateException
     * because the fragment manager thinks the state is still saved.
     *
     * @param intent
     */
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        initParams(intent);
    }

    /**
     * 绑定布局资源
     *
     * @return layout 资源ID
     */
    @Override
    protected int bindLayout() {
        return R.layout.activity_chat;
    }

    /**
     * 初始化视图
     *
     * @param view
     */
    @Override
    protected void initView(View view) {
        textName = $(R.id.title_text);
        textName.setOnClickListener(v -> PersonalActivity.startActivity(ChatActivity.this, friendId, false));
        swipeRefreshLayout = $(R.id.chat_activity_refresh);
        editContent = $(R.id.chat_activity_edit_input);
        btnSend = $(R.id.chat_activity_btn_send);
        btnSend.setOnClickListener(this);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            loadMessage(getRealm());
            swipeRefreshLayout.setRefreshing(false);
        });
        //初始化加载
        noAnimatorRecyclerView = $(R.id.chat_activity_recyclerview);
        noAnimatorRecyclerView.setLayoutManager(new NoAnimatorRecyclerView.BaseAdapter.DefaultLinearLayoutManager(ChatActivity.this));
        chatMsgRecyclerAdapter = new ChatMsgRecyclerAdapter(this, friendId);
        noAnimatorRecyclerView.setAdapter(chatMsgRecyclerAdapter);
        refreshInfo();
    }

    /**
     * [业务操作]
     *
     * @param mContext
     */
    @Override
    protected void doBusiness(Context mContext) {
        loadMessage(getRealm());
        if (chatMsgRecyclerAdapter.getItemCount() > 0)
            noAnimatorRecyclerView.scrollToPosition(chatMsgRecyclerAdapter.getItemCount() - 1);
    }

    @Override
    protected void doBusiness(Realm realm) {

    }

    private void loadMessage(Realm realm) {
        long leastId = chatMsgRecyclerAdapter.getItemCount() == 0 ? Long.MAX_VALUE : chatMsgRecyclerAdapter.getItemId(0);
        List<Message> messages = realm.where(Message.class)
                .lessThan("id", leastId)
                .beginGroup()
                .beginGroup()
                .equalTo("fromId", loginUser.getUserInfo().getId())
                .equalTo("toId", friendId)
                .endGroup()
                .or()
                .beginGroup()
                .equalTo("toId", loginUser.getUserInfo().getId())
                .equalTo("fromId", friendId)
                .endGroup()
                .endGroup()
                .sort("id", Sort.DESCENDING)
                .limit(10)
                .findAll();
        messages = realm.copyFromRealm(messages);
        for (Message message : messages) {
            Log.i(TAG, "添加:" + message.getId() + " : " + message.getMsg());
            chatMsgRecyclerAdapter.add(new ChatCell(this, message));
        }
        noAnimatorRecyclerView.scrollToPosition(messages.size()/2);
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        String content = editContent.getText().toString();
        if (StringUtil.isEmpty(content)) {
            ToastUtil.showToastShort(R.string.tip_content_is_empty);
            return;
        }
        Message message = new Message();
        message.setFromId(loginUser.getUserInfo().getId());
        message.setToId(friendId);
        message.setType(Message.Type.NEW_FRIEND_MESSAGE.getType());
        message.setTypeMsg(Message.TypeMsg.STRING.getType());
        message.setRead(true);
        message.setMsg(content);
        IMSender sender = new IMSender(message);
        sender.setOnIMSendListener(new IMSender.OnIMSendListener() {
            @Override
            public void onSuccess(Message message) {
                chatMsgRecyclerAdapter.add(new ChatCell(ChatActivity.this, message));
                noAnimatorRecyclerView.scrollToPosition(chatMsgRecyclerAdapter.getItemCount() - 1);
                getApplication(PeopletyApplication.class).onReceiveMessage(getRealm(), message, false);
                editContent.setText("");
            }

            @Override
            public void onFail(Message message) {
                ToastUtil.showToastShort(R.string.send_fail);
            }
        });
        sender.send();
    }

    /**
     * 收到消息
     *
     * @param message
     */
    @Override
    public boolean onReceive(Message message) {
        //是来自这个朋友的消息
        if (message.getFromId() == friendId) {
            getApplication(PeopletyApplication.class).onReceiveMessage(getRealm(), message, false);
            chatMsgRecyclerAdapter.add(new ChatCell(this, message));
            noAnimatorRecyclerView.scrollToPosition(chatMsgRecyclerAdapter.getItemCount()-1);
            return true;
        }
        return false;
    }

    /**
     * 发送失败，ID经过再次转发后会失效，但可以留在本地，作为发送失败的聊天记录
     *
     * @param message
     */
    @Override
    public boolean onFail(List<Message> message) {
        for (Message msg : message) {
            if (msg.getFromId() == friendId) {
                editContent.setText(msg.getMsg());
                ToastUtil.showToastShort(R.string.send_fail);
                break;
            }
        }
        return false;
    }

    /**
     * 优先级 ，处理的顺序
     *
     * @return
     */
    @Override
    public int order() {
        return 2;
    }
}