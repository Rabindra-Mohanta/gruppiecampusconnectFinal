package school.campusconnect.datamodel;

import com.google.gson.Gson;

public class LeaveReq {
    public String reason;

    public LeaveReq(String reason) {
        this.reason = reason;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
