package school.campusconnect.datamodel;

import android.os.Parcel;
import android.os.Parcelable;


public class ContactListItem implements Parcelable {

    private String userId;
    private String name;
    private String phone;
    private String image;


    public ContactListItem(String id, String name, String phone, String image) {
        this.userId = id;
        this.name = name;
        this.phone = phone;
        this.image = image;
    }

    public String  getId() {
        return userId;
    }

    public void setId(String id) {
        this.userId = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }


    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }


    public static Creator<ContactListItem> getCREATOR() {
        return CREATOR;
    }

    public ContactListItem() {

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeString(userId);
        dest.writeString(image);
        dest.writeString(name);
        dest.writeString(phone);

    }

    public ContactListItem(Parcel in) {
        this.userId = in.readString();
        this.image = in.readString();
        this.name = in.readString();
        this.phone = in.readString();
    }

    public static final Creator<ContactListItem> CREATOR = new Creator<ContactListItem>() {

        @Override
        public ContactListItem createFromParcel(Parcel source) {
            return new ContactListItem(source);
        }

        @Override
        public ContactListItem[] newArray(int size) {
            return new ContactListItem[size];
        }
    };


    @Override
    public String toString() {
        return name;
    }
}
