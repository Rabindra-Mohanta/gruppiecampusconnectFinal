package school.campusconnect.datamodel.fees;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.HashMap;

import school.campusconnect.datamodel.BaseResponse;

public class FeesRes extends BaseResponse {
    private ArrayList<Fees> data;

    public ArrayList<Fees> getData() {
        return data;
    }

    public void setData(ArrayList<Fees> data) {
        this.data = data;
    }

    public static class Fees {
        @SerializedName("totalFee")
        @Expose
        public String totalFee;
        @SerializedName("teamId")
        @Expose
        public String teamId;
        @SerializedName("insertedAt")
        @Expose
        public String insertedAt;
        @SerializedName("groupId")
        @Expose
        public String groupId;
        @SerializedName("feeTitle")
        @Expose
        public String feeTitle;
        @SerializedName("feeDetails")
        @Expose
        public HashMap<String,String> feeDetails;

        @SerializedName("dueDates")
        @Expose
        public ArrayList<DueDates> dueDates;

        public String getTotalFee() {
            return totalFee;
        }

        public void setTotalFee(String totalFee) {
            this.totalFee = totalFee;
        }

        public ArrayList<DueDates> getDueDates() {
            return dueDates;
        }

        public void setDueDates(ArrayList<DueDates> dueDates) {
            this.dueDates = dueDates;
        }

        public String getTeamId() {
            return teamId;
        }

        public void setTeamId(String teamId) {
            this.teamId = teamId;
        }

        public String getInsertedAt() {
            return insertedAt;
        }

        public void setInsertedAt(String insertedAt) {
            this.insertedAt = insertedAt;
        }

        public String getGroupId() {
            return groupId;
        }

        public void setGroupId(String groupId) {
            this.groupId = groupId;
        }

        public String getFeeTitle() {
            return feeTitle;
        }

        public void setFeeTitle(String feeTitle) {
            this.feeTitle = feeTitle;
        }

        public HashMap<String, String> getFeeDetails() {
            return feeDetails;
        }

        public void setFeeDetails(HashMap<String, String> feeDetails) {
            this.feeDetails = feeDetails;
        }

        @Override
        public String toString() {
            return new Gson().toJson(this);
        }
    }


    @Override
    public String toString() {
        return new Gson().toJson(this);
    }

}
