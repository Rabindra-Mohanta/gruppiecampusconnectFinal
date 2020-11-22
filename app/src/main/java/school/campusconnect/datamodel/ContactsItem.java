package school.campusconnect.datamodel;

import android.os.Parcel;
import android.os.Parcelable;

public class ContactsItem implements Parcelable  {

    private String id;
    private String image;
    private String name;
    private String phoneNumber;
    private String designation;

    public String getId() {
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

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeString(id);
        dest.writeString(image);
        dest.writeString(name);
        dest.writeString(phoneNumber);
        dest.writeString(designation);

    }

    public ContactsItem(Parcel in) {
        this.id = in.readString();
        this.image = in.readString();
        this.name = in.readString();
        this.phoneNumber = in.readString();
        this.designation = in.readString();

    }

    public static final Parcelable.Creator<ContactsItem> CREATOR = new Parcelable.Creator<ContactsItem>() {

        @Override
        public ContactsItem createFromParcel(Parcel source) {
            return new ContactsItem(source);
        }

        @Override
        public ContactsItem[] newArray(int size) {
            return new ContactsItem[size];
        }
    };


    @Override
    public String toString() {
        return name;
    }
}
