package school.campusconnect.datamodel;

import com.google.gson.Gson;

public class CreateTeamRequest {
    public String name;
    public String image;

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
