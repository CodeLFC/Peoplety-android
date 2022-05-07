package gaozhi.online.base.net.http;

import com.google.gson.Gson;
import java.util.Map;

import gaozhi.online.base.net.Result;

/**
 * 网络请求
 */
public class ApiRequest implements HttpRunnable.HttpHandler {
    private static volatile int idCount = 0;
    private final int id;

    public int getId() {
        return id;
    }

    /**
     * 结果处理器
     */
    public interface ResultHandler {
        void start(int id);

        void handle(int id, Result result);

        /**
         * @param id
         * @param code
         * @param message
         */
        void error(int id, int code, String message);
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

    private final Gson gson = new Gson();
    private final String baseURL;
    private final Type type;
    private final ResultHandler resultHandler;

    public ApiRequest(String baseURL, Type type, ResultHandler resultHandler) {
        id = (++idCount) % (Integer.MAX_VALUE - 10);
        this.baseURL = baseURL;
        this.type = type;
        this.resultHandler = resultHandler;
    }

    @Override
    public void handle(String text) {
        Result result = gson.fromJson(text, Result.class);
        if (resultHandler != null) {
            if (result.getCode() == Result.SUCCESS) {
                resultHandler.handle(getId(), result);
            } else {
                resultHandler.error(getId(), result.getCode(), result.getMessage());

            }
        }
    }

    @Override
    public void error(int code) {
        if (resultHandler != null) {
            resultHandler.error(getId(), code, "net error!");
        }
    }

    /**
     * @description: TODO 请求
     * @author LiFucheng
     * @date 2022/3/31 18:11
     * @version 1.0
     */
    public void request(String api, Map<String, String> params) {
        request(api, null, params, null);
    }

    /**
     * @description: TODO 请求
     * @author LiFucheng
     * @date 2022/3/31 18:11
     * @version 1.0
     */
    public void request(String api, Map<String, String> headers, Map<String, String> params) {
        request(api, headers, params, null);
    }

    /**
     * @description: TODO 请求
     * @author LiFucheng
     * @date 2022/3/31 18:11
     * @version 1.0
     */
    public void request(String api, Map<String, String> params, Object body) {
        request(api, null, params, body);
    }

    /**
     * @param api    api名称
     * @param params api参数
     */
    public void request(String api, Map<String, String> headers, Map<String, String> params, Object body) {
        if (resultHandler != null) {
            resultHandler.start(getId());
        }
        HttpRunnable httpRunnable = new HttpRunnable(type.getType(), UrlFactory.appendParams(baseURL + api, params), this);
        httpRunnable.setHeaderParams(headers);
        if (body != null) {
            httpRunnable.setBody(gson.toJson(body));
        }
        new Thread(httpRunnable).start();
    }
}
