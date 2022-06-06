package gaozhi.online.peoplety.ui.activity.personal;


import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.github.pagehelper.PageInfo;

import gaozhi.online.base.net.http.DataHelper;
import gaozhi.online.peoplety.R;
import gaozhi.online.peoplety.entity.Friend;
import gaozhi.online.peoplety.entity.Token;
import gaozhi.online.peoplety.entity.UserInfo;
import gaozhi.online.peoplety.entity.dto.UserDTO;
import gaozhi.online.peoplety.service.friend.GetAttentionService;
import gaozhi.online.peoplety.service.friend.GetFanService;
import gaozhi.online.peoplety.service.friend.GetFriendService;
import gaozhi.online.peoplety.service.user.GetUserInfoService;
import gaozhi.online.peoplety.ui.base.DBBaseActivity;
import gaozhi.online.peoplety.ui.widget.NoAnimatorRecyclerView;
import gaozhi.online.peoplety.util.GlideUtil;
import gaozhi.online.peoplety.util.StringUtil;
import gaozhi.online.peoplety.util.ToastUtil;
import io.realm.Realm;

/**
 * 朋友列表
 */
public class FriendsActivity extends DBBaseActivity implements DataHelper.OnDataListener<PageInfo<Friend>>, SwipeRefreshLayout.OnRefreshListener, NoAnimatorRecyclerView.OnLoadListener {
    private static final String INTENT_ATTENTION = "attention";
    private static final int PAGE_SIZE = 20;

    /**
     * @param context
     * @param attention 是否是关注列表
     */
    private static void startActivity(Context context, boolean attention) {
        Intent intent = new Intent(context, FriendsActivity.class);
        intent.putExtra(INTENT_ATTENTION, attention);
        context.startActivity(intent);
    }

    /**
     * 加载关注列表
     *
     * @param context
     */
    public static void startActivityForAttention(Context context) {
        startActivity(context, true);
    }

    /**
     * 加载粉丝列表
     *
     * @param context
     */
    public static void startActivityForFan(Context context) {
        startActivity(context, false);
    }

    //params
    private boolean isAttention;
    //service
    private final GetAttentionService getAttentionService = new GetAttentionService(this);
    private final GetFanService getFanService = new GetFanService(this);
    private PageInfo<Friend> currentPageInfo;
    //ui
    private SwipeRefreshLayout swipeRefreshLayout;
    private FriendAdapter friendAdapter;
    private TextView textTitle;
    //db
    private UserDTO loginUser;

    @Override
    protected void initParams(Intent intent) {
        isAttention = intent.getBooleanExtra(INTENT_ATTENTION, false);
    }

    @Override
    protected int bindLayout() {
        return R.layout.activity_friends;
    }

    @Override
    protected void initView(View view) {
        swipeRefreshLayout = $(R.id.friends_activity_swipe);
        NoAnimatorRecyclerView recyclerView = $(R.id.friends_activity_recycler_friend);
        recyclerView.setLayoutManager(new NoAnimatorRecyclerView.BaseAdapter.DefaultLinearLayoutManager(this));
        friendAdapter = new FriendAdapter(loginUser.getToken());
        recyclerView.setAdapter(friendAdapter);
        swipeRefreshLayout.setOnRefreshListener(this);
        recyclerView.setOnLoadListener(this);
        textTitle = $(R.id.title_text);
    }

    @Override
    protected void doBusiness(Context mContext) {
        if (isAttention) {
            textTitle.setText(R.string.attention);
        } else {
            textTitle.setText(R.string.fans);
        }
        onRefresh();
    }

    @Override
    protected void doBusiness(Realm realm) {
        loginUser = realm.where(UserDTO.class).equalTo("current", true).findFirst();
        loginUser = realm.copyFromRealm(loginUser);
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onRefresh() {
        request(1);
    }

    /**
     * 请求数据
     *
     * @param pageNum
     */
    private void request(int pageNum) {
        if (isAttention) {
            getAttentionService.request(loginUser.getToken(), pageNum, PAGE_SIZE);
            return;
        }
        getFanService.request(loginUser.getToken(), pageNum, PAGE_SIZE);
    }

    @Override
    public void onLoad() {
        if (currentPageInfo != null && currentPageInfo.isHasNextPage()) {
            request(currentPageInfo.getNextPage());
        }
    }

    @Override
    public void handle(int id, PageInfo<Friend> data, boolean local) {
        currentPageInfo = data;
        if (currentPageInfo.getPageNum() <= 1) {
            friendAdapter.clear();
        }
        friendAdapter.add(data.getList());
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void error(int id, int code, String message, String data) {
        ToastUtil.showToastShort(message + data);
        swipeRefreshLayout.setRefreshing(false);
    }

    private static class FriendAdapter extends NoAnimatorRecyclerView.BaseAdapter<FriendAdapter.FriendViewHolder, Friend> {
        private final Token token;

        public FriendAdapter(Token token) {
            super(Friend.class);
            this.token = token;
        }

        @NonNull
        @Override
        public FriendViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new FriendViewHolder(layoutInflate(parent, R.layout.item_recycler_friend), token);
        }

        private static class FriendViewHolder extends NoAnimatorRecyclerView.BaseViewHolder<Friend> implements DataHelper.OnDataListener<UserDTO> {
            private final Token token;
            //ui
            private final Context context;
            private final ImageView imageHead;
            private final ImageView imageGender;
            private final TextView textName;
            private final TextView textRemark;
            private final TextView textAttention;

            //service
            private final GetUserInfoService getUserInfoService = new GetUserInfoService(this);
            private final GetFriendService getFriendService = new GetFriendService(new DataHelper.OnDataListener<Friend>() {
                @Override
                public void handle(int id, Friend data, boolean local) {
                    if (data == null) {
                        textAttention.setVisibility(View.VISIBLE);
                        return;
                    }
                    textAttention.setVisibility(View.GONE);
                }
            });

            public FriendViewHolder(@NonNull View itemView, Token token) {
                super(itemView);
                context = itemView.getContext();
                this.token = token;
                imageHead = itemView.findViewById(R.id.item_recycler_friend_image_head);
                imageGender = itemView.findViewById(R.id.item_recycler_friend_image_gender);
                textName = itemView.findViewById(R.id.item_recycler_friend_text_name);
                textRemark = itemView.findViewById(R.id.item_recycler_friend_text_remark);
                textAttention = itemView.findViewById(R.id.item_recycler_friend_text_attention);
            }

            @Override
            public void bindView(Friend item) {
                long anotherId = item.getUserid() == token.getUserid() ? item.getFriendId() : item.getUserid();
                if (item.getUserid() == token.getUserid()) {
                    textName.setText(item.getRemark());
                } else {
                    //只有在粉丝页面才获取关注信息,否则就是已关注状态
                    getFriendService.request(token, anotherId);
                }
                //获取用户信息
                getUserInfoService.request(token, anotherId);
            }

            @Override
            public void handle(int id, UserDTO data) {
                GlideUtil.loadRoundRectangleImage(context, data.getUserInfo().getHeadUrl(), imageHead);
                if (StringUtil.isEmpty(textName.getText().toString())) {
                    textName.setText(data.getUserInfo().getNick());
                }
                textRemark.setText(data.getUserInfo().getRemark());
                UserInfo.Gender gender = UserInfo.Gender.getGender(data.getUserInfo().getGender());
                imageGender.setImageResource(gender == UserInfo.Gender.MALE ? R.drawable.male : gender == UserInfo.Gender.FEMALE ? R.drawable.female : R.drawable.other_gender);
            }

            @Override
            public void error(int id, int code, String message, String data) {
                ToastUtil.showToastShort(message + data);
            }
        }
    }
}