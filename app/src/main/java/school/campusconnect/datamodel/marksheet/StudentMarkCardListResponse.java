package school.campusconnect.datamodel.marksheet;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.HashMap;

import school.campusconnect.datamodel.BaseResponse;

public class StudentMarkCardListResponse extends BaseResponse {
    private ArrayList<SubjectDataTeam> data;

    public ArrayList<SubjectDataTeam> getData() {
        return data;
    }

    public void setData(ArrayList<SubjectDataTeam> data) {
        this.data = data;
    }

    public static class SubjectDataTeam {
        @SerializedName(value = "subjects")
        @Expose
        public ArrayList<HashMap<String,HashMap<String,String>>> subjects;
        @SerializedName("rollNumber")
        @Expose
        public String rollNumber;
        @SerializedName("name")
        @Expose
        public String name;
        @SerializedName("isMarksCardUploaded")
        @Expose
        public boolean isMarksCardUploaded;
        @SerializedName("image")
        @Expose
        public String image;
        @SerializedName("duration")
        @Expose
        public String duration;
        @SerializedName("studentId")
        @Expose
        public String studentId;

        public String getStudentId() {
            return studentId;
        }

        public void setStudentId(String studentId) {
            this.studentId = studentId;
        }

        public ArrayList<HashMap<String, HashMap<String, String>>> getSubjects() {
            return subjects;
        }

        public void setSubjects(ArrayList<HashMap<String, HashMap<String, String>>> subjects) {
            this.subjects = subjects;
        }

        public String getRollNumber() {
            return rollNumber;
        }

        public void setRollNumber(String rollNumber) {
            this.rollNumber = rollNumber;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public boolean isMarksCardUploaded() {
            return isMarksCardUploaded;
        }

        public void setMarksCardUploaded(boolean marksCardUploaded) {
            isMarksCardUploaded = marksCardUploaded;
        }

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }

        public String getDuration() {
            return duration;
        }

        public void setDuration(String duration) {
            this.duration = duration;
        }
    }
}
