package gaozhi.online.peoplety.ui.activity.chat;


import android.content.Context;
import android.content.Intent;
import android.view.View;

import gaozhi.online.peoplety.R;
import gaozhi.online.peoplety.entity.dto.UserDTO;
import gaozhi.online.peoplety.ui.base.DBBaseActivity;
import io.realm.Realm;

public class ChatActivity extends DBBaseActivity {
    private static final String INTENT_USER_ID = "user_id";

    public static void startActivity(Context context, long userid) {
        Intent intent = new Intent(context, ChatActivity.class);
        intent.putExtra(INTENT_USER_ID, userid);
        context.startActivity(intent);
    }
    private long friendId;
    private UserDTO loginUser;
    /**
     * 初始化参数
     *
     * @param intent
     */
    @Override
    protected void initParams(Intent intent) {
        friendId = intent.getLongExtra(INTENT_USER_ID,0);
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
           loginUser = getLoginUser();
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