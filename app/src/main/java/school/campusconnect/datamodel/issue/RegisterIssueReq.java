package school.campusconnect.datamodel.issue;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RegisterIssueReq{
    @SerializedName("issue")
    @Expose
    public String issue;
    @SerializedName("jurisdiction")
    @Expose
    public String jurisdiction;
    @SerializedName("dueDays")
    @Expose
    public String dueDays;

}
