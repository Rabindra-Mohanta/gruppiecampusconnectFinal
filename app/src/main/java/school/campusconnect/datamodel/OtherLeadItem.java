package school.campusconnect.datamodel;

import android.os.Parcel;
import android.os.Parcelable;

public class OtherLeadItem implements Parcelable {


    private String id;
    private String name;
    private String phone;
    private String email;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public OtherLeadItem() {

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(phone);
        dest.writeString(email);

    }

    public OtherLeadItem(Parcel in) {
        this.id = in.readString();
        this.name = in.readString();
        this.phone = in.readString();
        this.email = in.readString();

    }

    public static final Parcelable.Creator<OtherLeadItem> CREATOR = new Parcelable.Creator<OtherLeadItem>() {

        @Override
        public OtherLeadItem createFromParcel(Parcel source) {
            return new OtherLeadItem(source);
        }

        @Override
        public OtherLeadItem[] newArray(int size) {
            return new OtherLeadItem[size];
        }
    };

    @Override
    public String toString() {
        return name;
    }
}
