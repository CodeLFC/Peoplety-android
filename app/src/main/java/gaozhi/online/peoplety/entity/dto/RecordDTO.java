package gaozhi.online.peoplety.entity.dto;

import com.github.pagehelper.PageInfo;

import gaozhi.online.peoplety.entity.Comment;
import gaozhi.online.peoplety.entity.Record;
import lombok.Data;

/**
 * @author LiFucheng
 * @version 1.0
 * @description: TODO 父子卷宗
 * @date 2022/5/14 9:39
 */
@Data
public class RecordDTO {
    public static final int CHILD_PAGE_SIZE = 3;
    public static final int COMMENT_PAGE_SIZE = 5;

    private Record record;
    private Record parent;
    //收藏数量
    private int favoriteNum;
    private PageInfo<Record> childPageInfo;
    private PageInfo<Comment> commentPageInfo;

    public static RecordDTO wrapRecord(Record record) {
        RecordDTO recordDTO = new RecordDTO();
        recordDTO.setRecord(record);
        return recordDTO;
    }
}
