package school.campusconnect.datamodel.masterList;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

import school.campusconnect.datamodel.BaseResponse;
import school.campusconnect.datamodel.marksheet.MarkCardListResponse;

public class BoothMasterListModelResponse extends BaseResponse implements Serializable {

    @SerializedName("data")
    @Expose
    private ArrayList<BoothMasterListData> data;

    public ArrayList<BoothMasterListData> getData() {
        return data;
    }

    public void setData(ArrayList<BoothMasterListData> data) {
        this.data = data;
    }

    public static class BoothMasterListData implements Serializable
    {
        @SerializedName("teamId")
        @Expose
        public String teamId;

        @SerializedName("phone")
        @Expose
        public String phone;

        @SerializedName("name")
        @Expose
        public String name;

        @SerializedName("isTeamAdmin")
        @Expose
        public Boolean isTeamAdmin;

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
        public Boolean canAddUser;

        @SerializedName("boothId")
        @Expose
        public String boothId;

        @SerializedName("boothCommittee")
        @Expose
        public boothCommitteeData boothCommittee;

        @SerializedName("adminName")
        @Expose
        public String adminName;


        public String getTeamId() {
            return teamId;
        }

        public void setTeamId(String teamId) {
            this.teamId = teamId;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Boolean getTeamAdmin() {
            return isTeamAdmin;
        }

        public void setTeamAdmin(Boolean teamAdmin) {
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

        public Boolean getCanAddUser() {
            return canAddUser;
        }

        public void setCanAddUser(Boolean canAddUser) {
            this.canAddUser = canAddUser;
        }

        public String getBoothId() {
            return boothId;
        }

        public void setBoothId(String boothId) {
            this.boothId = boothId;
        }

        public boothCommitteeData getBoothCommittee() {
            return boothCommittee;
        }

        public void setBoothCommittee(boothCommitteeData boothCommittee) {
            this.boothCommittee = boothCommittee;
        }

        public String getAdminName() {
            return adminName;
        }

        public void setAdminName(String adminName) {
            this.adminName = adminName;
        }
    }

    public static class boothCommitteeData implements Serializable
    {
        @SerializedName("defaultCommittee")
        @Expose
        private Boolean defaultCommittee;

        @SerializedName("committeeName")
        @Expose
        private String committeeName;

        @SerializedName("committeeId")
        @Expose
        private String committeeId;

        public Boolean getDefaultCommittee() {
            return defaultCommittee;
        }

        public void setDefaultCommittee(Boolean defaultCommittee) {
            this.defaultCommittee = defaultCommittee;
        }

        public String getCommitteeName() {
            return committeeName;
        }

        public void setCommitteeName(String committeeName) {
            this.committeeName = committeeName;
        }

        public String getCommitteeId() {
            return committeeId;
        }

        public void setCommitteeId(String committeeId) {
            this.committeeId = committeeId;
        }
    }
}
