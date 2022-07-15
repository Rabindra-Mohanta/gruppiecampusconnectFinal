package school.campusconnect.datamodel.attendance_report;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ApplyLeaveReq {

    @SerializedName("leave")
    @Expose
    private LeaveData data;

    public LeaveData getData() {
        return data;
    }

    public void setData(LeaveData data) {
        this.data = data;
    }

    public static class LeaveData
    {
        @SerializedName("fromDate")
        @Expose
        private String fromDate;

        @SerializedName("toDate")
        @Expose
        private String toDate;

        @SerializedName("reason")
        @Expose
        private String reason;

        @SerializedName("noOfDays")
        @Expose
        private String noOfDays;

        public String getFromDate() {
            return fromDate;
        }

        public void setFromDate(String fromDate) {
            this.fromDate = fromDate;
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
    }
}
