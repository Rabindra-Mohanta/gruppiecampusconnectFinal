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

        @SerializedName("gender")
        @Expose
        public String gender;

        @SerializedName("familyIncome")
        @Expose
        private String familyIncome;

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

        @SerializedName("aadharNumber")
        @Expose
        public String aadharNumber;

        @SerializedName("religion")
        @Expose
        public String religion;

        @SerializedName("caste")
        @Expose
        public String caste;

        @SerializedName("subCaste")
        @Expose
        private String subCaste;

        @SerializedName("satsNo")
        @Expose
        public String satsNo;

        @SerializedName("bloodGroup")
        @Expose
        public String bloodGroup;

        @SerializedName("countryCode")
        @Expose
        public String countryCode;

        @SerializedName("userDownloadedApp")
        @Expose
        public Boolean userDownloadedApp;

        @SerializedName("gruppieRollNumber")
        @Expose
        public String gruppieRollNumber;

        @SerializedName("studentRegId")
        @Expose
        public String studentRegId;

        @SerializedName("disability")
        @Expose
        private String disability;

        @SerializedName("motherOccupation")
        @Expose
        private String motherOccupation;

        @SerializedName("motherEducation")
        @Expose
        private String motherEducation;

        @SerializedName("fatherOccupation")
        @Expose
        private String fatherOccupation;

        @SerializedName("fatherEducation")
        @Expose
        private String fatherEducation;

        @SerializedName("nationality")
        @Expose
        private String nationality;

        @SerializedName("category")
        @Expose
        private String category;

        public String getFamilyIncome() {
            return familyIncome;
        }

        public void setFamilyIncome(String familyIncome) {
            this.familyIncome = familyIncome;
        }

        public String getSubCaste() {
            return subCaste;
        }

        public void setSubCaste(String subCaste) {
            this.subCaste = subCaste;
        }

        public String getGender() {
            return gender;
        }

        public void setGender(String gender) {
            this.gender = gender;
        }

        public String getSatsNo() {
            return satsNo;
        }

        public void setSatsNo(String satsNo) {
            this.satsNo = satsNo;
        }

        public String getStudentDbId() {
            return studentDbId;
        }

        public void setStudentDbId(String studentDbId) {
            this.studentDbId = studentDbId;
        }

        public Boolean getUserDownloadedApp() {
            return userDownloadedApp;
        }

        public void setUserDownloadedApp(Boolean userDownloadedApp) {
            this.userDownloadedApp = userDownloadedApp;
        }

        public String getGruppieRollNumber() {
            return gruppieRollNumber;
        }

        public void setGruppieRollNumber(String gruppieRollNumber) {
            this.gruppieRollNumber = gruppieRollNumber;
        }

        public String getStudentRegId() {
            return studentRegId;
        }

        public void setStudentRegId(String studentRegId) {
            this.studentRegId = studentRegId;
        }

        public String getDisability() {
            return disability;
        }

        public void setDisability(String disability) {
            this.disability = disability;
        }

        public String getMotherOccupation() {
            return motherOccupation;
        }

        public void setMotherOccupation(String motherOccupation) {
            this.motherOccupation = motherOccupation;
        }

        public String getMotherEducation() {
            return motherEducation;
        }

        public void setMotherEducation(String motherEducation) {
            this.motherEducation = motherEducation;
        }

        public String getFatherOccupation() {
            return fatherOccupation;
        }

        public void setFatherOccupation(String fatherOccupation) {
            this.fatherOccupation = fatherOccupation;
        }

        public String getFatherEducation() {
            return fatherEducation;
        }

        public void setFatherEducation(String fatherEducation) {
            this.fatherEducation = fatherEducation;
        }

        public String getNationality() {
            return nationality;
        }

        public void setNationality(String nationality) {
            this.nationality = nationality;
        }

        public String getCategory() {
            return category;
        }

        public void setCategory(String category) {
            this.category = category;
        }

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

        public String getBloodGroup() {
            return bloodGroup;
        }

        public void setBloodGroup(String bloodGroup) {
            this.bloodGroup = bloodGroup;
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

        public String getAadharNumber() {
            return aadharNumber;
        }

        public void setAadharNumber(String aadharNumber) {
            this.aadharNumber = aadharNumber;
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

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }
    }
}
