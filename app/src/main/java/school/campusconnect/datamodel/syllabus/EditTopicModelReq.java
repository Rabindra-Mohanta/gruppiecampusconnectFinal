package school.campusconnect.datamodel.syllabus;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class EditTopicModelReq{

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
    }
}
