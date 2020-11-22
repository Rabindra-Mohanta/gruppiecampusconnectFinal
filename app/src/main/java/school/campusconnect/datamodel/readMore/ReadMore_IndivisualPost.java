package school.campusconnect.datamodel.readMore;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import school.campusconnect.datamodel.BaseResponse;

public class ReadMore_IndivisualPost extends BaseResponse {

    public Data data;

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public class Data{

        @SerializedName("senderName")
        @Expose
        private String senderName;
        @SerializedName("canEdit")
        @Expose
        private boolean canEdit;
        @SerializedName("updatedAt")
        @Expose
        private String updatedAt;
        @SerializedName("text")
        @Expose
        private String text;
        @SerializedName("teamName")
        @Expose
        private String teamName;
        @SerializedName("senderPhone")
        @Expose
        private String senderPhone;
        @SerializedName("senderImage")
        @Expose
        private String senderImage;
        @SerializedName("senderId")
        @Expose
        private String senderId;
        @SerializedName("likes")
        @Expose
        private int likes;
        @SerializedName("isLiked")
        @Expose
        private boolean isLiked;
        @SerializedName("id")
        @Expose
        private String id;
        @SerializedName("comments")
        @Expose
        private int comments;

        public String getSenderName() {
            return senderName;
        }

        public void setSenderName(String senderName) {
            this.senderName = senderName;
        }

        public boolean isCanEdit() {
            return canEdit;
        }

        public void setCanEdit(boolean canEdit) {
            this.canEdit = canEdit;
        }

        public String getUpdatedAt() {
            return updatedAt;
        }

        public void setUpdatedAt(String updatedAt) {
            this.updatedAt = updatedAt;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public String getTeamName() {
            return teamName;
        }

        public void setTeamName(String teamName) {
            this.teamName = teamName;
        }

        public String getSenderPhone() {
            return senderPhone;
        }

        public void setSenderPhone(String senderPhone) {
            this.senderPhone = senderPhone;
        }

        public String getSenderImage() {
            return senderImage;
        }

        public void setSenderImage(String senderImage) {
            this.senderImage = senderImage;
        }

        public String getSenderId() {
            return senderId;
        }

        public void setSenderId(String senderId) {
            this.senderId = senderId;
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

        public int getComments() {
            return comments;
        }

        public void setComments(int comments) {
            this.comments = comments;
        }
    }

}
