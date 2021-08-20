package school.campusconnect.datamodel.test_exam;

import androidx.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

import school.campusconnect.datamodel.BaseResponse;

public class TestExamRes extends BaseResponse {
    private ArrayList<TestExamData> data;

    public ArrayList<TestExamData> getData() {
        return data;
    }

    public void setData(ArrayList<TestExamData> data) {
        this.data = data;
    }

    public static class TestExamData implements Serializable {
        @SerializedName("topic")
        @Expose
        public String topicName;
        @SerializedName("createdByName")
        @Expose
        public String createdByName;
        @SerializedName("testExamId")
        @Expose
        public String testExamId;
        @SerializedName("testStartTime")
        @Expose
        public String testStartTime;
        @SerializedName("testEndTime")
        @Expose
        public String testEndTime;
        @SerializedName("testDate")
        @Expose
        public String testDate;
        @SerializedName("teamId")
        @Expose
        public String teamId;
        @SerializedName("groupId")
        @Expose
        public String groupId;
        @SerializedName("subjectId")
        @Expose
        public String subjectId;
        @SerializedName("lastSubmissionTime")
        @Expose
        public String lastSubmissionTime;

        @SerializedName("fileType")
        @Expose
        public String fileType;
        @SerializedName("fileName")
        @Expose
        public ArrayList<String> fileName;
        @SerializedName("thumbnailImage")
        @Expose
        public ArrayList<String> thumbnailImage;
        @SerializedName("thumbnail")
        @Expose
        public String thumbnail;
        @SerializedName("video")
        @Expose
        public String video;
        @SerializedName("createdById")
        @Expose
        public String createdById;
        @SerializedName("canPost")
        @Expose
        public boolean canPost;
    }
}
