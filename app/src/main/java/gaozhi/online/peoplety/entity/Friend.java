package gaozhi.online.peoplety.entity;

import lombok.Data;

/**
 * @description: TODO 朋友
 * @author LiFucheng
 * @date 2022/5/2 10:34
 * @version 1.0
 */
@Data
public class Friend {
    private long id;
    private long userid;
    private long friendId;
    private String remark;
    private long time;

}
