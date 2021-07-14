package promind.cleaner.app.core.data.model;

import android.os.Parcel;
import android.os.Parcelable;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class GroupItem implements Parcelable {
    public static final int TYPE_FILE = 0;
    public static final int TYPE_CACHE = 1;

    private String title;
    private long total;
    private boolean isCheck;
    private int type;
    private List<ChildItem> items = new ArrayList<>();

    public GroupItem() {
    }

    protected GroupItem(Parcel in) {
        title = in.readString();
        total = in.readLong();
        isCheck = in.readByte() != 0;
        type = in.readInt();
    }

    public static final Creator<GroupItem> CREATOR = new Creator<GroupItem>() {
        @Override
        public GroupItem createFromParcel(Parcel in) {
            return new GroupItem(in);
        }

        @Override
        public GroupItem[] newArray(int size) {
            return new GroupItem[size];
        }
    };

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @NotNull
    @Override
    public String toString() {
        return "GroupItem{" +
                "title='" + title + '\'' +
                ", total=" + total +
                ", isCheck=" + isCheck +
                ", type=" + type +
                ", items=" + items +
                '}';
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public boolean isCheck() {
        return isCheck;
    }

    public void setIsCheck(boolean isCheck) {
        this.isCheck = isCheck;
    }

    public List<ChildItem> getItems() {
        return items;
    }

    public void setItems(List<ChildItem> items) {
        this.items = items;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeLong(total);
        dest.writeByte((byte) (isCheck ? 1 : 0));
        dest.writeInt(type);
    }
}
