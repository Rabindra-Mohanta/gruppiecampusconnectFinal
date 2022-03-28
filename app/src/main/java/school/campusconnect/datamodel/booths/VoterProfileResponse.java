package school.campusconnect.datamodel.booths;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

import school.campusconnect.datamodel.BaseResponse;

public class VoterProfileResponse extends BaseResponse {

    private VoterData data;

    public VoterData getData() {
        return data;
    }

    public void setData(VoterData data) {
        this.data = data;
    }

    public static class VoterData implements Serializable {


        @SerializedName("userId")
        @Expose
        public String userId;

        @SerializedName("updatedAt")
        @Expose
        public String updatedAt;


        @SerializedName("phone")
        @Expose
        public String phone;

        @SerializedName("name")
        @Expose
        public String name;

        @SerializedName("insertedAt")
        @Expose
        public String insertedAt;

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public String getUpdatedAt() {
            return updatedAt;
        }

        public void setUpdatedAt(String updatedAt) {
            this.updatedAt = updatedAt;
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

        public String getInsertedAt() {
            return insertedAt;
        }

        public void setInsertedAt(String insertedAt) {
            this.insertedAt = insertedAt;
        }
    }

}
