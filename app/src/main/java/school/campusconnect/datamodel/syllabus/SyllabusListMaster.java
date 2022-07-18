package school.campusconnect.datamodel.syllabus;




import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

import school.campusconnect.datamodel.BaseResponse;

public   class SyllabusListMaster extends BaseResponse {

    @SerializedName("data")
    @Expose
    private ArrayList<SyllabusData> syllabusData;

    public ArrayList<SyllabusData> getSyllabusData() {
        return syllabusData;


    }

    public void setSyllabusData(ArrayList<SyllabusData> syllabusData) {
        this.syllabusData = syllabusData;
    }


    public static class SyllabusData implements Serializable {

        @SerializedName("totalTopicsCount")
        @Expose
        public Integer totalTopicsCount;
        @SerializedName("topicsList")
        @Expose
        public ArrayList<SyllabusListModelRes.TopicData> TopicData;

        @SerializedName("chapterName")
        @Expose
        public String chapterName;

        @SerializedName("chapterId")
        @Expose
        public String chapterId;

        public ArrayList<SyllabusListModelRes.TopicData> getTopicData() {
            return TopicData;
        }

        public void setTopicData(ArrayList<SyllabusListModelRes.TopicData> TopicData) {
            this.TopicData = TopicData;
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

    public static class TopicData implements Serializable {

        @SerializedName("topicName")
        @Expose
        public String topicName;
        @SerializedName("topicId")
        @Expose
        public String topicId;

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







