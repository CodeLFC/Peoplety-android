package gaozhi.online.peoplety.util;

import android.content.Context;

public class ScreenUtil {
    private Context context;

    public ScreenUtil(Context context) {
        this.context = context;
    }

    public float getDensity(Context context) {
        return context.getResources().getDisplayMetrics().density;
    }

    public int getScreenHeight() {
        return context.getResources().getDisplayMetrics().heightPixels;
    }

    public int getScreenWidth() {
        return context.getResources().getDisplayMetrics().widthPixels;
    }


    public float getXdpi() {
        return context.getResources().getDisplayMetrics().xdpi;
    }

    public float getYdpi() {
        return context.getResources().getDisplayMetrics().ydpi;
    }


    public int px2dip(int i) {
        float f = getDensity(context);
        return (int) (((double) i - 0.5D) / (double) f);
    }
    //px转换成dip
    public int px2dip(float pxValue){
        float scale =getDensity(context);
        return (int)(pxValue/scale + 0.5f);

    }
    //dip转换成px
    public int dip2px(float dipValue){
        float scale = getDensity(context);
        return (int)(dipValue * scale + 0.5f);
    }
    //px转换成sp
    public int px2sp(float pxValue){
        float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int)(pxValue / fontScale + 0.5f);
    }
    //sp转换成px
    public int sp2px(float spValue){
        float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int)(spValue * fontScale + 0.5f);
    }
}