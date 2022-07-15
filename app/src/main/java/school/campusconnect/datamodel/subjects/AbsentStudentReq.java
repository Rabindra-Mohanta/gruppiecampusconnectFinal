package school.campusconnect.datamodel.subjects;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class AbsentStudentReq {

    @SerializedName("absentStudentIds")
    @Expose
    private ArrayList<String> absentStudentIds;

    @SerializedName("subjectId")
    @Expose
    private String subjectId;

    public ArrayList<String> getAbsentStudentIds() {
        return absentStudentIds;
    }

    public void setAbsentStudentIds(ArrayList<String> absentStudentIds) {
        this.absentStudentIds = absentStudentIds;
    }

    public String getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(String subjectId) {
        this.subjectId = subjectId;
    }
}
