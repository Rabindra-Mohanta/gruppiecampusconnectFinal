package school.campusconnect.datamodel.readMore;

import java.util.ArrayList;

import school.campusconnect.datamodel.BaseResponse;

public class ReadMore_Gallery extends BaseResponse {

    public Data data;

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public class Data{

        public String updatedAt;
        public String groupId;
        public String fileType;
        public ArrayList<String> fileName;
        public String createdAt;
        public boolean canEdit;
        public String albumName;
        public String albumId;

        public String getUpdatedAt() {
            return updatedAt;
        }

        public void setUpdatedAt(String updatedAt) {
            this.updatedAt = updatedAt;
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

        public ArrayList<String> getFileName() {
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

        public boolean isCanEdit() {
            return canEdit;
        }

        public void setCanEdit(boolean canEdit) {
            this.canEdit = canEdit;
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
    }

}
