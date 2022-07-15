package school.campusconnect.datamodel.masterList;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

import school.campusconnect.datamodel.BaseResponse;

public class WorkerListResponse extends BaseResponse implements Serializable {

    @SerializedName("data")
    @Expose
    private ArrayList<WorkerData> data;

    public ArrayList<WorkerData> getData() {
        return data;
    }

    public void setData(ArrayList<WorkerData> data) {
        this.data = data;
    }

    public static class WorkerData implements Serializable
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
        public Boolean allowedToAddUser;

        @SerializedName("allowedToAddTeamPostComment")
        @Expose
        public Boolean allowedToAddTeamPostComment;

        @SerializedName("allowedToAddTeamPost")
        @Expose
        public Boolean allowedToAddTeamPost;

        @SerializedName("address")
        @Expose
        public String address;

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

        public Boolean getAllowedToAddUser() {
            return allowedToAddUser;
        }

        public void setAllowedToAddUser(Boolean allowedToAddUser) {
            this.allowedToAddUser = allowedToAddUser;
        }

        public Boolean getAllowedToAddTeamPostComment() {
            return allowedToAddTeamPostComment;
        }

        public void setAllowedToAddTeamPostComment(Boolean allowedToAddTeamPostComment) {
            this.allowedToAddTeamPostComment = allowedToAddTeamPostComment;
        }

        public Boolean getAllowedToAddTeamPost() {
            return allowedToAddTeamPost;
        }

        public void setAllowedToAddTeamPost(Boolean allowedToAddTeamPost) {
            this.allowedToAddTeamPost = allowedToAddTeamPost;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getAadharNumber() {
            return aadharNumber;
        }

        public void setAadharNumber(String aadharNumber) {
            this.aadharNumber = aadharNumber;
        }
    }
}
