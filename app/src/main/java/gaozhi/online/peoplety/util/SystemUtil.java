package gaozhi.online.peoplety.util;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.provider.MediaStore;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;


import gaozhi.online.peoplety.PeopletyApplication;


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
     * @param activity
     * @param REQUEST_CODE_PHOTO
     */
    public static void openAlbum(Activity activity, int REQUEST_CODE_PHOTO) {
        Intent pickIntent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        activity.startActivityForResult(pickIntent, REQUEST_CODE_PHOTO);
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

    public static void callKeyBoard(Activity context) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
    }
}
