package school.campusconnect.datamodel.time_table;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SubStaffTTReq {
    @SerializedName("day")
    @Expose
    private String day;
    @SerializedName("period")
    @Expose
    private String period;

    @SerializedName("startTime")
    @Expose
    private String startTime;

    @SerializedName("endTime")
    @Expose
    private String endTime;


    public SubStaffTTReq() {
    }

    public SubStaffTTReq(String day, String period,String startTime,String endTime) {
        this.day = day;
        this.period = period;
        this.startTime = startTime;
        this.endTime = endTime;
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

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
