package school.campusconnect.datamodel.booths;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

import school.campusconnect.datamodel.BaseResponse;
import school.campusconnect.datamodel.masterList.VoterListModelResponse;

public class BoothVotersListResponse extends BaseResponse {

    private ArrayList<VoterData> data;

    public ArrayList<VoterData> getData() {
        return data;
    }

    public void setData(ArrayList<VoterData> data) {
        this.data = data;
    }

    public static class VoterData implements Serializable
    {
        @SerializedName("voterId")
        @Expose
        public String voterId;

        @SerializedName("userId")
        @Expose
        public String userId;

        @SerializedName("roleOnConstituency")
        @Expose
        public String roleOnConstituency;

        @SerializedName("phone")
        @Expose
        public String phone;

        @SerializedName("name")
        @Expose
        public String name;

        @SerializedName("image")
        @Expose
        public String image;

        @SerializedName("gender")
        @Expose
        public String gender;

        @SerializedName("dob")
        @Expose
        public String dob;

        @SerializedName("bloodGroup")
        @Expose
        public String bloodGroup;

        @SerializedName("allowedToAddUser")
        @Expose
        public boolean allowedToAddUser;

        @SerializedName("allowedToAddTeamPostComment")
        @Expose
        public boolean allowedToAddTeamPostComment;

        @SerializedName("allowedToAddTeamPost")
        @Expose
        public boolean allowedToAddTeamPost;

      /*  @SerializedName("address")
        @Expose
        public String address;*/

        @SerializedName("aadharNumber")
        @Expose
        public String aadharNumber;

        public String getVoterId() {
            return voterId;
        }

        public void setVoterId(String voterId) {
            this.voterId = voterId;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public String getRoleOnConstituency() {
            return roleOnConstituency;
        }

        public void setRoleOnConstituency(String roleOnConstituency) {
            this.roleOnConstituency = roleOnConstituency;
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

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }

        public String getGender() {
            return gender;
        }

        public void setGender(String gender) {
            this.gender = gender;
        }

        public String getDob() {
            return dob;
        }

        public void setDob(String dob) {
            this.dob = dob;
        }

        public String getBloodGroup() {
            return bloodGroup;
        }

        public void setBloodGroup(String bloodGroup) {
            this.bloodGroup = bloodGroup;
        }

        public boolean isAllowedToAddUser() {
            return allowedToAddUser;
        }

        public void setAllowedToAddUser(boolean allowedToAddUser) {
            this.allowedToAddUser = allowedToAddUser;
        }

        public boolean isAllowedToAddTeamPostComment() {
            return allowedToAddTeamPostComment;
        }

        public void setAllowedToAddTeamPostComment(boolean allowedToAddTeamPostComment) {
            this.allowedToAddTeamPostComment = allowedToAddTeamPostComment;
        }

        public boolean isAllowedToAddTeamPost() {
            return allowedToAddTeamPost;
        }

        public void setAllowedToAddTeamPost(boolean allowedToAddTeamPost) {
            this.allowedToAddTeamPost = allowedToAddTeamPost;
        }

    /*    public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }*/

        public String getAadharNumber() {
            return aadharNumber;
        }

        public void setAadharNumber(String aadharNumber) {
            this.aadharNumber = aadharNumber;
        }
    }
}
