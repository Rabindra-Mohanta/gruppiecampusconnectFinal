package school.campusconnect.datamodel.test_exam;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

import school.campusconnect.datamodel.BaseResponse;

public class OfflineTestRes extends BaseResponse {
    @SerializedName("data")
    @Expose
    public ArrayList<ScheduleTestData> data;


    public static class ScheduleTestData implements Serializable {
        @SerializedName("updatedAt")
        @Expose
        public String updatedAt;
        @SerializedName("totalMinMarks")
        @Expose
        public String totalMinMarks;
        @SerializedName("totalMaxMarks")
        @Expose
        public String totalMaxMarks;
        @SerializedName("title")
        @Expose
        public String title;
        @SerializedName("resultDate")
        @Expose
        public String resultDate;
        @SerializedName("offlineTestExamId")
        @Expose
        public String offlineTestExamId;
        @SerializedName("isActive")
        @Expose
        public boolean isActive;
        @SerializedName("isApproved")
        @Expose
        public boolean isApproved;
        @SerializedName("insertedAt")
        @Expose
        public boolean insertedAt;
        @SerializedName("subjectMarksDetails")
        @Expose
        public ArrayList<TestOfflineSubjectMark> subjectMarksDetails;
    }
}
