package school.campusconnect.datamodel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class TimeTableRes extends BaseResponse {

    public int totalNumberOfPages;

    public ArrayList<TimeTableData> data;

    public class TimeTableData {
        @SerializedName("updatedAt")
        @Expose
        public String updatedAt;

        @SerializedName("title")
        @Expose
        public String title;


        @SerializedName("timeTableId")
        @Expose
        public String timeTableId;

        @SerializedName("phone")
        @Expose
        public String phone;

        @SerializedName("groupId")
        @Expose
        public String groupId;
        @SerializedName("fileType")
        @Expose
        public String fileType;
        @SerializedName("createdByImage")
        @Expose
        public String createdByImage;
        @SerializedName("createdById")
        @Expose
        public String createdById;
        @SerializedName("createdBy")
        @Expose
        public String createdBy;

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
