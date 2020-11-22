package school.campusconnect.datamodel.ebook;

import androidx.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

import school.campusconnect.datamodel.BaseResponse;
import school.campusconnect.utils.Constants;

public class EBooksResponse extends BaseResponse {
    private ArrayList<EBookData> data;

    public ArrayList<EBookData> getData() {
        return data;
    }

    public void setData(ArrayList<EBookData> data) {
        this.data = data;
    }

    public static class EBookData {
        @SerializedName(value = "className",alternate = "name")
        @Expose
        public String className;

        @SerializedName(value = "booksId")
        @Expose
        public String booksId;

        @SerializedName(value = "subjectBooks")
        @Expose
        public ArrayList<SubjectBook> subjectBooks;

        public String getClassName() {
            return className;
        }

        public void setClassName(String className) {
            this.className = className;
        }

        public String getBooksId() {
            return booksId;
        }

        public void setBooksId(String booksId) {
            this.booksId = booksId;
        }

        public ArrayList<SubjectBook> getSubjectBooks() {
            return subjectBooks;
        }

        public void setSubjectBooks(ArrayList<SubjectBook> subjectBooks) {
            this.subjectBooks = subjectBooks;
        }

        @NonNull
        @Override
        public String toString() {
            return className;
        }


    }
    public static class SubjectBook {
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


        public SubjectBook(String subjectName,String description, ArrayList<String> fileName) {
            this.subjectName = subjectName;
            this.description = description;
            this.fileName = new ArrayList<>();
            this.fileName.addAll(fileName);
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getSubjectName() {
            return subjectName;
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
