package school.campusconnect.datamodel.fees;

import java.util.ArrayList;

public class PayFeesRequest {
    public String paidDate;
    public String amountPaid;
    public String paymentMode;
    public ArrayList<String> attachment;

    public String bankName;
    public String bankBranch;
    public String chequeNo;
    public String chequeDate;


    @Override
    public String toString() {
        return "PayFeesRequest{" +
                "paidDate='" + paidDate + '\'' +
                ", amountPaid='" + amountPaid + '\'' +
                ", paymentMode='" + paymentMode + '\'' +
                ", attachment=" + attachment + '\'' +
                ", bankName=" + bankName + '\'' +
                ", bankBranch=" + bankBranch + '\'' +
                ", chequeNo=" + chequeNo + '\'' +
                ", chequeDate=" + chequeDate +
        '}';
    }
}
