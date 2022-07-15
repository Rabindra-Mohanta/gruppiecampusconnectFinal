package school.campusconnect.datamodel.chapter;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

import school.campusconnect.datamodel.BaseResponse;

public class ChapterRes extends BaseResponse {
    private ArrayList<ChapterData> data;

    public ArrayList<ChapterData> getData() {
        return data;
    }

    public void setData(ArrayList<ChapterData> data) {
        this.data = data;
    }

    public static class ChapterData {
        @SerializedName("createdByName")
        @Expose
        public String createdByName;
        @SerializedName("createdById")
        @Expose
        public String createdById;
        @SerializedName("chapterName")
        @Expose
        public String chapterName;
        @SerializedName("chapterId")
        @Expose
        public String chapterId;

        @SerializedName("topics")
        @Expose
        public ArrayList<TopicData> topicList;

        @NonNull
        @Override
        public String toString() {
            return chapterName;
        }
    }

    public static class TopicData implements Serializable {
        @SerializedName("topicName")
        @Expose
        public String topicName;
        @SerializedName("topicId")
        @Expose
        public String topicId;
        @SerializedName("insertedAt")
        @Expose
        public String insertedAt;
        @SerializedName("createdByName")
        @Expose
        public String createdByName;

        @SerializedName("fileType")
        @Expose
        public String fileType;
        @SerializedName("fileName")
        @Expose
        public ArrayList<String> fileName;
        @SerializedName("thumbnailImage")
        @Expose
        public ArrayList<String> thumbnailImage;
        @SerializedName("studentStatusCompleted")
        @Expose
        public ArrayList<StudentTopicCompleted> studentList;

        @SerializedName("thumbnail")
        @Expose
        public String thumbnail;
        @SerializedName("video")
        @Expose
        public String video;
        @SerializedName("topicCompleted")
        @Expose
        public boolean topicCompleted;



        public static class StudentTopicCompleted implements Serializable{
            @SerializedName("userId")
            @Expose
            public String userId;
            @SerializedName("studentDbId")
            @Expose
            public String studentDbId;
            @SerializedName("name")
            @Expose
            public String name;
        }
    }
}
