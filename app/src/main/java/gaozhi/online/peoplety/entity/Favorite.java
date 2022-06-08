package gaozhi.online.peoplety.entity;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author LiFucheng
 * @version 1.0
 * @description: TODO 收藏夹
 * @date 2022/5/14 11:21
 */
@Data
public class Favorite extends RealmObject {
    @PrimaryKey
    private long id;
    private long userid;
    private String name;
    private String description;
    private long time;
    private boolean visible;
}
