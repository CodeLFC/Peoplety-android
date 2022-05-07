package gaozhi.online.peoplety.entity;

/**
 * @author LiFucheng
 * @version 1.0
 * @description: TODO
 * @date 2022/4/10 13:55
 */

public class Friend {
    private long id;
    private long userid;
    private long friendId;
    private String remark;
    private long time;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getUserid() {
        return userid;
    }

    public void setUserid(long userid) {
        this.userid = userid;
    }

    public long getFriendId() {
        return friendId;
    }

    public void setFriendId(long friendId) {
        this.friendId = friendId;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "Friend{" +
                "id=" + id +
                ", userid=" + userid +
                ", friendId=" + friendId +
                ", remark='" + remark + '\'' +
                ", time=" + time +
                '}';
    }
}