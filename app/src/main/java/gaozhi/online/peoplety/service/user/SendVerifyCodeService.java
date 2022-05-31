package gaozhi.online.peoplety.service.user;
import gaozhi.online.base.net.Result;
import gaozhi.online.base.net.http.ApiRequest;
import gaozhi.online.peoplety.entity.VerifyCode;
import gaozhi.online.peoplety.service.NetConfig;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * 发送验证码
 */
public class SendVerifyCodeService extends ApiRequest<Result> {
    public SendVerifyCodeService(OnDataListener<Result> resultHandler) {
        super(NetConfig.userBaseURL, Type.POST);
        setDataListener(resultHandler);
    }

    public void request(VerifyCode.NotifyMethod method, VerifyCode.CodeTemplate type, String cell_phone){
        Map<String, String> params=new HashMap<>();
        params.put("phone",cell_phone);
        params.put("type",type.getType());
        params.put("method",method.getMethod());
        request("post/send_verify_code",params);
    }

    @Override
    public Result initLocalData(Map<String, String> headers, Map<String, String> params, Object body) {
        return null;
    }

    @Override
    public void getNetData(Result result, Consumer<Result> consumer) {
        consumer.accept(result);
    }
}
