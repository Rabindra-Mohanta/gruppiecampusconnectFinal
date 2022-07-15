package school.campusconnect.datamodel.profileCaste;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

import school.campusconnect.datamodel.BaseResponse;

public class ReligionResponse extends BaseResponse {

    @SerializedName("data")
    @Expose
    private ArrayList<ReligionData> religionData;

    public ArrayList<ReligionData> getReligionData() {
        return religionData;
    }

    public void setReligionData(ArrayList<ReligionData> religionData) {
        this.religionData = religionData;
    }

    public static class ReligionData implements Serializable
    {
        @SerializedName("religion")
        @Expose
        private ArrayList<String> religionList;

        public ArrayList<String> getReligionList() {
            return religionList;
        }

        public void setReligionList(ArrayList<String> religionList) {
            this.religionList = religionList;
        }
    }
}
