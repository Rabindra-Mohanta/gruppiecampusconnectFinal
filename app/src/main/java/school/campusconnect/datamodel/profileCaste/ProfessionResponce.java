package school.campusconnect.datamodel.profileCaste;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

import school.campusconnect.datamodel.BaseResponse;

public class ProfessionResponce extends BaseResponse {

    @SerializedName("data")
    @Expose
    private ArrayList<ProfessionData> professionDataList;

    public ArrayList<ProfessionData> getProfessionDataList() {
        return professionDataList;
    }

    public void setProfessionDataList(ArrayList<ProfessionData> professionDataList) {
        this.professionDataList = professionDataList;
    }

    public static class ProfessionData implements Serializable
    {
        @SerializedName("profession")
        @Expose
        public  ArrayList<String> profession;

        public ArrayList<String> getProfession() {
            return profession;
        }

        public void setProfession(ArrayList<String> profession) {
            this.profession = profession;
        }
    }
}
