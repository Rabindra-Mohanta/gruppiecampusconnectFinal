package school.campusconnect.datamodel.time_table;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

import school.campusconnect.datamodel.BaseResponse;

public class SubjectStaffTTResponse extends BaseResponse {
    private ArrayList<SubjectStaffTTData> data;

    public ArrayList<SubjectStaffTTData> getData() {
        return data;
    }

    public void setData(ArrayList<SubjectStaffTTData> data) {
        this.data = data;
    }

    public static class SubjectStaffTTData {

        @SerializedName("day")
        @Expose
        public String day;
        @SerializedName("period")
        @Expose
        public String period;

        @SerializedName("startTime")
        @Expose
        public String startTime;

        @SerializedName("endTime")
        @Expose
        public String endTime;


        @SerializedName("subjectName")
        @Expose
        public String subjectName;
        @SerializedName("subjectWithStaffId")
        @Expose
        public String subjectWithStaffId;

        @SerializedName(value = "subjectWithStaffs")
        @Expose
        public ArrayList<SubjectWithStaffs> subjectWithStaffs;


        public String getStartTime() {
            return startTime;
        }

        public void setStartTime(String startTime) {
            this.startTime = startTime;
        }

        public String getEndTime() {
            return endTime;
        }

        public void setEndTime(String endTime) {
            this.endTime = endTime;
        }

        public String getDay() {
            return day;
        }

        public void setDay(String day) {
            this.day = day;
        }

        public String getPeriod() {
            return period;
        }

        public void setPeriod(String period) {
            this.period = period;
        }

        public String getSubjectName() {
            return subjectName;
        }

        public void setSubjectName(String subjectName) {
            this.subjectName = subjectName;
        }

        public String getSubjectWithStaffId() {
            return subjectWithStaffId;
        }

        public void setSubjectWithStaffId(String subjectWithStaffId) {
            this.subjectWithStaffId = subjectWithStaffId;
        }

        public ArrayList<SubjectWithStaffs> getSubjectWithStaffs() {
            return subjectWithStaffs;
        }

        public void setSubjectWithStaffs(ArrayList<SubjectWithStaffs> subjectWithStaffs) {
            this.subjectWithStaffs = subjectWithStaffs;
        }
    }

    public static class SubjectWithStaffs {
        @SerializedName("staffId")
        @Expose
        private String staffId;
        @SerializedName("staffName")
        @Expose
        private String staffName;

        public String getStaffId() {
            return staffId;
        }

        public void setStaffId(String staffId) {
            this.staffId = staffId;
        }

        public String getStaffName() {
            return staffName;
        }

        public void setStaffName(String staffName) {
            this.staffName = staffName;
        }
    }
}
