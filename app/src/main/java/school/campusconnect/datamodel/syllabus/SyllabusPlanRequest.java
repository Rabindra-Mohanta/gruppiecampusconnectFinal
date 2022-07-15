package school.campusconnect.datamodel.syllabus;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class SyllabusPlanRequest{

    @SerializedName("topicsList")
    @Expose
    private ArrayList<TopicData> topicData;

    public ArrayList<TopicData> getTopicData() {
        return topicData;
    }

    public void setTopicData(ArrayList<TopicData> topicData) {
        this.topicData = topicData;
    }

    public static class TopicData implements Serializable
    {
        @SerializedName("topicName")
        @Expose
        private String topicName;

        @SerializedName("topicId")
        @Expose
        private String topicId;

        @SerializedName("fromDate")
        @Expose
        private String fromDate;

        @SerializedName("toDate")
        @Expose
        private String toDate;

        @SerializedName("actualStartDate")
        @Expose
        private String actualStartDate;

        @SerializedName("actualEndDate")
        @Expose
        private String actualEndDate;

        public String getTopicName() {
            return topicName;
        }

        public void setTopicName(String topicName) {
            this.topicName = topicName;
        }

        public String getTopicId() {
            return topicId;
        }

        public void setTopicId(String topicId) {
            this.topicId = topicId;
        }

        public String getFromDate() {
            return fromDate;
        }

        public void setFromDate(String fromDate) {
            this.fromDate = fromDate;
        }

        public String getToDate() {
            return toDate;
        }

        public void setToDate(String toDate) {
            this.toDate = toDate;
        }

        public String getActualStartDate() {
            return actualStartDate;
        }

        public void setActualStartDate(String actualStartDate) {
            this.actualStartDate = actualStartDate;
        }

        public String getActualEndDate() {
            return actualEndDate;
        }

        public void setActualEndDate(String actualEndDate) {
            this.actualEndDate = actualEndDate;
        }
    }
}
