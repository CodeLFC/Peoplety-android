package gaozhi.online.peoplety.service.user;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import gaozhi.online.base.net.Result;
import gaozhi.online.base.net.http.ApiRequest;
import gaozhi.online.peoplety.entity.Message;
import gaozhi.online.peoplety.entity.Token;
import gaozhi.online.peoplety.service.NetConfig;

/**
 * 发送消息
 */
public class PostMessageService extends ApiRequest<Message> {
    public PostMessageService(OnDataListener<Message> message) {
        super(NetConfig.userBaseURL, Type.POST);
        setDataListener(message);
    }

    public void request(Token token, Message message) {
        if (token.getUserid() == message.getToId()) {
            return;
        }
        Map<String, String> headers = new HashMap<>();
        headers.put("token", getGson().toJson(token));
        Map<String, String> params = new HashMap<>();
        message.setFromId(token.getUserid());
        request("post/message", headers, params, message);
    }

    /**
     * 初始化本地数据
     *
     * @param headers
     * @param params
     * @param body
     */
    @Override
    public Message initLocalData(Map<String, String> headers, Map<String, String> params, Object body) {
        return null;
    }

    /**
     * @param result   结果
     * @param consumer
     */
    @Override
    public void getNetData(Result result, Consumer<Message> consumer) {
        Message message = getGson().fromJson(result.getData(), Message.class);
        consumer.accept(message);
    }
}
