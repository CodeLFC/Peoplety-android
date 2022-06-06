package gaozhi.online.peoplety.ui.util.scan;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraDevice;
import android.media.Image;
import android.media.ImageReader;
import android.os.Build;
import android.util.Log;
import android.util.Size;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;

import androidx.annotation.NonNull;

import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.List;

import gaozhi.online.base.ui.BaseActivity;
import gaozhi.online.peoplety.PeopletyApplication;
import gaozhi.online.peoplety.R;
import gaozhi.online.peoplety.util.PermissionUtil;
import gaozhi.online.peoplety.util.ScreenUtil;
import gaozhi.online.peoplety.util.ToastUtil;
import gaozhi.online.peoplety.util.ZxingUtil;

/**
 * 扫码
 */
public class ScanActivity extends BaseActivity implements TextureView.SurfaceTextureListener, ImageReader.OnImageAvailableListener {
    public static final int QR_RESULT_CODE = 8888;
    public static final String QR_CONTENT_KEY = "qr-content";

    public static void startActivityForResult(Activity context, int QR_REQUEST_CODE) {
        Intent intent = new Intent(context, ScanActivity.class);
        context.startActivityForResult(intent, QR_REQUEST_CODE);
    }

    //permission
    //授权
    private PermissionUtil permissionUtil;
    private final String[] authorities = new String[]{
            Manifest.permission.CAMERA
    };

    //ui
    private CameraTextureView textureViewCamera;
    private CameraHelper cameraHelper;
    private ImageReader imageReader;
    //data
    private Size previewSize;

    @Override
    protected void initParams(Intent intent) {
        permissionUtil = new PermissionUtil(this, 100);
    }

    @Override
    protected int bindLayout() {
        return R.layout.activity_scan;
    }

    @Override
    protected void initView(View view) {
        textureViewCamera = $(R.id.scan_activity_texture_preview);
        textureViewCamera.setSurfaceTextureListener(this);
    }

    @Override
    protected void doBusiness(Context mContext) {
        permissionUtil.setPermissionListener(new PermissionUtil.PermissionListener() {
            @Override
            public void agreed() {
                cameraHelper = new CameraHelper(ScanActivity.this, PeopletyApplication.getGlobalExecutor());
                Size[] cameraSize = cameraHelper.getBackCameraSize();
                ScreenUtil screenUtil = new ScreenUtil(ScanActivity.this);
                if (cameraSize == null || cameraSize.length == 0) {
                    previewSize = new Size(screenUtil.getScreenWidth(), screenUtil.getScreenWidth() * 4 / 3);
                } else {
                    previewSize = new Size(screenUtil.getScreenWidth(), screenUtil.getScreenWidth() * cameraSize[0].getWidth() / cameraSize[0].getHeight());
                }
                Log.i(TAG, "预览大小：" + previewSize);
                textureViewCamera.resizePreview(previewSize);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    cameraHelper.openBackCamera(new CameraDevice.StateCallback() {
                        @Override
                        public void onOpened(@NonNull CameraDevice camera) {
                            cameraDevice = camera;
                            //                        Matrix matrix = new Matrix();
                            //                        //第1步:把视频区移动到View区,使两者中心点重合.
                            //                        matrix.preTranslate((textureViewWidth - videoWidth) / 2, (textureViewHeight - videoHeight) / 2);
                            //
                            //                        //第2步:因为默认视频是fitXY的形式显示的,所以首先要缩放还原回来.
                            //                        matrix.preScale(videoWidth / textureViewWidth, videoHeight / textureViewHeight);
                            //
                            //                        //第3步,等比例放大或缩小,直到视频区的一边和View一边相等.如果另一边和view的一边不相等，则留下空隙
                            //                        if (sx >= sy) {
                            //                            matrix.postScale(sy, sy, textureViewWidth / 2, textureViewHeight / 2);
                            //                        } else {
                            //                            matrix.postScale(sx, sx, textureViewWidth / 2, textureViewHeight / 2);
                            //                        }
                            //
                            //                        mTextureView.setTransform(matrix);
                            //                        mTextureView.postInvalidate();
                        }

                        @Override
                        public void onDisconnected(@NonNull CameraDevice camera) {

                        }

                        @Override
                        public void onError(@NonNull CameraDevice camera, int error) {

                        }
                    });
                } else {
                    ToastUtil.showToastShort(R.string.tip_version_not_adapter);
                    finish();
                }
            }

            @Override
            public void denied() {
                ToastUtil.showToastLong(R.string.not_permission);
                finish();
            }
        });
        permissionUtil.requestPermission(authorities);
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        permissionUtil.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    //camera
    private CameraDevice cameraDevice;

    @Override
    public void onSurfaceTextureAvailable(@NonNull SurfaceTexture surface, int width, int height) {
        imageReader = ImageReader.newInstance(previewSize.getWidth(), previewSize.getHeight(), CameraHelper.IMAGE_FORMAT, 3);
        imageReader.setOnImageAvailableListener(this, PeopletyApplication.getGlobalExecutor().getBackgroundHandler());
        List<Surface> surfaces = new LinkedList<>();
        surfaces.add(new Surface(surface));
        surfaces.add(imageReader.getSurface());
        if (cameraDevice != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                try {
                    cameraHelper.startPreview(cameraDevice, surfaces, null);
                } catch (CameraAccessException e) {
                    e.printStackTrace();
                }
            }
        } else {
            ToastUtil.showToastShort(R.string.camera_error);
            finish();
        }

    }

    @Override
    public void onSurfaceTextureSizeChanged(@NonNull SurfaceTexture surface, int width, int height) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(@NonNull SurfaceTexture surface) {
        if (cameraDevice != null) {
            cameraDevice.close();
        }
        imageReader.close();
        return true;
    }

    @Override
    public void onSurfaceTextureUpdated(@NonNull SurfaceTexture surface) {

    }

    @Override
    public void onImageAvailable(ImageReader reader) {
        Image image = reader.acquireNextImage();
        if (image != null) {
            ByteBuffer buffer = image.getPlanes()[0].getBuffer();
            byte[] bytes = new byte[buffer.capacity()];
            buffer.get(bytes);
            Bitmap bitmapImage = BitmapFactory.decodeByteArray(bytes, 0, bytes.length, null);
            System.out.println("height:" + bitmapImage.getHeight() + " width:" + bitmapImage.getWidth());
            String content = ZxingUtil.QRCodeAnalyser.analyzeBitmap(bitmapImage);
            image.close();
            if (content != null) {
                imageReader.close();
                Intent intent = new Intent();
                intent.putExtra(QR_CONTENT_KEY, content);
                setResult(QR_RESULT_CODE, intent);
                finish();
            }
        }
    }
}