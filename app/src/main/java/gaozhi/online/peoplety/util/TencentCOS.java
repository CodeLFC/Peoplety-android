package gaozhi.online.peoplety.util;

import android.content.Context;

import com.tencent.cos.xml.CosXmlService;
import com.tencent.cos.xml.CosXmlServiceConfig;
import com.tencent.cos.xml.listener.CosXmlProgressListener;
import com.tencent.cos.xml.listener.CosXmlResultListener;
import com.tencent.cos.xml.model.object.PutObjectRequest;
import com.tencent.qcloud.core.auth.BasicLifecycleCredentialProvider;
import com.tencent.qcloud.core.auth.QCloudLifecycleCredentials;
import com.tencent.qcloud.core.auth.SessionQCloudCredentials;

import lombok.Data;

/**
 * 对象存储
 */
public class TencentCOS {
    /**
     * 地区
     */
    private static final String region = "ap-nanjing";
    private static final String bucket = "peoplety-1258188886";
    private static final String linkPre = "https://peoplety-1258188886.cos.ap-nanjing.myqcloud.com/";
    /**
     * 对象存储返回
     */
    @Data
    public static class CosResponse {
        private Credentials credentials;
        private String requestId;
        private String expiration;
        private long startTime;
        private long expiredTime;

        @Data
        public static class Credentials {
            private String tmpSecretId;
            private String tmpSecretKey;
            private String sessionToken;
        }
    }

    /**
     * 创建证书
     */
    private static class ServerCredentialProvider extends BasicLifecycleCredentialProvider {

        private final CosResponse cosResponse;

        public ServerCredentialProvider(CosResponse cosResponse) {
            this.cosResponse = cosResponse;
        }

        @Override
        protected QCloudLifecycleCredentials fetchNewCredentials() {
            // 首先从您的临时密钥服务器获取包含了密钥信息的响应
            // 然后解析响应，获取临时密钥信
            // 临时密钥 SecretId
            // 临时密钥 SecretKey
            // 临时密钥 Token
            // 临时密钥有效截止时间戳，单位是秒
            //建议返回服务器时间作为签名的开始时间，避免由于用户手机本地时间偏差过大导致请求过期
            // 返回服务器时间作为签名的起始时间
            // 临时密钥有效起始时间，单位是秒

            // 最后返回临时密钥信息对象
            return new SessionQCloudCredentials(cosResponse.getCredentials().getTmpSecretId(), cosResponse.getCredentials().getTmpSecretKey(),
                    cosResponse.getCredentials().getSessionToken(), cosResponse.getStartTime(), cosResponse.getExpiredTime());
        }
    }

    private final CosXmlService cosXmlService;

    /**
     * 构造并初始化对象上传工具
     *
     * @param context
     * @param cosResponse
     */
    public TencentCOS(Context context, CosResponse cosResponse) {
        CosXmlServiceConfig serviceConfig = new CosXmlServiceConfig.Builder()
                .setRegion(region)
                .isHttps(true) // 使用 HTTPS 请求，默认为 HTTP 请求
                .builder();
        cosXmlService = new CosXmlService(context, serviceConfig,
                new ServerCredentialProvider(cosResponse));
    }

    /**
     * @param cosPath 腾讯云对象存储 COS 中的对象需具有合法的对象键，对象键（ObjectKey）是对象在存储桶中的唯一标识。
     *                例如：在对象的访问地址examplebucket-1250000000.cos.ap-guangzhou.myqcloud.com/folder/picture.jpg 中，对象键为folder/picture.jpg。
     * @param data    数据
     */
    private void putObjectWithBytes(String cosPath, byte[] data, CosXmlProgressListener cosXmlProgressListener, CosXmlResultListener cosXmlResultListener) {
        PutObjectRequest putObjectRequest = new PutObjectRequest(bucket, cosPath, data);
        putObjectRequest.setProgressListener(cosXmlProgressListener);
        cosXmlService.putObjectAsync(putObjectRequest, cosXmlResultListener);
    }

    /**
     * 简单上传文件
     */
    private void putObjectWithPath(String cosPath, String srcPath, CosXmlProgressListener cosXmlProgressListener, CosXmlResultListener cosXmlResultListener) {
        PutObjectRequest putObjectRequest = new PutObjectRequest(bucket, cosPath, srcPath);
        putObjectRequest.setProgressListener(cosXmlProgressListener);
        cosXmlService.putObjectAsync(putObjectRequest, cosXmlResultListener);
    }

    public String putHeadImageWithPath(String imageName, String srcPath, CosXmlProgressListener cosXmlProgressListener, CosXmlResultListener cosXmlResultListener) {
        String cosPath = "head/" + imageName;
        putObjectWithPath(cosPath, srcPath, cosXmlProgressListener, cosXmlResultListener);
        return linkPre+cosPath;
    }

    public String putRecordImageWithPath(String imageName, String srcPath, CosXmlProgressListener cosXmlProgressListener, CosXmlResultListener cosXmlResultListener) {
        String cosPath = "record/" + imageName;
        putObjectWithPath(cosPath, srcPath, cosXmlProgressListener, cosXmlResultListener);
        return linkPre+cosPath;
    }
}
