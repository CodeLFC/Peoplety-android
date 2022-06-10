package gaozhi.online.peoplety.entity;

import android.os.Parcel;
import android.os.Parcelable;

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
public class Favorite extends RealmObject implements Parcelable, NoAnimatorRecyclerView.BaseAdapter.BaseItem {
    @PrimaryKey
    private long id;
    private long userid;
    private String name;
    private String description;
    private long time;
    private boolean visible;

    protected Favorite(Parcel in) {
        id = in.readLong();
        userid = in.readLong();
        name = in.readString();
        description = in.readString();
        time = in.readLong();
        visible = in.readByte() != 0;
    }

    public static final Creator<Favorite> CREATOR = new Creator<Favorite>() {
        @Override
        public Favorite createFromParcel(Parcel in) {
            return new Favorite(in);
        }

        @Override
        public Favorite[] newArray(int size) {
            return new Favorite[size];
        }
    };

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeLong(userid);
        dest.writeString(name);
        dest.writeString(description);
        dest.writeLong(time);
        dest.writeByte((byte) (visible ? 1 : 0));
    }
}
