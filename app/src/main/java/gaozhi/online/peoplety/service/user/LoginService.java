package gaozhi.online.peoplety.service.user;

import gaozhi.online.base.net.Result;
import gaozhi.online.peoplety.entity.UserAuth;
import gaozhi.online.peoplety.entity.dto.UserDTO;
import gaozhi.online.peoplety.service.BaseApiRequest;
import gaozhi.online.peoplety.service.NetConfig;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * 登录方法
 */
public class LoginService extends BaseApiRequest<UserDTO> {
    public LoginService(OnDataListener<UserDTO> resultHandler) {
        super(NetConfig.userBaseURL, Type.POST);
        setDataListener(resultHandler);
    }

    /**
     * @param type    登录方式 ---
     * @param account 账号
     * @param pass    密码
     * @param device  设备类型 UserDTO 中的常量
     */
    public void request(UserAuth.AccountType type, String account, String pass, int device) {
        Map<String, String> params = new HashMap<>();
        params.put("account", account);
        params.put("pass", pass);
        params.put("device", "" + device);
        params.put("type", type.getType());
        request("post/login", params);
    }

    @Override
    public UserDTO initLocalData(Map<String, String> headers, Map<String, String> params, Object body) {
        return null;
    }

    @Override
    public void getNetData(Result result, Consumer<UserDTO>consumer) {
        consumer.accept(getGson().fromJson(result.getData(), UserDTO.class));
    }
}
