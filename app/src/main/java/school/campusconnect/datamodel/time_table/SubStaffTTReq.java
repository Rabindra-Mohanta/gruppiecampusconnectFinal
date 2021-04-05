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

    public SubStaffTTReq() {
    }

    public SubStaffTTReq(String day, String period) {
        this.day = day;
        this.period = period;
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
