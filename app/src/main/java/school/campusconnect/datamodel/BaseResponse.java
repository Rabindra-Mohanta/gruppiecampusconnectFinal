package school.campusconnect.datamodel;


import com.google.gson.Gson;

public class BaseResponse {
    public String status="";

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
