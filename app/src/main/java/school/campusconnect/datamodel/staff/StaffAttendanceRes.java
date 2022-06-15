package school.campusconnect.datamodel.staff;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

import school.campusconnect.datamodel.BaseResponse;

public class StaffAttendanceRes extends BaseResponse {

    @SerializedName("data")
    @Expose
    private ArrayList<StaffAttendData> staffAttendData;


    public ArrayList<StaffAttendData> getStaffAttendData() {
        return staffAttendData;
    }

    public void setStaffAttendData(ArrayList<StaffAttendData> staffAttendData) {
        this.staffAttendData = staffAttendData;
    }

    public static class StaffAttendData implements Serializable
    {
        @SerializedName("userId")
        @Expose
        private String userId;

        @SerializedName("name")
        @Expose
        private String name;

        @SerializedName("attendance")
        @Expose
        private ArrayList<AttendanceData> attendance;

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public ArrayList<AttendanceData> getAttendance() {
            return attendance;
        }

        public void setAttendance(ArrayList<AttendanceData> attendance) {
            this.attendance = attendance;
        }
    }

    public static class AttendanceData implements Serializable
    {
        @SerializedName("time")
        @Expose
        private String time;

        @SerializedName("session")
        @Expose
        private String session;

        @SerializedName("day")
        @Expose
        private int day;

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

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }

        public String getSession() {
            return session;
        }

        public void setSession(String session) {
            this.session = session;
        }

        public int getDay() {
            return day;
        }

        public void setDay(int day) {
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
