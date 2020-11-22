package school.campusconnect.datamodel.attendance_report;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

import school.campusconnect.datamodel.BaseResponse;

public class AttendanceReportRes extends BaseResponse {
    @SerializedName("data")
    @Expose
    public ArrayList<AttendanceReportData> result;

    public static class AttendanceReportData {
        @SerializedName("userId")
        @Expose
        private String userId;
        @SerializedName("totalMorningAttendance")
        @Expose
        private int totalMorningAttendance;
        @SerializedName("totalAfternoonAttendance")
        @Expose
        private int totalAfternoonAttendance;
        @SerializedName("studentName")
        @Expose
        private String studentName;
        @SerializedName("studentImage")
        @Expose
        private String studentImage;
        @SerializedName("rollNumber")
        @Expose
        private String rollNumber;
        @SerializedName("morningPresentCount")
        @Expose
        private int morningPresentCount;
        @SerializedName("afternoonPresentCount")
        @Expose
        private int afternoonPresentCount;

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public int getTotalMorningAttendance() {
            return totalMorningAttendance;
        }

        public void setTotalMorningAttendance(int totalMorningAttendance) {
            this.totalMorningAttendance = totalMorningAttendance;
        }

        public int getTotalAfternoonAttendance() {
            return totalAfternoonAttendance;
        }

        public void setTotalAfternoonAttendance(int totalAfternoonAttendance) {
            this.totalAfternoonAttendance = totalAfternoonAttendance;
        }

        public String getStudentName() {
            return studentName;
        }

        public void setStudentName(String studentName) {
            this.studentName = studentName;
        }

        public String getStudentImage() {
            return studentImage;
        }

        public void setStudentImage(String studentImage) {
            this.studentImage = studentImage;
        }

        public String getRollNumber() {
            return rollNumber;
        }

        public void setRollNumber(String rollNumber) {
            this.rollNumber = rollNumber;
        }

        public int getMorningPresentCount() {
            return morningPresentCount;
        }

        public void setMorningPresentCount(int morningPresentCount) {
            this.morningPresentCount = morningPresentCount;
        }

        public int getAfternoonPresentCount() {
            return afternoonPresentCount;
        }

        public void setAfternoonPresentCount(int afternoonPresentCount) {
            this.afternoonPresentCount = afternoonPresentCount;
        }

        @Override
        public String toString() {
            return new Gson().toJson(this);
        }
    }
}
