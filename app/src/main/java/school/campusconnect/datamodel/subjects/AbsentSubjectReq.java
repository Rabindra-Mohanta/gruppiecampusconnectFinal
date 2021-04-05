package school.campusconnect.datamodel.subjects;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AbsentSubjectReq {
    @SerializedName("subjectName")
    @Expose
    private String subjectName;

    public AbsentSubjectReq() {
    }

    public AbsentSubjectReq(String subjectName) {
        this.subjectName = subjectName;
    }
}
