package school.campusconnect.datamodel.time_table;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

import school.campusconnect.datamodel.BaseResponse;

public class TimeTableList2Response extends BaseResponse {
    private ArrayList<TimeTableData2> data;

    public ArrayList<TimeTableData2> getData() {
        return data;
    }

    public void setData(ArrayList<TimeTableData2> data) {
        this.data = data;
    }

    public static class TimeTableData2 {
        @SerializedName("day")
        @Expose
        public String day;

        @SerializedName(value = "sessions")
        @Expose
        public ArrayList<SessionsTimeTable> sessions;
        public boolean isSelected;
        public String getDay() {
            return day;
        }

        public void setDay(String day) {
            this.day = day;
        }

        public ArrayList<SessionsTimeTable> getSessions() {
            return sessions;
        }

        public void setSessions(ArrayList<SessionsTimeTable> sessions) {
            this.sessions = sessions;
        }

        @Override
        public String toString() {
            return new Gson().toJson(this);
        }
    }

    public static class SessionsTimeTable {
        @SerializedName("teacherName")
        @Expose
        private String teacherName;
        @SerializedName("subjectName")
        @Expose
        private String subjectName;
        @SerializedName("period")
        @Expose
        private String period;

        @SerializedName("startTime")
        @Expose
        private String startTime;

        @SerializedName("endTime")
        @Expose
        private String endTime;


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

        public String getPeriod() {
            return period;
        }

        public void setPeriod(String period) {
            this.period = period;
        }

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
    }
}
