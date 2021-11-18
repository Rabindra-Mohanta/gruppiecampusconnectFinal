package school.campusconnect.datamodel.test_exam;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class TestOfflineSubjectMark implements Serializable {
    @SerializedName("subjectId")
    @Expose
    public  String subjectId;
    @SerializedName("subjectName")
    @Expose
    public  String subjectName;
    @SerializedName("date")
    @Expose
    public  String date;
    @SerializedName("day")
    @Expose
    public  String day;
    @SerializedName("startTime")
    @Expose
    public  String startTime;
    @SerializedName("endTime")
    @Expose
    public  String endTime;
    @SerializedName("maxMarks")
    @Expose
    public  String maxMarks;
    @SerializedName("minMarks")
    @Expose
    public  String minMarks;

}
