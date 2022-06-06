package gaozhi.online.peoplety.ui.util.scan;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Size;
import android.view.TextureView;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * 相机预览
 */
public class CameraTextureView extends TextureView {
    public CameraTextureView(@NonNull Context context) {
        super(context);
    }

    public CameraTextureView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CameraTextureView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public CameraTextureView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void resizePreview(Size size) {
        ViewGroup.LayoutParams lp = getLayoutParams();
        lp.width = size.getWidth();
        lp.height = size.getHeight();
        setLayoutParams(lp);
    }
}
