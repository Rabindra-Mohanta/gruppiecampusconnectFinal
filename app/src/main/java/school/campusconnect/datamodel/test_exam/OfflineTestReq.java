package school.campusconnect.datamodel.test_exam;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class OfflineTestReq {
    @SerializedName("title")
    @Expose
    public String title;
    @SerializedName("subjectMarksDetails")
    @Expose
    public ArrayList<TestOfflineSubjectMark> subjectMarksDetails;
    @SerializedName("resultDate")
    @Expose
    public String resultDate;

}
