package school.campusconnect.datamodel.family;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

import school.campusconnect.datamodel.BaseResponse;

public class FamilyMemberResponse extends BaseResponse {
    @SerializedName(value = "familyMembers",alternate = "data")
    @Expose
    private ArrayList<FamilyMemberData> data;

    public ArrayList<FamilyMemberData> getData() {
        return data;
    }

    public void setData(ArrayList<FamilyMemberData> data) {
        this.data = data;
    }

    public static class FamilyMemberData {
        @SerializedName("voterId")
        @Expose
        public String voterId;
        @SerializedName("relationship")
        @Expose
        public String relationship;
        @SerializedName("name")
        @Expose
        public String name;
        @SerializedName("phone")
        @Expose
        public String phone;
        @SerializedName("image")
        @Expose
        public String image;
        @SerializedName("gender")
        @Expose
        public String gender;
        @SerializedName("familyMemberId")
        @Expose
        public String familyMemberId;
        @SerializedName("dob")
        @Expose
        public String dob;
        @SerializedName("countryCode")
        @Expose
        public String countryCode;
        @SerializedName("bloodGroup")
        @Expose
        public String bloodGroup;
        @SerializedName("address")
        @Expose
        public String address;
        @SerializedName("aadharNumber")
        @Expose
        public String aadharNumber;

        @Override
        public String toString() {
            return new Gson().toJson(this);
        }
    }
}