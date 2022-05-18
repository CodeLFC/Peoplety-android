package gaozhi.online.peoplety.util;

import android.annotation.SuppressLint;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateTimeUtil {
    @SuppressLint("SimpleDateFormat")
    private static final SimpleDateFormat birthFormat = new SimpleDateFormat("yyyy年MM月dd日");
    @SuppressLint("SimpleDateFormat")
    private static final SimpleDateFormat recordFormat = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");

    public static String getBirthTime(long time) {
        return birthFormat.format(new Date(time));
    }

    public static String getRecordTime(long time) {
        return recordFormat.format(new Date(time));
    }
}
