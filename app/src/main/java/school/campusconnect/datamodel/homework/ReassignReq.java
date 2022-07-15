package school.campusconnect.datamodel.homework;

import com.google.gson.Gson;

import java.util.ArrayList;

public class ReassignReq {
    public String text;
    public ArrayList<String> fileName;
    public String fileType="image";

    public ReassignReq(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
