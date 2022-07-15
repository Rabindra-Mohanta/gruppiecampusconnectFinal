package school.campusconnect.datamodel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class CoursePostResponse extends BaseResponse {
    public int totalNumberOfPages;

    public ArrayList<CoursePostData> data;

    public static class CoursePostData {

        @SerializedName("updatedAt")
        @Expose
        public String updatedAt;

        @SerializedName("courseName")
        @Expose
        public String courseName;

        @SerializedName("courseId")
        @Expose
        public String courseId;

        @SerializedName("description")
        @Expose
        public String description;

        @SerializedName("subjectList")
        @Expose
        public ArrayList<SubjectData> subjectList;

        @SerializedName("totalCutOff")
        @Expose
        public Boolean totalCutOff;

        @SerializedName("individualSubjectCutOff")
        @Expose
        public Boolean individualSubjectCutOff;

        @SerializedName("totalCutOffPercentage")
        @Expose
        public String totalCutOffPercentage;
    }

    public static class SubjectData
    {
        public SubjectData(String subjectName) {
            this.subjectName = subjectName;
        }

        @SerializedName("subjectName")
        @Expose
        public String subjectName;

        @SerializedName("minMarks")
        @Expose
        public String minMarks;

    }
}
