package school.campusconnect.datamodel.classs;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

import school.campusconnect.datamodel.BaseResponse;

public class StaffClassModelRes extends BaseResponse {

    @SerializedName("data")
    @Expose
    private ArrayList<StaffClassData> staffClassData;

    public ArrayList<StaffClassData> getStaffClassData() {
        return staffClassData;
    }

    public void setStaffClassData(ArrayList<StaffClassData> staffClassData) {
        this.staffClassData = staffClassData;
    }

    public static class StaffClassData implements Serializable
    {
        @SerializedName("teamId")
        @Expose
        private String teamId;

        @SerializedName("name")
        @Expose
        private String name;

        @SerializedName("members")
        @Expose
        private int members;

        @SerializedName("image")
        @Expose
        private String image;

        @SerializedName("category")
        @Expose
        private String category;

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

        public int getMembers() {
            return members;
        }

        public void setMembers(int members) {
            this.members = members;
        }

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }

        public String getCategory() {
            return category;
        }

        public void setCategory(String category) {
            this.category = category;
        }
    }
}
