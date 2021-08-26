package school.campusconnect.datamodel.staff;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

import school.campusconnect.datamodel.BaseResponse;

public class StaffResponse extends BaseResponse {
    private ArrayList<StaffData> data;

    public ArrayList<StaffData> getData() {
        return data;
    }

    public void setData(ArrayList<StaffData> data) {
        this.data = data;
    }
    public static class StaffData{
        @SerializedName("userId")
        @Expose
        public String userId;
        @SerializedName("name")
        @Expose
        public String name;
        @SerializedName("image")
        @Expose
        public String image;
        @SerializedName("designation")
        @Expose
        public String designation;

        @SerializedName("countryCode")
        @Expose
        public String countryCode;
        @SerializedName("phone")
        @Expose
        public String phone;
        @SerializedName("email")
        @Expose
        public String email;
        @SerializedName("staffId")
        @Expose
        public String staffId;
        @SerializedName("DOJ")
        @Expose
        public String DOJ;
        @SerializedName("class")
        @Expose
        public String className;
        @SerializedName("gender")
        @Expose
        public String gender;
        @SerializedName("qualification")
        @Expose
        public String qualification;
        @SerializedName("address")
        @Expose
        public String address;
        @SerializedName("religion")
        @Expose
        public String religion;
        @SerializedName("caste")
        @Expose
        public String caste;
        @SerializedName("bloodGroup")
        @Expose
        public String bloodGroup;
        @SerializedName("aadharNumber")
        @Expose
        public String aadharNumber;
        public boolean isSelected;

        @SerializedName("isAllowedToPost")
        @Expose
        public boolean isPost;

        public String getCountryCode() {
            return countryCode;
        }

        public void setCountryCode(String countryCode) {
            this.countryCode = countryCode;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getStaffId() {
            return staffId;
        }

        public void setStaffId(String staffId) {
            this.staffId = staffId;
        }

        public String getDOJ() {
            return DOJ;
        }

        public void setDOJ(String DOJ) {
            this.DOJ = DOJ;
        }

        public String getClassName() {
            return className;
        }

        public void setClassName(String className) {
            this.className = className;
        }

        public String getGender() {
            return gender;
        }

        public void setGender(String gender) {
            this.gender = gender;
        }

        public String getQualification() {
            return qualification;
        }

        public void setQualification(String qualification) {
            this.qualification = qualification;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getReligion() {
            return religion;
        }

        public void setReligion(String religion) {
            this.religion = religion;
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

        public String getAadharNumber() {
            return aadharNumber;
        }

        public void setAadharNumber(String aadharNumber) {
            this.aadharNumber = aadharNumber;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
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

        public String getDesignation() {
            return designation;
        }

        public void setDesignation(String designation) {
            this.designation = designation;
        }
    }
}
