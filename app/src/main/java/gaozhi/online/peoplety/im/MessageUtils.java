package gaozhi.online.peoplety.im;

import net.x52im.mobileimsdk.protocal.Protocal;
import net.x52im.mobileimsdk.protocal.ProtocalFactory;
import net.x52im.mobileimsdk.protocal.ProtocalType;

import gaozhi.online.peoplety.entity.Message;

/**
 * @author http://gaozhi.online
 * @version 1.0
 * @description: TODO 消息协议转换
 * @date 2022/9/11 22:15
 */
public class MessageUtils {
    public static Message toMessage(Protocal protocal) {
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
    public static Protocal toCommonProtocol(Message message) {
        Protocal protocal = toProtocol(message);
        protocal.setType(ProtocalType.C.FROM_CLIENT_TYPE_OF_COMMON$DATA);
        return protocal;
    }

    public static Protocal toProtocol(Message message) {
        Protocal protocal = new Protocal();
        protocal.setQoS(true);
        //消息id
        protocal.setFp(String.valueOf(message.getId()));
        //消息小类
        protocal.setTypeMsg(message.getTypeMsg());
        //消息大类
        protocal.setTypeu(message.getType());

        //消息来自和发往
        protocal.setFrom(String.valueOf(message.getFromId()));
        protocal.setTo(String.valueOf(message.getToId()));

        //消息内容
        protocal.setDataContent(message.getMsg());
        protocal.setRemark(message.getRemark());
        //消息时间
        protocal.setSm(message.getTime());
        //消息时间
        return protocal;
    }
}
