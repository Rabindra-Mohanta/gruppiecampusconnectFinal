package school.campusconnect.datamodel.attendance_report;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

import school.campusconnect.datamodel.BaseResponse;

public class PreSchoolStudentRes extends BaseResponse {
    @SerializedName("data")
    @Expose
    private ArrayList<PreSchoolStudentData> data;

    public ArrayList<PreSchoolStudentData> getData() {
        return data;
    }

    public void setData(ArrayList<PreSchoolStudentData> data) {
        this.data = data;
    }

    public class PreSchoolStudentData {
        @SerializedName("userId")
        @Expose
        private String userId;
        @SerializedName("studentName")
        @Expose
        private String studentName;
        @SerializedName("studentImage")
        @Expose
        private String studentImage;

        @SerializedName("rollNumber")
        @Expose
        private String rollNumber;
        @SerializedName("id")
        @Expose
        private String id;
        @SerializedName("attendanceOut")
        @Expose
        private boolean attendanceOut;
        @SerializedName("attendanceIn")
        @Expose
        private boolean attendanceIn;

        public String getStudentImage() {
            return studentImage;
        }

        public void setStudentImage(String studentImage) {
            this.studentImage = studentImage;
        }

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

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public boolean isAttendanceOut() {
            return attendanceOut;
        }

        public void setAttendanceOut(boolean attendanceOut) {
            this.attendanceOut = attendanceOut;
        }

        public boolean isAttendanceIn() {
            return attendanceIn;
        }

        public void setAttendanceIn(boolean attendanceIn) {
            this.attendanceIn = attendanceIn;
        }
    }
}
