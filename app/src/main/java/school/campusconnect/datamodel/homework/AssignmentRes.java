package school.campusconnect.datamodel.homework;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

import school.campusconnect.datamodel.BaseResponse;

public class AssignmentRes extends BaseResponse {
    private ArrayList<AssignmentData> data;

    public ArrayList<AssignmentData> getData() {
        return data;
    }

    public void setData(ArrayList<AssignmentData> data) {
        this.data = data;
    }

    public static class AssignmentData implements Serializable {
        @SerializedName("submittedById")
        @Expose
        public String submittedById;
        @SerializedName("studentName")
        @Expose
        public String studentName;
        @SerializedName("studentImage")
        @Expose
        public String studentImage;
        @SerializedName("studentAssignmentId")
        @Expose
        public String studentAssignmentId;
        @SerializedName("insertedAt")
        @Expose
        public String insertedAt;
        @SerializedName("description")
        @Expose
        public String description;
        @SerializedName("reassignComment")
        @Expose
        public String reassignComment;
        @SerializedName("reassignedAt")
        @Expose
        public String reassignedAt;

        @SerializedName("assignmentVerified")
        @Expose
        public boolean assignmentVerified;
        @SerializedName("assignmentReassigned")
        @Expose
        public boolean assignmentReassigned;
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
