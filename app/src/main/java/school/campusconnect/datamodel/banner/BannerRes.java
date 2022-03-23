package school.campusconnect.datamodel.banner;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

import school.campusconnect.datamodel.BaseResponse;
import school.campusconnect.datamodel.baseTeam.BaseTeamv2Response;
import school.campusconnect.datamodel.teamdiscussion.MyTeamData;

public class BannerRes extends BaseResponse {

    @SerializedName("data")
    @Expose
    private ArrayList<BannerData> BannerData;

    public ArrayList<BannerRes.BannerData> getBannerData() {
        return BannerData;
    }

    public void setBannerData(ArrayList<BannerRes.BannerData> bannerData) {
        BannerData = bannerData;
    }

    public static class BannerData implements Serializable
    {
        @SerializedName("updatedAt")
        @Expose
        public String updatedAt;

        @SerializedName("fileType")
        @Expose
        public String fileType;

        @SerializedName("fileName")
        @Expose
        public ArrayList<String> fileName;

        public String getUpdatedAt() {
            return updatedAt;
        }

        public void setUpdatedAt(String updatedAt) {
            this.updatedAt = updatedAt;
        }

        public String getFileType() {
            return fileType;
        }

        public void setFileType(String fileType) {
            this.fileType = fileType;
        }

        public ArrayList<String> getFileName() {
            return fileName;
        }

        public void setFileName(ArrayList<String> fileName) {
            this.fileName = fileName;
        }
    }
}
