package school.campusconnect.datamodel.syllabus;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class SyllabusModelReq implements Serializable {

    @SerializedName("syllabus")
    @Expose
    private ArrayList<SyllabusModelData> syllabusModelData;

    public ArrayList<SyllabusModelData> getSyllabusModelData() {
        return syllabusModelData;
    }

    public void setSyllabusModelData(ArrayList<SyllabusModelData> syllabusModelData) {
        this.syllabusModelData = syllabusModelData;
    }

    public static class SyllabusModelData implements Serializable
    {
        @SerializedName("chapterName")
        @Expose
        private String chapterName;

        @SerializedName("topicsList")
        @Expose
        private ArrayList<TopicModelData> topicsList;

        public String getChapterName() {
            return chapterName;
        }

        public void setChapterName(String chapterName) {
            this.chapterName = chapterName;
        }

        public ArrayList<TopicModelData> getTopicsList() {
            return topicsList;
        }

        public void setTopicsList(ArrayList<TopicModelData> topicsList) {
            this.topicsList = topicsList;
        }
    }

    public static class TopicModelData implements Serializable
    {
        @SerializedName("topicName")
        @Expose
        private String topicName;

        public String getTopicName() {
            return topicName;
        }

        public void setTopicName(String topicName) {
            this.topicName = topicName;
        }
    }

}
