package school.campusconnect.datamodel.booths;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class BoothMemberReq {
    @SerializedName("user")
    @Expose
    public ArrayList<String> user;

    @SerializedName("dafaultCommittee")
    @Expose
    public Boolean dafaultCommittee;

    @SerializedName("committeeId")
    @Expose
    public String committeeId;
}
