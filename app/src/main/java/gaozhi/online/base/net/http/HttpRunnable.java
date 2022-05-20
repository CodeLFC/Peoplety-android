package gaozhi.online.base.net.http;


import android.os.Message;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Map;

import gaozhi.online.base.net.Result;
import gaozhi.online.base.asynchronization.Handler;


public class HttpRunnable implements Runnable, Handler.Worker {
    private final RestfulHTTP restfulHTTP;
    private final HttpHandler httpHandler;
    private final String type;
    private Map<String, String> headers;
    private String body;

    private final Handler handler;

    public HttpRunnable(String type, String url, Charset encode, HttpHandler httpHandler) {
        restfulHTTP = new RestfulHTTP(url, encode);
        this.httpHandler = httpHandler;
        this.type = type;
        handler = new Handler(this);
    }

    /**
     * @param url 默认使用UTF8
     */
    public HttpRunnable(String type, String url, HttpHandler httpHandler) {
        this(type, url, null, httpHandler);
    }

    @Override
    public void run() {
        Message message = new Message();
        message.what = 0;
        try {
            message.obj = restfulHTTP.setHeaderParams(headers)
                    .writeBody(body)
                    .open(type)
                    .read()
                    .toString();
            handler.sendMessage(message);
        } catch (IOException e) {
            Message errorMessage = new Message();
            errorMessage.what = Result.NET_ERROR;
            handler.sendMessage(errorMessage);
            e.printStackTrace();
        }
    }

    @Override
    public void handleMessage(@NonNull Message msg) {
        if (httpHandler == null) return;
        if (msg.what == Result.NET_ERROR) {
            httpHandler.error(Result.NET_ERROR);
            return;
        }
        if (msg.what == 0) {
            httpHandler.handle((String) (msg.obj));
        }
    }

    public void setHeaderParams(Map<String, String> headers) {
        this.headers = headers;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public interface HttpHandler {

        void handle(String text);

        void error(int code);
    }
}
