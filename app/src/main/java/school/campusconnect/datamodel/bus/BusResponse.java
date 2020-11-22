package school.campusconnect.datamodel.bus;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

import school.campusconnect.datamodel.BaseResponse;

public class BusResponse extends BaseResponse {
    private ArrayList<BusData> data;

    public ArrayList<BusData> getData() {
        return data;
    }

    public void setData(ArrayList<BusData> data) {
        this.data = data;
    }

    public static class BusData {
        @SerializedName("countryCode")
        @Expose
        public String countryCode;
        @SerializedName("phone")
        @Expose
        public String phone;
        @SerializedName(value = "routeName")
        @Expose
        public String routeName;
        @SerializedName(value = "driverName")
        @Expose
        public String driverName;

        @SerializedName("image")
        @Expose
        public String image;

        @SerializedName("teamId")
        @Expose
        private String id;

        @SerializedName("members")
        @Expose
        public String members;



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

        public String getRouteName() {
            return routeName;
        }

        public void setRouteName(String routeName) {
            this.routeName = routeName;
        }

        public String getDriverName() {
            return driverName;
        }

        public void setDriverName(String driverName) {
            this.driverName = driverName;
        }

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getMembers() {
            return members;
        }

        public void setMembers(String members) {
            this.members = members;
        }

        @Override
        public String toString() {
            return new Gson().toJson(this);
        }
    }
}
