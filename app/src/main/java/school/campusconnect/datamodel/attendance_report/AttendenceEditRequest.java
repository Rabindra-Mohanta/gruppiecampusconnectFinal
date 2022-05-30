package school.campusconnect.datamodel.attendance_report;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AttendenceEditRequest{

    @SerializedName("attendanceId")
    @Expose
    private String attendanceId;

    @SerializedName("userId")
    @Expose
    private String userId;

    @SerializedName("attendance")
    @Expose
    private String attendance;

    public String getAttendanceId() {
        return attendanceId;
    }

    public void setAttendanceId(String attendanceId) {
        this.attendanceId = attendanceId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getAttendance() {
        return attendance;
    }

    public void setAttendance(String attendance) {
        this.attendance = attendance;
    }
}
