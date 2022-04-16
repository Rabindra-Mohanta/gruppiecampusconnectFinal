package school.campusconnect.datamodel.event;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

import school.campusconnect.datamodel.BaseResponse;

public class UpdateDataEventRes extends BaseResponse {
    public ArrayList<EventMainData> data;

    public static class EventMainData {

        @SerializedName("teamsListCount")
        @Expose
        public TeamListCount teamsListCount;

        @SerializedName("subjectCountList")
        @Expose
        public ArrayList<SubjectCountList> subjectCountList;

        @SerializedName("eventList")
        @Expose
        public ArrayList<EventResData> eventList;

        @SerializedName("imagePreviewUrl")
        @Expose
        public String imagePreviewUrl;

        @SerializedName("roles")
        @Expose
        public RoleData roleData;

        @SerializedName("notificationFeedEventAt")
        @Expose
        public String notificationFeedEventAt;

        @SerializedName("myProfileUpdatedEventAt")
        @Expose
        public String myProfileUpdatedEventAt;

        @SerializedName("lastUpdatedTeamTime")
        @Expose
        public String lastUpdatedTeamTime;

        @SerializedName("lastInsertedTeamTime")
        @Expose
        public String lastInsertedTeamTime;

        @SerializedName("lastInsertedBoothTeamTime")
        @Expose
        public String lastInsertedBoothTeamTime;

        @SerializedName("homeTeamsLastPostEventAt")
        @Expose
        public ArrayList<HomeTeamData> homeTeamData;

        @SerializedName("galleryPostEventAt")
        @Expose
        public String galleryPostEventAt;

        @SerializedName("calendarEventAt")
        @Expose
        public String calendarEventAt;

        @SerializedName("bannerPostEventAt")
        @Expose
        public String bannerPostEventAt;

        @SerializedName("announcementPostEventAt")
        @Expose
        public String announcementPostEventAt;

        @SerializedName("allBoothsPostEventAt")
        @Expose
        public ArrayList<AllBoothData> allBoothsPostEventAt;

    }
    public static class AllBoothData implements Serializable
    {
        @SerializedName("members")
        @Expose
        public int members;

        @SerializedName("lastBoothPostAt")
        @Expose
        public String lastBoothPostAt;

        @SerializedName("boothId")
        @Expose
        public String boothId;


        @SerializedName("lastCommitteeForBoothUpdatedEventAt")
        @Expose
        public String lastCommitteeForBoothUpdatedEventAt;


    }
    public static class HomeTeamData implements Serializable
    {
        @SerializedName("teamId")
        @Expose
        public String teamId;

        @SerializedName("members")
        @Expose
        public int members;

        @SerializedName("lastTeamPostAt")
        @Expose
        public String lastTeamPostAt;

        @SerializedName("lastCommitteeForBoothUpdatedEventAt")
        @Expose
        public String lastCommitteeForBoothUpdatedEventAt;

        @SerializedName("canPost")
        @Expose
        public boolean canPost;

        @SerializedName("canComment")
        @Expose
        public boolean canComment;

    }
    public static class RoleData implements Serializable {

        @SerializedName("isPublic")
        @Expose
        public boolean isPublic;

        @SerializedName("isPartyTaskForce")
        @Expose
        public boolean isPartyTaskForce;

        @SerializedName("isDepartmentTaskForce")
        @Expose
        public boolean isDepartmentTaskForce;

        @SerializedName("isBoothWorker")
        @Expose
        public boolean isBoothWorker;

        @SerializedName("isBoothPresident")
        @Expose
        public boolean isBoothPresident;

        @SerializedName("isBoothMember")
        @Expose
        public boolean isBoothMember;

        @SerializedName("isAuthorizedUser")
        @Expose
        public boolean isAuthorizedUser;

        @SerializedName("isAdmin")
        @Expose
        public boolean isAdmin;


    }

    public static class EventResData {
        @SerializedName("insertedId")
        @Expose
        public String insertedId;
        @SerializedName("groupId")
        @Expose
        public String groupId;
        @SerializedName("subjectId")
        @Expose
        public String subjectId;
        @SerializedName("teamId")
        @Expose
        public String teamId;
        @SerializedName("eventType")
        @Expose
        public String eventType;
        @SerializedName("eventName")
        @Expose
        public String eventName;
        @SerializedName("eventAt")
        @Expose
        public String eventAt;
    }

    public static class TeamListCount {
        @SerializedName("liveClassTeamCount")
        @Expose
        public int liveClassTeamCount;
        @SerializedName("lastNotificationAt")
        @Expose
        public String lastNotificationAt;
        @SerializedName("lastInsertedTeamTime")
        @Expose
        public String lastInsertedTeamTime;
        @SerializedName("getAllClassTeamCount")
        @Expose
        public int getAllClassTeamCount;
        @SerializedName("dashboardTeamCount")
        @Expose
        public int dashboardTeamCount;
        @SerializedName("schoolGroupCount")
        @Expose
        public int schoolGroupCount;

        @SerializedName("secretKey")
        @Expose
        public String secretKey;

        @SerializedName("accessKey")
        @Expose
        public String accessKey;
    }
    public static class SubjectCountList {
        @SerializedName("teamId")
        @Expose
        public String teamId;
        @SerializedName("subjectCount")
        @Expose
        public int subjectCount;
    }


}
