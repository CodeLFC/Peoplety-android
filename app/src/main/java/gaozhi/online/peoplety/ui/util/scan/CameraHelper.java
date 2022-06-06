package gaozhi.online.peoplety.ui.util.scan;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.OutputConfiguration;
import android.hardware.camera2.params.SessionConfiguration;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.os.Build;
import android.os.Handler;
import android.util.Log;
import android.util.Size;
import android.view.Surface;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;

import gaozhi.online.base.asynchronization.GlobalExecutor;

/**
 * 相机的简单封装
 */
public class CameraHelper {
    public interface PreviewListener {
        void success();

        void fail(String msg);
    }

    /**
     * 默认图像预览编码
     */
    public static int IMAGE_FORMAT = ImageFormat.JPEG;

    /**
     * 当前相机列表位置
     */
    private int current = -1;
    /**
     * 后置相机列表位置
     */
    private int backCamera = -1;
    /**
     * 前置相机列表位置
     */
    private int frontCamera = -1;

    private final Context context;
    private String[] cameraList;
    private final Map<String, Size[]> cameraSizeMap;
    private final CameraManager cameraManager;
    private final GlobalExecutor globalExecutor;

    public CameraHelper(Context context,@NotNull GlobalExecutor globalExecutor) {
        this.context = context;
        this.globalExecutor = globalExecutor;
        cameraSizeMap = new HashMap<>();
        cameraManager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
        try {
            cameraList = cameraManager.getCameraIdList();
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
        for (int i = 0; i < cameraList.length; i++) {
            CameraCharacteristics characteristics = null;
            try {
                characteristics = cameraManager.getCameraCharacteristics(cameraList[i]);
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
            StreamConfigurationMap map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
            // 获取尺寸
            Size[] size = map.getOutputSizes(IMAGE_FORMAT);
            cameraSizeMap.put(cameraList[i], size);
            if (characteristics.get(CameraCharacteristics.LENS_FACING) == CameraCharacteristics.LENS_FACING_BACK) {
                backCamera = i;
                Log.i(getClass().getName(), "后置相机：" + backCamera);
                continue;
            }
            if (characteristics.get(CameraCharacteristics.LENS_FACING) == CameraCharacteristics.LENS_FACING_FRONT) {
                frontCamera = i;
                Log.i(getClass().getName(), "前置相机：" + frontCamera);
            }
        }
    }

    /**
     * @param stateCallback The callback which is invoked once the camera is opened
     */
    @RequiresApi(api = Build.VERSION_CODES.P)
    public void openBackCamera(CameraDevice.StateCallback stateCallback) {
        openCamera(backCamera, stateCallback);
    }

    /**
     * @param stateCallback The callback which is invoked once the camera is opened
     */
    @RequiresApi(api = Build.VERSION_CODES.P)
    public void openFrontCamera(CameraDevice.StateCallback stateCallback) {
        openCamera(frontCamera, stateCallback);
    }

    /**
     * @param camera        The unique identifier of the camera device to open
     * @param stateCallback The callback which is invoked once the camera is opened
     */
    @RequiresApi(api = Build.VERSION_CODES.P)
    private void openCamera(int camera,CameraDevice.StateCallback stateCallback) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            stateCallback.onError(null, CameraDevice.StateCallback.ERROR_CAMERA_DISABLED);
            return;
        }
        try {
            cameraManager.openCamera(cameraList[camera], command -> globalExecutor.getBackgroundHandler().post(command), stateCallback);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    public void startPreview(CameraDevice cameraDevice, Surface preview, PreviewListener previewListener) throws CameraAccessException {
        List<Surface> surfaces = new LinkedList<>();
        surfaces.add(preview);
        startPreview(cameraDevice, surfaces, previewListener);
    }

    /**
     * @param cameraDevice
     * @param preview
     * @return
     * @throws CameraAccessException
     */
    @RequiresApi(api = Build.VERSION_CODES.P)
    public void startPreview(CameraDevice cameraDevice, List<Surface> preview, PreviewListener previewListener) throws CameraAccessException {
        Log.i(this.getClass().getSimpleName(), "开始预览！");
        CaptureRequest.Builder builder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
        List<OutputConfiguration> outputConfigurationList = new LinkedList<>();
        for (Surface surface : preview) {
            outputConfigurationList.add(new OutputConfiguration(surface));
            builder.addTarget(surface);
        }
        SessionConfiguration sessionConfiguration = new SessionConfiguration(SessionConfiguration.SESSION_REGULAR, outputConfigurationList, command -> globalExecutor.getBackgroundHandler().post(command), new CameraCaptureSession.StateCallback() {
            @Override
            public void onConfigured(@NonNull CameraCaptureSession session) {
                Log.i(this.getClass().getSimpleName(), "预览会话开始创建！");
                builder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
                //builder.set(CaptureRequest.JPEG_THUMBNAIL_SIZE, new Size(1080,1920));
                CaptureRequest captureRequest = builder.build();
                try {
                    //线程handler
                    session.setRepeatingRequest(captureRequest, new CameraCaptureSession.CaptureCallback() {
                        @Override
                        public void onCaptureStarted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, long timestamp, long frameNumber) {
                            super.onCaptureStarted(session, request, timestamp, frameNumber);
                        }
                    }, null);
                } catch (CameraAccessException e) {
                    e.printStackTrace();
                }
                if (previewListener != null)
                    previewListener.success();
            }

            @Override
            public void onConfigureFailed(@NonNull CameraCaptureSession session) {
                if (previewListener != null)
                    previewListener.fail("onConfigureFailed");
            }
        });
        try {
            cameraDevice.createCaptureSession(sessionConfiguration);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }
    private void setCameraSize(Camera camera, float needW, float needH) {
        if(null == camera ) return;
        Camera.Parameters parameters = camera.getParameters();
        List<Camera.Size> list = parameters.getSupportedPreviewSizes();
        /**
         * 这个返回的是所有camera支持的尺寸，需要注意的是并不是所有我们需要的尺寸摄像头都支持，
         * 比如我现在的画布尺寸是宽230高120，这个尺寸摄像头是绝对不支持的所以我们需要在摄像头
         * 支持的所有尺寸中选择以一个最接近我们目标的
         */
        float needRatio = needW/needH;
        Log.e("我需要的宽高比为", String.valueOf(needRatio));
        LinkedHashMap<Float, Camera.Size> map = new LinkedHashMap<>();
        float bestRatio = 0;
        for (Camera.Size size : list){
            Log.e("Camera.Size", size.width + "," + size.height + "," + (float)size.width/size.height);
            /**
             * 先把所有的尺寸打出来让大家有一个认识
             */
            map.put((float)size.width/size.height, size);
            /**
             * 将所有的尺寸根据宽高比，存入map
             */
            if(bestRatio == 0 || Math.abs(needRatio - (float)size.width/size.height) < Math.abs(needRatio - bestRatio)) {
                bestRatio = (float)size.width/size.height;
            }
        }
        Log.e("最佳的Camera.Size", String.valueOf(bestRatio));

//        parameters.setPreviewSize();
        camera.setParameters(parameters);
    }
}
