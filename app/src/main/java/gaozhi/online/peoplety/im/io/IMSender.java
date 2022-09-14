package gaozhi.online.peoplety.im.io;

import gaozhi.online.base.im.core.LocalDataSender;
import gaozhi.online.peoplety.entity.Message;
import gaozhi.online.peoplety.im.MessageUtils;

/**
 * 消息发送者
 */
public class IMSender extends LocalDataSender.SendCommonDataAsync {
    private OnIMSendListener onIMSendListener;

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
        super(MessageUtils.toCommonProtocol(message));
    }

    public OnIMSendListener getOnIMSendListener() {
        return onIMSendListener;
    }

    public void setOnIMSendListener(OnIMSendListener onIMSendListener) {
        this.onIMSendListener = onIMSendListener;
    }

    @Override
    protected void onPostExecute(Integer code) {
        if (onIMSendListener == null) return;
        if (code == 0) {
            onIMSendListener.onSuccess(MessageUtils.toMessage(p));
        } else {
            onIMSendListener.onFail(MessageUtils.toMessage(p));
        }
    }

    public interface OnIMSendListener {
        /**
         * 发送成功，不代表已送达
         *
         * @param message
         */
        void onSuccess(Message message);

        /**
         * 发送失败 还没有生成ID
         *
         * @param message
         */
        void onFail(Message message);
    }

}
