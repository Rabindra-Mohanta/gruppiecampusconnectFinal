package school.campusconnect.datamodel.readMore;

import school.campusconnect.datamodel.BaseResponse;

public class ReadMore_GroupPost extends BaseResponse {

    public Data data;

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public class Data{

        public String updatedAt;
        public String title;
        public String text;
        public String phone;
        public int likes;
        public boolean isLiked;
        public boolean isFavourited;
        public String id;
        public String groupId;
        public String createdByImage;
        public String createdById;
        public String createdBy;
        public String createdAt;
        public int comments;
        public boolean canEdit;

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
    }

}
