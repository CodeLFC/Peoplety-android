package gaozhi.online.peoplety.ui.activity.personal;

import android.content.Context;
import android.view.View;

import gaozhi.online.base.net.http.DataHelper;
import gaozhi.online.peoplety.entity.Friend;
import gaozhi.online.peoplety.entity.Token;
import gaozhi.online.peoplety.service.friend.UpdateFriendService;
import gaozhi.online.peoplety.ui.util.pop.EditTextPopWindow;

/**
 * 编辑备注
 */
public class EditFriendRemarkPopWindow extends EditTextPopWindow implements View.OnClickListener {
    private Friend friend;
    private Token token;
    //service
    private final UpdateFriendService updateFriendService = new UpdateFriendService(null);

    public EditFriendRemarkPopWindow(Context context) {
        super(context, true);
        getEditUrl().setVisibility(View.GONE);
        getBtnSend().setOnClickListener(this);
    }

    public void showPopWindow(View parent, Token token, Friend friend, DataHelper.OnDataListener<Friend> onDataListener) {
        this.token = token;
        this.friend = friend;
        showPopupWindow(parent);
        getEditContent().setText(friend.getRemark());
        updateFriendService.setDataListener(onDataListener);
    }

    @Override
    public void onClick(View v) {
        friend.setRemark(getEditContent().getText().toString());
        updateFriendService.request(token, friend);
    }
}
