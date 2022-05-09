package gaozhi.online.peoplety.entity;

import lombok.Data;

/**
 * @author lfc
 * @title: UserAuth
 * @projectName huan
 * @description: TODO 用户登录权限
 * @date 2021/10/19 13:57
 * CREATE TABLE `user_auth` (
 *   `id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户权限表主键',
 *   `userid` bigint NOT NULL COMMENT '用户id标识',
 *   `type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT 'phone' COMMENT '登录类型',
 *   `account` varchar(255) DEFAULT NULL COMMENT '账号',
 *   `pass` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '凭证',
 *   `create_time` bigint DEFAULT '0' COMMENT '创建时间',
 *   `update_time` bigint DEFAULT '0' COMMENT '更新时间',
 *   PRIMARY KEY (`id`)
 * ) ENGINE=MyISAM AUTO_INCREMENT=19 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
 *
 */
@Data
public class UserAuth {
    /**
     * @description: TODO 账户类型
     * @author LiFucheng
     * @date 2021/10/22 23:37
     * @version 1.0
     */
    public enum AccountType{
        ID("id"),
        PHONE("phone")
        ;

        private String type;

        AccountType(String type){
            this.type=type;
        }

        public String getType() {
            return type;
        }

        public static AccountType getType(String type){
            for(AccountType accountType: AccountType.values()){
                if(accountType.type.equals(type)){
                    return accountType;
                }
            }
            return null;
        }
    }
    private long userid;
    private String type;
    private String account;
    private String pass;
    private long createTime;
    private long updateTime;

}
