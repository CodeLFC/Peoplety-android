package gaozhi.online.base.net.http;

import com.google.gson.Gson;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Supplier;

import gaozhi.online.base.net.Result;

/**
 * 网络请求
 */
public abstract class ApiRequest<T> implements HttpRunnable.HttpHandler, DataHelper<T> {
    //线程安全
    private static final AtomicInteger idCount = new AtomicInteger(0);
    private final int id;

    public int getId() {
        return id;
    }

    //是否正在请求
    private volatile boolean requesting;

    public boolean isRequesting() {
        return requesting;
    }

    /**
     * 请求类型
     */
    public enum Type {
        POST("POST"),
        GET("GET"),
        PATCH("PATCH"),
        DELETE("DELETE"),
        PUT("PUT");
        String type;

        Type(String type) {
            this.type = type;
        }

        public String getType() {
            return type;
        }
    }

    private final static Gson gson = new Gson();
    private final String baseURL;
    private final Type type;
    private DataHelper.OnDataListener<T> dataListener;

    public ApiRequest(String baseURL, Type type) {
        id = (idCount.getAndAdd(1)) % (Integer.MAX_VALUE - 10);
        this.baseURL = baseURL;
        this.type = type;
    }

    public void setDataListener(DataHelper.OnDataListener<T> dataListener) {
        this.dataListener = dataListener;
    }

    protected Gson getGson() {
        return gson;
    }

    @Override
    public void handle(String text) {
        Result result = gson.fromJson(text, Result.class);
        if (dataListener != null) {
            if (result.getCode() == Result.SUCCESS) {
                dataListener.handle(getId(), getNetData(result), false);
            } else {
                dataListener.error(getId(), result.getCode(), result.getMessage(), result.getData());
            }
        }
        requesting = false;
    }

    @Override
    public void error(int code) {
        if (dataListener != null) {
            dataListener.error(getId(), code, "net error!", "");
        }
        requesting = false;
    }

    /**
     * @description: TODO 请求
     * @author LiFucheng
     * @date 2022/3/31 18:11
     * @version 1.0
     */
    protected void request(String api, Map<String, String> params) {
        request(api, null, params, null);
    }

    /**
     * @description: TODO 请求
     * @author LiFucheng
     * @date 2022/3/31 18:11
     * @version 1.0
     */
    protected void request(String api, Map<String, String> headers, Map<String, String> params) {
        request(api, headers, params, null);
    }

    /**
     * @description: TODO 请求
     * @author LiFucheng
     * @date 2022/3/31 18:11
     * @version 1.0
     */
    protected void request(String api, Map<String, String> params, Object body) {
        request(api, null, params, body);
    }

    /**
     * @param api    api名称
     * @param params api参数
     */
    protected void request(String api, Map<String, String> headers, Map<String, String> params, Object body) {
        if(requesting){//如果正在请求，则不进行请求
            return;
        }
        requesting = true;
        if (dataListener != null) {
            dataListener.start(getId());
            T data = initLocalData(headers, params, body);
            dataListener.handle(getId(), data, true);
        }
        HttpRunnable httpRunnable = new HttpRunnable(type.getType(), UrlFactory.appendParams(baseURL + api, params), this);
        httpRunnable.setHeaderParams(headers);
        if (body != null) {
            httpRunnable.setBody(gson.toJson(body));
        }
        new Thread(httpRunnable).start();
    }
}
