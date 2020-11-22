package school.campusconnect.datamodel.teamdiscussion;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by frenzin04 on 1/12/2017.
 */
public class TeamListItemModel implements Parcelable{

    public String id;
    public String createdById;
    public String name;
    public String phone;
    public String image;
    public String profileCompletion;
    public String email;
    public String gender;
    public String dob;
    public String qualification;
    public String occupation;
    public MyTeamAddress address;
    public int leadCount;
    public boolean isPost;
    public String[] otherLeads;
    public int teamCount;
    public int teamPostUnreadCount;

    public TeamListItemModel() {
    }

    public TeamListItemModel(Parcel in) {
        id = in.readString();
        createdById = in.readString();
        name = in.readString();
        phone = in.readString();
        image = in.readString();
        profileCompletion = in.readString();
        email = in.readString();
        gender = in.readString();
        dob = in.readString();
        qualification = in.readString();
        occupation = in.readString();
        leadCount = in.readInt();
        isPost = in.readByte() != 0;
        otherLeads = in.createStringArray();
        teamCount = in.readInt();
        teamPostUnreadCount = in.readInt();
    }

    public static final Creator<TeamListItemModel> CREATOR = new Creator<TeamListItemModel>() {
        @Override
        public TeamListItemModel createFromParcel(Parcel in) {
            return new TeamListItemModel(in);
        }

        @Override
        public TeamListItemModel[] newArray(int size) {
            return new TeamListItemModel[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(createdById);
        dest.writeString(name);
        dest.writeString(phone);
        dest.writeString(image);
        dest.writeString(profileCompletion);
        dest.writeString(email);
        dest.writeString(gender);
        dest.writeString(dob);
        dest.writeString(qualification);
        dest.writeString(occupation);
        dest.writeInt(leadCount);
        dest.writeByte((byte) (isPost ? 1 : 0));
        dest.writeStringArray(otherLeads);
        dest.writeInt(teamCount);
        dest.writeInt(teamPostUnreadCount);
    }
}
