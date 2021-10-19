package school.campusconnect.datamodel.fees;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class FeePaidDetails {
    @SerializedName("amountPaid")
    @Expose
    private String amountPaid;

    @SerializedName("studentName")
    @Expose
    public String studentName;
    @SerializedName("status")
    @Expose
    public String status;
    @SerializedName("paymentMode")
    @Expose
    public String paymentMode;
    @SerializedName("paymentId")
    @Expose
    public String paymentId;
    @SerializedName("paidDate")
    @Expose
    public String paidDate;
    @SerializedName("paidAtTime")
    @Expose
    public String paidAtTime;
    @SerializedName("className")
    @Expose
    public String className;
    @SerializedName("attachment")
    @Expose
    public ArrayList<String> attachment;

    public FeePaidDetails( String paidDate,String amountPaid) {
        this.paidDate = paidDate;
        this.amountPaid = amountPaid;
    }

    public FeePaidDetails() {
    }

    public String getAmountPaid() {
        return amountPaid;
    }

    public void setAmountPaid(String amountPaid) {
        this.amountPaid = amountPaid;
    }


    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}