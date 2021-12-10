package school.campusconnect.datamodel.booths;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class BoothMemberReq {
    @SerializedName("user")
    @Expose
    public ArrayList<String> user;
}
