package school.campusconnect.datamodel.booths;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

import school.campusconnect.datamodel.BaseResponse;
import school.campusconnect.datamodel.teamdiscussion.MyTeamData;

public class BoothResponse extends BaseResponse {
    private ArrayList<MyTeamData> data;

    public ArrayList<MyTeamData> getData() {
        return data;
    }

    public void setData(ArrayList<MyTeamData> data) {
        this.data = data;
    }


}