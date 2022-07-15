package school.campusconnect.datamodel.syllabus;




import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import school.campusconnect.datamodel.BaseResponse;

public    class SyllabusListMaster extends BaseResponse {

    @SerializedName("data")
    @Expose
    private List<Datum> data = null;

    public List<Datum> getData() {
        return data;
    }

    public void setData(List<Datum> data) {
        this.data = data;
    }

    public class Datum {

        @SerializedName("totalTopicsCount")
        @Expose
        private Integer totalTopicsCount;
        @SerializedName("topicsList")
        @Expose
        private List<Topics> topicsList = null;
        @SerializedName("chapterName")
        @Expose
        private String chapterName;
        @SerializedName("chapterId")
        @Expose
        private String chapterId;

        public Integer getTotalTopicsCount() {
            return totalTopicsCount;
        }

        public void setTotalTopicsCount(Integer totalTopicsCount) {
            this.totalTopicsCount = totalTopicsCount;
        }

        public List<Topics> getTopicsList() {
            return topicsList;
        }

        public void setTopicsList(List<Topics> topicsList) {
            this.topicsList = topicsList;
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

    public class Topics {

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







