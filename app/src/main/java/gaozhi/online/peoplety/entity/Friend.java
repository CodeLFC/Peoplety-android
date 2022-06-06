package gaozhi.online.peoplety.entity;

import gaozhi.online.peoplety.ui.widget.NoAnimatorRecyclerView;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import lombok.Data;

/**
 * @description: TODO 朋友
 * @author LiFucheng
 * @date 2022/5/2 10:34
 * @version 1.0
 */
@Data
public class Friend extends RealmObject implements NoAnimatorRecyclerView.BaseAdapter.BaseItem {
    @PrimaryKey
    private long id;
    private long userid;
    private long friendId;
    private String remark;
    private long time;

    @Override
    public long getItemId() {
        return time;
    }
}
