package school.campusconnect.datamodel.fees;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DueDates {
    @SerializedName("minimumAmount")
    @Expose
    private String minimumAmount;
    @SerializedName("date")
    @Expose
    private String date;
    @SerializedName("status")
    @Expose
    private String status;

    public DueDates() {
    }

    public DueDates(String date,String minimumAmount) {
        this.minimumAmount = minimumAmount;
        this.date = date;
    }
    public DueDates(String date,String minimumAmount,String status) {
        this.minimumAmount = minimumAmount;
        this.date = date;
        this.status = status;
    }

    public String getMinimumAmount() {
        return minimumAmount;
    }

    public void setMinimumAmount(String minimumAmount) {
        this.minimumAmount = minimumAmount;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}