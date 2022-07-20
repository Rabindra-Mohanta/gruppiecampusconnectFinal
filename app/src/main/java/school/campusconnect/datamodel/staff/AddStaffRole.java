package school.campusconnect.datamodel.staff;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class AddStaffRole {


    public AddStaffRole(ArrayList<String> staffId) {
        this.staffId = staffId;
    }

    @SerializedName("staffIds")
    @Expose
    private ArrayList<String> staffId;



    public ArrayList<String> getStaffId() {
        return staffId;
    }

    public void setStaffId(ArrayList<String> staffId) {
        this.staffId = staffId;
    }
}
