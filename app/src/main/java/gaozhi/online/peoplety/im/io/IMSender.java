package gaozhi.online.peoplety.im.io;

import android.util.Log;

import gaozhi.online.base.im.core.LocalDataSender;
import gaozhi.online.base.net.Result;
import gaozhi.online.base.net.http.DataHelper;
import gaozhi.online.peoplety.entity.Message;
import gaozhi.online.peoplety.entity.Token;
import gaozhi.online.peoplety.im.MessageUtils;
import gaozhi.online.peoplety.service.msg.GetMessageIdService;

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

    /**
     * 发送消息
     */
    public void send() {
        new GetMessageIdService(new DataHelper.OnDataListener<>() {
            @Override
            public void handle(int id, Result data) {
                p.setFp(data.getData());
                execute();
            }

            @Override
            public void error(int id, int code, String message, String data) {
                if(onIMSendListener!=null){
                    onIMSendListener.onFail(MessageUtils.toMessage(p));
                }
            }
        }).request();
    }

    public OnIMSendListener getOnIMSendListener() {
        return onIMSendListener;
    }

    public void setOnIMSendListener(OnIMSendListener onIMSendListener) {
        this.onIMSendListener = onIMSendListener;
    }

    @Override
    protected void onPostExecute(Integer code) {
        if (onIMSendListener == null) {
            Log.i(getClass().getName(), "发送消息结果：" + code + ":" + p.getFp());
            return;
        }
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
