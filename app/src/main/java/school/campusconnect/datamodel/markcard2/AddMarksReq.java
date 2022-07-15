package school.campusconnect.datamodel.markcard2;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class AddMarksReq {
    @SerializedName("subjectMarksDetails")
    @Expose
    public ArrayList<MarkCardResponse2.SubjectMarkData> subjectMarksDetails;
}
