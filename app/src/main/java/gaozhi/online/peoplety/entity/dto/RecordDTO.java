package gaozhi.online.peoplety.entity.dto;

import gaozhi.online.peoplety.entity.Favorite;
import gaozhi.online.peoplety.entity.Record;
import gaozhi.online.peoplety.entity.RecordType;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import lombok.Data;

/**
 * @author LiFucheng
 * @version 1.0
 * @description: TODO 父子卷宗
 * @date 2022/5/14 9:39
 */
@Data
public class RecordDTO extends RealmObject {
    @PrimaryKey
    private long id;
    private Record record;
    private Record parent;
    //收藏数量
    private int favoriteNum;
    //所在的收藏夹
    private Favorite favorite;
    //子数量
    private long childNum;
    //评论数量
    private long commentNum;

    private RecordType recordType;
}
