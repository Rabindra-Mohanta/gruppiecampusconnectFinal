package school.campusconnect.datamodel.staff;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

import school.campusconnect.activities.AttendanceActivity;

public class TakeAttendanceReq {

    @SerializedName("attendanceObject")
    @Expose
    private ArrayList<AttendanceObject> attendanceObjects;

    public ArrayList<AttendanceObject> getAttendanceObjects() {
        return attendanceObjects;
    }

    public void setAttendanceObjects(ArrayList<AttendanceObject> attendanceObjects) {
        this.attendanceObjects = attendanceObjects;
    }

    public static class AttendanceObject implements Serializable
    {
        @SerializedName("userIds")
        @Expose
        private ArrayList<String> userId;

        @SerializedName("attendance")
        @Expose
        private String attendance;

        @SerializedName("session")
        @Expose
        private String session;

        public ArrayList<String> getUserId() {
            return userId;
        }

        public void setUserId(ArrayList<String> userId) {
            this.userId = userId;
        }

        public String getAttendance() {
            return attendance;
        }

        public void setAttendance(String attendance) {
            this.attendance = attendance;
        }

        public String getSession() {
            return session;
        }

        public void setSession(String session) {
            this.session = session;
        }
    }
}
