package school.campusconnect.datamodel.bus;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

import school.campusconnect.datamodel.BaseResponse;

public class BusStudentRes extends BaseResponse {
    private ArrayList<StudentData> data;

    public ArrayList<StudentData> getData() {
        return data;
    }

    public void setData(ArrayList<StudentData> data) {
        this.data = data;
    }

    public static class StudentData {
        @SerializedName("userId")
        @Expose
        private String userId;
        @SerializedName("teamId")
        @Expose
        private String teamId;
        @SerializedName("name")
        @Expose
        public String name;
        @SerializedName("image")
        @Expose
        public String image;
        @SerializedName("groupId")
        @Expose
        public String groupId;
        @SerializedName("countryCode")
        @Expose
        public String countryCode;
        @SerializedName("phone")
        @Expose
        public String phone;

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

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public String getTeamId() {
            return teamId;
        }

        public void setTeamId(String teamId) {
            this.teamId = teamId;
        }


        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }

        public String getGroupId() {
            return groupId;
        }

        public void setGroupId(String groupId) {
            this.groupId = groupId;
        }
    }
}
