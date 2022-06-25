package school.campusconnect.datamodel.student;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class AddMultipleStaffReq {

    @SerializedName(value = "staffData")
    @Expose
    private ArrayList<AddMultipleStaffReq.staffData> staffData;


    public ArrayList<AddMultipleStaffReq.staffData> getStaffData() {
        return staffData;
    }

    public void setStaffData(ArrayList<AddMultipleStaffReq.staffData> staffData) {
        this.staffData = staffData;
    }

    public static class staffData implements Serializable
    {
        @SerializedName("name")
        @Expose
        private String name;

        @SerializedName("countryCode")
        @Expose
        private String countryCode;

        @SerializedName("phone")
        @Expose
        private String phone;

        private int cCode;

        public int getcCode() {
            return cCode;
        }

        public void setcCode(int cCode) {
            this.cCode = cCode;
        }

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
    }
}
