package gaozhi.online.peoplety.ui.base;

import android.content.Context;
import android.widget.PopupWindow;

import gaozhi.online.base.ui.BasePopupWindow;
import io.realm.Realm;

/**
 * 带有数据库的
 */
public abstract class DBBasePopWindow extends BasePopupWindow{

    public DBBasePopWindow(Context context, int resID, boolean fullScreen) {
        super(context, resID, fullScreen);
    }

    @Override
    protected void initParam() {
    }

    public Realm getRealm() {
        return Realm.getInstance(Realm.getDefaultConfiguration());
    }

}
