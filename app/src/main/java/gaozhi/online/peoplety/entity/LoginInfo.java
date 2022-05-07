package gaozhi.online.peoplety.entity;

/**
 * @author LiFucheng
 * @version 1.0
 * @description: TODO 登录信息 登录信息
 * @date 2022/4/13 19:31
 */
public class LoginInfo {
    private UserInfo userInfo;
    private String account;
    private String pass;
    private boolean authLogin;

    public UserInfo getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public boolean isAuthLogin() {
        return authLogin;
    }

    public void setAuthLogin(boolean authLogin) {
        this.authLogin = authLogin;
    }

    @Override
    public String toString() {
        return "LoginInfo{" +
                "userInfo=" + userInfo +
                ", account='" + account + '\'' +
                ", pass='" + pass + '\'' +
                ", authLogin=" + authLogin +
                '}';
    }
}
