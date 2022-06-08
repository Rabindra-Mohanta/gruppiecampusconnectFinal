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

        @SerializedName("toDate")
        @Expose
        private String toDate;

        @SerializedName("fromDate")
        @Expose
        private String fromDate;

        public String getTopicName() {
            return topicName;
        }

        public void setTopicName(String topicName) {
            this.topicName = topicName;
        }

        public String getToDate() {
            return toDate;
        }

        public void setToDate(String toDate) {
            this.toDate = toDate;
        }

        public String getFromDate() {
            return fromDate;
        }

        public void setFromDate(String fromDate) {
            this.fromDate = fromDate;
        }
    }

}
