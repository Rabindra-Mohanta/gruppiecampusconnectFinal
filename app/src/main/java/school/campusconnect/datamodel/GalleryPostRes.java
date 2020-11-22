package school.campusconnect.datamodel;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class GalleryPostRes extends BaseResponse{

    public  int totalNumberOfPages;

    public ArrayList<GalleryData> data;

    public class GalleryData {
        @SerializedName("updatedAt")
        @Expose
        public String updatedAt;
        @SerializedName("albumId")
        @Expose
        private String albumId;
        @SerializedName("groupId")
        @Expose
        private String groupId;
        @SerializedName("fileType")
        @Expose
        public String fileType;
        @SerializedName("video")
        @Expose
        public String video;

        @SerializedName("thumbnail")
        @Expose
        public String thumbnail;



        @SerializedName("fileName")
        @Expose
        public ArrayList<String> fileName;

        @SerializedName("thumbnailImage")
        @Expose
        public ArrayList<String> thumbnailImage; // Url Encoded in base 64



        @SerializedName("createdAt")
        @Expose
        public String createdAt;
        @SerializedName("canEdit")
        @Expose
        public Boolean canEdit;
        @SerializedName("albumName")
        @Expose
        public String albumName;

        public String getVideo() {
            return video;
        }

        public void setVideo(String video) {
            this.video = video;
        }

        public String getUpdatedAt() {
            return updatedAt;
        }

        public void setUpdatedAt(String updatedAt) {
            this.updatedAt = updatedAt;
        }

        public String getAlbumId() {
            return albumId;
        }

        public void setAlbumId(String albumId) {
            this.albumId = albumId;
        }

        public String getGroupId() {
            return groupId;
        }

        public void setGroupId(String groupId) {
            this.groupId = groupId;
        }

        public String getFileType() {
            return fileType;
        }

        public void setFileType(String fileType) {
            this.fileType = fileType;
        }

        public List<String> getFileName() {
            return fileName;
        }

        public void setFileName(ArrayList<String> fileName) {
            this.fileName = fileName;
        }

        public String getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(String createdAt) {
            this.createdAt = createdAt;
        }

        public Boolean getCanEdit() {
            return canEdit;
        }

        public void setCanEdit(Boolean canEdit) {
            this.canEdit = canEdit;
        }

        public String getAlbumName() {
            return albumName;
        }

        public void setAlbumName(String albumName) {
            this.albumName = albumName;
        }

        @NonNull
        @Override
        public String toString() {
            return new Gson().toJson(this);
        }
    }
}