package gaozhi.online.peoplety.im.io;

import net.x52im.mobileimsdk.protocol.s.PKickoutInfo;

import java.util.List;

import gaozhi.online.peoplety.entity.Message;

/**
 * 消息接收者
 */
public interface IMReceiver extends Comparable<IMReceiver> {
    /**
     * 收到消息
     *
     * @param message
     */
    boolean onReceive(Message message);

    /**
     * 消息已成功送达
     *
     * @param msgId
     */
    default boolean onSuccess(long msgId) {
        return false;
    }

    /**
     * 发送失败，ID经过再次转发后会失效，但可以留在本地，作为发送失败的聊天记录
     *
     * @param message
     */
    default boolean onFail(List<Message> message) {
        return false;
    }

    /**
     * 错误
     *
     * @param errorCode
     * @param errorMsg
     */
    default boolean onError(int errorCode, String errorMsg) {
        return false;
    }

    /**
     * 踢出
     *
     * @param kickOutInfo
     */
    default boolean onKickOut(PKickoutInfo kickOutInfo) {
        return false;
    }

    /**
     * 优先级 ，处理的顺序
     * @return
     */
    default int order() {
        return 0;
    }

    /**
     * @param o the object to be compared.
     * @return a negative integer, zero, or a positive integer as this object
     * is less than, equal to, or greater than the specified object.
     * @throws NullPointerException if the specified object is null
     * @throws ClassCastException   if the specified object's type prevents it
     *                              from being compared to this object.
     */
    @Override
    default int compareTo(IMReceiver o) {
        return order() - o.order();
    }
}
