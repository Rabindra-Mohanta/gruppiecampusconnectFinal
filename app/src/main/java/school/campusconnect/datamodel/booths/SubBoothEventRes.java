package school.campusconnect.datamodel.booths;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

import school.campusconnect.datamodel.BaseResponse;

public class SubBoothEventRes extends BaseResponse {

    public ArrayList<SubBoothEvent> data;



    public static class SubBoothEvent implements Serializable
    {
        public String lastUpdatedSubBoothTeamTime;
        public ArrayList<SubBoothEventData> subBoothTeamsLastPostEventAt;
    }

    public static class SubBoothEventData implements Serializable
    {
        @SerializedName("teamId")
        @Expose
        private String teamId;

        @SerializedName("members")
        @Expose
        private String members;

        @SerializedName("lastTeamPostAt")
        @Expose
        private String lastTeamPostAt;

        public String getTeamId() {
            return teamId;
        }

        public void setTeamId(String teamId) {
            this.teamId = teamId;
        }

        public String getMembers() {
            return members;
        }

        public void setMembers(String members) {
            this.members = members;
        }

        public String getLastTeamPostAt() {
            return lastTeamPostAt;
        }

        public void setLastTeamPostAt(String lastTeamPostAt) {
            this.lastTeamPostAt = lastTeamPostAt;
        }
    }
}
