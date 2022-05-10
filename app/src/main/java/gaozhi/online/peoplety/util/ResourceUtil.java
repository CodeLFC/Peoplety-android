package gaozhi.online.peoplety.util;

import android.content.Context;

import androidx.annotation.RawRes;

import java.io.IOException;
import java.io.InputStream;

/**
 * 资源工具
 */
public class ResourceUtil {
    /**
     *  读取raw文件的内容
     * @param context
     * @param rawId
     * @return
     * @throws IOException
     */
    public static String readRaw(Context context,@RawRes int rawId) throws IOException {
        InputStream inputStream = context.getResources().openRawResource(rawId);
        byte [] buffer = new byte[inputStream.available()];
        inputStream.read(buffer);
        return StringUtil.bytesToString(buffer);
    }
}
