# Peoplety-android (城市之声安卓版)（已开源）
# 一、简介
- [demo展示及下载体验网站，底部有GitHub开源地址链接](http://gaozhi.online)
- 后端接口开源地址（整理后开放）
- **城市之声是什么类型的软件？**
  城市之声是一个社区类应用，目前还处在开发阶段，仍然存在不少bug和不太合理的设计。但应用业务逻辑已经比较完整。
- **后端采用了什么技术？**
  后端采用微服务方式开发，微服务采用springcloud框架。由于我本人只有一台服务器，因此这台服务器承载了数据库，euraka注册中心，用户服务，社区服务。因为贫穷原因，可以认为后端在物理上是单体的，在逻辑上是微服务的。
- **为什么开发安卓版？**
  我的技术栈是JAVA后端，移动端的开发是临时学习的，由于本人只有安卓机，且安卓可以使用JAVA，因此选择了安卓作为业务的载体。
- **这个软件有什么模块和功能？**
    - 完整的用户体系
        - 登陆注册找回密码（短信验证）
        - 关注取关朋友逻辑
        - 个人主页
        - 扫码（存在问题）
    - 较为完整的UGC（用户生产内容）模块
        - 发布、查阅内容（腾讯云存储图片）
        - 收藏夹
- **这个软件还有什么工作可以做?**
    - 后端部分的消息推送部分没有设计。
    - 安卓端仍然存在不少问题和缺陷。
    - 后台管理系统的WEB端。
    - 除安卓端外的其他平台。
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
  一名98年的即将被社会毒打的年轻人。
- 技术栈
    - spring全家桶： spring、springBoot、springCloud (主要技术栈，熟练)
    - web前端（了解，能看懂，互联网毕竟是web的天下，完全不懂说不过去）
    - Android（略懂，作为后端技术的输出环境，能熟练使用JAVA版本（kotlin仅了解））
    - Qt（略懂，科研项目用，能熟练使用）
- 爱好
    - 跑步
    - 乒乓球
    - 米粉一个（就算是组装厂也是高级组装厂）
- 疑惑
    - 怎样才能坚持写出好的注释？
    - 怎样完美应用设计模式？
    - 算法果然是前人智慧的结晶，确实难，男上加男。
# 四、有兴趣的朋友可以在群内联系
## 1、QQ群：532685220
