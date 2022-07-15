package school.campusconnect.datamodel.profileCaste;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

import school.campusconnect.datamodel.BaseResponse;

public class CasteResponse extends BaseResponse {

    @SerializedName("data")
    @Expose
    private ArrayList<CasteData> casteData;

    public ArrayList<CasteData> getCasteData() {
        return casteData;
    }

    public void setCasteData(ArrayList<CasteData> casteData) {
        this.casteData = casteData;
    }

    public static class CasteData implements Serializable
    {
        @SerializedName("categoryName")
        @Expose
        private String categoryName;

        @SerializedName("casteName")
        @Expose
        private String casteName;

        @SerializedName("casteId")
        @Expose
        private String casteId;

        public String getCategoryName() {
            return categoryName;
        }

        public void setCategoryName(String categoryName) {
            this.categoryName = categoryName;
        }

        public String getCasteName() {
            return casteName;
        }

        public void setCasteName(String casteName) {
            this.casteName = casteName;
        }

        public String getCasteId() {
            return casteId;
        }

        public void setCasteId(String casteId) {
            this.casteId = casteId;
        }
    }
}
