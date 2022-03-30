package school.campusconnect.datamodel.booths;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

import school.campusconnect.datamodel.BaseResponse;

public class MyTeamSubBoothResponse extends BaseResponse {

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

        @SerializedName("userId")
        @Expose
        public String userId;

        @SerializedName("name")
        @Expose
        public String name;

        @SerializedName("subBoothId")
        @Expose
        public String subBoothId;

        @SerializedName("members")
        @Expose
        public int members;

        @SerializedName("isTeamAdmin")
        @Expose
        public boolean isTeamAdmin;

        @SerializedName("image")
        @Expose
        public String image;

        @SerializedName("groupId")
        @Expose
        public String groupId;

        @SerializedName("category")
        @Expose
        public String category;

        @SerializedName("canAddUser")
        @Expose
        public boolean canAddUser;

        @SerializedName("allowTeamPostCommentAll")
        @Expose
        public boolean allowTeamPostCommentAll;

        @SerializedName("allowTeamPostAll")
        @Expose
        public boolean allowTeamPostAll;


        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

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

        public String getSubBoothId() {
            return subBoothId;
        }

        public void setSubBoothId(String subBoothId) {
            this.subBoothId = subBoothId;
        }

        public int getMembers() {
            return members;
        }

        public void setMembers(int members) {
            this.members = members;
        }

        public boolean isTeamAdmin() {
            return isTeamAdmin;
        }

        public void setTeamAdmin(boolean teamAdmin) {
            isTeamAdmin = teamAdmin;
        }

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }

        public String getGroupId() {
            return groupId;
        }

        public void setGroupId(String groupId) {
            this.groupId = groupId;
        }

        public String getCategory() {
            return category;
        }

        public void setCategory(String category) {
            this.category = category;
        }

        public boolean isCanAddUser() {
            return canAddUser;
        }

        public void setCanAddUser(boolean canAddUser) {
            this.canAddUser = canAddUser;
        }

        public boolean isAllowTeamPostCommentAll() {
            return allowTeamPostCommentAll;
        }

        public void setAllowTeamPostCommentAll(boolean allowTeamPostCommentAll) {
            this.allowTeamPostCommentAll = allowTeamPostCommentAll;
        }

        public boolean isAllowTeamPostAll() {
            return allowTeamPostAll;
        }

        public void setAllowTeamPostAll(boolean allowTeamPostAll) {
            this.allowTeamPostAll = allowTeamPostAll;
        }
    }
}
