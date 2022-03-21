package school.campusconnect.datamodel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class AttendanceListRes extends BaseResponse{

    public ArrayList<AttendanceData> data;

    public class AttendanceData {
        public String userId;
        public String rollNumber;
        public String id;
        public boolean isChecked;

        @SerializedName("studentName")
        @Expose
        public String name;

        @SerializedName("parentNumber")
        @Expose
        public String phone;

        @SerializedName("studentImage")
        @Expose
        public String studentImage;

        @SerializedName("lastDaysAttendance")
        @Expose
        public ArrayList<lastDayData> lastDaysAttendance;


    }
    public static class lastDayData implements Serializable
    {
        @SerializedName("time")
        @Expose
        public String time;

        @SerializedName("date")
        @Expose
        public String date;

        @SerializedName("attendance")
        @Expose
        public Boolean attendance;

    }
}
