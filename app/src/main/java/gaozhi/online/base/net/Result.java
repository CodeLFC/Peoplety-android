package gaozhi.online.base.net;

/**
 * @description(结果)
 * @author: gaozhi.online
 * @createDate: 2021/3/4 0004
 * @version: 1.0
 */
public class Result {
    public static final int NET_ERROR = -1;
    public static final int SUCCESS = 200;
    int code;
    String message;
    String data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "Result{" +
                "code=" + code +
                ", message='" + message + '\'' +
                ", data='" + data + '\'' +
                '}';
    }
}
