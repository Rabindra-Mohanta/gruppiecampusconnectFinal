package school.campusconnect.datamodel.fees;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.HashMap;

import school.campusconnect.datamodel.BaseResponse;
import school.campusconnect.datamodel.student.StudentRes;

public class StudentFeesRes extends BaseResponse {
    private ArrayList<StudentFees> data;

    public ArrayList<StudentFees> getData() {
        return data;
    }

    public void setData(ArrayList<StudentFees> data) {
        this.data = data;
    }

    public static class StudentFees {
        @SerializedName("userId")
        @Expose
        public String userId;
        @SerializedName("totalFee")
        @Expose
        public String totalFee;
        @SerializedName("totalBalanceAmount")
        @Expose
        public String totalBalanceAmount;
        @SerializedName("totalAmountPaid")
        @Expose
        public String totalAmountPaid;
        @SerializedName("teamId")
        @Expose
        public String teamId;
        @SerializedName("studentRollNumber")
        @Expose
        public String studentRollNumber;
        @SerializedName("studentName")
        @Expose
        public String studentName;
        @SerializedName("phone")
        @Expose
        public String phone;
        @SerializedName("studentImage")
        @Expose
        public String studentImage;
        @SerializedName("studentDbId")
        @Expose
        public String studentDbId;
        @SerializedName("groupId")
        @Expose
        public String groupId;
        @SerializedName("feeTitle")
        @Expose
        public String feeTitle;
        @SerializedName("feePaidDetails")
        @Expose
        public ArrayList<FeePaidDetails> feePaidDetails;
        @SerializedName("feeDetails")
        @Expose
        public HashMap<String,String> feeDetails;
        @SerializedName("dueDates")
        @Expose
        public ArrayList<DueDates> dueDates;

        public String getTotalFee() {
            return totalFee;
        }

        public void setTotalFee(String totalFee) {
            this.totalFee = totalFee;
        }

        public String getTotalBalanceAmount() {
            return totalBalanceAmount;
        }

        public void setTotalBalanceAmount(String totalBalanceAmount) {
            this.totalBalanceAmount = totalBalanceAmount;
        }

        public String getTotalAmountPaid() {
            return totalAmountPaid;
        }

        public void setTotalAmountPaid(String totalAmountPaid) {
            this.totalAmountPaid = totalAmountPaid;
        }

        public String getTeamId() {
            return teamId;
        }

        public void setTeamId(String teamId) {
            this.teamId = teamId;
        }

        public String getStudentRollNumber() {
            return studentRollNumber;
        }

        public void setStudentRollNumber(String studentRollNumber) {
            this.studentRollNumber = studentRollNumber;
        }

        public String getStudentName() {
            return studentName;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public void setStudentName(String studentName) {
            this.studentName = studentName;
        }

        public String getStudentImage() {
            return studentImage;
        }

        public void setStudentImage(String studentImage) {
            this.studentImage = studentImage;
        }

        public String getStudentDbId() {
            return studentDbId;
        }

        public void setStudentDbId(String studentDbId) {
            this.studentDbId = studentDbId;
        }

        public String getGroupId() {
            return groupId;
        }

        public void setGroupId(String groupId) {
            this.groupId = groupId;
        }

        public String getFeeTitle() {
            return feeTitle;
        }

        public void setFeeTitle(String feeTitle) {
            this.feeTitle = feeTitle;
        }

        public HashMap<String, String> getFeeDetails() {
            return feeDetails;
        }

        public void setFeeDetails(HashMap<String, String> feeDetails) {
            this.feeDetails = feeDetails;
        }

        public ArrayList<FeePaidDetails> getFeePaidDetails() {
            return feePaidDetails;
        }

        public void setFeePaidDetails(ArrayList<FeePaidDetails> feePaidDetails) {
            this.feePaidDetails = feePaidDetails;
        }

        public ArrayList<DueDates> getDueDates() {
            return dueDates;
        }

        public void setDueDates(ArrayList<DueDates> dueDates) {
            this.dueDates = dueDates;
        }


        @Override
        public String toString() {
            return new Gson().toJson(this);
        }
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
