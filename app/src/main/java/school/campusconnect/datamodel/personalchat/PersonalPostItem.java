package school.campusconnect.datamodel.personalchat;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.Gson;

/**
 * Created by frenzin04 on 2/6/2017.
 */

public class PersonalPostItem implements Parcelable {

    public String updatedAtTime;
    public String userId;
    public int postUnreadCount;
    public String name;
    public String image;
    public String phone;
    public boolean allowToPost;
    public boolean allowPostComment;
    public boolean provideSettings;

    public PersonalPostItem() {
    }

    protected PersonalPostItem(Parcel in) {
        updatedAtTime = in.readString();
        userId = in.readString();
        postUnreadCount = in.readInt();
        name = in.readString();
        image = in.readString();
        phone = in.readString();
        allowToPost = in.readByte() != 0;
        allowPostComment = in.readByte() != 0;
        provideSettings = in.readByte() != 0;
    }

    public static final Creator<PersonalPostItem> CREATOR = new Creator<PersonalPostItem>() {
        @Override
        public PersonalPostItem createFromParcel(Parcel in) {
            return new PersonalPostItem(in);
        }

        @Override
        public PersonalPostItem[] newArray(int size) {
            return new PersonalPostItem[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(updatedAtTime);
        dest.writeString(userId);
        dest.writeInt(postUnreadCount);
        dest.writeString(name);
        dest.writeString(image);
        dest.writeString(phone);
        dest.writeByte((byte) (allowToPost ? 1 : 0));
        dest.writeByte((byte) (allowPostComment ? 1 : 0));
        dest.writeByte((byte) (provideSettings ? 1 : 0));
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
