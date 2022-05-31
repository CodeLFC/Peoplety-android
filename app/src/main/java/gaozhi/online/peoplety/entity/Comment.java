package gaozhi.online.peoplety.entity;

import gaozhi.online.peoplety.ui.widget.NoAnimatorRecyclerView;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import lombok.Data;

/**
 * @author LiFucheng
 * @version 1.0
 * @description: TODO 评论
 * @date 2022/5/14 9:56
 */
@Data
public class Comment  extends RealmObject implements NoAnimatorRecyclerView.BaseAdapter.BaseItem {
    @PrimaryKey
    private long id;
    private long userid;
    private long recordId;
    private String content;
    private String url;
    private long time;
    private String ip;

    @Override
    public long getItemId() {
        return id;
    }

    @Override
    public int compare(NoAnimatorRecyclerView.BaseAdapter.BaseItem item) {
        return (int) (item.getItemId() - getItemId());
    }
}
