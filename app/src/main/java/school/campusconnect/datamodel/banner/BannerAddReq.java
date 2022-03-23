package school.campusconnect.datamodel.banner;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

import school.campusconnect.datamodel.BaseResponse;

public class BannerAddReq extends BaseResponse {

    @SerializedName("bannerFile")
    @Expose
    private ArrayList<String> bannerFile;

    @SerializedName("bannerFileType")
    @Expose
    private String bannerFileType;

    public ArrayList<String> getBannerFile() {
        return bannerFile;
    }

    public void setBannerFile(ArrayList<String> bannerFile) {
        this.bannerFile = bannerFile;
    }

    public String getBannerFileType() {
        return bannerFileType;
    }

    public void setBannerFileType(String bannerFileType) {
        this.bannerFileType = bannerFileType;
    }
}
