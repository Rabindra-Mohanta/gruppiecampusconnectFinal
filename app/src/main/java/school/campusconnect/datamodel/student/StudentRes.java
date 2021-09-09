package school.campusconnect.datamodel.student;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

import school.campusconnect.datamodel.BaseResponse;

public class StudentRes extends BaseResponse {
    private ArrayList<StudentData> data;

    public ArrayList<StudentData> getData() {
        return data;
    }

    public void setData(ArrayList<StudentData> data) {
        this.data = data;
    }

    public static class StudentData {
        public boolean isSelected;
        @SerializedName("userId")
        @Expose
        private String userId;
        @SerializedName("teamId")
        @Expose
        private String teamId;
        @SerializedName("studentId")
        @Expose
        public String studentId;
        @SerializedName("studentDbId")
        @Expose
        public String studentDbId;
        @SerializedName("section")
        @Expose
        public String section;
        @SerializedName("rollNumber")
        @Expose
        public String rollNumber;
        @SerializedName("name")
        @Expose
        public String name;
        @SerializedName("phone")
        @Expose
        public String phone;
        @SerializedName("motherNumber")
        @Expose
        public String motherNumber;
        @SerializedName("motherName")
        @Expose
        public String motherName;
        @SerializedName("image")
        @Expose
        public String image;
        @SerializedName("groupId")
        @Expose
        public String groupId;
        @SerializedName("fatherNumber")
        @Expose
        public String fatherNumber;
        @SerializedName("fatherName")
        @Expose
        public String fatherName;
        @SerializedName("email")
        @Expose
        public String email;
        @SerializedName("doj")
        @Expose
        public String doj;
        @SerializedName("dob")
        @Expose
        public String dob;
        @SerializedName("class")
        @Expose
        public String _class;
        @SerializedName("admissionNumber")
        @Expose
        public String admissionNumber;
        @SerializedName("address")
        @Expose
        public String address;


        @SerializedName("countryCode")
        @Expose
        public String countryCode;

        public String get_class() {
            return _class;
        }

        public void set_class(String _class) {
            this._class = _class;
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

        public String getStudentId() {
            return studentId;
        }

        public void setStudentId(String studentId) {
            this.studentId = studentId;
        }

        public String getSection() {
            return section;
        }

        public void setSection(String section) {
            this.section = section;
        }

        public String getRollNumber() {
            return rollNumber;
        }

        public void setRollNumber(String rollNumber) {
            this.rollNumber = rollNumber;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getMotherNumber() {
            return motherNumber;
        }

        public void setMotherNumber(String motherNumber) {
            this.motherNumber = motherNumber;
        }

        public String getMotherName() {
            return motherName;
        }

        public void setMotherName(String motherName) {
            this.motherName = motherName;
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

        public String getFatherNumber() {
            return fatherNumber;
        }

        public void setFatherNumber(String fatherNumber) {
            this.fatherNumber = fatherNumber;
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

        public String getDoj() {
            return doj;
        }

        public void setDoj(String doj) {
            this.doj = doj;
        }

        public String getDob() {
            return dob;
        }

        public void setDob(String dob) {
            this.dob = dob;
        }

        public String getClass_() {
            return _class;
        }

        public void setClass_(String _class) {
            this._class = _class;
        }

        public String getAdmissionNumber() {
            return admissionNumber;
        }

        public void setAdmissionNumber(String admissionNumber) {
            this.admissionNumber = admissionNumber;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }
    }
}
