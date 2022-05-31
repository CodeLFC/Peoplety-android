package gaozhi.online.peoplety.ui.base;

import android.content.Context;
import android.widget.PopupWindow;

import gaozhi.online.base.ui.BasePopupWindow;
import io.realm.Realm;

/**
 * 带有数据库的
 */
public abstract class DBBasePopWindow extends BasePopupWindow{
    //db
    private Realm realm;

    public DBBasePopWindow(Context context, int resID, boolean fullScreen) {
        super(context, resID, fullScreen);
        setOnDismissListener(() -> realm.close());
    }

    @Override
    protected void initParam() {
        realm = Realm.getDefaultInstance();
    }

    public Realm getRealm() {
        return realm;
    }

}
