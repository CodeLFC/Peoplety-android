/*
 * Copyright (C) 2022  即时通讯网(52im.net) & Jack Jiang.
 * The MobileIMSDK_TCP (MobileIMSDK v6.x TCP版) Project. 
 * All rights reserved.
 * 
 * > Github地址：https://github.com/JackJiang2011/MobileIMSDK
 * > 文档地址：  http://www.52im.net/forum-89-1.html
 * > 技术社区：  http://www.52im.net/
 * > 技术交流群：185926912 (http://www.52im.net/topic-qqgroup.html)
 * > 作者公众号：“即时通讯技术圈】”，欢迎关注！
 * > 联系作者：  http://www.52im.net/thread-2792-1-1.html
 *  
 * "即时通讯网(52im.net) - 即时通讯开发者社区!" 推荐开源工程。
 * 
 * MBThreadPoolExecutor.java at 2022-7-28 17:24:48, code by Jack Jiang.
 */
package gaozhi.online.base.im.utils;


import gaozhi.online.base.asynchronization.GlobalExecutor;
import gaozhi.online.peoplety.PeopletyApplication;

/**
 *   线程池
 */
public class MBThreadPoolExecutor {
    private static final String TAG = MBThreadPoolExecutor.class.getSimpleName();
    private static GlobalExecutor globalExecutor = PeopletyApplication.getGlobalExecutor();
    public static void runInBackground(Runnable runnable) {
        globalExecutor.executeInBackground(runnable);
    }

    public static void runOnMainThread(Runnable r) {
        globalExecutor.executeInUiThread(r);
    }

    public static void runOnMainThread(Runnable r, long delayMillis) {
        globalExecutor.executeInUiThread(r,delayMillis);
    }

    public static void runInBackground(final Runnable runnable, long delayMillis) {

    }

}
