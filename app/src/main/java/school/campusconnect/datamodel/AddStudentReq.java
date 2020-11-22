package school.campusconnect.datamodel;

import com.google.gson.Gson;

public class AddStudentReq {
    public String studentName;
    public String countryCode;
    public String parentNumber;
    public String rollNumber;

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
