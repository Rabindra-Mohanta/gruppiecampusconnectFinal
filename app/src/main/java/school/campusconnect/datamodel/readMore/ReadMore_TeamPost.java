package school.campusconnect.datamodel.readMore;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

import school.campusconnect.datamodel.BaseResponse;

public class ReadMore_TeamPost extends BaseResponse {

    public Data data;

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public class Data{

        @SerializedName("updatedAt")
        @Expose
        private String updatedAt;
        @SerializedName("title")
        @Expose
        private String title;
        @SerializedName("text")
        @Expose
        private String text;
        @SerializedName("phone")
        @Expose
        private String phone;
        @SerializedName("likes")
        @Expose
        private int likes;
        @SerializedName("isLiked")
        @Expose
        private boolean isLiked;
        @SerializedName("id")
        @Expose
        private String id;
        @SerializedName("fileType")
        @Expose
        private String fileType;
        @SerializedName("fileName")
        @Expose
        private ArrayList<String> fileName = null;
        @SerializedName("createdByImage")
        @Expose
        private String createdByImage;
        @SerializedName("createdById")
        @Expose
        private String createdById;
        @SerializedName("createdBy")
        @Expose
        private String createdBy;
        @SerializedName("createdAt")
        @Expose
        private String createdAt;
        @SerializedName("comments")
        @Expose
        private int comments;
        @SerializedName("canEdit")
        @Expose
        private boolean canEdit;

        public String getUpdatedAt() {
            return updatedAt;
        }

        public void setUpdatedAt(String updatedAt) {
            this.updatedAt = updatedAt;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public int getLikes() {
            return likes;
        }

        public void setLikes(int likes) {
            this.likes = likes;
        }

        public boolean isIsLiked() {
            return isLiked;
        }

        public void setIsLiked(boolean isLiked) {
            this.isLiked = isLiked;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getFileType() {
            return fileType;
        }

        public void setFileType(String fileType) {
            this.fileType = fileType;
        }

        public ArrayList<String> getFileName() {
            return fileName;
        }

        public void setFileName(ArrayList<String> fileName) {
            this.fileName = fileName;
        }

        public String getCreatedByImage() {
            return createdByImage;
        }

        public void setCreatedByImage(String createdByImage) {
            this.createdByImage = createdByImage;
        }

        public String getCreatedById() {
            return createdById;
        }

        public void setCreatedById(String createdById) {
            this.createdById = createdById;
        }

        public String getCreatedBy() {
            return createdBy;
        }

        public void setCreatedBy(String createdBy) {
            this.createdBy = createdBy;
        }

        public String getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(String createdAt) {
            this.createdAt = createdAt;
        }

        public int getComments() {
            return comments;
        }

        public void setComments(int comments) {
            this.comments = comments;
        }

        public boolean isCanEdit() {
            return canEdit;
        }

        public void setCanEdit(boolean canEdit) {
            this.canEdit = canEdit;
        }
    }

}
