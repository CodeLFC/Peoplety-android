package gaozhi.online.peoplety.entity;


import gaozhi.online.peoplety.util.StringUtil;
import lombok.Data;

/**
 * 验证码
 */
@Data
public class VerifyCode {
    public enum NotifyMethod {
        SMS("sms"),
        EMAIL("email");
        private final String method;

        NotifyMethod(String method) {
            this.method = method;
        }

        public String getMethod() {
            return method;
        }

        @Override
        public String toString() {
            return "NotifyMethod{" +
                    "method='" + method + '\'' +
                    '}';
        }

        public static NotifyMethod getMethod(String method) {
            for (NotifyMethod e : NotifyMethod.values()) {
                if (StringUtil.equals(e.getMethod(), method)) {
                    return e;
                }
            }
            return null;
        }
    }

    public enum CodeTemplate {
        LOGIN("login", "【西风吹瘦马】登录验证码"),
        UPDATE_INFO("update_info", "【西风吹瘦马】修改资料验证码"),
        REGISTER("register", "【西风吹瘦马】注册验证码"),
        FORGET_PASS("forget_pass", "【西风吹瘦马】找回密码验证码");
        private final String type;
        private final String description;

        CodeTemplate(String type, String description) {
            this.type = type;
            this.description = description;
        }

        public String getType() {
            return type;
        }

        public String getDescription() {
            return description;
        }

        @Override
        public String toString() {
            return "CodeTemplate{" +
                    "type='" + type + '\'' +
                    ", description='" + description + '\'' +
                    '}';
        }

        public static CodeTemplate getTemplate(String type) {
            for (CodeTemplate template : CodeTemplate.values()) {
                if (StringUtil.equals(template.type, type)) {
                    return template;
                }
            }
            return null;
        }

    }

    private String phone;
    private String code;
    private String type;
    private long validateTime;
    private int dailyCount;

}
