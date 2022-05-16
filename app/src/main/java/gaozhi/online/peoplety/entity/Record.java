package gaozhi.online.peoplety.entity;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import lombok.Data;

/**
 * @author LiFucheng
 * @version 1.0
 * @description: TODO 卷宗
 * @date 2022/5/14 9:35
 */
@Data
public class Record  extends RealmObject {
     @PrimaryKey
     private long id;
     private long parentId;
     private long userid;
     private int areaId;
     private int recordTypeId;
     private boolean enable;
     private String title;
     private String description;
     private String content;
     private String imgs;
     private String url;
     private long time;
     private String ip;
     private boolean top;
}
