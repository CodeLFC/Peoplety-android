package gaozhi.online.peoplety.util;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.LinkedList;
import java.util.List;

import gaozhi.online.base.ui.BaseActivity;

/**
 * 权限请求工具
 */
public class PermissionUtil {
    public interface PermissionListener {
        default void agreed() {
        }

        void denied();
    }

    private final BaseActivity activity;
    private final int PERMISSION_REQUEST_CODE;
    private PermissionListener permissionListener;

    public PermissionUtil(BaseActivity activity, int PERMISSION_REQUEST_CODE) {
        this.activity = activity;
        this.PERMISSION_REQUEST_CODE = PERMISSION_REQUEST_CODE;
        activity.setOnRequestPermissionsResultListener(this::onRequestPermissionsResult);
    }

    public void setPermissionListener(PermissionListener permissionListener) {
        this.permissionListener = permissionListener;
    }

    /**
     * 申请权限
     */
    public void requestPermission(String[] authorities) {
        List<String> permissionList = new LinkedList<>();
        for (String authority : authorities) {
            if (ContextCompat.checkSelfPermission(activity, authority) != PackageManager.PERMISSION_GRANTED) {
                permissionList.add(authority);
            }
        }

        if (permissionList.size() > 0) {
            ActivityCompat.requestPermissions(activity, authorities, PERMISSION_REQUEST_CODE);
        } else {
            if (permissionListener != null)
                permissionListener.agreed();
        }
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            boolean hasNotGranted = false;//是否有权限没有通过
            for (int grant : grantResults) {
                if (grant == PackageManager.PERMISSION_DENIED) {
                    hasNotGranted = true;
                }
            }
            if (hasNotGranted) {
                if (permissionListener != null) {
                    permissionListener.denied();
                }
                return;
            }
            if (permissionListener != null)
                permissionListener.agreed();
        }
    }
}
