package school.campusconnect.datamodel.classs;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

import school.campusconnect.datamodel.BaseResponse;

public class ParentKidsResponse extends BaseResponse {
    private ArrayList<ParentKidsData> data;

    public ArrayList<ParentKidsData> getData() {
        return data;
    }

    public void setData(ArrayList<ParentKidsData> data) {
        this.data = data;
    }

    public static class ParentKidsData {
        @SerializedName("userId")
        @Expose
        public String userId;
        @SerializedName("teamId")
        @Expose
        public String teamId;
        @SerializedName("rollNumber")
        @Expose
        public String rollNumber;
        @SerializedName("name")
        @Expose
        public String name;
        @SerializedName("className")
        @Expose
        public String className;

        @SerializedName("image")
        @Expose
        public String image;

        @SerializedName("groupId")
        @Expose
        private String groupId;

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

        public String getRollNumber() {
            return rollNumber;
        }

        public void setRollNumber(String rollNumber) {
            this.rollNumber = rollNumber;
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
