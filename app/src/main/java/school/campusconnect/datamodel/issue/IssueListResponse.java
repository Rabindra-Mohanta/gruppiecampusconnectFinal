package school.campusconnect.datamodel.issue;

import androidx.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

import school.campusconnect.datamodel.BaseResponse;

public class IssueListResponse extends BaseResponse {
    private ArrayList<IssueData> data;

    public ArrayList<IssueData> getData() {
        return data;
    }

    public void setData(ArrayList<IssueData> data) {
        this.data = data;
    }

    public static class IssueData {
        @SerializedName("partyUser")
        @Expose
        public IssueUserData partyUser;
        @SerializedName("departmentUser")
        @Expose
        public IssueUserData departmentUser;
        @SerializedName("jurisdiction")
        @Expose
        public String jurisdiction;
        @SerializedName("issueId")
        @Expose
        public String issueId;
        @SerializedName("issue")
        @Expose
        public String issue;
        @SerializedName("groupId")
        @Expose
        public String groupId;
        @SerializedName("dueDays")
        @Expose
        public String dueDays;

    }

    public static class IssueUserData {
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
        @SerializedName("designation")
        @Expose
        private String designation;

    }
}
