package gaozhi.online.peoplety.ui.activity.chat.chat.cells;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import gaozhi.online.peoplety.R;
import gaozhi.online.peoplety.entity.Message;
import gaozhi.online.peoplety.entity.UserInfo;
import gaozhi.online.peoplety.entity.dto.UserDTO;
import gaozhi.online.peoplety.util.DateTimeUtil;
import gaozhi.online.peoplety.util.GlideUtil;


/**
 * 文本消息
 */
public class TextRightCell extends ChatBaseCell {
    public static final int TYPE_STRING_RIGHT = 1;

    //文本消息
    public TextRightCell(Context context, Message message, UserDTO user, UserInfo friendInfo) {
        super(context, message, user, friendInfo);
    }


    @Override
    public int getItemType() {
        return TYPE_STRING_RIGHT;
    }

    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new TextMsgRightViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_msg_text_right, parent, false));
    }

    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        TextMsgRightViewHolder textMsgRightViewHolder = (TextMsgRightViewHolder) holder;
        GlideUtil.loadRoundRectangleImage(context, user.getUserInfo().getHeadUrl(), R.drawable.default_img_head, textMsgRightViewHolder.imageView);
        textMsgRightViewHolder.textViewContent.setText(getData().getMsg());
        textMsgRightViewHolder.textViewTime.setText(DateTimeUtil.getChatTime(data.getTime()));
        // Log.i("onBindViewHolder","head:"+user.getHeadUrl()+" time:"+mData.getTime());
    }

    //接收消息的文本
    public static class TextMsgRightViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;
        private TextView textViewContent;
        private TextView textViewTime;

        public TextMsgRightViewHolder(View itemView) {
            super(itemView);
            //绑定控件
            imageView = itemView.findViewById(R.id.chat_item_right_img_head);
            textViewContent = itemView.findViewById(R.id.chat_item_right_text_content);
            textViewTime = itemView.findViewById(R.id.chat_item_right_text_time);
        }
    }
}
