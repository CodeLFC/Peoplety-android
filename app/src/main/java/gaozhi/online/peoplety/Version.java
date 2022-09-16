package gaozhi.online.peoplety;

/**
 * 枚举所有的软件版本
 */
public enum Version{
    _1_0Beta(20220512,"1.0 beta","测试版本"),
    _1_1(20220816,"1.1","1.1版本"),
    _1_5(20220916,"1.5","1.5版本,增加消息推送系统");
    private int version;
    private String versionName;
    private String versionDescription;

    Version(int version, String versionName, String versionDescription) {
        this.version = version;
        this.versionName = versionName;
        this.versionDescription = versionDescription;
    }

    public int getVersion() {
        return version;
    }

    public String getVersionName() {
        return versionName;
    }

    public String getVersionDescription() {
        return versionDescription;
    }

    @Override
    public String toString() {
        return "Version{" +
                "version=" + version +
                ", versionName='" + versionName + '\'' +
                ", versionDescription='" + versionDescription + '\'' +
                '}';
    }
}
