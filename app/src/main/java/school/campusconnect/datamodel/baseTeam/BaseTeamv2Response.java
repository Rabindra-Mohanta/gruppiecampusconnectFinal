package school.campusconnect.datamodel.baseTeam;

import androidx.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

import school.campusconnect.datamodel.BaseResponse;
import school.campusconnect.datamodel.teamdiscussion.MyTeamData;

public class BaseTeamv2Response extends BaseResponse implements Serializable {

    @SerializedName("data")
    @Expose
    private ArrayList<TeamListData> TeamData;

    public ArrayList<TeamListData> getTeamData() {
        return TeamData;
    }

    public void setTeamData(ArrayList<TeamListData> teamData) {
        TeamData = teamData;
    }

    public static class TeamListData implements Serializable
    {
        @SerializedName("featureIcons")
        @Expose
        public ArrayList<MyTeamData> MyTeamData;

        @SerializedName("activity")
        @Expose
        public String activity;

        @SerializedName("kanActivity")
        @Expose
        public String kanActivity;

        public ArrayList<MyTeamData> getFeaturedIconData() {
            return MyTeamData;
        }

        public void setFeaturedIconData(ArrayList<MyTeamData> featuredIconData) {
            this.MyTeamData = featuredIconData;
        }

        public String getActivity() {
            return activity;
        }

        public void setActivity(String activity) {
            this.activity = activity;
        }

        public String getKanActivity() {
            return kanActivity;
        }

        public void setKanActivity(String kanActivity) {
            this.kanActivity = kanActivity;
        }
    }
  /*  public static class featuredIconData implements Serializable
    {
        @SerializedName("type")
        @Expose
        private String type;

        @SerializedName("role")
        @Expose
        private String role;

        @SerializedName("name")
        @Expose
        private String name;

        @SerializedName("postUnseenCount")
        @Expose
        private int postUnseenCount;

        @SerializedName("image")
        @Expose
        private String image;

        @SerializedName("id")
        @Expose
        private int id;

        @SerializedName("groupId")
        @Expose
        private String groupId;

        @SerializedName("category")
        @Expose
        private String category;

        @SerializedName("details")
        @Expose
        private TeamDetails details;

        @SerializedName("count")
        @Expose
        private int count;

        @SerializedName("isClass")
        @Expose
        private Boolean isClasses;

        @SerializedName("enableGps")
        @Expose
        private Boolean enableGps;

        @SerializedName("enableAttendance")
        @Expose
        private Boolean enableAttendance;

        @SerializedName("teamId")
        @Expose
        private String teamId;

        @SerializedName("phone")
        @Expose
        private String phone;

        @SerializedName("members")
        @Expose
        private int members;

        @SerializedName("isTeamAdmin")
        @Expose
        private Boolean isTeamAdmin;

        @SerializedName("canAddUser")
        @Expose
        private Boolean canAddUser;

        @SerializedName("allowTeamPostCommentAll")
        @Expose
        private Boolean allowTeamPostCommentAll;

        @SerializedName("allowTeamPostAll")
        @Expose
        private Boolean allowTeamPostAll;


        public String getRole() {
            return role;
        }

        public void setRole(String role) {
            this.role = role;
        }

        public int getPostUnseenCount() {
            return postUnseenCount;
        }

        public void setPostUnseenCount(int postUnseenCount) {
            this.postUnseenCount = postUnseenCount;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getGroupId() {
            return groupId;
        }

        public void setGroupId(String groupId) {
            this.groupId = groupId;
        }

        public String getCategory() {
            return category;
        }

        public void setCategory(String category) {
            this.category = category;
        }

        public TeamDetails getDetails() {
            return details;
        }

        public void setDetails(TeamDetails details) {
            this.details = details;
        }

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }

        public Boolean getClasses() {
            return isClasses;
        }

        public void setClasses(Boolean classes) {
            isClasses = classes;
        }

        public Boolean getEnableGps() {
            return enableGps;
        }

        public void setEnableGps(Boolean enableGps) {
            this.enableGps = enableGps;
        }

        public Boolean getEnableAttendance() {
            return enableAttendance;
        }

        public void setEnableAttendance(Boolean enableAttendance) {
            this.enableAttendance = enableAttendance;
        }

        public String getTeamId() {
            return teamId;
        }

        public void setTeamId(String teamId) {
            this.teamId = teamId;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public int getMembers() {
            return members;
        }

        public void setMembers(int members) {
            this.members = members;
        }

        public Boolean getTeamAdmin() {
            return isTeamAdmin;
        }

        public void setTeamAdmin(Boolean teamAdmin) {
            isTeamAdmin = teamAdmin;
        }

        public Boolean getCanAddUser() {
            return canAddUser;
        }

        public void setCanAddUser(Boolean canAddUser) {
            this.canAddUser = canAddUser;
        }

        public Boolean getAllowTeamPostCommentAll() {
            return allowTeamPostCommentAll;
        }

        public void setAllowTeamPostCommentAll(Boolean allowTeamPostCommentAll) {
            this.allowTeamPostCommentAll = allowTeamPostCommentAll;
        }

        public Boolean getAllowTeamPostAll() {
            return allowTeamPostAll;
        }

        public void setAllowTeamPostAll(Boolean allowTeamPostAll) {
            this.allowTeamPostAll = allowTeamPostAll;
        }
    }
    public static class TeamDetails implements Serializable{
        public String userId;
        public String teamName;
        public String teamImage;
        public String teamId;
        public String studentCount;
        public String category;
        public String rollNumber;
        public String studentName;

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public String getTeamName() {
            return teamName;
        }

        public void setTeamName(String teamName) {
            this.teamName = teamName;
        }

        public String getTeamImage() {
            return teamImage;
        }

        public void setTeamImage(String teamImage) {
            this.teamImage = teamImage;
        }

        public String getTeamId() {
            return teamId;
        }

        public void setTeamId(String teamId) {
            this.teamId = teamId;
        }

        public String getStudentCount() {
            return studentCount;
        }

        public void setStudentCount(String studentCount) {
            this.studentCount = studentCount;
        }

        public String getCategory() {
            return category;
        }

        public void setCategory(String category) {
            this.category = category;
        }

        public String getRollNumber() {
            return rollNumber;
        }

        public void setRollNumber(String rollNumber) {
            this.rollNumber = rollNumber;
        }

        public String getStudentName() {
            return studentName;
        }

        public void setStudentName(String studentName) {
            this.studentName = studentName;
        }
    }*/
}
