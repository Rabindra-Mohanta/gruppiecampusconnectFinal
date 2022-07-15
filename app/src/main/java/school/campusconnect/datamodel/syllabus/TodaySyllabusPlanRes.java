package school.campusconnect.datamodel.syllabus;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

import school.campusconnect.datamodel.BaseResponse;

public class TodaySyllabusPlanRes extends BaseResponse {

    @SerializedName("data")
    @Expose
    private ArrayList<TodaySyllabusData> todaySyllabusData;

    public ArrayList<TodaySyllabusData> getTodaySyllabusData() {
        return todaySyllabusData;
    }

    public void setTodaySyllabusData(ArrayList<TodaySyllabusData> todaySyllabusData) {
        this.todaySyllabusData = todaySyllabusData;
    }

    public static class TodaySyllabusData implements Serializable
    {
        @SerializedName("topicName")
        @Expose
        private String topicName;

        @SerializedName("topicId")
        @Expose
        private String topicId;

        @SerializedName("teamName")
        @Expose
        private String teamName;

        @SerializedName("teamId")
        @Expose
        private String teamId;

        @SerializedName("subjectId")
        @Expose
        private String subjectId;

        @SerializedName("chapterName")
        @Expose
        private String chapterName;

        @SerializedName("chapterId")
        @Expose
        private String chapterId;

        @SerializedName("toDate")
        @Expose
        private String toDate;

        @SerializedName("actualStartDate")
        @Expose
        private String toactualStartDateDate;

        @SerializedName("fromDate")
        @Expose
        private String fromDate;

        @SerializedName("actualEndDate")
        @Expose
        private String actualEndDate;


        public String getToDate() {
            return toDate;
        }

        public void setToDate(String toDate) {
            this.toDate = toDate;
        }

        public String getToactualStartDateDate() {
            return toactualStartDateDate;
        }

        public void setToactualStartDateDate(String toactualStartDateDate) {
            this.toactualStartDateDate = toactualStartDateDate;
        }

        public String getFromDate() {
            return fromDate;
        }

        public void setFromDate(String fromDate) {
            this.fromDate = fromDate;
        }

        public String getActualEndDate() {
            return actualEndDate;
        }

        public void setActualEndDate(String actualEndDate) {
            this.actualEndDate = actualEndDate;
        }

        public String getSubjectId() {
            return subjectId;
        }

        public void setSubjectId(String subjectId) {
            this.subjectId = subjectId;
        }

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

        public String getTeamName() {
            return teamName;
        }

        public void setTeamName(String teamName) {
            this.teamName = teamName;
        }

        public String getTeamId() {
            return teamId;
        }

        public void setTeamId(String teamId) {
            this.teamId = teamId;
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
}
