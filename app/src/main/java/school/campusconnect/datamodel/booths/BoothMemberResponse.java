package school.campusconnect.datamodel.booths;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

import school.campusconnect.datamodel.BaseResponse;

public class BoothMemberResponse extends BaseResponse {
    private ArrayList<BoothMemberData> data;

    public ArrayList<BoothMemberData> getData() {
        return data;
    }

    public void setData(ArrayList<BoothMemberData> data) {
        this.data = data;
    }

    public static class BoothMemberData {
        @SerializedName("phone")
        @Expose
        public String phone;
        @SerializedName("name")
        @Expose
        public String name;
        @SerializedName("image")
        @Expose
        public String image;
        @SerializedName("id")
        @Expose
        public String id;
        @SerializedName("allowedToAddUser")
        @Expose
        public boolean allowedToAddUser;
        @SerializedName("allowedToAddTeamPostComment")
        @Expose
        public boolean allowedToAddTeamPostComment;
        @SerializedName("allowedToAddTeamPost")
        @Expose
        public boolean allowedToAddTeamPost;
        @SerializedName("roleOnConstituency")
        @Expose
        public String roleOnConstituency;

        @Override
        public String toString() {
            return new Gson().toJson(this);
        }
    }
}