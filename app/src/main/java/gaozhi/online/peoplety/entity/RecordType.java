package gaozhi.online.peoplety.entity;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Objects;

import gaozhi.online.peoplety.ui.widget.NoAnimatorRecyclerView;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import lombok.Data;

/**
 * @author LiFucheng
 * @version 1.0
 * @description: TODO 记录类型
 * @date 2022/5/14 11:05
 */
@Data
public class RecordType extends RealmObject implements Parcelable, NoAnimatorRecyclerView.BaseAdapter.BaseItem {
    @PrimaryKey
    private int id;
    private int parentId;
    private String name;
    private String description;
    private boolean enable;
    private int grade;
    private boolean minimum;
    //是否被选中
    private boolean isSelected;

    public RecordType() {

    }

    protected RecordType(Parcel in) {
        id = in.readInt();
        parentId = in.readInt();
        name = in.readString();
        description = in.readString();
        enable = in.readByte() != 0;
        grade = in.readInt();
        minimum = in.readByte() != 0;
        isSelected = in.readByte() != 0;
    }

    public static final Creator<RecordType> CREATOR = new Creator<RecordType>() {
        @Override
        public RecordType createFromParcel(Parcel in) {
            return new RecordType(in);
        }

        @Override
        public RecordType[] newArray(int size) {
            return new RecordType[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeInt(parentId);
        dest.writeString(name);
        dest.writeString(description);
        dest.writeByte((byte) (enable ? 1 : 0));
        dest.writeInt(grade);
        dest.writeByte((byte) (minimum ? 1 : 0));
        dest.writeByte((byte) (isSelected ? 1 : 0));
    }

    @Override
    public long getItemId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RecordType that = (RecordType) o;
        return id == that.id && parentId == that.parentId && enable == that.enable && grade == that.grade && minimum == that.minimum && isSelected == that.isSelected && Objects.equals(name, that.name) && Objects.equals(description, that.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, parentId, name, description, enable, grade, minimum, isSelected);
    }
}
