package school.campusconnect.datamodel.attendance_report;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

import school.campusconnect.datamodel.BaseResponse;

public class AttendanceReportResv2 extends BaseResponse {

    @SerializedName("data")
    @Expose
    public ArrayList<AttendanceReportResv2.AttendanceReportData> result;

    public ArrayList<AttendanceReportResv2.AttendanceReportData> getResult() {
        return result;
    }

    public void setResult(ArrayList<AttendanceReportResv2.AttendanceReportData> result) {
        this.result = result;
    }

    public static class AttendanceReportData implements Serializable {

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
        private attendanceReportsdata attendanceReport;

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

        public attendanceReportsdata getAttendanceReport() {
            return attendanceReport;
        }

        public void setAttendanceReport(attendanceReportsdata attendanceReport) {
            this.attendanceReport = attendanceReport;
        }
    }
    public static class attendanceReportsdata implements Serializable
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

        @SerializedName("date")
        @Expose
        private String date;

        @SerializedName("attendance")
        @Expose
        private Boolean attendance;

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

        public Boolean getAttendance() {
            return attendance;
        }

        public void setAttendance(Boolean attendance) {
            this.attendance = attendance;
        }
    }
}
