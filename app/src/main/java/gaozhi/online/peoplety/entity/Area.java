package gaozhi.online.peoplety.entity;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import lombok.Data;

/**
 * @author LiFucheng
 * @version 1.0
 * @description: TODO 地区
 * @date 2022/5/14 9:37
 */
@Data
public class Area  extends RealmObject {
    @PrimaryKey
    private int id;
    private int parentId;
    private int postCode;
    private String name;
    private String description;
    private String url;
    private boolean minimum;
}