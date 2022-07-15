package school.campusconnect.datamodel.fees;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.HashMap;

import school.campusconnect.datamodel.BaseResponse;

public class PaidStudentFeesRes extends BaseResponse {
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
        @SerializedName("totalAmountPaid")
        @Expose
        public String totalAmountPaid;
        @SerializedName("teamId")
        @Expose
        public String teamId;
        @SerializedName("studentName")
        @Expose
        public String studentName;
        @SerializedName("studentImage")
        @Expose
        public String studentImage;
        @SerializedName("status")
        @Expose
        public String status;
        @SerializedName("paymentMode")
        @Expose
        public String paymentMode;
        @SerializedName("paymentId")
        @Expose
        public String paymentId;
        @SerializedName("paidImageAttachment")
        @Expose
        public ArrayList<String> paidImageAttachment;
        @SerializedName("paidDate")
        @Expose
        public String paidDate;
        @SerializedName("groupId")
        @Expose
        public String groupId;
        @SerializedName("dueDates")
        @Expose
        public ArrayList<DueDates> dueDates;
        @SerializedName("className")
        @Expose
        public String className;
        @SerializedName("amountPaid")
        @Expose
        public String amountPaid;
        @SerializedName("addedDateTime")
        @Expose
        public String addedDateTime;

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
