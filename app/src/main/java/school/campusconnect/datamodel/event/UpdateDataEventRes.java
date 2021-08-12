package school.campusconnect.datamodel.event;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

import school.campusconnect.datamodel.BaseResponse;

public class UpdateDataEventRes extends BaseResponse {
    public ArrayList<EventResData> data;

    public static class EventResData {
        @SerializedName("insertedId")
        @Expose
        public String insertedId;
        @SerializedName("groupId")
        @Expose
        public String groupId;
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
}
