package school.campusconnect.datamodel.attendance_report;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

import school.campusconnect.datamodel.BaseResponse;

public class LeaveRes extends BaseResponse {

    @SerializedName("data")
    @Expose
    private ArrayList<LeaveData> data;

    public ArrayList<LeaveData> getData() {
        return data;
    }

    public void setData(ArrayList<LeaveData> data) {
        this.data = data;
    }

    public static class LeaveData implements Serializable
    {
        @SerializedName("userId")
        @Expose
        private String userId;

        @SerializedName("teamId")
        @Expose
        private String teamId;

        @SerializedName("leaveApplies")
        @Expose
        private LeaveAppliesData leaveApplies;

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public String getTeamId() {
            return teamId;
        }

        public void setTeamId(String teamId) {
            this.teamId = teamId;
        }

        public LeaveAppliesData getLeaveApplies() {
            return leaveApplies;
        }

        public void setLeaveApplies(LeaveAppliesData leaveApplies) {
            this.leaveApplies = leaveApplies;
        }
    }
    public static class LeaveAppliesData implements Serializable
    {
        @SerializedName("toDateString")
        @Expose
        private String toDateString;

        @SerializedName("toDate")
        @Expose
        private String toDate;

        @SerializedName("reason")
        @Expose
        private String reason;

        @SerializedName("noOfDays")
        @Expose
        private String noOfDays;

        @SerializedName("fromDateString")
        @Expose
        private String fromDateString;

        @SerializedName("fromDate")
        @Expose
        private String fromDate;


        public String getToDateString() {
            return toDateString;
        }

        public void setToDateString(String toDateString) {
            this.toDateString = toDateString;
        }

        public String getToDate() {
            return toDate;
        }

        public void setToDate(String toDate) {
            this.toDate = toDate;
        }

        public String getReason() {
            return reason;
        }

        public void setReason(String reason) {
            this.reason = reason;
        }

        public String getNoOfDays() {
            return noOfDays;
        }

        public void setNoOfDays(String noOfDays) {
            this.noOfDays = noOfDays;
        }

        public String getFromDateString() {
            return fromDateString;
        }

        public void setFromDateString(String fromDateString) {
            this.fromDateString = fromDateString;
        }

        public String getFromDate() {
            return fromDate;
        }

        public void setFromDate(String fromDate) {
            this.fromDate = fromDate;
        }
    }
}
