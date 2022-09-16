package gaozhi.online.peoplety.im.io;

import net.x52im.mobileimsdk.protocol.s.PKickoutInfo;

import java.util.List;

import gaozhi.online.peoplety.entity.Message;

/**
 * 消息接收者
 */
public interface IMReceiver {
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
    default boolean onSuccess(long msgId){
        return false;
    }

    /**
     * 发送失败，ID经过再次转发后会失效，但可以留在本地，作为发送失败的聊天记录
     *
     * @param message
     */
    default boolean onFail(List<Message> message){
        return false;
    }

    /**
     * 错误
     * @param errorCode
     * @param errorMsg
     */
    default boolean onError(int errorCode, String errorMsg){
        return false;
    }

    /**
     * 踢出
     * @param kickOutInfo
     */
    default boolean onKickOut(PKickoutInfo kickOutInfo){
        return false;
    }
}
