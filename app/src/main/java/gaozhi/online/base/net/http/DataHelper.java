package gaozhi.online.base.net.http;

import java.util.Map;

import gaozhi.online.base.net.Result;
import io.realm.Realm;

/**
 * result转 data
 *
 * @param <T>
 */
public interface DataHelper<T> {
    /**
     * 初始化本地数据
     */
    T initLocalData(Map<String, String> headers, Map<String, String> params, Object body);

    /**
     * @param result 结果
     */
    T getNetData(Result result);


    /**
     * 数据监听
     *
     * @param <T>
     */
    interface OnDataListener<T> {
        void start(int id);

        /**
         * 是否是本地数据
         *
         * @param id
         * @param data
         * @param local
         */
        default void handle(int id, T data, boolean local) {
            if (!local) {
                handle(id, data);
            }
        }

        default void handle(int id, T data) {
        }

        void error(int id, int code, String message, String data);
    }
}
