package gaozhi.online.peoplety.entity;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import lombok.Data;

/**
 * @author lfc
 * @title: UserInfo
 * @projectName huan
 * @description: TODO  用户基本资料
 * @date 2021/10/19 12:07
 * <p>
 * DDL 信息 ------------
 * <p>
 *    CREATE TABLE `user_info` (
 *   `id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户账号',
 *   `head_url` varchar(2083) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '头像地址',
 *   `nick` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '群众天眼新用户' COMMENT '昵称',
 *   `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT '富强、民主、文明、和谐；自由、平等、公正、法治；爱国、敬业、诚信、友善。' COMMENT '备注',
 *   `gender` varchar(6) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT 'male' COMMENT '性别（male/female/other）',
 *   `birth` bigint DEFAULT '0' COMMENT '出生日期',
 *   `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '电话',
 *   `wechat` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '微信号',
 *   `qq` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT 'qq号',
 *   `email` varchar(320) DEFAULT NULL COMMENT '邮箱',
 *   `visible` varchar(1000) DEFAULT NULL COMMENT '各个属性是否可见的json串',
 *   `ip` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT 'ip归属地',
 *   `status` int DEFAULT '0' COMMENT '用户身份',
 *   `vip` int DEFAULT '0' COMMENT 'vip',
 *   `ban_time` bigint DEFAULT '0' COMMENT '用户封禁到期时间',
 *   `update_time` bigint DEFAULT '0' COMMENT '信息更新时间',
 *   `create_time` bigint DEFAULT '0' COMMENT '信息创建时间',
 *   PRIMARY KEY (`id`)
 * ) ENGINE=MyISAM AUTO_INCREMENT=2147483652 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
 */
@Data
public class UserInfo extends RealmObject {
    //------------------------------------------------------------------性别
    public enum Gender {
        FEMALE("female", "女"),
        MALE("male", "男"),
        OTHER("other","其他");

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
            return OTHER;
        }
    }

    public enum Status {
        NORMAL(1, "用户"),
        MANAGER(2, "管理员");
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
    @PrimaryKey
    private long id;
    private String headUrl;
    private String nick;
    private String remark;
    private String gender;
    private long birth;
    private String phone;
    private String wechat;
    private String qq;
    private String email;
    private String visible;

    private String ip;
    private int status;
    private int vip;
    private long banTime;
    private long updateTime;
    private long createTime;
    private boolean enable;
}
