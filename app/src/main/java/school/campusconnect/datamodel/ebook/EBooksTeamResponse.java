package school.campusconnect.datamodel.ebook;

import androidx.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

import school.campusconnect.datamodel.BaseResponse;
import school.campusconnect.utils.Constants;

public class EBooksTeamResponse extends BaseResponse {
    private ArrayList<SubjectBook> data;

    public ArrayList<SubjectBook> getData() {
        return data;
    }

    public void setData(ArrayList<SubjectBook> data) {
        this.data = data;
    }

    public static class SubjectBook {
        @SerializedName(value = "ebookId")
        @Expose
        public String ebookId;
        @SerializedName(value = "subjectName")
        @Expose
        public String subjectName;
        @SerializedName(value = "description")
        @Expose
        public String description;
        @SerializedName(value = "fileType")
        @Expose
        public String fileType= Constants.FILE_TYPE_PDF;
        @SerializedName(value = "fileName")
        @Expose
        public ArrayList<String> fileName;
        @SerializedName(value = "thumbnailImage")
        @Expose
        public ArrayList<String> thumbnailImage;


        public String getSubjectName() {
            return subjectName;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public void setSubjectName(String subjectName) {
            this.subjectName = subjectName;
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

        @NonNull
        @Override
        public String toString() {
            return subjectName;
        }
    }
}
