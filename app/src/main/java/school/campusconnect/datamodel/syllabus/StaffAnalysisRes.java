package school.campusconnect.datamodel.syllabus;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

import school.campusconnect.datamodel.BaseResponse;

public class StaffAnalysisRes extends BaseResponse {
    @SerializedName("data")
    @Expose
    private ArrayList<StaffAnalysisData> data;

    public ArrayList<StaffAnalysisData> getData() {
        return data;
    }

    public void setData(ArrayList<StaffAnalysisData> data) {
        this.data = data;
    }

    public static class StaffAnalysisData implements Serializable
    {
        @SerializedName("totalTopicsPending")
        @Expose
        private int totalTopicsPending;

        @SerializedName("totalTopicsCount")
        @Expose
        private int totalTopicsCount;

        @SerializedName("totalTopicsCompleted")
        @Expose
        private int totalTopicsCompleted;

        @SerializedName("totalStartOnTime")
        @Expose
        private int totalStartOnTime;

        @SerializedName("totalStartDelay")
        @Expose
        private int totalStartDelay;

        @SerializedName("totalEndOnTime")
        @Expose
        private int totalEndOnTime;

        @SerializedName("totalEndDelay")
        @Expose
        private int totalEndDelay;

        public int getTotalTopicsPending() {
            return totalTopicsPending;
        }

        public void setTotalTopicsPending(int totalTopicsPending) {
            this.totalTopicsPending = totalTopicsPending;
        }

        public int getTotalTopicsCount() {
            return totalTopicsCount;
        }

        public void setTotalTopicsCount(int totalTopicsCount) {
            this.totalTopicsCount = totalTopicsCount;
        }

        public int getTotalTopicsCompleted() {
            return totalTopicsCompleted;
        }

        public void setTotalTopicsCompleted(int totalTopicsCompleted) {
            this.totalTopicsCompleted = totalTopicsCompleted;
        }

        public int getTotalStartOnTime() {
            return totalStartOnTime;
        }

        public void setTotalStartOnTime(int totalStartOnTime) {
            this.totalStartOnTime = totalStartOnTime;
        }

        public int getTotalStartDelay() {
            return totalStartDelay;
        }

        public void setTotalStartDelay(int totalStartDelay) {
            this.totalStartDelay = totalStartDelay;
        }

        public int getTotalEndOnTime() {
            return totalEndOnTime;
        }

        public void setTotalEndOnTime(int totalEndOnTime) {
            this.totalEndOnTime = totalEndOnTime;
        }

        public int getTotalEndDelay() {
            return totalEndDelay;
        }

        public void setTotalEndDelay(int totalEndDelay) {
            this.totalEndDelay = totalEndDelay;
        }
    }
}
