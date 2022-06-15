package school.campusconnect.datamodel.staff;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ChangeStaffAttendanceReq {

    @SerializedName("day")
    @Expose
    private String day;

    @SerializedName("year")
    @Expose
    private String year;

    @SerializedName("month")
    @Expose
    private String month;

    @SerializedName("session")
    @Expose
    private String session;

    @SerializedName("attendanceId")
    @Expose
    private String attendanceId;

    @SerializedName("userId")
    @Expose
    private String userId;

    @SerializedName("attendance")
    @Expose
    private String attendance;

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getSession() {
        return session;
    }

    public void setSession(String session) {
        this.session = session;
    }

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
