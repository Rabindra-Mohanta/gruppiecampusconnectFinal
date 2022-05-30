package school.campusconnect.datamodel.attendance_report;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

import school.campusconnect.datamodel.BaseResponse;

public class AttendanceReportParentRes extends BaseResponse {

    @SerializedName("data")
    @Expose
    private ArrayList<ReportData> data;

    public ArrayList<ReportData> getData() {
        return data;
    }

    public void setData(ArrayList<ReportData> data) {
        this.data = data;
    }

    public static class ReportData implements Serializable
    {
        @SerializedName("userId")
        @Expose
        private String userId;

        @SerializedName("studentName")
        @Expose
        private String studentName;

        @SerializedName("rollNumber")
        @Expose
        private String rollNumber;

        @SerializedName("attendanceReport")
        @Expose
        private ArrayList<AttendanceReportData> attendanceReport;

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public String getStudentName() {
            return studentName;
        }

        public void setStudentName(String studentName) {
            this.studentName = studentName;
        }

        public String getRollNumber() {
            return rollNumber;
        }

        public void setRollNumber(String rollNumber) {
            this.rollNumber = rollNumber;
        }

        public ArrayList<AttendanceReportData> getAttendanceReport() {
            return attendanceReport;
        }

        public void setAttendanceReport(ArrayList<AttendanceReportData> attendanceReport) {
            this.attendanceReport = attendanceReport;
        }
    }

    public static class AttendanceReportData implements Serializable
    {
        @SerializedName("time")
        @Expose
        private String time;

        @SerializedName("teacherName")
        @Expose
        private String teacherName;

        @SerializedName("subjectName")
        @Expose
        private String subjectName;

        @SerializedName("day")
        @Expose
        private int day;

        @SerializedName("dateString")
        @Expose
        private String dateString;

        @SerializedName("date")
        @Expose
        private String date;

        @SerializedName("attendance")
        @Expose
        private String attendance;

        
        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }

        public String getTeacherName() {
            return teacherName;
        }

        public void setTeacherName(String teacherName) {
            this.teacherName = teacherName;
        }

        public String getSubjectName() {
            return subjectName;
        }

        public void setSubjectName(String subjectName) {
            this.subjectName = subjectName;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
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

        public String getAttendance() {
            return attendance;
        }

        public void setAttendance(String attendance) {
            this.attendance = attendance;
        }
    }
}
