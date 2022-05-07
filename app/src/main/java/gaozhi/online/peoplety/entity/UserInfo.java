package gaozhi.online.peoplety.entity;

/**
 * @author lfc
 * @title: UserInfo
 * @projectName huan
 * @description: TODO  用户基本资料
 * @date 2021/10/19 12:07
 * <p>
 * DDL 信息 ------------
 * <p>
 * CREATE TABLE `user_info` (
 * `id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户账号',
 * `head_url` varchar(2083) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '头像地址',
 * `nick` varchar(255) DEFAULT NULL COMMENT '昵称',
 * `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '备注',
 * `gender` varchar(6) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT 'male' COMMENT '性别（male/female）',
 * `birth` bigint DEFAULT '0' COMMENT '出生日期',
 * `gps` varchar(255) DEFAULT NULL COMMENT 'gps坐标',
 * `cell_phone` varchar(11) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '手机号',
 * `phone` varchar(255) DEFAULT NULL COMMENT '电话',
 * `wechat` varchar(255) DEFAULT NULL COMMENT '微信号',
 * `qq` varchar(11) DEFAULT NULL COMMENT 'qq号',
 * `email` varchar(320) DEFAULT NULL COMMENT '邮箱',
 * `create_time` bigint DEFAULT '0' COMMENT '信息创建时间',
 * `update_time` bigint DEFAULT '0' COMMENT '信息更新时间',
 * `ban_time` bigint DEFAULT '0' COMMENT '用户封禁到期时间',
 * `status` int DEFAULT '0' COMMENT '用户状态',
 * PRIMARY KEY (`id`)
 * ) ENGINE=MyISAM AUTO_INCREMENT=28 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
 */

public class UserInfo {

    public enum Gender {
        FEMALE("female", "女"),
        MALE("male", "男");

        private final String key;
        private final String description;

        Gender(String key, String description) {
            this.key = key;
            this.description = description;
        }

        public String getKey() {
            return key;
        }

        public String getDescription() {
            return description;
        }

        @Override
        public String toString() {
            return description;
        }

        public static Gender getGender(String key) {
            for (Gender e : Gender.values()) {
                if (e.getKey().equals(key)) {
                    return e;
                }
            }
            return null;
        }
    }

    public enum Status {
        CANCELLED(-1, "已注销"),
        NORMAL(0, "用户"),
        MANAGER(1, "管理员");
        private final int status;
        private final String description;

        Status(int status, String description) {
            this.status = status;
            this.description = description;
        }

        public int getStatus() {
            return status;
        }

        public String getDescription() {
            return description;
        }

        public static Status getStatus(int status) {
            for (Status e : Status.values()) {
                if (e.getStatus() == status) {
                    return e;
                }
            }
            return null;
        }
    }

    private long id;
    private String headUrl;
    private String nick;
    private String remark;
    private String gender;
    private long birth;
    private String gps;
    private String cellPhone;
    private String phone;
    private String wechat;
    private String qq;
    private String email;
    private String visible;
    private long createTime;
    private long updateTime;
    private long banTime;
    private int status;
    private int vip;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getHeadUrl() {
        return headUrl;
    }

    public void setHeadUrl(String headUrl) {
        this.headUrl = headUrl;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public long getBirth() {
        return birth;
    }

    public void setBirth(long birth) {
        this.birth = birth;
    }

    public String getGps() {
        return gps;
    }

    public void setGps(String gps) {
        this.gps = gps;
    }

    public String getCellPhone() {
        return cellPhone;
    }

    public void setCellPhone(String cellPhone) {
        this.cellPhone = cellPhone;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getWechat() {
        return wechat;
    }

    public void setWechat(String wechat) {
        this.wechat = wechat;
    }

    public String getQq() {
        return qq;
    }

    public void setQq(String qq) {
        this.qq = qq;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getVisible() {
        return visible;
    }

    public void setVisible(String visible) {
        this.visible = visible;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(long updateTime) {
        this.updateTime = updateTime;
    }

    public long getBanTime() {
        return banTime;
    }

    public void setBanTime(long banTime) {
        this.banTime = banTime;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getVip() {
        return vip;
    }

    public void setVip(int vip) {
        this.vip = vip;
    }

    @Override
    public String toString() {
        return "UserInfo{" +
                "id=" + id +
                ", headUrl='" + headUrl + '\'' +
                ", nick='" + nick + '\'' +
                ", remark='" + remark + '\'' +
                ", gender='" + gender + '\'' +
                ", birth=" + birth +
                ", gps='" + gps + '\'' +
                ", cellPhone='" + cellPhone + '\'' +
                ", phone='" + phone + '\'' +
                ", wechat='" + wechat + '\'' +
                ", qq='" + qq + '\'' +
                ", email='" + email + '\'' +
                ", visible='" + visible + '\'' +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                ", banTime=" + banTime +
                ", status=" + status +
                ", vip=" + vip +
                '}';
    }
}
