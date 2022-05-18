package gaozhi.online.peoplety.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.tencent.cos.xml.exception.CosXmlClientException;
import com.tencent.cos.xml.exception.CosXmlServiceException;
import com.tencent.cos.xml.listener.CosXmlProgressListener;
import com.tencent.cos.xml.listener.CosXmlResultListener;
import com.tencent.cos.xml.model.CosXmlRequest;
import com.tencent.cos.xml.model.CosXmlResult;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import gaozhi.online.base.asynchronization.Handler;
import gaozhi.online.base.net.Result;
import gaozhi.online.base.net.http.ApiRequest;
import gaozhi.online.base.ui.BasePopupWindow;
import gaozhi.online.peoplety.R;
import gaozhi.online.peoplety.entity.Record;
import gaozhi.online.peoplety.entity.RecordType;
import gaozhi.online.peoplety.entity.client.ImageModel;
import gaozhi.online.peoplety.entity.dto.UserDTO;
import gaozhi.online.peoplety.service.cos.GetCosTempSecretService;
import gaozhi.online.peoplety.service.record.PublishRecordService;
import gaozhi.online.peoplety.ui.activity.record.ImageAdapter;
import gaozhi.online.peoplety.ui.base.DBBaseActivity;
import gaozhi.online.peoplety.ui.util.image.ShowImageActivity;
import gaozhi.online.peoplety.ui.util.pop.AreaPopWindow;
import gaozhi.online.peoplety.ui.util.pop.OptionsPopWindow;
import gaozhi.online.peoplety.ui.util.pop.TipPopWindow;
import gaozhi.online.peoplety.ui.widget.NoAnimatorRecyclerView;
import gaozhi.online.peoplety.util.FileUtil;
import gaozhi.online.peoplety.util.GlideUtil;
import gaozhi.online.peoplety.util.ImageUtil;
import gaozhi.online.peoplety.util.PatternUtil;
import gaozhi.online.peoplety.util.StringUtil;
import gaozhi.online.peoplety.util.TencentCOS;
import gaozhi.online.peoplety.util.ToastUtil;
import gaozhi.online.peoplety.util.pictureselector.GlideEngine;
import io.realm.Realm;

public class PublishRecordActivity extends DBBaseActivity implements Consumer<ImageModel>, ApiRequest.ResultHandler, Handler.Worker {
    private static final String INTENT_TYPE = "type";
    private static final String INTENT_PARENT = "parent";

    public static void startActivity(Context context, RecordType recordType) {
        startActivity(context, recordType, null);
    }

    public static void startActivity(Context context, RecordType recordType, Record parent) {
        Intent intent = new Intent(context, PublishRecordActivity.class);
        intent.putExtra(INTENT_TYPE, recordType);
        intent.putExtra(INTENT_PARENT, parent);
        context.startActivity(intent);
    }

    //最多上传图片数量
    private static final int MAX_IMAGE_SIZE = 9;
    private static final int MIN_TITLE_LEN = 6;
    private static final int MIN_DESCRIPTION = 20;
    private static final int MIN_CONTENT = 80;
    private RecordType recordType;
    private Record parent;
    private Record record;
    //ui
    private TextView title;
    private TextView textParentTip;
    private TextView textParent;
    private EditText editTitle;
    private EditText editDescription;
    private EditText editContent;
    private EditText editUrl;

    private TextView textArea;
    private AreaPopWindow areaPopWindow;
    private ImageView imageSelectImage;
    private ImageAdapter imageAdapter;
    private CheckBox checkIsTop;
    private Button btnPublish;
    //db
    private UserDTO loginUser;
    //service
    private TencentCOS.CosResponse cosResponse;
    private final GetCosTempSecretService getCosTempSecretService = new GetCosTempSecretService(this);
    private final PublishRecordService publishRecordService = new PublishRecordService(this);
    //handler,发送图片上传状态
    private final Handler handlerImgUploadProcess = new Handler(this);

    @Override
    protected void initParams(Intent intent) {
        recordType = intent.getParcelableExtra(INTENT_TYPE);
        parent = intent.getParcelableExtra(INTENT_PARENT);
        record = new Record();
        //无法修改的内容
        record.setParentId(parent == null ? 0 : parent.getId());
        record.setRecordTypeId(recordType.getId());
    }

    @Override
    protected int bindLayout() {
        return R.layout.activity_publish_record;
    }

    @Override
    protected void initView(View view) {
        title = $(R.id.title_text);
        textArea = $(R.id.publish_record_activity_text_area);
        textArea.setOnClickListener(this);
        textParentTip = $(R.id.publish_record_activity_text_parent_tip);
        textParent = $(R.id.publish_record_activity_text_parent);
        editTitle = $(R.id.publish_record_activity_edit_title);
        editDescription = $(R.id.publish_record_activity_edit_description);
        editContent = $(R.id.publish_record_activity_edit_content);
        editUrl = $(R.id.publish_record_activity_edit_url);
        editUrl.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (PatternUtil.matchUrl(s.toString())) {
                    editUrl.setTextColor(getColor(R.color.deep_text_color));
                } else {
                    editUrl.setTextColor(getColor(R.color.red));
                }
            }
        });
        areaPopWindow = new AreaPopWindow(this, true);
        areaPopWindow.setOnAreaClickedListener(area -> {
            loginUser.setArea(area);
            textArea.setText(area.getName());
        });
        imageSelectImage = $(R.id.publish_record_activity_image_select_img);
        imageSelectImage.setOnClickListener(this);
        RecyclerView imageRecyclerView = $(R.id.publish_record_activity_recycler_img);
        LinearLayoutManager linearLayout = new LinearLayoutManager(this);
        linearLayout.setOrientation(RecyclerView.HORIZONTAL);
        imageRecyclerView.setLayoutManager(linearLayout);
        imageAdapter = new ImageAdapter();
        imageRecyclerView.setAdapter(imageAdapter);
        imageAdapter.setOnItemClickedListener(this);
        checkIsTop = $(R.id.publish_record_activity_check_top);
        btnPublish = $(R.id.publish_record_activity_btn_publish);
        btnPublish.setOnClickListener(this);

    }

    /**
     * 避免返回误操作
     *
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            new TipPopWindow(this, true).setMessage(getString(R.string.tip_record_loss)).setOkClickListener((basePopupWindow, view) -> {
                basePopupWindow.dismiss();
                finish();
            }).showPopupWindow(this);
            return true;
        }
        return false;
    }

    /**
     * 初始化业务
     *
     * @param mContext
     */
    @Override
    protected void doBusiness(Context mContext) {
        title.setText(recordType.getName());

        //没有父卷宗
        if (record.getParentId() == 0) {
            textParentTip.setVisibility(View.GONE);
            textParent.setVisibility(View.GONE);
        }

        if (loginUser.getArea() != null) {
            textArea.setText(loginUser.getArea().getName());
        }

        //请求对象存储密钥
        getCosTempSecretService.request(loginUser.getToken());
    }

    @Override
    protected void doBusiness(Realm realm) {
        loginUser = realm.where(UserDTO.class).equalTo("current", true).findFirst();
        //build一个没有Realm绑定的副本
        loginUser = realm.copyFromRealm(loginUser);
    }

    /**
     * 处理点击事件
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
        //选择地区
        if (v.getId() == textArea.getId()) {
            areaPopWindow.showPopupWindow(this);
            return;
        }
        //选择图片
        if (v.getId() == imageSelectImage.getId()) {
            if (imageAdapter.getItemCount() >= MAX_IMAGE_SIZE) {
                ToastUtil.showToastLong(R.string.tip_upload_image_too_much);
                return;
            }
            selectImages();
            return;
        }
        //上传
        if (v.getId() == btnPublish.getId()) {
            //检查密钥是否请求成功
            if (cosResponse == null) {
                getCosTempSecretService.request(loginUser.getToken());
                ToastUtil.showToastShort(R.string.request_ing_cos_secret);
                return;
            }
            //检查其他内容
            record.setTitle(editTitle.getText().toString());
            record.setDescription(editDescription.getText().toString());
            record.setContent(editContent.getText().toString());
            record.setUrl(editUrl.getText().toString());
            if (record.getTitle().length() < MIN_TITLE_LEN) {
                new TipPopWindow(this, true).setMessage(getString(R.string.tip_enter_title_short) + MIN_TITLE_LEN).showPopupWindow(this);
                return;
            }
            if (record.getDescription().length() < MIN_DESCRIPTION) {
                new TipPopWindow(this, true).setMessage(getString(R.string.tip_enter_description_short) + MIN_DESCRIPTION).showPopupWindow(this);
                return;
            }
            if (record.getContent().length() < MIN_CONTENT) {
                new TipPopWindow(this, true).setMessage(getString(R.string.tip_enter_content_short) + MIN_CONTENT).showPopupWindow(this);
                return;
            }
            if (!StringUtil.isEmpty(record.getUrl()) && !PatternUtil.matchUrl(record.getUrl())) {
                new TipPopWindow(this, true).setMessage(getString(R.string.tip_please_enter_right_url)).showPopupWindow(this);
                return;
            }

            if (loginUser.getArea() == null) {
                new TipPopWindow(this, true).setMessage(getString(R.string.tip_please_select_area)).showPopupWindow(this);
                return;
            }
            record.setAreaId(loginUser.getArea().getId());
            record.setTop(checkIsTop.isChecked());

            //检查图片是否全部上传
            if (checkAlreadyUploadAllImg()) {
                //直接发布
                attemptPublishRecord();
            }
            return;
        }
    }

    //检查是否完全上传了图片
    private boolean checkAlreadyUploadAllImg() {
        boolean allUploaded = true;
        if (imageAdapter.getItemCount() > 0) {
            for (int i = 0; i < imageAdapter.getItemCount(); i++) {
                ImageModel model = imageAdapter.getItem(i);
                if (model.getProcess() != ImageModel.UPLOAD_SUCCESS_PROCESS) {
                    //上传图像
                    allUploaded = false;
                    new TencentCOS(this, cosResponse).putRecordImageWithPath(model.getFileName(), model.getUrl(), new CosXmlProgressListener() {
                        @Override
                        public void onProgress(long complete, long target) {
                            double rate = complete * 1.0 / target;
                            Message message = new Message();
                            model.setProcess((int) rate);
                            message.obj = model;
                            handlerImgUploadProcess.sendMessage(message);
                        }
                    }, new CosXmlResultListener() {
                        @Override
                        public void onSuccess(CosXmlRequest cosXmlRequest, CosXmlResult cosXmlResult) {
                            Message message = new Message();
                            model.setProcess(ImageModel.UPLOAD_SUCCESS_PROCESS);
                            message.what = ImageModel.UPLOAD_SUCCESS_PROCESS;
                            message.obj = model;
                            handlerImgUploadProcess.sendMessage(message);
                        }

                        @Override
                        public void onFail(CosXmlRequest cosXmlRequest, @Nullable CosXmlClientException e, @Nullable CosXmlServiceException e1) {
                            Message message = new Message();
                            model.setProcess(ImageModel.UPLOAD_FAIL_PROCESS);
                            message.what = ImageModel.UPLOAD_FAIL_PROCESS;
                            message.obj = model;
                            handlerImgUploadProcess.sendMessage(message);
                        }
                    });
                }
            }
        }
        return allUploaded;
    }

    /**
     * 选择图片
     */
    private void selectImages() {
        //参数很多，根据需要添加
        PictureSelector.create(this)
                .openGallery(PictureMimeType.ofImage())// 全部.PictureMimeType.ofAll()、图片.ofImage()、视频.ofVideo()、音频.ofAudio()
                .imageEngine(GlideEngine.createGlideEngine())
                .maxSelectNum(MAX_IMAGE_SIZE - imageAdapter.getItemCount())// 最大图片选择数量
                .minSelectNum(1)// 最小选择数量
                //.imageSpanCount(3)// 每行显示个数
                .selectionMode(PictureConfig.MULTIPLE)// 多选 or 单选PictureConfig.MULTIPLE : PictureConfig.SINGLE
                .isCamera(true)// 是否显示拍照按钮
                .isZoomAnim(true)// 图片列表点击 缩放效果 默认true
                //.setOutputCameraPath("/CustomPath")// 自定义拍照保存路径
                //.withAspectRatio(1, 1)// 裁剪比例 如16:9 3:2 3:4 1:1 可自定义
                //.selectionMedia(selectList)// 是否传入已选图片
                //.previewEggs(false)// 预览图片时 是否增强左右滑动图片体验(图片滑动一半即可看到上一张是否选中)
                .cutOutQuality(60)// 裁剪压缩质量 默认100
                //.compressMaxKB()//压缩最大值kb compressGrade()为Luban.CUSTOM_GEAR有效
                //.compressWH() // 压缩宽高比 compressGrade()为Luban.CUSTOM_GEAR有效
                //.cropWH()// 裁剪宽高比，设置如果大于图片本身宽高则无效
                .rotateEnabled(true) // 裁剪是否可旋转图片
                .scaleEnabled(true)// 裁剪是否可放大缩小图片
                //.recordVideoSecond()//录制视频秒数 默认60s
                .forResult(PictureConfig.CHOOSE_REQUEST);//结果回调onActivityResult code
    }

    /*
    处理选择图片返回结果
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == PictureConfig.CHOOSE_REQUEST) {// 图片选择结果回调
                List<LocalMedia> images = PictureSelector.obtainMultipleResult(data);
                for (LocalMedia image : images) {
                    String imgPath = ImageUtil.getAdaptedImgPath(image.getPath(), this);
                    String hashCode = image.getFileName();
                    try {
                        //获取图像的hash值
                        hashCode = FileUtil.hashFile(new File(imgPath));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    imageAdapter.add(new ImageModel(0, imgPath, hashCode));
                }

                //selectList = PictureSelector.obtainMultipleResult(data);
                // 例如 LocalMedia 里面返回三种path
                // 1.media.getPath(); 为原图path
                // 2.media.getCutPath();为裁剪后path，需判断media.isCut();是否为true
                // 3.media.getCompressPath();为压缩后path，需判断media.isCompressed();是否为true
                // 如果裁剪并压缩了，以取压缩路径为准，因为是先裁剪后压缩的
            }
        }
    }

    //图片点击事件
    @Override
    public void accept(ImageModel imageModel) {
        //ToastUtil.showToastShort("链接：" + imageModel);
        //删除或查看
        List<OptionsPopWindow.Option> options = new LinkedList<>();
        options.add(new OptionsPopWindow.Option(0, getString(R.string.preview)));
        options.add(new OptionsPopWindow.Option(1, getString(R.string.delete)));
        new OptionsPopWindow(this, true)
                .setOptions(options)
                .setOnItemClickedListener((window, option) -> {
                    window.dismiss();
                    if (option.getId() == 0) {
                        ShowImageActivity.startActivity(PublishRecordActivity.this, imageModel.getUrl());
                        return;
                    }
                    if (option.getId() == 1) {
                        imageAdapter.remove(imageModel);
                    }
                })
                .showPopupWindow(this);
    }

    //处理请求结果
    @Override
    public void start(int id) {
        btnPublish.setText(R.string.request_ing);
        btnPublish.setEnabled(false);
    }

    @Override
    public void handle(int id, Result result) {
        btnPublish.setText(R.string.bottom_publish);
        btnPublish.setEnabled(true);
        if (id == getCosTempSecretService.getId()) {
            cosResponse = new Gson().fromJson(result.getData(), TencentCOS.CosResponse.class);
        }
        if (id == publishRecordService.getId()) {
            ToastUtil.showToastLong(R.string.tip_publish_success);
            finish();
        }
    }

    @Override
    public void error(int id, int code, String message, String data) {
        btnPublish.setText(R.string.bottom_publish);
        btnPublish.setEnabled(true);
        new TipPopWindow(this, true).setMessage(message + data).showPopupWindow(this);
    }

    //异步处理图片上传结果
    @Override
    public void handleMessage(@NonNull Message msg) {
        switch (msg.what) {
            case ImageModel.UPLOAD_SUCCESS_PROCESS:
                ImageModel imageModel = (ImageModel) msg.obj;
                imageAdapter.updateItem(imageModel);
                imageAdapter.addUploaded();
                attemptPublishRecord();
                break;
            case ImageModel.UPLOAD_FAIL_PROCESS:
                //上传失败
                new TipPopWindow(this, true).setMessage(R.string.tip_upload_fail).showPopupWindow(this);
                btnPublish.setEnabled(true);
                btnPublish.setText(R.string.bottom_publish);
                break;
            default:
                imageAdapter.updateItem((ImageModel) msg.obj);
        }
    }

    /**
     * 发布
     */
    private void attemptPublishRecord() {
        //如果全部上传完成
        if (imageAdapter.allUploaded()) {
            List<String> imgURLs = new LinkedList<>();
            for (int i = 0; i < imageAdapter.getItemCount(); i++) {
                imgURLs.add(TencentCOS.getRecordImageURL(imageAdapter.getItem(i).getFileName()));
            }
            record.setImgs(new Gson().toJson(imgURLs));
            publishRecordService.request(loginUser.getToken(), record);
        }
    }
}