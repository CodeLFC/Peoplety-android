package gaozhi.online.base.net.http;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Map;

class RestfulHTTP {
    private URL url;
    private HttpURLConnection connection;
    private InputStreamReader isr;
    private Map<String, String> headers;
    private final Charset charset;
    private String body;

    /**
     * @description: TODO 构造请求
     * @author LiFucheng
     * @date 2022/4/1 1:36
     * @version 1.0
     */
    public RestfulHTTP(String url) {
        this(url, null);
    }

    /**
     * @param url
     * @Date: 2020/7/7 0007 13:57
     * @Version: 1.0
     * @Method: RestfulHTTP
     * @Description:TODO(资源定位url)
     * @Author: LiFucheng
     * @Return:
     * @Throws:
     */
    public RestfulHTTP(String url, Charset charset) {
        if (charset == null) charset = StandardCharsets.UTF_8;
        this.charset = charset;
        url = urlReplaceBlankSpace(url);
        try {
            this.url = new URL(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

    }

    public RestfulHTTP open(String type) throws IOException {
        return open(type, "application/json");
    }

    public RestfulHTTP open(String type, String mediaType) throws IOException {
        System.out.println(type + "  :   " + url);
        if (url != null) {
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod(type);
            connection.setDoOutput(true);
            connection.setRequestProperty("Accept", mediaType);
            connection.setRequestProperty("Content-Type", mediaType);
            if (headers != null) {
                for (Map.Entry<String, String> entry : headers.entrySet()) {
                    connection.setRequestProperty(entry.getKey(), entry.getValue());
                }
            }
            connection.connect();
        } else {
            throw new IOException("URL为空！");
        }
        if (body != null) {
            OutputStream os = connection.getOutputStream();
            OutputStreamWriter osw = new OutputStreamWriter(os, charset);
            osw.write(body);
            osw.flush();
            osw.close();
            os.close();
        }
        return this;
    }

    /**
     * @description: TODO 写入body请求体
     * @author LiFucheng
     * @date 2022/4/1 1:40
     * @version 1.0
     */
    public RestfulHTTP writeBody(String body) {
        this.body = body;
        return this;
    }

    /**
     * @param
     * @Date: 2020/7/7 0007 13:35
     * @Version: 1.0
     * @Method: read
     * @Description:TODO(读取字符串流)
     * @Author: LiFucheng
     * @Return: java.lang.StringBuilder
     * @Throws:
     */
    public StringBuilder read() throws IOException {
        if (connection != null) {
            isr = new InputStreamReader(connection.getInputStream(), charset);
        } else {
            throw new IOException("InputStream为空！");
        }
        StringBuilder text = new StringBuilder();
        int temp = readISR();
        while (temp != -1) {
            text.append((char) temp);
            temp = readISR();
        }
        if (isr != null)
            isr.close();
        connection.disconnect();
        return text;
    }

    /**
     * @param
     * @Date: 2020/7/7 0007 13:53
     * @Version: 1.0
     * @Method: readISR
     * @Description:TODO(读取字节流)
     * @Author: LiFucheng
     * @Return: int
     * @Throws:
     */
    private int readISR() throws IOException {
        if (isr == null)
            return -1;
        return isr.read();
    }

    /**
     * @param text
     * @Date: 2020/7/7 0007 13:33
     * @Version: 1.0
     * @Method: encode_utf8
     * @Description:TODO(对字符串进行UTF8编码)
     * @Author: LiFucheng
     * @Return: java.lang.String
     * @Throws:
     */
    public static String encode_utf8(String text) {
        if (text == null) return null;
        if (text.length() == text.getBytes().length) {//没有中文
            return text;
        }
        try {
            text = URLEncoder.encode(text, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return text;
    }

    /**
     * @param url
     * @Date: 2020/7/7 0007 11:23
     * @Version: 1.0
     * @Method: urlReplaceBlankSpace
     * @Description:TODO(将url中的空格替换成转义)
     * @Author: LiFucheng
     * @Return: java.lang.String
     * @Throws:
     */
    public static String urlReplaceBlankSpace(String url) {
        return url.replace(" ", "%20");
    }

    public RestfulHTTP setHeaderParams(Map<String, String> headers) {
        this.headers = headers;
        return this;
    }
}
