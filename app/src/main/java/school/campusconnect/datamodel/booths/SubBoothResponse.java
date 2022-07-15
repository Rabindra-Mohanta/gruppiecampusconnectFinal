package school.campusconnect.datamodel.booths;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

import school.campusconnect.datamodel.BaseResponse;


public class SubBoothResponse extends BaseResponse {

    public ArrayList<TeamData> data;

    public ArrayList<TeamData> getData() {
        return data;
    }

    public void setData(ArrayList<TeamData> data) {
        this.data = data;
    }
    public static class TeamData implements Serializable
    {
        @SerializedName("teamId")
        @Expose
        public String teamId;

        @SerializedName("name")
        @Expose
        public String name;

        public String getTeamId() {
            return teamId;
        }

        public void setTeamId(String teamId) {
            this.teamId = teamId;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
