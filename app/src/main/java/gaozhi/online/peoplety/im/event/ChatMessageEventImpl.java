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
 * ChatMessageEventImpl.java at 2022-7-28 17:17:23, code by Jack Jiang.
 */
package gaozhi.online.peoplety.im.event;

import android.util.Log;

import net.x52im.mobileimsdk.protocol.ErrorCode;
import net.x52im.mobileimsdk.protocol.Protocol;


import java.util.Iterator;
import java.util.Map;

import gaozhi.online.base.im.event.ChatMessageEvent;
import gaozhi.online.peoplety.PeopletyApplication;
import gaozhi.online.peoplety.entity.Message;
import gaozhi.online.peoplety.im.IMClient;
import gaozhi.online.peoplety.im.MessageUtils;
import gaozhi.online.peoplety.im.io.IMReceiver;

/**
 * 与IM服务器的数据交互事件在此ChatTransDataEvent子类中实现即可。
 *
 * @author Jack Jiang(http://www.52im.net/thread-2792-1-1.html)
 * @version 1.1
 */
public class ChatMessageEventImpl implements ChatMessageEvent {
    private final static String TAG = ChatMessageEventImpl.class.getSimpleName();

    /**
     * 收到普通消息的回调事件通知。
     * <br>应用层可以将此消息进一步按自已的IM协议进行定义，从而实现完整的即时通信软件逻辑。
     *
     * @param protocol 当该消息
     * @see <a href="http://docs.52im.net/extend/docs/api/mobileimsdk/server_netty/net/openmob/mobileimsdk/server/protocal/Protocal.html" target="_blank">Protocal</a>
     */

    @Override
    public void onReceiveMessage(Protocol protocol) {
        Log.d(TAG, "收到消息：" + protocol.getDataContent());
        Message message = MessageUtils.toMessage(protocol);
        Iterator<IMReceiver> entryIterator = IMClient.getInstance(PeopletyApplication.getContext()).iteratorIMReceiver();
        while (entryIterator.hasNext()) {
            IMReceiver value = entryIterator.next();
            boolean res = value.onReceive(message);
            if (res){
                break;
            }
        }
    }

    /**
     * 服务端反馈的出错信息回调事件通知。
     *
     * @param errorCode 错误码，定义在常量表 ErrorCode.ForS 类中
     * @param errorMsg  描述错误内容的文本信息
     * @see <a href="http://docs.52im.net/extend/docs/api/mobileimsdk/server/net/openmob/mobileimsdk/server/protocal/ErrorCode.ForS.html">ErrorCode.ForS类</a>
     */
    @Override
    public void onErrorResponse(int errorCode, String errorMsg) {
        Log.d(TAG, "【DEBUG_UI】收到服务端错误消息，errorCode=" + errorCode + ", errorMsg=" + errorMsg);
        if (errorCode == ErrorCode.ForS.RESPONSE_FOR_UNLOGIN) {
            ;//this.mainGUI.showIMInfo_brightred("服务端会话已失效，自动登陆/重连将启动! ("+errorCode+")");
        } else {
            //"Server反馈错误码：" + errorCode + ",errorMsg=" + errorMsg
        }
        Iterator<IMReceiver> entryIterator = IMClient.getInstance(PeopletyApplication.getContext()).iteratorIMReceiver();
        while (entryIterator.hasNext()) {
            IMReceiver value = entryIterator.next();
            boolean res = value.onError(errorCode, errorMsg);
            if (res){
                break;
            }
        }
    }
}
