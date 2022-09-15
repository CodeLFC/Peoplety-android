package gaozhi.online.peoplety.im;

import net.x52im.mobileimsdk.protocol.Protocol;
import net.x52im.mobileimsdk.protocol.ProtocolFactory;
import net.x52im.mobileimsdk.protocol.ProtocolType;

import gaozhi.online.peoplety.entity.Message;

/**
 * @author http://gaozhi.online
 * @version 1.0
 * @description: TODO 消息协议转换
 * @date 2022/9/11 22:15
 */
public class MessageUtils {
    public static Message toMessage(Protocol protocal) {
        Message message = new Message();

        //消息唯一识别码
        message.setId(Long.parseLong(protocal.getFp()));

        //一级消息类型
        message.setType(protocal.getTypeu());
        //消息内容类型
        message.setTypeMsg(protocal.getTypeMsg());

        //消息来自
        message.setFromId(Long.parseLong(protocal.getFrom()));
        //消息发往
        message.setToId(Long.parseLong(protocal.getTo()));

        //消息内容
        message.setMsg(protocal.getDataContent());
        message.setRemark(protocal.getRemark());
        //消息时间
        message.setTime(protocal.getSm());
        return message;
    }
    /**创建普通的消息*/
    public static Protocol toCommonProtocol(Message message) {
        Protocol protocol = toProtocol(message);
        protocol.setType(ProtocolType.C.FROM_CLIENT_TYPE_OF_COMMON$DATA);
        return protocol;
    }

    public static Protocol toProtocol(Message message) {
        Protocol protocol = new Protocol();
        protocol.setQoS(true);
        //消息id
        protocol.setFp(String.valueOf(message.getId()));
        //消息小类
        protocol.setTypeMsg(message.getTypeMsg());
        //消息大类
        protocol.setTypeu(message.getType());

        //消息来自和发往
        protocol.setFrom(String.valueOf(message.getFromId()));
        protocol.setTo(String.valueOf(message.getToId()));

        //消息内容
        protocol.setDataContent(message.getMsg());
        protocol.setRemark(message.getRemark());
        //消息时间
        protocol.setSm(message.getTime());
        //消息时间
        return protocol;
    }
}
