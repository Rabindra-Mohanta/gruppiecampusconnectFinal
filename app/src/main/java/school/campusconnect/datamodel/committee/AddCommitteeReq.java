package school.campusconnect.datamodel.committee;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AddCommitteeReq {

    @SerializedName("committeeName")
    @Expose
    private String committeeName;

    public String getCommitteeName() {
        return committeeName;
    }

    public void setCommitteeName(String committeeName) {
        this.committeeName = committeeName;
    }
}
