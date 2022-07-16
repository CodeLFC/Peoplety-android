package gaozhi.online.peoplety.ui.activity.home.fragment.message;


import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import gaozhi.online.base.net.http.DataHelper;
import gaozhi.online.peoplety.R;
import gaozhi.online.peoplety.entity.Friend;
import gaozhi.online.peoplety.entity.Message;
import gaozhi.online.peoplety.entity.Record;
import gaozhi.online.peoplety.entity.dto.UserDTO;
import gaozhi.online.peoplety.service.user.GetMessageService;
import gaozhi.online.peoplety.ui.activity.personal.PersonalActivity;
import gaozhi.online.peoplety.ui.activity.record.RecordDetailActivity;
import gaozhi.online.peoplety.ui.base.DBBaseActivity;
import gaozhi.online.peoplety.ui.widget.NoAnimatorRecyclerView;
import gaozhi.online.peoplety.util.ToastUtil;
import io.realm.Realm;

public class MessageActivity extends DBBaseActivity implements DataHelper.OnDataListener<List<Message>>, Consumer<Message> {
    private static final String INTENT_TYPES = "types";

    public static void startActivity(Context context, int[] types) {
        Intent intent = new Intent(context, MessageActivity.class);
        intent.putExtra(INTENT_TYPES, types);
        context.startActivity(intent);
    }

    // 消息类型
    private Message.Type[] msgTypes;
    //当前登陆用户
    private UserDTO loginUser;
    private final Gson gson = new Gson();
    //service
    private final GetMessageService getMessageService = new GetMessageService(this);

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

        TextView title =$(R.id.title_text);
        title.setText(R.string.bottom_message);
    }

    @Override
    protected void doBusiness(Context mContext) {

    }
    private void refreshData(){
        messageAdapter.clear();
        getMessageService.request(loginUser.getToken());
    }
    @Override
    protected void doBusiness(Realm realm) {
        loginUser = realm.where(UserDTO.class).equalTo("current", true).findFirst();
        loginUser = realm.copyFromRealm(loginUser);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == titleRight.getId()){
            getRealm().executeTransactionAsync(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    realm.where(Message.class).findAll().setBoolean("read", true);
                }
            }, () -> refreshData());
            return;
        }
    }

    @Override
    public void handle(int id, List<Message> data, boolean local) {
        if (!local) return;
        Stream<Message> messageStream = Message.filter(data, msgTypes, false);
        messageAdapter.add(messageStream.collect(Collectors.toList()));
    }

    /**
     * Performs this operation on the given argument.
     *
     * @param message the input argument
     */
    @Override
    public void accept(Message message) {
        Log.i(TAG, "查阅消息：" + message);
        Message msg = getRealm().copyFromRealm(message);
        getRealm().executeTransactionAsync(realm -> {
            msg.setRead(true);
            realm.copyToRealmOrUpdate(msg);
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                handleMsg(message);
            }
        });
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
                Record record = gson.fromJson(message.getMsg(), Record.class);
                RecordDetailActivity.startActivity(this, record.getId());
                return;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshData();
    }
}