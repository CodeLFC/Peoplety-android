package gaozhi.online.peoplety.ui.activity.chat;


import android.content.Context;
import android.content.Intent;
import android.view.View;

import gaozhi.online.peoplety.PeopletyApplication;
import gaozhi.online.peoplety.R;
import gaozhi.online.peoplety.entity.client.Conversation;
import gaozhi.online.peoplety.entity.dto.UserDTO;
import gaozhi.online.peoplety.ui.base.DBBaseActivity;
import gaozhi.online.peoplety.util.ToastUtil;
import io.realm.Realm;

public class ChatActivity extends DBBaseActivity {
    public static final String INTENT_USER_ID = "user_id";

    public static void startActivity(Context context, long userid) {
        Intent intent = new Intent(context, ChatActivity.class);
        intent.putExtra(INTENT_USER_ID, userid);
        context.startActivity(intent);
    }

    private long friendId;
    private UserDTO loginUser;
    private Conversation conversation;

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
        ToastUtil.showToastShort("朋友ID"+friendId);
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

    }

    /**
     * [业务操作]
     *
     * @param mContext
     */
    @Override
    protected void doBusiness(Context mContext) {

    }

    @Override
    protected void doBusiness(Realm realm) {

    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {

    }
}