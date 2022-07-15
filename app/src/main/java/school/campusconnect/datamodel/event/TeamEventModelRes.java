package school.campusconnect.datamodel.event;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

import school.campusconnect.datamodel.BaseResponse;

public class TeamEventModelRes extends BaseResponse {

    @SerializedName("data")
    @Expose
    private ArrayList<TeamEventData> data;

    public ArrayList<TeamEventData> getData() {
        return data;
    }

    public void setData(ArrayList<TeamEventData> data) {
        this.data = data;
    }

    public static class TeamEventData implements Serializable
    {
        @SerializedName("teamId")
        @Expose
        private String teamId;

        @SerializedName("members")
        @Expose
        private int members;

        @SerializedName("lastUserToTeamUpdatedAtEventAt")
        @Expose
        private String lastUserToTeamUpdatedAtEventAt;

        @SerializedName("lastCommitteeForBoothUpdatedEventAt")
        @Expose
        private String lastCommitteeForBoothUpdatedEventAt;

        public String getTeamId() {
            return teamId;
        }

        public void setTeamId(String teamId) {
            this.teamId = teamId;
        }

        public int getMembers() {
            return members;
        }

        public void setMembers(int members) {
            this.members = members;
        }

        public String getLastUserToTeamUpdatedAtEventAt() {
            return lastUserToTeamUpdatedAtEventAt;
        }

        public void setLastUserToTeamUpdatedAtEventAt(String lastUserToTeamUpdatedAtEventAt) {
            this.lastUserToTeamUpdatedAtEventAt = lastUserToTeamUpdatedAtEventAt;
        }

        public String getLastCommitteeForBoothUpdatedEventAt() {
            return lastCommitteeForBoothUpdatedEventAt;
        }

        public void setLastCommitteeForBoothUpdatedEventAt(String lastCommitteeForBoothUpdatedEventAt) {
            this.lastCommitteeForBoothUpdatedEventAt = lastCommitteeForBoothUpdatedEventAt;
        }
    }
}
