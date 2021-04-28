package school.campusconnect.datamodel.chapter;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

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

    public static class TopicData {
        @SerializedName("topicName")
        @Expose
        public String topicName;
        @SerializedName("topicId")
        @Expose
        public String topicId;
        @SerializedName("insertedAt")
        @Expose
        public String insertedAt;
        @SerializedName("fileType")
        @Expose
        public String fileType;
        @SerializedName("fileName")
        @Expose
        public ArrayList<String> fileName;
        @SerializedName("thumbnailImage")
        @Expose
        public ArrayList<String> thumbnailImage;
        @SerializedName("thumbnail")
        @Expose
        public String thumbnail;
        @SerializedName("video")
        @Expose
        public String video;


    }
}
