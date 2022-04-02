package school.campusconnect.datamodel;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

public class GroupItem implements Parcelable {

    @SerializedName(value = "id",alternate = "groupId")
    public String id;
    public String type;
    public int totalUsers;
    public int totalPostsCount;
    public int subBoothCount;
    public String subBoothName;
    public String subBoothId;
    public int totalCommentsCount;
    public String subCategory;
    @SerializedName(value = "name",alternate = "groupName")
    public String name;
    public boolean isPostShareAllowed;
    public boolean isAdminChangeAllowed;
    public boolean isAdmin;
    public boolean isPartyTaskForce;
    public boolean isDepartmentTaskForce;
    public boolean isAuthorizedUser;
    public boolean isBoothMember;
    public String image;
    public boolean isBoothWorker;
    public int groupPostUnreadCount;
    public String category="";
    public boolean canPost;
    public boolean allowPostAll;
    public boolean isPublic;
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

    public String constituencyName;
    public String categoryName;
    public int groupCount;
    public String boothId;
    public String boothName;
    public int boothCount;
    public boolean isBoothPresident;


    protected GroupItem(Parcel in) {
        id = in.readString();
        type = in.readString();
        subBoothName = in.readString();
        subBoothId = in.readString();
        totalUsers = in.readInt();
        totalPostsCount = in.readInt();
        subBoothCount = in.readInt();
        totalCommentsCount = in.readInt();
        subCategory = in.readString();
        name = in.readString();
        isPostShareAllowed = in.readByte() != 0;
        isAdminChangeAllowed = in.readByte() != 0;
        isAuthorizedUser= in.readByte() != 0;
        isAdmin = in.readByte() != 0;
        isPartyTaskForce = in.readByte() != 0;
        isBoothMember = in.readByte() != 0;
        isDepartmentTaskForce = in.readByte() != 0;
        image = in.readString();
        groupPostUnreadCount = in.readInt();
        category = in.readString();
        canPost = in.readByte() != 0;
        allowPostAll = in.readByte() != 0;
        adminPhone = in.readString();
        adminName = in.readString();
        adminId = in.readString();
        isPublic = in.readByte() != 0;
        allowedToAddUser = in.readByte() != 0;
        allowPostQuestion = in.readByte() != 0;
        createdBy = in.readString();
        shortDescription = in.readString();
        aboutGroup = in.readString();
        notificationUnseenCount = in.readInt();
        appVersion = in.readInt();
        constituencyName = in.readString();
        categoryName = in.readString();
        groupCount = in.readInt();
        boothId = in.readString();
        boothName = in.readString();
        boothCount = in.readInt();
        isBoothPresident = in.readByte() != 0;
        isBoothWorker = in.readByte() != 0;
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

    public GroupItem() {

    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(type);
        dest.writeString(subBoothName);
        dest.writeString(subBoothId);
        dest.writeInt(totalUsers);
        dest.writeInt(totalPostsCount);
        dest.writeInt(subBoothCount);
        dest.writeInt(totalCommentsCount);
        dest.writeString(subCategory);
        dest.writeString(name);
        dest.writeByte((byte) (isPostShareAllowed ? 1 : 0));
        dest.writeByte((byte) (isAuthorizedUser ? 1 : 0));
        dest.writeByte((byte) (isPublic ? 1 : 0));
        dest.writeByte((byte) (isAdminChangeAllowed ? 1 : 0));
        dest.writeByte((byte) (isAdmin ? 1 : 0));
        dest.writeByte((byte) (isPartyTaskForce ? 1 : 0));
        dest.writeByte((byte) (isDepartmentTaskForce ? 1 : 0));
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
        dest.writeInt(notificationUnseenCount);
        dest.writeInt(appVersion);
        dest.writeString(constituencyName);
        dest.writeString(categoryName);
        dest.writeInt(groupCount);
        dest.writeString(boothId);
        dest.writeString(boothName);
        dest.writeInt(boothCount);
        dest.writeInt((byte) (isBoothMember ? 1 : 0));
        dest.writeInt((byte) (isBoothPresident ? 1 : 0));
        dest.writeInt((byte) (isBoothWorker ? 1 : 0));
    }
}
