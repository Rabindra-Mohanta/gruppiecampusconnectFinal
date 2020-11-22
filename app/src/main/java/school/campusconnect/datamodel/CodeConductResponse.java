package school.campusconnect.datamodel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class CodeConductResponse extends BaseResponse {
    public int totalNumberOfPages;

    public ArrayList<CodeConductData> data;

    public class CodeConductData {
        @SerializedName("updatedAt")
        @Expose
        public String updatedAt;

        @SerializedName("title")
        @Expose
        public String title;

        @SerializedName("cocId")
        @Expose
        public String cocId;

        @SerializedName("groupId")
        @Expose
        public String groupId;
        @SerializedName("fileType")
        @Expose
        public String fileType;
        @SerializedName("description")
        @Expose
        public String description;

        @SerializedName("fileName")
        @Expose
        public ArrayList<String> fileName;

        @SerializedName("thumbnailImage")
        @Expose
        public ArrayList<String> thumbnailImage;



        @SerializedName("createdAt")
        @Expose
        public String createdAt;

        @SerializedName("canEdit")
        @Expose
        public Boolean canEdit;
    }
}
