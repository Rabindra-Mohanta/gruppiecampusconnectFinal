package school.campusconnect.datamodel.test_exam;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

import school.campusconnect.datamodel.BaseResponse;

public class TestPaperRes extends BaseResponse {
    private ArrayList<TestPaperData> data;

    public ArrayList<TestPaperData> getData() {
        return data;
    }

    public void setData(ArrayList<TestPaperData> data) {
        this.data = data;
    }

    public static class TestPaperData implements Serializable {
        @SerializedName("testexamVerified")
        @Expose
        public boolean testexamVerified;
        @SerializedName("verifiedComment")
        @Expose
        public String verifiedComment;
        @SerializedName("submittedById")
        @Expose
        public String submittedById;
        @SerializedName("studentTestExamId")
        @Expose
        public String studentTestExamId;
        @SerializedName("studentName")
        @Expose
        public String studentName;
        @SerializedName("studentImage")
        @Expose
        public String studentImage;
        @SerializedName("insertedAt")
        @Expose
        public String insertedAt;
        @SerializedName("description")
        @Expose
        public String description;

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

        @SerializedName("userId")
        @Expose
        public String userId;
        @SerializedName("studentDbId")
        @Expose
        public String studentDbId;
        @SerializedName("rollNumber")
        @Expose
        public String rollNumber;
    }
}
