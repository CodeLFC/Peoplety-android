package gaozhi.online.peoplety.ui.activity.userinfo;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;

import gaozhi.online.peoplety.R;
import gaozhi.online.peoplety.entity.Status;
import gaozhi.online.peoplety.entity.UserInfo;
import gaozhi.online.peoplety.entity.dto.UserDTO;
import gaozhi.online.peoplety.service.NetConfig;
import gaozhi.online.peoplety.ui.base.DBBaseActivity;
import gaozhi.online.peoplety.util.GlideUtil;
import gaozhi.online.peoplety.util.ImageUtil;
import gaozhi.online.peoplety.util.ZxingUtil;
import io.realm.Realm;

/**
 * 二维码
 */
public class QRCodeActivity extends DBBaseActivity {
    private UserDTO loginUser;
    private Status status;
    //ui
    private TextView textTitle;
    private ImageView imageHead;
    private TextView textName;
    private ImageView imageGender;
    private TextView textStatus;
    private TextView textVip;
    private TextView textId;
    private ImageView imageQRCode;
    private Bitmap qrCode;

    @Override
    protected void initParams(Intent intent) {

    }

    @Override
    protected int bindLayout() {
        return R.layout.activity_qrcode;
    }

    @Override
    protected void initView(View view) {
        textTitle = $(R.id.title_text);
        textTitle.setText(R.string.qr_code_card);
        imageHead = $(R.id.qrcode_activity_image_head);
        textName = $(R.id.qrcode_activity_text_name);
        imageGender = $(R.id.qrcode_activity_image_gender);
        textStatus = $(R.id.qrcode_activity_text_status);
        textId = $(R.id.qrcode_activity_text_id);
        textVip = $(R.id.qrcode_activity_text_vip);
        imageQRCode = $(R.id.qrcode_activity_image_code);
    }

    @Override
    protected void doBusiness(Context mContext) {
        GlideUtil.loadRoundRectangleImage(this, loginUser.getUserInfo().getHeadUrl(), R.drawable.app_logo, imageHead);
        textName.setText(loginUser.getUserInfo().getNick());
        textStatus.setText(status.getName());
        textId.setText(getString(R.string.id) + loginUser.getUserInfo().getId());
        textVip.setText(getString(R.string.vip) + loginUser.getUserInfo().getVip());
        switch (UserInfo.Gender.getGender(loginUser.getUserInfo().getGender())) {
            case MALE:
                imageGender.setImageResource(R.drawable.male);
                break;
            case FEMALE:
                imageGender.setImageResource(R.drawable.female);
                break;
            case OTHER:
                imageGender.setImageResource(R.drawable.other_gender);
        }
        Bitmap logo = ImageUtil.drawableToBitmap(getDrawable(R.drawable.app_logo));
        qrCode = ZxingUtil.QRCodeGenerator.generateImage(NetConfig.officialURL + loginUser.getUserInfo().getId(), 1024, 1024, logo);
        imageQRCode.setImageBitmap(qrCode);
        GlideUtil.loadBitmap(this, loginUser.getUserInfo().getHeadUrl(), R.drawable.app_logo, new CustomTarget<>() {
            @Override
            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                qrCode = ZxingUtil.QRCodeGenerator.generateImage(NetConfig.officialURL + loginUser.getUserInfo().getId(), 1024, 1024, resource);
                imageQRCode.setImageBitmap(qrCode);
            }

            @Override
            public void onLoadCleared(@Nullable Drawable placeholder) {
            }
        });
    }

    @Override
    protected void doBusiness(Realm realm) {
        loginUser = realm.where(UserDTO.class).equalTo("current", true).findFirst();
        //build一个没有Realm绑定的副本
        loginUser = realm.copyFromRealm(loginUser);
        status = realm.where(Status.class).equalTo("id", loginUser.getUserInfo().getStatus()).findFirst();
    }

    @Override
    public void onClick(View v) {

    }

    public static void startActivity(Context context) {
        Intent intent = new Intent(context, QRCodeActivity.class);
        context.startActivity(intent);
    }
}