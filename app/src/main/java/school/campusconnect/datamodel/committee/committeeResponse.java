package school.campusconnect.datamodel.committee;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

import school.campusconnect.datamodel.BaseResponse;

public class committeeResponse extends BaseResponse implements Serializable {

    @SerializedName("data")
    @Expose
    private ArrayList<committeeData> committeeData;

    public ArrayList<committeeResponse.committeeData> getCommitteeData() {
        return committeeData;
    }

    public void setCommitteeData(ArrayList<committeeResponse.committeeData> committeeData) {
        this.committeeData = committeeData;
    }

    public static class committeeData implements Serializable
    {
        @SerializedName("defaultCommittee")
        @Expose
        public Boolean defaultCommittee;

        @SerializedName("committeeName")
        @Expose
        public String committeeName;

        @SerializedName("committeeId")
        @Expose
        public String committeeId;

        public Boolean getDefaultCommittee() {
            return defaultCommittee;
        }

        public void setDefaultCommittee(Boolean defaultCommittee) {
            this.defaultCommittee = defaultCommittee;
        }

        public String getCommitteeName() {
            return committeeName;
        }

        public void setCommitteeName(String committeeName) {
            this.committeeName = committeeName;
        }

        public String getCommitteeId() {
            return committeeId;
        }

        public void setCommitteeId(String committeeId) {
            this.committeeId = committeeId;
        }
    }
}
