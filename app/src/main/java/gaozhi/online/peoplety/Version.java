package gaozhi.online.peoplety;

/**
 * 枚举所有的软件版本
 */
public enum Version{
    _1_0Beta(20220512,"1.0 beta","测试版本");
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
