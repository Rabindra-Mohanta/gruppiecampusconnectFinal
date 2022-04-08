package school.campusconnect.datamodel.booths;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

import school.campusconnect.datamodel.BaseResponse;

public class VoterProfileResponse extends BaseResponse {

    private VoterData data;

    public VoterData getData() {
        return data;
    }

    public void setData(VoterData data) {
        this.data = data;
    }

    public static class VoterData implements Serializable {


        @SerializedName("userId")
        @Expose
        public String userId;

        @SerializedName("updatedAt")
        @Expose
        public String updatedAt;

        @SerializedName("voterId")
        @Expose
        public String voterId;

        @SerializedName("phone")
        @Expose
        public String phone;

        @SerializedName("roleOnConstituency")
        @Expose
        public String roleOnConstituency;

        @SerializedName("name")
        @Expose
        public String name;

        @SerializedName("insertedAt")
        @Expose
        public String insertedAt;

        @SerializedName("subcaste")
        @Expose
        public String subcaste;

        @SerializedName("religion")
        @Expose
        public String religion;

        @SerializedName("qualification")
        @Expose
        public String qualification;

        @SerializedName("email")
        @Expose
        public String email;

        @SerializedName("dob")
        @Expose
        public String dob;

        @SerializedName("designation")
        @Expose
        public String designation;

        @SerializedName("caste")
        @Expose
        public String caste;

        @SerializedName("bloodGroup")
        @Expose
        public String bloodGroup;

        @SerializedName("address")
        @Expose
        public String address;

        @SerializedName("gender")
        @Expose
        public String gender;

        @SerializedName("image")
        @Expose
        public String image;

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

        public String getVoterId() {
            return voterId;
        }

        public void setVoterId(String voterId) {
            this.voterId = voterId;
        }

        public String getSubcaste() {
            return subcaste;
        }

        public void setSubcaste(String subcaste) {
            this.subcaste = subcaste;
        }

        public String getReligion() {
            return religion;
        }

        public void setReligion(String religion) {
            this.religion = religion;
        }

        public String getQualification() {
            return qualification;
        }

        public void setQualification(String qualification) {
            this.qualification = qualification;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getDob() {
            return dob;
        }

        public void setDob(String dob) {
            this.dob = dob;
        }

        public String getDesignation() {
            return designation;
        }

        public void setDesignation(String designation) {
            this.designation = designation;
        }

        public String getCaste() {
            return caste;
        }

        public void setCaste(String caste) {
            this.caste = caste;
        }

        public String getBloodGroup() {
            return bloodGroup;
        }

        public void setBloodGroup(String bloodGroup) {
            this.bloodGroup = bloodGroup;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public String getUpdatedAt() {
            return updatedAt;
        }

        public void setUpdatedAt(String updatedAt) {
            this.updatedAt = updatedAt;
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

        public String getRoleOnConstituency() {
            return roleOnConstituency;
        }

        public void setRoleOnConstituency(String roleOnConstituency) {
            this.roleOnConstituency = roleOnConstituency;
        }

        public String getInsertedAt() {
            return insertedAt;
        }

        public void setInsertedAt(String insertedAt) {
            this.insertedAt = insertedAt;
        }
    }

}
