package gaozhi.online.peoplety.ui.activity.chat.chat;

import android.content.Context;
import android.util.Log;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.LinkedList;
import java.util.List;

import gaozhi.online.peoplety.entity.Message;
import gaozhi.online.peoplety.entity.UserInfo;
import gaozhi.online.peoplety.entity.dto.UserDTO;
import gaozhi.online.peoplety.ui.activity.chat.chat.cells.ChatBaseCell;
import gaozhi.online.peoplety.ui.activity.chat.chat.cells.TextLeftCell;
import gaozhi.online.peoplety.ui.activity.chat.chat.cells.TextRightCell;
import gaozhi.online.peoplety.util.DateTimeUtil;

/**
 * 消息的列表
 */
public class ChatMsgRecyclerAdapter extends RecyclerView.Adapter {

    private List<ChatBaseCell> historyMsg = new LinkedList<>();
    private Context context;
    private UserDTO user;
    private UserInfo friendInfo;

    public ChatMsgRecyclerAdapter(Context context, UserDTO user, UserInfo friendInfo) {
        this.context = context;
        this.user = user;
        this.friendInfo = friendInfo;
    }


    /**
     * 添加消息记录
     *
     * @param message
     */
    public void addMessage(Message message) {
        Log.i("addMessage", "有消息添加进来:" + message.getType());
        switch (Message.TypeMsg.getType(message.getTypeMsg())) {
            case STRING:
                if (message.getFromId() == user.getUserInfo().getId()) {//我发的
                    //Log.i("addMessage","显示我发的文本消息");
                    //add(new TextRightCell(context, message, user, friendInfo), bottom);
                } else {//朋友发的
                    //Log.i("addMessage","显示朋友发的文本消息");
                   // add(new TextLeftCell(context, message, user, friendInfo), bottom);
                }
                break;
            default:
                return;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        for (ChatBaseCell chatRVBaseCell : historyMsg) {
            if (chatRVBaseCell.getItemType() == viewType) {
               // return chatRVBaseCell.onCreateViewHolder(parent, viewType);
            }
        }
        throw new RuntimeException("wrong viewType");
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
       // historyMsg.get(position).onBindViewHolder(holder, position);
    }

    @Override
    public int getItemCount() {
        return historyMsg.size();
    }

    @Override
    public int getItemViewType(int position) {
        return historyMsg.get(position).getItemType();
    }

    //查询的上一次时间
    public long lastStartTime() {
        if (historyMsg.size() == 0) {
            return System.currentTimeMillis();
        }
        long time = historyMsg.get(0).getTime();
        Log.i(getClass().getSimpleName(), "查询聊天记录的时间点：" + DateTimeUtil.getChatTime(time));
        return time;
    }
}
