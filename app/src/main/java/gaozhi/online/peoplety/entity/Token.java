package gaozhi.online.peoplety.entity;

/**
 * @author lfc
 * @title: Token
 * @projectName huan
 * @description: TODO 权限验证
 * @date 2021/10/19 18:10
 * CREATE TABLE `token` (
 * `userid` bigint NOT NULL COMMENT '用户账号',
 * `device` int DEFAULT NULL COMMENT '设备类型码',
 * `token` varchar(100) DEFAULT NULL COMMENT 'token校验码',
 * `validate_time` bigint DEFAULT NULL COMMENT '有效截至时间',
 * PRIMARY KEY (`token`)
 * ) ENGINE=MyISAM DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
 */
public class Token {
    public enum Device {
        MOBILE(0),
        COMPUTER(1);
        private final int device;

        Device(int device) {
            this.device = device;
        }

        public int getDevice() {
            return device;
        }

        public static Device getDevice(int device) {
            for (Device e : Device.values()) {
                if (e.getDevice() == device) {
                    return e;
                }
            }
            return null;
        }
    }

    private long userid;
    private int device;
    private String token;
    private long validateTime;

    public long getUserid() {
        return userid;
    }

    public void setUserid(long userid) {
        this.userid = userid;
    }

    public int getDevice() {
        return device;
    }

    public void setDevice(int device) {
        this.device = device;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public long getValidateTime() {
        return validateTime;
    }

    public void setValidateTime(long validateTime) {
        this.validateTime = validateTime;
    }

    @Override
    public String toString() {
        return "Token{" +
                "userid=" + userid +
                ", device=" + device +
                ", token='" + token + '\'' +
                ", validateTime=" + validateTime +
                '}';
    }
}
