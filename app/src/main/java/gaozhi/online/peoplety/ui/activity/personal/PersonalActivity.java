package gaozhi.online.peoplety.ui.activity.personal;


import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import gaozhi.online.base.net.http.DataHelper;
import gaozhi.online.peoplety.R;
import gaozhi.online.peoplety.entity.Friend;
import gaozhi.online.peoplety.entity.IPInfo;
import gaozhi.online.peoplety.entity.Status;
import gaozhi.online.peoplety.entity.UserInfo;
import gaozhi.online.peoplety.entity.UserRecordCount;
import gaozhi.online.peoplety.entity.dto.UserDTO;
import gaozhi.online.peoplety.service.constant.GetIPInfoService;
import gaozhi.online.peoplety.service.friend.AddAttentionService;
import gaozhi.online.peoplety.service.friend.DeleteFriendService;
import gaozhi.online.peoplety.service.friend.GetFriendService;
import gaozhi.online.peoplety.service.record.GetRecordCountByUseridService;
import gaozhi.online.peoplety.service.user.GetUserInfoService;
import gaozhi.online.peoplety.ui.activity.userinfo.QRCodeActivity;
import gaozhi.online.peoplety.ui.activity.userinfo.EditUserInfoActivity;
import gaozhi.online.peoplety.ui.base.DBBaseActivity;
import gaozhi.online.peoplety.ui.util.image.ShowImageActivity;
import gaozhi.online.peoplety.util.DateTimeUtil;
import gaozhi.online.peoplety.util.GlideUtil;
import gaozhi.online.peoplety.util.StringUtil;
import gaozhi.online.peoplety.util.ToastUtil;
import io.realm.Realm;

/**
 * 个人主页
 */
public class PersonalActivity extends DBBaseActivity implements DataHelper.OnDataListener<Friend> {
    private static final String INTENT_USER_ID = "user_id";

    public static void startActivity(Context context, long userid) {
        Intent intent = new Intent(context, PersonalActivity.class);
        intent.putExtra(INTENT_USER_ID, userid);
        context.startActivity(intent);
    }

    //intent
    private long userid;
    //ui
    private TextView textAttentionNum;
    private View viewAttentionNum;
    private TextView textFansNum;
    private View viewFansNum;
    private TextView textRecordNum;
    private View viewRecordNum;
    private TextView textFavoriteNum;
    private View viewFavoriteNum;

    private ImageView imageBg;
    private TextView textEdit;
    private ImageView imageQRCode;
    private TextView textAttention;

    private TextView textID;
    private TextView textVip;
    private View viewFriendRemark;
    private TextView textFriendRemark;
    private TextView textIp;
    private TextView textStatus;
    private TextView textName;
    private TextView textRemark;
    private TextView textEmail;
    private TextView textWechat;
    private TextView textQQ;
    private TextView textGender;
    private TextView textBirth;
    private EditFriendRemarkPopWindow editFriendRemarkPopWindow;
    //db
    private UserDTO loginUser;
    //service
    //获取位置信息
    private final GetIPInfoService getIPInfoService = new GetIPInfoService(new DataHelper.OnDataListener<>() {
        @Override
        public void handle(int id, IPInfo data, boolean local) {
            if(data ==null)return;
            textIp.setVisibility(View.VISIBLE);
            textIp.setText(data.getShowArea());
        }
    });
    private final GetRecordCountByUseridService getRecordCountByUseridService = new GetRecordCountByUseridService(new DataHelper.OnDataListener<>() {
        @Override
        public void handle(int id, UserRecordCount data, boolean local) {
            if (data == null) return;
            textRecordNum.setText(StringUtil.numLong2Str(data.getRecordNum()));
            textFavoriteNum.setText(StringUtil.numLong2Str(data.getFavoriteNum()));
        }
    });

    private UserDTO userInfo;
    private final GetUserInfoService getUserInfoService = new GetUserInfoService(new DataHelper.OnDataListener<>() {
        @Override
        public void handle(int id, UserDTO data, boolean local) {
            if (data == null) return;
            userInfo = data;
            UserInfo userInfo = data.getUserInfo();
            GlideUtil.loadImage(PersonalActivity.this, userInfo.getHeadUrl(), imageBg);
            textRemark.setText(userInfo.getRemark());
            textName.setText(userInfo.getNick());
            textGender.setText(UserInfo.Gender.getGender(userInfo.getGender()).getDescription());
            Status status = getRealm().where(Status.class).equalTo("id", userInfo.getStatus()).findFirst();
            if (status != null) {
                textStatus.setText(status.getName());
            }
            textBirth.setText(DateTimeUtil.getBirthTime(userInfo.getBirth()));
            textEmail.setText(userInfo.getEmail());
            textWechat.setText(userInfo.getWechat());
            textQQ.setText(userInfo.getQq());
            getIPInfoService.request(loginUser.getToken(),userInfo.getIp());
            textVip.setText(Integer.toString(userInfo.getVip()));
            textAttentionNum.setText(StringUtil.numLong2Str(data.getAttentionNum()));
            textFansNum.setText(StringUtil.numLong2Str(data.getFanNum()));
            getRecordCountByUseridService.request(loginUser.getToken(), data.getUserInfo().getId());
        }

        @Override
        public void error(int id, int code, String message, String data) {
            ToastUtil.showToastShort(message + data);
        }
    });

    //朋友
    private Friend friend;
    private final GetFriendService getFriendService = new GetFriendService(this);
    private final AddAttentionService addAttentionService = new AddAttentionService(this);
    private final DeleteFriendService deleteFriendService = new DeleteFriendService(this);

    @Override
    protected void initParams(Intent intent) {
        userid = intent.getLongExtra(INTENT_USER_ID, 0);
    }

    @Override
    protected int bindLayout() {
        return R.layout.activity_personal;
    }

    @Override
    protected void initView(View view) {
        imageBg = $(R.id.personal_activity_image_big_head);
        imageBg.setOnClickListener(this);
        textEdit = $(R.id.personal_activity_text_edit_info);
        textEdit.setOnClickListener(this);
        imageQRCode = $(R.id.personal_activity_image_qr_code);
        imageQRCode.setOnClickListener(this);
        textAttention = $(R.id.personal_activity_text_attention);
        textAttention.setOnClickListener(this);

        textIp = $(R.id.personal_activity_text_ip);
        textID = $(R.id.personal_activity_text_id);
        textStatus = $(R.id.personal_activity_text_status);
        textName = $(R.id.personal_activity_text_name);
        textRemark = $(R.id.personal_activity_text_remark);
        textEmail = $(R.id.personal_activity_text_email);
        textWechat = $(R.id.personal_activity_text_wechat);
        textQQ = $(R.id.personal_activity_text_qq);
        textGender = $(R.id.personal_activity_text_gender);
        textBirth = $(R.id.personal_activity_text_birth);
        textFriendRemark = $(R.id.personal_activity_text_friend_remark);
        viewFriendRemark = $(R.id.personal_activity_view_friend_remark);
        viewFriendRemark.setOnClickListener(this);
        textVip = $(R.id.personal_activity_text_vip);

        textAttentionNum = view.findViewById(R.id.personal_activity_text_attention_num);
        textAttentionNum.setOnClickListener(this);
        viewAttentionNum = view.findViewById(R.id.personal_activity_view_attention);
        viewAttentionNum.setOnClickListener(this);
        textFansNum = view.findViewById(R.id.personal_activity_text_fans_num);
        textFansNum.setOnClickListener(this);
        viewFansNum = view.findViewById(R.id.personal_activity_view_fans);
        viewFansNum.setOnClickListener(this);
        textRecordNum = view.findViewById(R.id.personal_activity_text_record_num);
        textRecordNum.setOnClickListener(this);
        viewRecordNum = view.findViewById(R.id.personal_activity_view_record);
        viewRecordNum.setOnClickListener(this);
        textFavoriteNum = view.findViewById(R.id.personal_activity_text_favorite_num);
        textFavoriteNum.setOnClickListener(this);
        viewFavoriteNum = view.findViewById(R.id.personal_activity_view_favorite);
        viewFavoriteNum.setOnClickListener(this);

        editFriendRemarkPopWindow = new EditFriendRemarkPopWindow(this);
    }

    @Override
    protected void doBusiness(Context mContext) {
        textID.setText(Long.toString(userid));
        getUserInfoService.request(loginUser.getToken(), userid);
        if (loginUser.getUserInfo().getId() != userid) {
            textEdit.setVisibility(View.GONE);
            imageQRCode.setVisibility(View.GONE);
            textAttention.setVisibility(View.VISIBLE);
            getFriendService.request(loginUser.getToken(), userid);
            viewFriendRemark.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void doBusiness(Realm realm) {
        loginUser =getLoginUser();
    }

    @Override
    public void onClick(View v) {
        if (imageBg.getId() == v.getId()) {
            if (userInfo != null)
                ShowImageActivity.startActivity(this, userInfo.getUserInfo().getHeadUrl());
            return;
        }
        if (textEdit.getId() == v.getId()) {
            EditUserInfoActivity.startActivity(this);
            return;
        }
        if (imageQRCode.getId() == v.getId()) {
            QRCodeActivity.startActivity(this);
            return;
        }
        if (textAttention.getId() == v.getId()) {
            if (friend == null) {//关注
                addAttentionService.request(loginUser.getToken(), userid);
            } else {//取关
                deleteFriendService.request(loginUser.getToken(), friend.getId());
            }
            return;
        }
        if (userInfo != null) {//查阅信息
            if (v.getId() == textRecordNum.getId() || v.getId() == viewRecordNum.getId()) {
                UserRecordActivity.startActivity(this, userInfo.getUserInfo().getId());
                return;
            }
            if (v.getId() == textFavoriteNum.getId() || v.getId() == viewFavoriteNum.getId()) {
                FavoriteActivity.startActivity(this, userInfo.getUserInfo().getId());
                return;
            }
            if (v.getId() == textAttentionNum.getId() || v.getId() == viewAttentionNum.getId()) {
                FriendsActivity.startActivityForAttention(this, userInfo.getUserInfo().getId());
                return;
            }
            if (v.getId() == textFansNum.getId() || v.getId() == viewFansNum.getId()) {
                FriendsActivity.startActivityForFan(this, userInfo.getUserInfo().getId());
                return;
            }
        }

        if (v.getId() == viewFriendRemark.getId()) {//修改备注
            editFriendRemarkPopWindow.showPopWindow(v, loginUser.getToken(), friend, new DataHelper.OnDataListener<Friend>() {
                @Override
                public void handle(int id, Friend data) {
                    friend = data;
                    textFriendRemark.setText(data.getRemark());
                    editFriendRemarkPopWindow.dismiss();
                    ToastUtil.showToastShort(R.string.tip_update_success);
                }

                @Override
                public void error(int id, int code, String message, String data) {
                    ToastUtil.showToastShort(message + data);
                }
            });
        }
    }

    @Override
    public void handle(int id, Friend data, boolean local) {
        friend = data;
        if (friend != null) {
            textFriendRemark.setText(friend.getRemark());
        }
        if (id == getFriendService.getId()) {
            if (friend != null) {
                textAttention.setText(R.string.cancel_attention);
            } else {
                textAttention.setText(R.string.attention);
            }
        }
        if (id == addAttentionService.getId()) {//关注
            textAttention.setText(R.string.cancel_attention);
        }
        if (id == deleteFriendService.getId()) {//取关
            textAttention.setText(R.string.attention);
            friend = null;
        }
        viewFriendRemark.setVisibility(friend == null ? View.GONE : View.VISIBLE);
    }

    @Override
    public void error(int id, int code, String message, String data) {
        ToastUtil.showToastShort(message + data);
    }

    @Override
    public void onResume() {
        super.onResume();
        doBusiness(getRealm());
        doBusiness(this);
    }
}