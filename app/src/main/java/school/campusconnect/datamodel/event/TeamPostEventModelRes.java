package school.campusconnect.datamodel.event;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

import school.campusconnect.datamodel.BaseResponse;

public class TeamPostEventModelRes extends BaseResponse {

    @SerializedName("data")
    @Expose
    private ArrayList<TeamPostEventData> data;

    public ArrayList<TeamPostEventData> getData() {
        return data;
    }

    public void setData(ArrayList<TeamPostEventData> data) {
        this.data = data;
    }

    public static class TeamPostEventData implements Serializable
    {
        @SerializedName("teamId")
        @Expose
        private String teamId;

        @SerializedName("members")
        @Expose
        private int members;

        @SerializedName("lastTeamPostAt")
        @Expose
        private String lastTeamPostAt;

        @SerializedName("canPost")
        @Expose
        private boolean canPost;

        @SerializedName("canComment")
        @Expose
        private boolean canComment;

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

        public String getLastTeamPostAt() {
            return lastTeamPostAt;
        }

        public void setLastTeamPostAt(String lastTeamPostAt) {
            this.lastTeamPostAt = lastTeamPostAt;
        }

        public boolean isCanPost() {
            return canPost;
        }

        public void setCanPost(boolean canPost) {
            this.canPost = canPost;
        }

        public boolean isCanComment() {
            return canComment;
        }

        public void setCanComment(boolean canComment) {
            this.canComment = canComment;
        }
    }
}
