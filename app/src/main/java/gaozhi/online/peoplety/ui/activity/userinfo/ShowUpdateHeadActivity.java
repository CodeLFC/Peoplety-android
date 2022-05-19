package gaozhi.online.peoplety.ui.activity.userinfo;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Message;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.gson.Gson;
import com.tencent.cos.xml.exception.CosXmlClientException;
import com.tencent.cos.xml.exception.CosXmlServiceException;
import com.tencent.cos.xml.listener.CosXmlProgressListener;
import com.tencent.cos.xml.listener.CosXmlResultListener;
import com.tencent.cos.xml.model.CosXmlRequest;
import com.tencent.cos.xml.model.CosXmlResult;

import java.io.File;

import gaozhi.online.base.asynchronization.Handler;
import gaozhi.online.base.net.Result;
import gaozhi.online.base.net.http.ApiRequest;
import gaozhi.online.base.net.http.DataHelper;
import gaozhi.online.peoplety.R;
import gaozhi.online.peoplety.entity.dto.UserDTO;
import gaozhi.online.peoplety.service.cos.GetCosTempSecretService;
import gaozhi.online.peoplety.service.user.UpdateUserInfoService;
import gaozhi.online.peoplety.ui.base.DBBaseActivity;
import gaozhi.online.peoplety.ui.widget.CropPhotoView;
import gaozhi.online.peoplety.util.ImageUtil;
import gaozhi.online.peoplety.util.TencentCOS;
import gaozhi.online.peoplety.util.ToastUtil;
import io.realm.Realm;

/**
 * 修改头像
 */
public class ShowUpdateHeadActivity extends DBBaseActivity implements DataHelper.OnDataListener<Result>, CosXmlProgressListener, CosXmlResultListener, Handler.Worker {
    //intent
    private Uri imagePath;
    //ui
    private CropPhotoView image_head;
    private Button btn_upload;
    private UserDTO loginUser;
    //service
    private final GetCosTempSecretService getCosTempSecretService = new GetCosTempSecretService(this);
    private final UpdateUserInfoService updateUserInfoService = new UpdateUserInfoService(this);
    private File headLocalFile;
    //util
    private final Gson gson = new Gson();
    private final Handler handler = new Handler(this);

    @Override
    protected void initParams(Intent intent) {
        imagePath = intent.getData();
    }

    @Override
    protected int bindLayout() {
        return R.layout.activity_show_update_head;
    }

    @Override
    protected void initView(View view) {
        image_head = $(R.id.update_head_activity_image_head);
        btn_upload = $(R.id.update_head_activity_btn_upload);
        btn_upload.setOnClickListener(this);
    }

    @Override
    protected void doBusiness(Realm realm) {
        loginUser = realm.where(UserDTO.class).equalTo("current", true).findFirst();
        //build一个没有Realm绑定的副本
        loginUser = realm.copyFromRealm(loginUser);
    }

    @Override
    protected void doBusiness(Context mContext) {
        image_head.setImageURI(imagePath);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == btn_upload.getId()) {
            Bitmap bitmap = null;
            try {
                bitmap = image_head.cropImage();
            } catch (Exception e) {
                e.printStackTrace();
                ToastUtil.showToastLong(e.getMessage());
            }
            if (bitmap == null) {
                ToastUtil.showToastLong(R.string.tip_err_resource_write);
                return;
            }

            try {
                headLocalFile = ImageUtil.saveBmp2JPG2Gallery(this, bitmap, 30, loginUser.getUserInfo().getId() + "-" + System.currentTimeMillis());
            } catch (Exception e) {
                e.printStackTrace();
                ToastUtil.showToastLong(R.string.tip_err_resource_write);
            }
            getCosTempSecretService.request(loginUser.getToken());
        }
    }

    public static void startActivity(Context context, Uri data) {
        Intent intent = new Intent(context, ShowUpdateHeadActivity.class);
        intent.setData(data);
        context.startActivity(intent);
    }

    @Override
    public void start(int id) {
        btn_upload.setEnabled(false);
        btn_upload.setText(R.string.request_ing);
    }

    @Override
    public void handle(int id, Result result) {
        if (id == getCosTempSecretService.getId()) {
            //开始上传
            TencentCOS.CosResponse cosCredentials = gson.fromJson(result.getData(), TencentCOS.CosResponse.class);
            TencentCOS tencentCOS = new TencentCOS(this, cosCredentials);
            String link = tencentCOS.putHeadImageWithPath(headLocalFile.getName(), headLocalFile.getPath(), this, this);
            loginUser.getUserInfo().setHeadUrl(link);
            return;
        }
        if (id == updateUserInfoService.getId()) {
            ToastUtil.showToastLong(R.string.tip_update_success);
            getRealm().executeTransactionAsync(realm -> realm.copyToRealmOrUpdate(loginUser));
            finish();
        }
    }

    @Override
    public void error(int id, int code, String message, String data) {
        btn_upload.setEnabled(true);
        btn_upload.setText(R.string.upload_head);
    }

    @Override
    public void onSuccess(CosXmlRequest cosXmlRequest, CosXmlResult cosXmlResult) {
        handler.sendEmptyMessage(1);
    }

    @Override
    public void onFail(CosXmlRequest cosXmlRequest, @Nullable CosXmlClientException e, @Nullable CosXmlServiceException e1) {
        btn_upload.setEnabled(true);
        btn_upload.setText(R.string.upload_head);
        ToastUtil.showToastLong(R.string.tip_update_fail);
    }

    @Override
    public void onProgress(long complete, long target) {
        int rate = (int) (complete * 100 / target);
        String process = getText(R.string.request_ing) + Integer.toString(rate) + "%";
        Message message = new Message();
        message.what = 0;
        message.obj = process;
        handler.sendMessage(message);
    }

    @Override
    public void handleMessage(@NonNull Message msg) {
        if (msg.what == 0) {
            btn_upload.setText(msg.obj.toString());
            return;
        }
        if (msg.what == 1) {
            updateUserInfoService.request(loginUser.getToken(), loginUser.getUserInfo());
        }
    }
}