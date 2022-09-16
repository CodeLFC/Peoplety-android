package gaozhi.online.peoplety.ui.activity.home.fragment.message;


import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.List;
import java.util.function.Consumer;

import gaozhi.online.peoplety.R;
import gaozhi.online.peoplety.entity.Friend;
import gaozhi.online.peoplety.entity.Message;
import gaozhi.online.peoplety.entity.Record;
import gaozhi.online.peoplety.entity.dto.UserDTO;
import gaozhi.online.peoplety.ui.activity.personal.PersonalActivity;
import gaozhi.online.peoplety.ui.activity.record.RecordDetailActivity;
import gaozhi.online.peoplety.ui.base.DBBaseActivity;
import gaozhi.online.peoplety.ui.widget.NoAnimatorRecyclerView;
import gaozhi.online.peoplety.util.ToastUtil;
import io.realm.Realm;

public class MessageActivity extends DBBaseActivity implements Consumer<Message> {
    private static final String INTENT_TYPES = "types";

    public static void startActivity(Context context, int[] types) {
        Intent intent = new Intent(context, MessageActivity.class);
        intent.putExtra(INTENT_TYPES, types);
        context.startActivity(intent);
    }

    // 消息类型
    private Message.Type[] msgTypes;
    private List<Message> messageList;

    private Integer[] getMsgTypes() {
        Integer[] type = new Integer[msgTypes.length];
        for (int i = 0; i < type.length; i++) {
            type[i] = msgTypes[i].getType();
        }
        return type;
    }

    private final Gson gson = new Gson();

    @Override
    protected void initParams(Intent intent) {
        int[] types = intent.getIntArrayExtra(INTENT_TYPES);
        msgTypes = new Message.Type[types.length];
        for (int i = 0; i < types.length; i++) {
            msgTypes[i] = Message.Type.getType(types[i]);
        }
    }

    //ui
    private TextView titleRight;
    private NoAnimatorRecyclerView recyclerView;
    private MessageAdapter messageAdapter;
    //user
    private UserDTO loginUser;

    @Override
    protected int bindLayout() {
        return R.layout.activity_message;
    }

    @Override
    protected void initView(View view) {
        recyclerView = $(R.id.activity_message_recycler_message);
        recyclerView.setLayoutManager(new NoAnimatorRecyclerView.BaseAdapter.DefaultLinearLayoutManager(this));
        messageAdapter = new MessageAdapter();
        recyclerView.setAdapter(messageAdapter);
        messageAdapter.setOnItemClickedListener(this);
        titleRight = $(R.id.title_text_right);

        titleRight.setOnClickListener(this);

        titleRight.setText(R.string.tip_all_read);

        TextView title = $(R.id.title_text);
        title.setText(R.string.bottom_message);
    }

    @Override
    protected void doBusiness(Context mContext) {
        if (messageList != null) {
            messageAdapter.add(messageList);
        }
    }

    @Override
    protected void doBusiness(Realm realm) {
        loginUser = getLoginUser();
        realm.executeTransaction(realm1 -> {
            List<Message> messages = realm1.where(Message.class).equalTo("toId", loginUser.getUserInfo().getId()).in("type", getMsgTypes()).equalTo("read", false).findAll();
            messageList = realm1.copyFromRealm(messages);
        });
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == titleRight.getId()) {
            getRealm().executeTransaction(realm -> realm.where(Message.class).equalTo("toId", loginUser.getUserInfo().getId()).in("type", getMsgTypes()).findAll().deleteAllFromRealm());
        }
    }

    /**
     * Performs this operation on the given argument.
     *
     * @param message the input argument
     */
    @Override
    public void accept(Message message) {
        Log.i(TAG, "查阅消息：" + message);
        getRealm().executeTransaction(realm -> {
            realm.where(Message.class).equalTo("id", message.getId()).findAll().deleteAllFromRealm();
        });
        handleMsg(message);
    }

    //处理消息
    private void handleMsg(Message message) {
        switch (Message.Type.getType(message.getType())) {
            case UNKNOWN:
                ToastUtil.showToastShort(Message.Type.UNKNOWN.getRemark());
                return;
            case NEW_FANS:
                Friend friend = gson.fromJson(message.getMsg(), Friend.class);
                PersonalActivity.startActivity(this, friend.getUserid());
                return;
            case NEW_EXTEND:
            case NEW_COMMENT:
            case NEW_FAVORITE:
                Record record = gson.fromJson(message.getMsg(), Record.class);
                RecordDetailActivity.startActivity(this, record.getId());
                return;
        }
    }
}