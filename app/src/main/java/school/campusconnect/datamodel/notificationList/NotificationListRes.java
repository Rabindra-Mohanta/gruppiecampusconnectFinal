package school.campusconnect.datamodel.notificationList;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

import school.campusconnect.datamodel.BaseResponse;

public class NotificationListRes extends BaseResponse {

    @SerializedName("data")
    @Expose
    public List<NotificationListData> data;

    public List<NotificationListData> getData() {
        return data;
    }

    public void setData(List<NotificationListData> data) {
        this.data = data;
    }

    public static class NotificationListData extends BaseResponse implements Serializable {

        @SerializedName(value = "idPrimary")
        @Expose
        private long idPrimary;

        @SerializedName(value = "id",alternate = "userId")
        @Expose
        private String userId;

        @SerializedName("type")
        @Expose
        private String type;

        @SerializedName("showComment")
        @Expose
        private boolean showComment;

        @SerializedName(value = "postId",alternate = "albumId")
        @Expose
        private String postId;
        @SerializedName("message")
        @Expose
        private String message;
        @SerializedName("insertedAt")
        @Expose
        private String insertedAt;
        @SerializedName("groupId")
        @Expose
        private String groupId;
        @SerializedName("createdByPhone")
        @Expose
        private String createdByPhone;
        @SerializedName("createdByName")
        @Expose
        private String createdByName;
        @SerializedName("createdByImage")
        @Expose
        private String createdByImage;
        @SerializedName("createdById")
        @Expose
        private String createdById;

        @SerializedName("teamId")
        @Expose
        private String teamId;

        @SerializedName(value = "readedComment")
        @Expose
        private String readedComment;

        public long getIdPrimary() {
            return idPrimary;
        }

        public void setIdPrimary(long idPrimary) {
            this.idPrimary = idPrimary;
        }

        public String getReadedComment() {
            return readedComment;
        }

        public void setReadedComment(String readedComment) {
            this.readedComment = readedComment;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public boolean getShowComment() {
            return showComment;
        }

        public void setShowComment(boolean showComment) {
            this.showComment = showComment;
        }

        public String getPostId() {
            return postId;
        }

        public void setPostId(String postId) {
            this.postId = postId;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getInsertedAt() {
            return insertedAt;
        }

        public void setInsertedAt(String insertedAt) {
            this.insertedAt = insertedAt;
        }

        public String getGroupId() {
            return groupId;
        }

        public void setGroupId(String groupId) {
            this.groupId = groupId;
        }

        public String getCreatedByPhone() {
            return createdByPhone;
        }

        public void setCreatedByPhone(String createdByPhone) {
            this.createdByPhone = createdByPhone;
        }

        public String getCreatedByName() {
            return createdByName;
        }

        public void setCreatedByName(String createdByName) {
            this.createdByName = createdByName;
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

        public String getTeamId() {
            return teamId;
        }

        public void setTeamId(String teamId) {
            this.teamId = teamId;
        }
    }
}
