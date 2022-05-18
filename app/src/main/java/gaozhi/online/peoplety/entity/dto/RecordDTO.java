package gaozhi.online.peoplety.entity.dto;

import com.github.pagehelper.PageInfo;

import gaozhi.online.peoplety.entity.Comment;
import gaozhi.online.peoplety.entity.Record;
import io.realm.Realm;
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
public class RecordDTO {
    private Record record;
    private Record parent;
    //收藏数量
    private int favoriteNum;
    private PageInfo<Record> childPageInfo;
    private PageInfo<Comment> commentPageInfo;

    public static RecordDTO wrap2RecordDTO(Record record, Realm realm) {
        RecordDTO recordDTO = new RecordDTO();
        recordDTO.setRecord(record);
        recordDTO.setParent(realm.where(Record.class).equalTo("id", record.getParentId()).findFirst());
        recordDTO.setChildPageInfo(new PageInfo<>(realm.where(Record.class).equalTo("parentId", record.getId()).findAll()));
        recordDTO.setCommentPageInfo(new PageInfo<>(realm.where(Comment.class).equalTo("recordId", record.getId()).findAll()));
        return recordDTO;
    }

    public static void save2DB(RecordDTO recordDTO, Realm realm) {
        realm.delete(Record.class);
        realm.delete(Comment.class);
        realm.copyToRealmOrUpdate(recordDTO.getRecord());
        if (recordDTO.getParent() != null) {//空指针异常
            realm.copyToRealmOrUpdate(recordDTO.getParent());
        }
        if (recordDTO.getCommentPageInfo() != null) {
            for (Comment comment : recordDTO.getCommentPageInfo().getList()) {
                realm.copyToRealmOrUpdate(comment);
            }
        }
        if (recordDTO.getChildPageInfo() != null) {
            for (Record record : recordDTO.getChildPageInfo().getList()) {
                realm.copyToRealmOrUpdate(record);
            }
        }
    }
}
