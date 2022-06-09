package gaozhi.online.peoplety.entity;

import java.util.Objects;

import gaozhi.online.peoplety.ui.widget.NoAnimatorRecyclerView;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author LiFucheng
 * @version 1.0
 * @description: TODO 收藏夹
 * @date 2022/5/14 11:21
 */
@Data
public class Favorite extends RealmObject implements NoAnimatorRecyclerView.BaseAdapter.BaseItem {
    @PrimaryKey
    private long id;
    private long userid;
    private String name;
    private String description;
    private long time;
    private boolean visible;

    @Override
    public long getItemId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Favorite favorite = (Favorite) o;
        return id == favorite.id && userid == favorite.userid && time == favorite.time && visible == favorite.visible && Objects.equals(name, favorite.name) && Objects.equals(description, favorite.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, userid, name, description, time, visible);
    }
}
