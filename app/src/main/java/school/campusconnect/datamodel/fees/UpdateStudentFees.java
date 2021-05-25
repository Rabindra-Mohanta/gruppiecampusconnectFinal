package school.campusconnect.datamodel.fees;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.HashMap;

public class UpdateStudentFees{
    @SerializedName("totalFee")
    @Expose
    public String totalFee;
    @SerializedName("paidDates")
    @Expose
    public ArrayList<FeePaidDetails> paidDates;
    @SerializedName("dueDates")
    @Expose
    public ArrayList<DueDates> dueDates;
    @SerializedName("feeDetails")
    @Expose
    public HashMap<String,String> feeDetails;

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
