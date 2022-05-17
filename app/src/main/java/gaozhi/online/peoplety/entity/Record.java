package gaozhi.online.peoplety.entity;

import android.os.Parcel;
import android.os.Parcelable;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import lombok.Data;

/**
 * @author LiFucheng
 * @version 1.0
 * @description: TODO 卷宗
 * @date 2022/5/14 9:35
 */
@Data
public class Record  extends RealmObject implements Parcelable {
     @PrimaryKey
     private long id;
     private long parentId;
     private long userid;
     private int areaId;
     private int recordTypeId;
     private boolean enable;
     private String title;
     private String description;
     private String content;
     private String imgs;
     private String url;
     private long time;
     private String ip;
     private boolean top;

     public Record(){

     }
     protected Record(Parcel in) {
          id = in.readLong();
          parentId = in.readLong();
          userid = in.readLong();
          areaId = in.readInt();
          recordTypeId = in.readInt();
          enable = in.readByte() != 0;
          title = in.readString();
          description = in.readString();
          content = in.readString();
          imgs = in.readString();
          url = in.readString();
          time = in.readLong();
          ip = in.readString();
          top = in.readByte() != 0;
     }

     public static final Creator<Record> CREATOR = new Creator<Record>() {
          @Override
          public Record createFromParcel(Parcel in) {
               return new Record(in);
          }

          @Override
          public Record[] newArray(int size) {
               return new Record[size];
          }
     };

     @Override
     public int describeContents() {
          return 0;
     }

     @Override
     public void writeToParcel(Parcel dest, int flags) {
          dest.writeLong(id);
          dest.writeLong(parentId);
          dest.writeLong(userid);
          dest.writeInt(areaId);
          dest.writeInt(recordTypeId);
          dest.writeByte((byte) (enable ? 1 : 0));
          dest.writeString(title);
          dest.writeString(description);
          dest.writeString(content);
          dest.writeString(imgs);
          dest.writeString(url);
          dest.writeLong(time);
          dest.writeString(ip);
          dest.writeByte((byte) (top ? 1 : 0));
     }
}
