package school.campusconnect.datamodel.subjects;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class AddSubjectStaffReq {
    @SerializedName("subjectName")
    @Expose
    private String subjectName;

    @SerializedName("subjectId")
    @Expose
    private String subjectId;

    @SerializedName("staffId")
    @Expose
    private ArrayList<String> staffId;

    public String getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(String subjectId) {
        this.subjectId = subjectId;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public ArrayList<String> getStaffId() {
        return staffId;
    }

    public void setStaffId(ArrayList<String> staffId) {
        this.staffId = staffId;
    }
}
