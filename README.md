# Peoplety-android (城市之声安卓版)（已开源）
# 一、简介
- [demo展示及下载体验网站，网站底部有GitHub开源地址链接](http://gaozhi.online)
- **城市之声是什么类型的软件？**
  城市之声是一个社区类应用，目前还处在开发阶段，仍然存在不少bug和不太合理的设计。但应用业务逻辑已经比较完整。
- **采用了什么技术？**
    - 后端使用阿里云RDS云数据库，业务逻辑采用微服务方式开发，微服务采用springCloud框架。由于我本人只有一台服务器，因此这台服务器承载了euraka注册中心，用户服务和社区内容服务。可以认为在物理上是单体的，在逻辑上是微服务的。QPS没有测过，云数据库和服务器都是最低配置的学习版本。
    - 移动端使用android，开发语言为java，数据库使用Realm的安卓版本，Realm是我用过的最好用的移动端数据库了，之前用过Sugar，但这个开源项目很久已经没有维护过了。

- **这个软件目前有什么模块和功能？**
    - 完整的用户体系，包括权限管理，身份验证
        - 登陆注册找回密码（短信验证）
        - 关注取关朋友逻辑
        - 个人主页
        - 扫码（相机调用存在问题）
    - 较为完整的UGC（用户生产内容）模块
        - 发布、查阅内容（腾讯云存储图片）
        - 收藏夹
    - 即将开发完成的消息推送模块
- **这个软件还有什么工作可以做?**
    - 后端部分的消息推送部分没有设计。
    - 安卓端仍然存在不少问题和缺陷。
    - 后台管理系统的WEB端。
# 二、第三方依赖
``` 
    //图片Glide工具
    implementation 'com.github.bumptech.glide:glide:4.12.0'
    //json工具
    implementation 'com.google.code.gson:gson:2.8.9'
    //photoview 图片预览工具
    implementation 'com.github.chrisbanes:PhotoView:2.3.0'
    //zxing二维码工具
    implementation 'com.google.zxing:core:3.4.1'
    //图片选择工具
    implementation 'io.github.lucksiege:pictureselector:v2.7.3-rc08'
    //pageHelper分页插件
    implementation 'com.github.pagehelper:pagehelper:5.3.0'
    //lombok代码工具
    compileOnly 'org.projectlombok:lombok:1.18.22'
    annotationProcessor 'org.projectlombok:lombok:1.18.22'

    testCompileOnly 'org.projectlombok:lombok:1.18.22'
    testAnnotationProcessor 'org.projectlombok:lombok:1.18.22'

    //腾讯云存储上传工具
    implementation ('com.qcloud.cos:cos-android:5.7.+')
```
# 三、关于开发者
- 个人简介
  一名98年的即将打工的研究生。
- 技术栈
    - spring全家桶： spring、springBoot、springCloud (主要技术栈，熟练)
    - Android（略懂，作为后端技术的输出环境，能熟练使用JAVA版本（kotlin仅了解））
    - Qt（略懂，科研项目用，平时也用来开发一些桌面端的小工具，能熟练使用）
    - web前端（了解，能看懂，互联网毕竟是web的互联网，完全不懂说不过去）
- 爱好
    - 跑步
    - 乒乓球
    - 电子产品