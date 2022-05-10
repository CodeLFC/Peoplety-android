package gaozhi.online.peoplety.entity.dto;


import gaozhi.online.peoplety.entity.Token;
import gaozhi.online.peoplety.entity.UserInfo;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import lombok.Data;

/**
 * 用户信息
 */
@Data
public class UserDTO extends RealmObject {
    @PrimaryKey
    private String account;
    private UserInfo userInfo;
    private Token token;
    private String pass;
    private long time;
    //是否是当前登陆用户
    private boolean current;
}
