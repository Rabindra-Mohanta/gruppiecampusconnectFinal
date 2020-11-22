package school.campusconnect.datamodel;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.Gson;

public class GroupItem implements Parcelable {

    public String id;
    public String type;
    public int totalUsers;
    public int totalPostsCount;
    public int totalCommentsCount;
    public String subCategory;
    public String name;
    public boolean isPostShareAllowed;
    public boolean isAdminChangeAllowed;
    public boolean isAdmin;
    public String image;
    public int groupPostUnreadCount;
    public String category="";
    public boolean canPost;
    public boolean allowPostAll;
    public String adminPhone;
    public String adminName;
    public String adminId;
    public boolean allowedToAddUser;
    public boolean allowPostQuestion;
    public String createdBy;
    public String shortDescription;
    public String aboutGroup;
    public int notificationUnseenCount;
    public int appVersion;


    protected GroupItem(Parcel in) {
        id = in.readString();
        type = in.readString();
        totalUsers = in.readInt();
        totalPostsCount = in.readInt();
        totalCommentsCount = in.readInt();
        subCategory = in.readString();
        name = in.readString();
        isPostShareAllowed = in.readByte() != 0;
        isAdminChangeAllowed = in.readByte() != 0;
        isAdmin = in.readByte() != 0;
        image = in.readString();
        groupPostUnreadCount = in.readInt();
        category = in.readString();
        canPost = in.readByte() != 0;
        allowPostAll = in.readByte() != 0;
        adminPhone = in.readString();
        adminName = in.readString();
        adminId = in.readString();
        allowedToAddUser = in.readByte() != 0;
        allowPostQuestion = in.readByte() != 0;
        createdBy = in.readString();
        shortDescription = in.readString();
        aboutGroup = in.readString();
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

    public int getTotalCommentsCount() {
        return totalCommentsCount;
    }

    public void setTotalCommentsCount(int totalCommentsCount) {
        this.totalCommentsCount = totalCommentsCount;
    }



    public int getTotalPostsCount() {
        return totalPostsCount;
    }

    public void setTotalPostsCount(int totalPostsCount) {
        this.totalPostsCount = totalPostsCount;
    }



    public String getGroupId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeString(id);
        dest.writeString(type);
        dest.writeInt(totalUsers);
        dest.writeInt(totalPostsCount);
        dest.writeInt(totalCommentsCount);
        dest.writeString(subCategory);
        dest.writeString(name);
        dest.writeByte((byte) (isPostShareAllowed ? 1 : 0));
        dest.writeByte((byte) (isAdminChangeAllowed ? 1 : 0));
        dest.writeByte((byte) (isAdmin ? 1 : 0));
        dest.writeString(image);
        dest.writeInt(groupPostUnreadCount);
        dest.writeString(category);
        dest.writeByte((byte) (canPost ? 1 : 0));
        dest.writeByte((byte) (allowPostAll ? 1 : 0));
        dest.writeString(adminPhone);
        dest.writeString(adminName);
        dest.writeString(adminId);
        dest.writeByte((byte) (allowedToAddUser ? 1 : 0));
        dest.writeByte((byte) (allowPostQuestion ? 1 : 0));
        dest.writeString(createdBy);
        dest.writeString(shortDescription);
        dest.writeString(aboutGroup);
    }

    public GroupItem() {

    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
