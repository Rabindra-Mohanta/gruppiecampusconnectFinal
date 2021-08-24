package school.campusconnect.datamodel.videocall;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

import school.campusconnect.datamodel.BaseResponse;

public class LiveClassEventRes extends BaseResponse {
    public ArrayList<LiveClassEventData> data;

    public static class LiveClassEventData {
        @SerializedName("teamId")
        @Expose
        public String teamId;
        @SerializedName("meetingOnLiveId")
        @Expose
        public String meetingOnLiveId;
        @SerializedName("groupId")
        @Expose
        public String groupId;
        @SerializedName("eventType")
        @Expose
        public String eventType;
        @SerializedName("eventName")
        @Expose
        public String eventName;
        @SerializedName("createdByName")
        @Expose
        public String createdByName;
        @SerializedName("createdById")
        @Expose
        public String createdById;
    }
}
