package gaozhi.online.peoplety.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
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
import com.tencent.cos.xml.listener.CosXmlResultListener;
import com.tencent.cos.xml.model.CosXmlRequest;
import com.tencent.cos.xml.model.CosXmlResult;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

import gaozhi.online.base.asynchronization.Handler;
import gaozhi.online.base.net.Result;
import gaozhi.online.base.net.http.DataHelper;
import gaozhi.online.peoplety.R;
import gaozhi.online.peoplety.entity.Record;
import gaozhi.online.peoplety.entity.RecordType;
import gaozhi.online.peoplety.entity.Token;
import gaozhi.online.peoplety.entity.client.ImageModel;
import gaozhi.online.peoplety.entity.dto.UserDTO;
import gaozhi.online.peoplety.service.cos.GetCosTempSecretService;
import gaozhi.online.peoplety.service.record.PublishRecordService;
import gaozhi.online.peoplety.service.user.PostMessageService;
import gaozhi.online.peoplety.ui.activity.record.ImageAdapter;
import gaozhi.online.peoplety.ui.base.DBBaseActivity;
import gaozhi.online.peoplety.ui.util.image.ShowImageActivity;
import gaozhi.online.peoplety.ui.util.pop.AreaPopWindow;
import gaozhi.online.peoplety.ui.util.pop.OptionsPopWindow;
import gaozhi.online.peoplety.ui.util.pop.TipPopWindow;
import gaozhi.online.peoplety.util.FileUtil;
import gaozhi.online.peoplety.util.ImageUtil;
import gaozhi.online.peoplety.util.PatternUtil;
import gaozhi.online.peoplety.util.StringUtil;
import gaozhi.online.peoplety.util.TencentCOS;
import gaozhi.online.peoplety.util.ToastUtil;
import gaozhi.online.peoplety.util.pictureselector.GlideEngine;
import io.realm.Realm;

public class PublishRecordActivity extends DBBaseActivity implements Consumer<ImageModel>, DataHelper.OnDataListener<Result>, Handler.Worker {
    private static final String INTENT_TYPE = "type";
    private static final String INTENT_PARENT = "parent";

    public static void startActivity(Context context, @NonNull Record parent) {
        Intent intent = new Intent(context, PublishRecordActivity.class);
        intent.putExtra(INTENT_PARENT, parent);
        context.startActivity(intent);
    }

    public static void startActivity(Context context, @NonNull RecordType recordType) {
        Intent intent = new Intent(context, PublishRecordActivity.class);
        intent.putExtra(INTENT_TYPE, recordType);
        context.startActivity(intent);
    }

    //????????????????????????
    private static final int MAX_IMAGE_SIZE = 9;
    private static final int MIN_TITLE_LEN = 4;
    private static final int MIN_DESCRIPTION = 10;
    private static final int MIN_CONTENT = 20;
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
    private final GetCosTempSecretService getCosTempSecretService = new GetCosTempSecretService(this);
    private final PublishRecordService publishRecordService = new PublishRecordService(this);
    private final PostMessageService postMessageService = new PostMessageService(new DataHelper.OnDataListener<>() {
    });
    //handler,????????????????????????
    private final Handler handlerImgUploadProcess = new Handler(this);

    @Override
    protected void initParams(Intent intent) {
        recordType = intent.getParcelableExtra(INTENT_TYPE);
        parent = intent.getParcelableExtra(INTENT_PARENT);
        record = new Record();
        //?????????????????????
        record.setParentId(parent == null ? 0 : parent.getId());
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
     * ?????????????????????
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
     * ???????????????
     *
     * @param mContext
     */
    @Override
    protected void doBusiness(Context mContext) {
        record.setRecordTypeId(recordType.getId());
        title.setText(recordType.getName());

        //???????????????
        if (parent == null) {
            textParentTip.setVisibility(View.GONE);
            textParent.setVisibility(View.GONE);
        } else {
            textParentTip.setVisibility(View.VISIBLE);
            textParent.setVisibility(View.VISIBLE);
            textParent.setText(parent.getTitle());
        }

        if (loginUser.getArea() != null) {
            textArea.setText(loginUser.getArea().getName());
        }

    }

    @Override
    protected void doBusiness(Realm realm) {
        loginUser = realm.where(UserDTO.class).equalTo("current", true).findFirst();
        //build????????????Realm???????????????
        loginUser = realm.copyFromRealm(loginUser);
        if (parent != null)
            recordType = realm.where(RecordType.class).equalTo("id", parent.getRecordTypeId()).findFirst();
    }

    /**
     * ??????????????????
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
        //????????????
        if (v.getId() == textArea.getId()) {
            areaPopWindow.showPopupWindow(this);
            return;
        }
        //????????????
        if (v.getId() == imageSelectImage.getId()) {
            if (imageAdapter.getItemCount() >= MAX_IMAGE_SIZE) {
                ToastUtil.showToastLong(R.string.tip_upload_image_too_much);
                return;
            }
            selectImages();
            return;
        }
        //??????
        if (v.getId() == btnPublish.getId()) {

            //??????????????????
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
            //?????????????????????????????????????????????
            getCosTempSecretService.request(loginUser.getToken());
            return;
        }
    }

    //?????????????????????????????????
    private boolean checkAlreadyUploadAllImg(final TencentCOS.CosResponse cosResponse) {
        boolean allUploaded = true;
        if (imageAdapter.getItemCount() > 0) {
            for (int i = 0; i < imageAdapter.getItemCount(); i++) {
                ImageModel model = imageAdapter.getItem(i);
                if (model.getProcess() != ImageModel.UPLOAD_SUCCESS_PROCESS) {
                    //????????????
                    allUploaded = false;
                    new TencentCOS(this, cosResponse).putRecordImageWithPath(model.getFileName(), model.getUrl(), (complete, target) -> {
                        double rate = complete * 1.0 / target;
                        Message message = new Message();
                        model.setProcess((int) rate);
                        message.obj = model;
                        handlerImgUploadProcess.sendMessage(message);
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
     * ????????????
     */
    private void selectImages() {
        //?????????????????????????????????
        PictureSelector.create(this)
                .openGallery(PictureMimeType.ofImage())// ??????.PictureMimeType.ofAll()?????????.ofImage()?????????.ofVideo()?????????.ofAudio()
                .imageEngine(GlideEngine.createGlideEngine())
                .maxSelectNum(MAX_IMAGE_SIZE - imageAdapter.getItemCount())// ????????????????????????
                .minSelectNum(1)// ??????????????????
                //.imageSpanCount(3)// ??????????????????
                .selectionMode(PictureConfig.MULTIPLE)// ?????? or ??????PictureConfig.MULTIPLE : PictureConfig.SINGLE
                .isCamera(true)// ????????????????????????
                .isZoomAnim(true)// ?????????????????? ???????????? ??????true
                //.setOutputCameraPath("/CustomPath")// ???????????????????????????
                //.withAspectRatio(1, 1)// ???????????? ???16:9 3:2 3:4 1:1 ????????????
                //.selectionMedia(selectList)// ????????????????????????
                //.previewEggs(false)// ??????????????? ????????????????????????????????????(???????????????????????????????????????????????????)
                .cutOutQuality(60)// ?????????????????? ??????100
                //.compressMaxKB()//???????????????kb compressGrade()???Luban.CUSTOM_GEAR??????
                //.compressWH() // ??????????????? compressGrade()???Luban.CUSTOM_GEAR??????
                //.cropWH()// ???????????????????????????????????????????????????????????????
                .rotateEnabled(true) // ???????????????????????????
                .scaleEnabled(true)// ?????????????????????????????????
                //.recordVideoSecond()//?????????????????? ??????60s
                .forResult(PictureConfig.CHOOSE_REQUEST);//????????????onActivityResult code
    }

    /*
    ??????????????????????????????
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == PictureConfig.CHOOSE_REQUEST) {// ????????????????????????
                List<LocalMedia> images = PictureSelector.obtainMultipleResult(data);
                for (LocalMedia image : images) {
                    String imgPath = ImageUtil.getAdaptedImgPath(image.getPath(), this);
                    String hashCode = image.getFileName();
                    try {
                        //???????????????hash???
                        hashCode = FileUtil.hashFile(new File(imgPath));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    imageAdapter.add(new ImageModel(imageAdapter.getItemCount(), 0, imgPath, hashCode));
                }

                //selectList = PictureSelector.obtainMultipleResult(data);
                // ?????? LocalMedia ??????????????????path
                // 1.media.getPath(); ?????????path
                // 2.media.getCutPath();????????????path????????????media.isCut();?????????true
                // 3.media.getCompressPath();????????????path????????????media.isCompressed();?????????true
                // ????????????????????????????????????????????????????????????????????????????????????
            }
        }
    }

    //??????????????????
    @Override
    public void accept(ImageModel imageModel) {
        //???????????????
        List<OptionsPopWindow.Option> options = new LinkedList<>();
        options.add(new OptionsPopWindow.Option(0, getString(R.string.preview)));
        options.add(new OptionsPopWindow.Option(1, getString(R.string.delete)));
        new OptionsPopWindow(this)
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

    //??????????????????
    @Override
    public void start(int id) {
        btnPublish.setText(R.string.request_ing);
        btnPublish.setEnabled(false);
    }

    @Override
    public void handle(int id, Result result) {
        if (id == getCosTempSecretService.getId()) {
            TencentCOS.CosResponse cosResponse = new Gson().fromJson(result.getData(), TencentCOS.CosResponse.class);
            //??????????????????????????????
            if (checkAlreadyUploadAllImg(cosResponse)) {
                //????????????
                attemptPublishRecord();
            }
        }
        if (id == publishRecordService.getId()) {
            Gson gson = new Gson();
            record = gson.fromJson(result.getData(),Record.class);
            if(parent!=null) {
                Token token = loginUser.getToken();
                gaozhi.online.peoplety.entity.Message message = new gaozhi.online.peoplety.entity.Message();
                message.setType(gaozhi.online.peoplety.entity.Message.Type.NEW_EXTEND.getType());
                message.setToId(parent.getUserid());
                message.setMsg(gson.toJson(record));
                message.setRemark(getString(R.string.id) + token.getUserid() + getString(R.string.child_record) + parent.getId() + getString(R.string.floor) + getString(R.string.record) + parent.getTitle());
                postMessageService.request(token, message);
            }
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

    //??????????????????????????????
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
                //????????????
                new TipPopWindow(this, true).setMessage(R.string.tip_upload_fail).showPopupWindow(this);
                btnPublish.setEnabled(true);
                btnPublish.setText(R.string.bottom_publish);
                break;
            default:
                imageAdapter.updateItem((ImageModel) msg.obj);
        }
    }

    /**
     * ??????
     */
    private void attemptPublishRecord() {
        //????????????????????????
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