package school.campusconnect.datamodel;

import android.os.Parcel;
import android.os.Parcelable;

public class AddressItem implements Parcelable {
    public String line1;
    public String line2;
    public String district;
    public String state;
    public String country;
    public String pin;
    public String city;

    @Override
    public int describeContents() {
        return 0;
    }

    public AddressItem(Parcel in) {
        this.line1 = in.readString();
        this.line2 = in.readString();
        this.district = in.readString();
        this.state = in.readString();
        this.country = in.readString();
        this.pin = in.readString();
        this.city = in.readString();
    }

    public AddressItem() {

    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(line1);
        dest.writeString(line2);
        dest.writeString(district);
        dest.writeString(state);
        dest.writeString(country);
        dest.writeString(pin);
        dest.writeString(city);
    }

    public static final Parcelable.Creator<AddressItem> CREATOR = new Parcelable.Creator<AddressItem>() {

        @Override
        public AddressItem createFromParcel(Parcel source) {
            return new AddressItem(source);
        }

        @Override
        public AddressItem[] newArray(int size) {
            return new AddressItem[size];
        }
    };

}
