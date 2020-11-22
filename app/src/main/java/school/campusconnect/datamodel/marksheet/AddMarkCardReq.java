package school.campusconnect.datamodel.marksheet;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;

public class AddMarkCardReq {
    public String title;
    public ArrayList<HashMap<String,HashMap<String,Object>>> subjects;
    public String duration;

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
