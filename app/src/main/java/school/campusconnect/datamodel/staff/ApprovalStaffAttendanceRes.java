package school.campusconnect.datamodel.staff;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

import school.campusconnect.datamodel.BaseResponse;

public class ApprovalStaffAttendanceRes extends BaseResponse {

    @SerializedName("data")
    @Expose
    private ArrayList<ApprovalStaffData> approvalStaffData;

    public ArrayList<ApprovalStaffData> getApprovalStaffData() {
        return approvalStaffData;
    }

    public void setApprovalStaffData(ArrayList<ApprovalStaffData> approvalStaffData) {
        this.approvalStaffData = approvalStaffData;
    }

    private static class ApprovalStaffData implements Serializable
    {
        @SerializedName("userId")
        @Expose
        private String userId;

        @SerializedName("attendance")
        @Expose
        private ArrayList<AttendanceData> attendanceData;


        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public ArrayList<AttendanceData> getAttendanceData() {
            return attendanceData;
        }

        public void setAttendanceData(ArrayList<AttendanceData> attendanceData) {
            this.attendanceData = attendanceData;
        }
    }

    private static class AttendanceData implements Serializable
    {
        @SerializedName("session")
        @Expose
        private String session;

        @SerializedName("isApproved")
        @Expose
        private boolean isApproved;

        @SerializedName("day")
        @Expose
        private String day;

        @SerializedName("dateString")
        @Expose
        private String dateString;

        @SerializedName("date")
        @Expose
        private String date;

        @SerializedName("attendanceTakenByName")
        @Expose
        private String attendanceTakenByName;

        @SerializedName("attendanceTakenById")
        @Expose
        private String attendanceTakenById;

        @SerializedName("attendanceId")
        @Expose
        private String attendanceId;

        @SerializedName("attendance")
        @Expose
        private String attendance;


        public String getSession() {
            return session;
        }

        public void setSession(String session) {
            this.session = session;
        }

        public boolean isApproved() {
            return isApproved;
        }

        public void setApproved(boolean approved) {
            isApproved = approved;
        }

        public String getDay() {
            return day;
        }

        public void setDay(String day) {
            this.day = day;
        }

        public String getDateString() {
            return dateString;
        }

        public void setDateString(String dateString) {
            this.dateString = dateString;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public String getAttendanceTakenByName() {
            return attendanceTakenByName;
        }

        public void setAttendanceTakenByName(String attendanceTakenByName) {
            this.attendanceTakenByName = attendanceTakenByName;
        }

        public String getAttendanceTakenById() {
            return attendanceTakenById;
        }

        public void setAttendanceTakenById(String attendanceTakenById) {
            this.attendanceTakenById = attendanceTakenById;
        }

        public String getAttendanceId() {
            return attendanceId;
        }

        public void setAttendanceId(String attendanceId) {
            this.attendanceId = attendanceId;
        }

        public String getAttendance() {
            return attendance;
        }

        public void setAttendance(String attendance) {
            this.attendance = attendance;
        }
    }
}
