package school.campusconnect.datamodel.event;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

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
