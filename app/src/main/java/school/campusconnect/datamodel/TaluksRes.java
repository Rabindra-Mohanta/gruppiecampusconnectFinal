package school.campusconnect.datamodel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class TaluksRes extends BaseResponse{
    public List<TalusData> data;

    public static class TalusData {
        @SerializedName("talukName")
        @Expose
        public String talukName;
        @SerializedName("image")
        @Expose
        public String image;
    }
}
