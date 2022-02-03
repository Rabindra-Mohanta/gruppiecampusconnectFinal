package school.campusconnect.datamodel.teamdiscussion;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import school.campusconnect.datamodel.BaseResponse;

/**
 * Created by frenzin04 on 1/12/2017.
 */
public class MyTeamData extends BaseResponse {

    public String teamId;
    public int postUnseenCount;
    public String phone;

    @SerializedName(value = "name",alternate = {"teamName"})
    @Expose
    public String name;

    public int members;
    public String boothNumber;

    @SerializedName(value = "image",alternate = {"teamImage"})
    @Expose
    public String image;
    public String groupId;
    public boolean canAddUser;

    @SerializedName(value = "allowTeamPostCommentAll",alternate = {"allowedToAddTeamPostComment"})
    @Expose
    public boolean allowTeamPostCommentAll;
    public boolean allowTeamPostAll;
    public boolean isTeamAdmin;
    public boolean isClass;
    public String teamType="";
    public boolean enableGps;
    public boolean enableAttendance;
    public String type="";
    public String category="";
    public String role="";
    public int count;

    // for view discussion
    public boolean allowedToAddTeamPost;
    public boolean leaveRequest;

    public TeamDetails details;

    @SerializedName("boothPresidentName")
    @Expose
    public String boothPresidentName;

    @SerializedName("countryCode")
    @Expose
    public String countryCode;


    @Override
    public String toString() {
        return new Gson().toJson(this);
    }


    public class TeamDetails{
        public String userId;
        public String teamName;
        public String teamImage;
        public String teamId;
        public String studentCount;
        public String category;
        public String rollNumber;
        public String studentName;
    }
}
