package school.campusconnect.datamodel.searchUser;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

import school.campusconnect.datamodel.BaseResponse;

public class SearchUserModel extends BaseResponse {


    @SerializedName("data")
    @Expose
    private ArrayList<SearchUserData> data;

    public ArrayList<SearchUserData> getData() {
        return data;
    }

    public void setData(ArrayList<SearchUserData> data) {
        this.data = data;
    }

    public static class SearchUserData implements Serializable
    {
        @SerializedName("voterId")
        @Expose
        private String voterId;

        @SerializedName("userId")
        @Expose
        private String userId;

        @SerializedName("phone")
        @Expose
        private String phone;

        @SerializedName("name")
        @Expose
        private String name;

        @SerializedName("image")
        @Expose
        private String image;

        public String getVoterId() {
            return voterId;
        }

        public void setVoterId(String voterId) {
            this.voterId = voterId;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
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
    }

}
