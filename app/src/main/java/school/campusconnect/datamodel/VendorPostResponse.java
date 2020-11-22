package school.campusconnect.datamodel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class VendorPostResponse extends BaseResponse {
    public int totalNumberOfPages;

    public ArrayList<VendorPostData> data;

    public class VendorPostData {
        @SerializedName("updatedAt")
        @Expose
        public String updatedAt;

        @SerializedName("vendor")
        @Expose
        public String vendor;

        @SerializedName("vendorId")
        @Expose
        public String vendorId;

        @SerializedName("phone")
        @Expose
        public String phone;

        @SerializedName("groupId")
        @Expose
        public String groupId;
        @SerializedName("fileType")
        @Expose
        public String fileType="";
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
