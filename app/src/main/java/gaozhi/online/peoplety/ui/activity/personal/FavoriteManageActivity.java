package gaozhi.online.peoplety.ui.activity.personal;


import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import gaozhi.online.base.net.Result;
import gaozhi.online.base.net.http.DataHelper;
import gaozhi.online.peoplety.R;
import gaozhi.online.peoplety.entity.Favorite;
import gaozhi.online.peoplety.entity.dto.UserDTO;
import gaozhi.online.peoplety.service.record.DeleteFavoriteService;
import gaozhi.online.peoplety.service.record.PublishFavoriteService;
import gaozhi.online.peoplety.service.record.UpdateFavoriteService;
import gaozhi.online.peoplety.ui.base.DBBaseActivity;
import gaozhi.online.peoplety.ui.util.pop.DialogPopWindow;
import gaozhi.online.peoplety.util.StringUtil;
import gaozhi.online.peoplety.util.ToastUtil;
import io.realm.Realm;

/**
 * 收藏夹管理
 */
public class FavoriteManageActivity extends DBBaseActivity {
    private static final String INTENT_FAVORITE = "favorite";

    public static void startActivity(Context context) {
        startActivity(context, null);
    }

    public static void startActivity(Context context, Favorite favorite) {
        Intent intent = new Intent(context, FavoriteManageActivity.class);
        intent.putExtra(INTENT_FAVORITE, favorite);
        context.startActivity(intent);
    }

    //data
    private Favorite favorite;
    private boolean newFavorite;
    //ui
    private EditText editName;
    private EditText editRemark;
    private TextView textPrivilege;
    private Button btnUpdate;
    private Button btnDelete;
    private DialogPopWindow dialogPopWindow;
    //db
    private UserDTO loginUser;
    //service
    private final PublishFavoriteService publishFavoriteService = new PublishFavoriteService(new DataHelper.OnDataListener<Favorite>() {
        @Override
        public void handle(int id, Favorite data) {
            ToastUtil.showToastShort(R.string.tip_new_success);
            finish();
        }

        @Override
        public void error(int id, int code, String message, String data) {
            ToastUtil.showToastShort(message + data);
        }
    });
    private final DeleteFavoriteService deleteFavoriteService = new DeleteFavoriteService(new DataHelper.OnDataListener<Result>() {
        @Override
        public void handle(int id, Result data) {
            ToastUtil.showToastShort(R.string.tip_delete_success);
            finish();
        }

        @Override
        public void error(int id, int code, String message, String data) {
            ToastUtil.showToastShort(message + data);
        }
    });
    private final UpdateFavoriteService updateFavoriteService = new UpdateFavoriteService(new DataHelper.OnDataListener<Favorite>() {
        @Override
        public void handle(int id, Favorite data) {
            ToastUtil.showToastShort(R.string.tip_update_success);
            finish();
        }

        @Override
        public void error(int id, int code, String message, String data) {
            ToastUtil.showToastShort(message + data);
        }
    });

    @Override
    protected void initParams(Intent intent) {
        favorite = intent.getParcelableExtra(INTENT_FAVORITE);
        newFavorite = favorite == null;
    }

    @Override
    protected int bindLayout() {
        return R.layout.activity_favorite_manage;
    }

    @Override
    protected void initView(View view) {
        //ui
        TextView textTitle = $(R.id.title_text);
        textTitle.setText(R.string.manage);
        editName = $(R.id.favorite_manage_activity_edit_name);
        editRemark = $(R.id.favorite_manage_activity_edit_remark);
        textPrivilege = $(R.id.favorite_manage_activity_text_privilege);
        btnUpdate = $(R.id.favorite_manage_activity_btn_submit);
        btnDelete = $(R.id.favorite_manage_activity_btn_delete);
        dialogPopWindow = new DialogPopWindow(this);
        dialogPopWindow.getMessage().setText(R.string.tip_ensure_delete);
        dialogPopWindow.getBtnRight().setOnClickListener(this);
    }

    @Override
    protected void doBusiness(Context mContext) {
        textPrivilege.setOnClickListener(this);
        btnUpdate.setOnClickListener(this);
        if (newFavorite) {
            favorite = new Favorite();
            favorite.setVisible(true);
            return;
        }
        editName.setText(favorite.getName());
        editRemark.setText(favorite.getDescription());
        textPrivilege.setText(favorite.isVisible() ? R.string.favorite_public : R.string.favorite_private);
        btnUpdate.setText(R.string.update);
        btnDelete.setVisibility(View.VISIBLE);
        btnDelete.setOnClickListener(this);
    }

    @Override
    protected void doBusiness(Realm realm) {
        loginUser = realm.where(UserDTO.class).equalTo("current", true).findFirst();
        loginUser = realm.copyFromRealm(loginUser);
    }

    @Override
    public void onClick(View v) {
        if (textPrivilege.getId() == v.getId()) {
            favorite.setVisible(!favorite.isVisible());
            textPrivilege.setText(favorite.isVisible() ? R.string.favorite_public : R.string.favorite_private);
            return;
        }
        favorite.setName(editName.getText().toString());
        favorite.setDescription(editRemark.getText().toString());
        if (btnUpdate.getId() == v.getId()) {
            if (StringUtil.isEmpty(favorite.getName())) {
                ToastUtil.showToastShort(R.string.tip_please_enter_favorite_name);
                return;
            }
            if (StringUtil.isEmpty(favorite.getDescription())) {
                ToastUtil.showToastShort(R.string.tip_please_enter_favorite_remark);
                return;
            }
            if (newFavorite) {
                publishFavoriteService.request(loginUser.getToken(), favorite);
            } else {
                updateFavoriteService.request(loginUser.getToken(), favorite);
            }
            return;
        }
        if (btnDelete.getId() == v.getId()) {
            dialogPopWindow.showPopupWindow(v);
            return;
        }
        if (dialogPopWindow.getBtnRight().getId() == v.getId()) {
            deleteFavoriteService.request(loginUser.getToken(), favorite.getId());
        }
    }
}