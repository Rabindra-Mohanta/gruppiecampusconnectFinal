package school.campusconnect.datamodel.homework;

import androidx.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

import school.campusconnect.datamodel.BaseResponse;

public class HwRes extends BaseResponse {
    private ArrayList<HwData> data;

    public ArrayList<HwData> getData() {
        return data;
    }

    public void setData(ArrayList<HwData> data) {
        this.data = data;
    }

    public static class HwData implements Serializable {
        @SerializedName("topic")
        @Expose
        public String topic;
        @SerializedName("teamId")
        @Expose
        public String teamId;
        @SerializedName("subjectId")
        @Expose
        public String subjectId;
        @SerializedName("lastSubmissionDate")
        @Expose
        public String lastSubmissionDate;
        @SerializedName("groupId")
        @Expose
        public String groupId;
        @SerializedName("description")
        @Expose
        public String description;
        @SerializedName("createdByName")
        @Expose
        public String createdByName;
        @SerializedName("createdByImage")
        @Expose
        public String createdByImage;
        @SerializedName("createdById")
        @Expose
        public String createdById;
        @SerializedName("canPost")
        @Expose
        public boolean canPost;
        @SerializedName("assignmentId")
        @Expose
        public String assignmentId;
        @SerializedName("studentAssignmentId")
        @Expose
        public String studentAssignmentId;

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
    }
}
