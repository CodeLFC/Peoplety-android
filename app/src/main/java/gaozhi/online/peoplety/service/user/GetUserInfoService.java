package gaozhi.online.peoplety.service.user;

import android.util.Log;

import gaozhi.online.base.net.Result;
import gaozhi.online.base.net.http.ApiRequest;
import gaozhi.online.peoplety.entity.Status;
import gaozhi.online.peoplety.entity.Token;
import gaozhi.online.peoplety.entity.UserInfo;
import gaozhi.online.peoplety.entity.dto.UserDTO;
import gaozhi.online.peoplety.service.BaseApiRequest;
import gaozhi.online.peoplety.service.NetConfig;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * @author LiFucheng
 * @version 1.0
 * @description: TODO 获取用户信息
 * @date 2022/4/2 19:06
 */
public class GetUserInfoService extends BaseApiRequest<UserDTO> {

    public GetUserInfoService(OnDataListener<UserDTO> resultHandler) {
        super(NetConfig.userBaseURL, Type.GET);
        setDataListener(resultHandler);
    }

    public void request(Token token, long friendID) {
        Map<String, String> headers = new HashMap<>();
        headers.put("token", getGson().toJson(token));
        Map<String, String> params = new HashMap<>();
        params.put("userId", "" + friendID);
        request("get/user_info", headers, params);
    }

    @Override
    public UserDTO initLocalData(Map<String, String> headers, Map<String, String> params, Object body) {
        long id = Long.parseLong(params.get("userId"));
        UserInfo userInfo = getRealm().where(UserInfo.class).equalTo("id", id).findFirst();
        UserDTO userDTO = new UserDTO();
        userDTO.setUserInfo(userInfo);
        if (userInfo == null)
            return userDTO;
        userDTO.setStatus(getRealm().where(Status.class).equalTo("id", userInfo.getStatus()).findFirst());
        return userDTO;
    }

    @Override
    public void getNetData(Result result, Consumer<UserDTO> consumer) {
        UserDTO userDTO = getGson().fromJson(result.getData(), UserDTO.class);
        getRealm().executeTransactionAsync(realm -> {
            //更新数据库中的用户信息
            //realm.copyToRealmOrUpdate(userDTO.getUserInfo());
            UserDTO temp = realm.where(UserDTO.class).equalTo("account", userDTO.getUserInfo().getPhone()).findFirst();
            if (temp == null) {
                temp = realm.where(UserDTO.class).equalTo("account", Long.toString(userDTO.getUserInfo().getId())).findFirst();
            }
            if (temp != null) {
                temp.setAttentionNum(userDTO.getAttentionNum());
                temp.setFanNum(userDTO.getFanNum());
                realm.copyToRealmOrUpdate(temp);
            }
        }, () -> {//success
            userDTO.setStatus(getRealm().where(Status.class).equalTo("id", userDTO.getUserInfo().getStatus()).findFirst());
            consumer.accept(userDTO);
        });
    }
}
