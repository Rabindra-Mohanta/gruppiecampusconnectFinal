package school.campusconnect.datamodel.teamdiscussion;

import android.os.Parcel;
import android.os.Parcelable;

import school.campusconnect.datamodel.BaseResponse;

/**
 * Created by frenzin04 on 1/12/2017.
 */
public class MyTeamAddress extends BaseResponse implements Parcelable{

    public String line1;
    public String line2;
    public String district;
    public String state;
    public String country;
    public String pin;

    public MyTeamAddress() {
    }

    protected MyTeamAddress(Parcel in) {
        line1 = in.readString();
        line2 = in.readString();
        district = in.readString();
        state = in.readString();
        country = in.readString();
        pin = in.readString();
    }

    public static final Creator<MyTeamAddress> CREATOR = new Creator<MyTeamAddress>() {
        @Override
        public MyTeamAddress createFromParcel(Parcel in) {
            return new MyTeamAddress(in);
        }

        @Override
        public MyTeamAddress[] newArray(int size) {
            return new MyTeamAddress[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(line1);
        dest.writeString(line2);
        dest.writeString(district);
        dest.writeString(state);
        dest.writeString(country);
        dest.writeString(pin);
    }
}
