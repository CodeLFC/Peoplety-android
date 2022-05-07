package gaozhi.online.peoplety.util;

import android.widget.Toast;

import gaozhi.online.peoplety.PeopletyApplication;

/**
 * Toast 工具
 */
public final class ToastUtil {
    public static void showToastLong(int resId){
        Toast.makeText(PeopletyApplication.getContext(),resId,Toast.LENGTH_LONG).show();
    }
    public static void showToastShort(int resId){
        Toast.makeText(PeopletyApplication.getContext(),resId,Toast.LENGTH_SHORT).show();
    }
    public static void showToastLong(String content){
        Toast.makeText(PeopletyApplication.getContext(),content,Toast.LENGTH_LONG).show();
    }
    public static void showToastShort(String content){
        Toast.makeText(PeopletyApplication.getContext(),content,Toast.LENGTH_SHORT).show();
    }
}
