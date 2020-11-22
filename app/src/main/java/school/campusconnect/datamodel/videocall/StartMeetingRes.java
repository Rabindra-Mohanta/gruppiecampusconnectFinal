package school.campusconnect.datamodel.videocall;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

import school.campusconnect.datamodel.BaseResponse;

public class StartMeetingRes extends BaseResponse {
    @SerializedName("data")
    @Expose
    public ArrayList<StartMeetingData> data;

    public static class StartMeetingData {
        @SerializedName("name")
        @Expose
        public String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getJitsiToken() {
            return jitsiToken;
        }

        public void setJitsiToken(String jitsiToken) {
            this.jitsiToken = jitsiToken;
        }

        @SerializedName("jitsiToken")
        @Expose
        public String jitsiToken;

        public boolean isMeetingCreatedBy() {
            return meetingCreatedBy;
        }

        public void setMeetingCreatedBy(boolean meetingCreatedBy) {
            this.meetingCreatedBy = meetingCreatedBy;
        }

        @SerializedName("meetingCreatedBy")
        @Expose
        public boolean meetingCreatedBy;
    }
}
