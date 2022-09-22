package gaozhi.online.peoplety.util;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;


import java.io.FileNotFoundException;
import java.io.OutputStream;

import gaozhi.online.peoplety.PeopletyApplication;
import gaozhi.online.peoplety.R;


/**
 * 系统工具
 */
public class SystemUtil {
    private static Context getApplicationContext() {
        return PeopletyApplication.getContext();
    }

    private static WindowManager getWindowManager() {
        return (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
    }

    /**
     * 打开相册
     *
     * @param activity
     * @param REQUEST_CODE_PHOTO
     */
    public static void openAlbum(Activity activity, int REQUEST_CODE_PHOTO) {
        Intent pickIntent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        activity.startActivityForResult(pickIntent, REQUEST_CODE_PHOTO);
    }

    public static void saveImage(Bitmap toBitmap) {
        if (Build.VERSION.SDK_INT > 29) {
            saveImage29(toBitmap, getApplicationContext());
        } else {
            saveImage(toBitmap, getApplicationContext());
        }
    }

    /**
     * API 29及以下保存图片到相册的方法
     *
     * @param toBitmap 要保存的图片
     */
    private static void saveImage(Bitmap toBitmap, Context context) {
        String insertImage = MediaStore.Images.Media.insertImage(context.getContentResolver(), toBitmap, context.getString(R.string.app_name), context.getString(R.string.tip_app_slogan));
        if (!TextUtils.isEmpty(insertImage)) {
            ToastUtil.showToastShort(R.string.picture_save_success);
        }
    }

    /**
     * API29 中的最新保存图片到相册的方法
     */
    private static void saveImage29(Bitmap toBitmap, Context context) {
        //开始一个新的进程执行保存图片的操作
        Uri insertUri = context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new ContentValues());
        //使用use可以自动关闭流
        try {
            OutputStream outputStream = context.getContentResolver().openOutputStream(insertUri, "rw");
            if (toBitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)) {
                ToastUtil.showToastShort(R.string.image_save_success);
            } else {
                ToastUtil.showToastShort(R.string.image_save_error);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param context     上下文
     * @param description 字符串描述
     * @param text        字符串内容
     */
    public static void copyStr2Clipboard(Context context, String description, String text) {
        //获取剪贴板管理器：
        ClipboardManager cm = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        // 创建普通字符型ClipData
        ClipData mClipData = ClipData.newPlainText(description, text);
        // 将ClipData内容放到系统剪贴板里。
        cm.setPrimaryClip(mClipData);
    }

    /**
     * 获取手机型号
     *
     * @return 手机型号
     */
    public static String getSystemModel() {
        return android.os.Build.MODEL;
    }

    public static void closeKeyboard(Activity context) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm.isActive() && context.getCurrentFocus() != null) {
            if (context.getCurrentFocus().getWindowToken() != null) {
                imm.hideSoftInputFromWindow(context.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }

    /**
     * 判断键盘是否开启
     *
     * @return
     */
    public static boolean isSoftInputShowing(Activity context) {
        //获取当前屏幕内容的高度
        int screenHeight = context.getWindow().getDecorView().getHeight();
        //获取View可见区域的bottom
        Rect rect = new Rect();
        context.getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);

        return screenHeight - rect.bottom != 0;
    }


    public static void callKeyBoard(Activity context) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);

    }
}
