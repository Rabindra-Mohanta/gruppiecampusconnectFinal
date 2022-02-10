package school.campusconnect.datamodel.comments;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

import school.campusconnect.datamodel.BaseResponse;

public class CommentTaskDetailsRes extends BaseResponse implements Serializable {

    @SerializedName("data")
    @Expose
    private ArrayList<CommentData> commentData;

    public ArrayList<CommentData> getCommentData() {
        return commentData;
    }

    public void setCommentData(ArrayList<CommentData> commentData) {
        this.commentData = commentData;
    }
    public static class CommentData implements Serializable
    {
        @SerializedName("text")
        @Expose
        private String text;

        @SerializedName("name")
        @Expose
        private String name;

        @SerializedName("insertedAt")
        @Expose
        private String insertedAt;

        @SerializedName("image")
        @Expose
        private String image;

        @SerializedName("constituencyDesignation")
        @Expose
        private String constituencyDesignation;

        @SerializedName("commentId")
        @Expose
        private String commentId;

        @SerializedName("canEdit")
        @Expose
        private Boolean canEdit;

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getInsertedAt() {
            return insertedAt;
        }

        public void setInsertedAt(String insertedAt) {
            this.insertedAt = insertedAt;
        }

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }

        public String getConstituencyDesignation() {
            return constituencyDesignation;
        }

        public void setConstituencyDesignation(String constituencyDesignation) {
            this.constituencyDesignation = constituencyDesignation;
        }

        public String getCommentId() {
            return commentId;
        }

        public void setCommentId(String commentId) {
            this.commentId = commentId;
        }

        public Boolean getCanEdit() {
            return canEdit;
        }

        public void setCanEdit(Boolean canEdit) {
            this.canEdit = canEdit;
        }
    }
}
