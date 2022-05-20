package gaozhi.online.peoplety.entity.dto;


import gaozhi.online.peoplety.entity.Area;
import gaozhi.online.peoplety.entity.Status;
import gaozhi.online.peoplety.entity.Token;
import gaozhi.online.peoplety.entity.UserInfo;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import lombok.Data;
import lombok.Getter;

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
    //粉丝数量
    private int fanNum;
    //关注数量
    private int attentionNum;
    //是否是当前登陆用户
    private boolean current;
    //资源有效时间
    private long resourceValidateTime;

    //选择的地区
    private Area area;
    //用户的身份
    private Status status;
}
