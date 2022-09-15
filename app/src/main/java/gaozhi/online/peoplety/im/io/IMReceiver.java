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
    void onReceive(Message message);

    /**
     * 消息已成功送达
     *
     * @param msgId
     */
    void onSuccess(long msgId);

    /**
     * 发送失败，ID经过再次转发后会失效，但可以留在本地，作为发送失败的聊天记录
     *
     * @param message
     */
    void onFail(List<Message> message);

    /**
     * 错误
     * @param errorCode
     * @param errorMsg
     */
    void onError(int errorCode, String errorMsg);

    /**
     * 踢出
     * @param kickOutInfo
     */
    void onKickOut(PKickoutInfo kickOutInfo);
}
