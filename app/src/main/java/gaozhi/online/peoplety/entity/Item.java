package gaozhi.online.peoplety.entity;

import gaozhi.online.peoplety.ui.widget.NoAnimatorRecyclerView;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import lombok.Data;

/**
 * @author LiFucheng
 * @version 1.0
 * @description: TODO 收藏卷宗
 * @date 2022/5/14 11:21
 */
@Data
public class Item extends RealmObject implements NoAnimatorRecyclerView.BaseAdapter.BaseItem {
    @PrimaryKey
    private long id;
    private long favoriteId;
    private long recordId;
    private long time;

    @Override
    public long getItemId() {
        return id;
    }
}