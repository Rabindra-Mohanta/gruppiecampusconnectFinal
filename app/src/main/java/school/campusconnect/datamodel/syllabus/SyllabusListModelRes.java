package school.campusconnect.datamodel.syllabus;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

import school.campusconnect.datamodel.BaseResponse;
import school.campusconnect.datamodel.ticket.TicketListResponse;

public class SyllabusListModelRes extends BaseResponse {

    @SerializedName("data")
    @Expose
    private ArrayList<SyllabusData> syllabusData;

    public ArrayList<SyllabusData> getSyllabusData() {
        return syllabusData;
    }

    public void setSyllabusData(ArrayList<SyllabusData> syllabusData) {
        this.syllabusData = syllabusData;
    }

    public static class SyllabusData implements Serializable
    {
        @SerializedName("topicsList")
        @Expose
        private ArrayList<TopicData> topicData;

        @SerializedName("chapterName")
        @Expose
        private String chapterName;

        @SerializedName("chapterId")
        @Expose
        private String chapterId;

        public ArrayList<TopicData> getTopicData() {
            return topicData;
        }

        public void setTopicData(ArrayList<TopicData> topicData) {
            this.topicData = topicData;
        }

        public String getChapterName() {
            return chapterName;
        }

        public void setChapterName(String chapterName) {
            this.chapterName = chapterName;
        }

        public String getChapterId() {
            return chapterId;
        }

        public void setChapterId(String chapterId) {
            this.chapterId = chapterId;
        }
    }

    public static class TopicData implements Serializable
    {
        @SerializedName("topicName")
        @Expose
        private String topicName;

        @SerializedName("topicId")
        @Expose
        private String topicId;

        @SerializedName("toDate")
        @Expose
        private String toDate;

        @SerializedName("fromDate")
        @Expose
        private String fromDate;

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
