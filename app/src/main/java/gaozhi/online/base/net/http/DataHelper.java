package gaozhi.online.base.net.http;

import java.util.Map;
import java.util.function.Consumer;

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
     * 结果
     * @param result
     * @return
     */
    // T getNetData(Result result);

    /**
     * @param result 结果
     */
    void getNetData(Result result, Consumer<T> consumer);

    /**
     * 仅获取本地数据
     * @return
     */
    default boolean onlyLocal() {
        return false;
    }

    /**
     * 数据监听
     *
     * @param <T>
     */
    interface OnDataListener<T> {
        default void start(int id) {
        }

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

        default void error(int id, int code, String message, String data) {
        }
    }
}
