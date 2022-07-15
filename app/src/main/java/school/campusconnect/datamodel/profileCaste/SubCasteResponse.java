package school.campusconnect.datamodel.profileCaste;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

import school.campusconnect.datamodel.BaseResponse;

public class SubCasteResponse extends BaseResponse {

    @SerializedName("data")
    @Expose
    private ArrayList<SubCasteData> subCasteData;

    public ArrayList<SubCasteData> getSubCasteData() {
        return subCasteData;
    }

    public void setSubCasteData(ArrayList<SubCasteData> subCasteData) {
        this.subCasteData = subCasteData;
    }

    public static class SubCasteData implements Serializable
    {
        @SerializedName("subCasteName")
        @Expose
        private String subCasteName;

        @SerializedName("subCasteId")
        @Expose
        private String subCasteId;

        public String getSubCasteName() {
            return subCasteName;
        }

        public void setSubCasteName(String subCasteName) {
            this.subCasteName = subCasteName;
        }

        public String getSubCasteId() {
            return subCasteId;
        }

        public void setSubCasteId(String subCasteId) {
            this.subCasteId = subCasteId;
        }
    }
}
