package school.campusconnect.datamodel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class AttendanceListRes extends BaseResponse{
    public ArrayList<AttendanceData> data;

    public class AttendanceData {
        public String userId;
        public String rollNumber;

        @SerializedName("studentName")
        @Expose
        public String name;

        @SerializedName("parentNumber")
        @Expose
        public String phone;

        @SerializedName("studentImage")
        @Expose
        public String studentImage;

        public String id;

        public boolean isChecked;
    }
}
