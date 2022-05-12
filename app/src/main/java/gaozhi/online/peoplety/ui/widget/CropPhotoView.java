package gaozhi.online.peoplety.ui.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;

import com.github.chrisbanes.photoview.PhotoView;

public class CropPhotoView extends PhotoView {
    public CropPhotoView(Context context) {
        super(context);
    }

    public CropPhotoView(Context context, AttributeSet attr) {
        super(context, attr);
    }

    public CropPhotoView(Context context, AttributeSet attr, int defStyle) {
        super(context, attr, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec);
    }

    public Bitmap cropImage(){
        Bitmap bitmap = ((BitmapDrawable)getDrawable()).getBitmap();
        RectF rect          = getDisplayRect();
        float viewScale     = getScale();
        float imageRatio = (float)bitmap.getWidth() / (float)bitmap.getHeight();
        float viewRatio = (float)getWidth() / (float)getHeight();
        float scale = 0;
        if (imageRatio > viewRatio) {
            // scale is based on image width
            scale = 1 / ((float)bitmap.getWidth() / (float)getWidth() / viewScale);

        } else {
            // scale is based on image height, or 1
            scale = 1 / ((float)bitmap.getHeight() / (float)getHeight() / viewScale);
        }
        // translate to bitmap scale
        rect.left       = -rect.left / scale;
        rect.top        = -rect.top / scale;
        rect.right      = rect.left + ((float)getWidth() / scale);
        rect.bottom     = rect.top + ((float)getHeight() / scale);

        if (rect.top<0) {
            rect.bottom -= Math.abs(rect.top);
            rect.top = 0;
        }
        if (rect.left<0) {
            rect.right -= Math.abs(rect.left);
            rect.left = 0;
        }
        Bitmap bitmapRes=null;
        try {
            bitmapRes = Bitmap.createBitmap(bitmap, (int) rect.left, (int) rect.top, (int) rect.width(), (int) rect.height());
            setImageBitmap(bitmapRes);
        }catch (Exception e){
            e.printStackTrace();
        }
        return bitmapRes;
    }
}
