package gaozhi.online.peoplety.im.io;

import gaozhi.online.base.im.core.LocalDataSender;
import gaozhi.online.peoplety.entity.Message;
import gaozhi.online.peoplety.im.MessageUtils;

/**
 * 消息发送者
 */
public abstract class IMSender extends LocalDataSender.SendCommonDataAsync {

    public IMSender(String dataContentWidthStr, String to_user_id) {
        super(dataContentWidthStr, to_user_id);
    }

    public IMSender(String dataContentWidthStr, String to_user_id, int type) {
        super(dataContentWidthStr, to_user_id, type);
    }

    public IMSender(String dataContentWidthStr, String to_user_id, String fingerPrint, int type) {
        super(dataContentWidthStr, to_user_id, fingerPrint, type);
    }

    public IMSender(Message message) {
        super(MessageUtils.toProtocol(message));
    }

    @Override
    protected void onPostExecute(Integer code) {
        if (code == 0) {
            onSuccess(MessageUtils.toMessage(p));
        } else {
            onFail(MessageUtils.toMessage(p));
        }
    }

    /**
     * 发送成功，不代表已送达
     * @param message
     */
    public abstract void onSuccess(Message message);

    /**
     * 发送失败 还没有生成ID
     * @param message
     */
    public abstract void onFail(Message message);
}
