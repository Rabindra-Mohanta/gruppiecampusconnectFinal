package school.campusconnect.datamodel.masterList;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

import school.campusconnect.datamodel.BaseResponse;

public class VoterListModelResponse {

    public static class VoterListRes extends BaseResponse implements Serializable
    {
        @SerializedName("data")
        @Expose
        private ArrayList<VoterData> data;

        public ArrayList<VoterData> getData() {
            return data;
        }

        public void setData(ArrayList<VoterData> data) {
            this.data = data;
        }
    }
    public static class VoterData implements Serializable
    {
        @SerializedName("voterId")
        @Expose
        public String voterId;

        @SerializedName("teamId")
        @Expose
        public String teamId;

        @SerializedName("serialNumber")
        @Expose
        public String serialNumber;

        @SerializedName("phone")
        @Expose
        public String phone;

        @SerializedName("name")
        @Expose
        public String name;

        @SerializedName("image")
        @Expose
        public String image;

        @SerializedName("husbandName")
        @Expose
        public String husbandName;

        @SerializedName("groupId")
        @Expose
        public String groupId;

        @SerializedName("gender")
        @Expose
        public String gender;

        @SerializedName("fatherName")
        @Expose
        public String fatherName;

        @SerializedName("email")
        @Expose
        public String email;

        @SerializedName("dob")
        @Expose
        public String dob;

        @SerializedName("bloodGroup")
        @Expose
        public String bloodGroup;

        @SerializedName("age")
        @Expose
        public String age;

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

        public String getTeamId() {
            return teamId;
        }

        public void setTeamId(String teamId) {
            this.teamId = teamId;
        }

        public String getSerialNumber() {
            return serialNumber;
        }

        public void setSerialNumber(String serialNumber) {
            this.serialNumber = serialNumber;
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

        public String getHusbandName() {
            return husbandName;
        }

        public void setHusbandName(String husbandName) {
            this.husbandName = husbandName;
        }

        public String getGroupId() {
            return groupId;
        }

        public void setGroupId(String groupId) {
            this.groupId = groupId;
        }

        public String getGender() {
            return gender;
        }

        public void setGender(String gender) {
            this.gender = gender;
        }

        public String getFatherName() {
            return fatherName;
        }

        public void setFatherName(String fatherName) {
            this.fatherName = fatherName;
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

        public String getBloodGroup() {
            return bloodGroup;
        }

        public void setBloodGroup(String bloodGroup) {
            this.bloodGroup = bloodGroup;
        }

        public String getAge() {
            return age;
        }

        public void setAge(String age) {
            this.age = age;
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
    public static class AddVoterReq implements Serializable
    {
        @SerializedName("name")
        @Expose
        public String name;

        @SerializedName("countryCode")
        @Expose
        public String countryCode;

        @SerializedName("phone")
        @Expose
        public String phone;

        @SerializedName("image")
        @Expose
        public String image;

        @SerializedName("husbandName")
        @Expose
        public String husbandName;

        @SerializedName("fatherName")
        @Expose
        public String fatherName;

        @SerializedName("voterId")
        @Expose
        public String voterId;

        @SerializedName("serialNumber")
        @Expose
        public String serialNumber;

        @SerializedName("address")
        @Expose
        public String address;

        @SerializedName("dob")
        @Expose
        public String dob;

        @SerializedName("age")
        @Expose
        public String age;

        @SerializedName("gender")
        @Expose
        public String gender;

        @SerializedName("aadharNumber")
        @Expose
        public String aadharNumber;

        @SerializedName("bloodGroup")
        @Expose
        public String bloodGroup;

        @SerializedName("email")
        @Expose
        public String email;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

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

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }

        public String getHusbandName() {
            return husbandName;
        }

        public void setHusbandName(String husbandName) {
            this.husbandName = husbandName;
        }

        public String getFatherName() {
            return fatherName;
        }

        public void setFatherName(String fatherName) {
            this.fatherName = fatherName;
        }

        public String getVoterId() {
            return voterId;
        }

        public void setVoterId(String voterId) {
            this.voterId = voterId;
        }

        public String getSerialNumber() {
            return serialNumber;
        }

        public void setSerialNumber(String serialNumber) {
            this.serialNumber = serialNumber;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getDob() {
            return dob;
        }

        public void setDob(String dob) {
            this.dob = dob;
        }

        public String getAge() {
            return age;
        }

        public void setAge(String age) {
            this.age = age;
        }

        public String getGender() {
            return gender;
        }

        public void setGender(String gender) {
            this.gender = gender;
        }

        public String getAadharNumber() {
            return aadharNumber;
        }

        public void setAadharNumber(String aadharNumber) {
            this.aadharNumber = aadharNumber;
        }

        public String getBloodGroup() {
            return bloodGroup;
        }

        public void setBloodGroup(String bloodGroup) {
            this.bloodGroup = bloodGroup;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }
    }
}
