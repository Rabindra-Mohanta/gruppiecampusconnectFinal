package school.campusconnect.datamodel.fees;

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
        @SerializedName("totalFee")
        @Expose
        private String totalFee;
        @SerializedName("totalBalanceAmount")
        @Expose
        private String totalBalanceAmount;
        @SerializedName("totalAmountPaid")
        @Expose
        private String totalAmountPaid;
        @SerializedName("teamId")
        @Expose
        private String teamId;
        @SerializedName("studentRollNumber")
        @Expose
        private String studentRollNumber;
        @SerializedName("studentName")
        @Expose
        private String studentName;
        @SerializedName("studentImage")
        @Expose
        private String studentImage;
        @SerializedName("studentDbId")
        @Expose
        private String studentDbId;
        @SerializedName("groupId")
        @Expose
        private String groupId;
        @SerializedName("feeTitle")
        @Expose
        private String feeTitle;
        @SerializedName("feePaidDetails")
        @Expose
        private ArrayList<FeePaidDetails> feePaidDetails;
        @SerializedName("feeDetails")
        @Expose
        private HashMap<String,String> feeDetails;
        @SerializedName("dueDates")
        @Expose
        private ArrayList<DueDates> dueDates;

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
    }
    public static class FeePaidDetails{
        @SerializedName("date")
        @Expose
        private String date;
        @SerializedName("amountPaid")
        @Expose
        private String amountPaid;

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public String getAmountPaid() {
            return amountPaid;
        }

        public void setAmountPaid(String amountPaid) {
            this.amountPaid = amountPaid;
        }
    }

    private static class DueDates {
        @SerializedName("minimumAmount")
        @Expose
        private String minimumAmount;
        @SerializedName("date")
        @Expose
        private String date;

        public String getMinimumAmount() {
            return minimumAmount;
        }

        public void setMinimumAmount(String minimumAmount) {
            this.minimumAmount = minimumAmount;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }
    }
}
