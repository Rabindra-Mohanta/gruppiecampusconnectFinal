package school.campusconnect.datamodel.readMore;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

import school.campusconnect.datamodel.BaseResponse;

public class ReadMoreRes extends BaseResponse {

    public Data data;

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public class Data{

        String updatedAt;
        String title;
        String text;
        @SerializedName(value = "phone" ,alternate = "senderPhone")
        String phone;
        int likes;
        boolean isLiked;
        boolean isFavourited;
        String id;
        String groupId;
        @SerializedName(value = "createdByImage" ,alternate = "senderImage")
        String createdByImage;
        String createdById;

        @SerializedName(value = "createdBy", alternate = "senderName")
        String createdBy;

        String createdAt;
        int comments;
        boolean canEdit;

       //Gallery
       String fileType;
       ArrayList<String>fileName;
       public ArrayList<String> thumbnailImage; // Url Encoded in base 64public
       String albumName;
       String albumId;

        String teamName;
        String senderId;
        String thumbnail;
        String video;

        public String getVideo() {
            return video;
        }

        public void setVideo(String video) {
            this.video = video;
        }

        public String getThumbnail() {
            return thumbnail;
        }

        public void setThumbnail(String thumbnail) {
            this.thumbnail = thumbnail;
        }

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

        public boolean isLiked() {
            return isLiked;
        }

        public void setLiked(boolean liked) {
            isLiked = liked;
        }

        public boolean isFavourited() {
            return isFavourited;
        }

        public void setFavourited(boolean favourited) {
            isFavourited = favourited;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getGroupId() {
            return groupId;
        }

        public void setGroupId(String groupId) {
            this.groupId = groupId;
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

        public String getAlbumName() {
            return albumName;
        }

        public void setAlbumName(String albumName) {
            this.albumName = albumName;
        }

        public String getAlbumId() {
            return albumId;
        }

        public void setAlbumId(String albumId) {
            this.albumId = albumId;
        }

        public String getTeamName() {
            return teamName;
        }

        public void setTeamName(String teamName) {
            this.teamName = teamName;
        }

        public String getSenderId() {
            return senderId;
        }

        public void setSenderId(String senderId) {
            this.senderId = senderId;
        }
    }

}
