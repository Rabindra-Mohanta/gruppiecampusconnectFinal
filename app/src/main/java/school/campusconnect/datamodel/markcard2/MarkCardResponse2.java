package school.campusconnect.datamodel.markcard2;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

import school.campusconnect.datamodel.BaseResponse;

public class MarkCardResponse2 extends BaseResponse {
    @SerializedName("data")
    @Expose
    public ArrayList<MarkCardStudent> data;

    public static class MarkCardStudent {
        @SerializedName("userId")
        @Expose
        public String userId;
        @SerializedName("totalObtainedMarks")
        @Expose
        public String totalObtainedMarks;
        @SerializedName("totalMinMarks")
        @Expose
        public String totalMinMarks;
        @SerializedName("totalMaxMarks")
        @Expose
        public String totalMaxMarks;
        @SerializedName("subjectMarksDetails")
        @Expose
        public ArrayList<SubjectMarkData> subjectMarksDetails;
        @SerializedName("studentName")
        @Expose
        public String studentName;
        @SerializedName("studentImage")
        @Expose
        public String studentImage;
        @SerializedName("rollNumber")
        @Expose
        public String rollNumber;
        @SerializedName("offlineTestExamId")
        @Expose
        public String offlineTestExamId;
        @SerializedName("examTitle")
        @Expose
        public String examTitle;
        @SerializedName("examDuration")
        @Expose
        public String examDuration;
        @SerializedName("admissionNumber")
        @Expose
        public String admissionNumber;

        public boolean isExpand = false;
    }

    public static class SubjectMarkData {
        @SerializedName("subjectName")
        @Expose
        public String subjectName;
        @SerializedName("subjectId")
        @Expose
        public String subjectId;
        @SerializedName("startTime")
        @Expose
        public String startTime;
        @SerializedName("obtainedMarks")
        @Expose
        public String obtainedMarks;
        @SerializedName("minMarks")
        @Expose
        public String minMarks;
        @SerializedName("maxMarks")
        @Expose
        public String maxMarks;
        @SerializedName("endTime")
        @Expose
        public String endTime;
        @SerializedName("day")
        @Expose
        public String day;
        @SerializedName("date")
        @Expose
        public String date;
    }
}
