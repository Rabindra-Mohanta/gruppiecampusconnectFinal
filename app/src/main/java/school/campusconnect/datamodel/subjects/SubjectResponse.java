package school.campusconnect.datamodel.subjects;

import androidx.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

import school.campusconnect.datamodel.BaseResponse;

public class SubjectResponse extends BaseResponse {
    private ArrayList<SubjectData> data;

    public ArrayList<SubjectData> getData() {
        return data;
    }

    public void setData(ArrayList<SubjectData> data) {
        this.data = data;
    }

    public static class SubjectData {
        @SerializedName(value = "classSubjects",alternate = "subjects")
        @Expose
        public ArrayList<String> subjects;

        @SerializedName("subjectId")
        @Expose
        public String subjectId;
        @SerializedName("name")
        @Expose
        public String name;

        public ArrayList<String> getSubjects() {
            return subjects;
        }

        public void setSubjects(ArrayList<String> subjects) {
            this.subjects = subjects;
        }

        public String getSubjectId() {
            return subjectId;
        }

        public void setSubjectId(String subjectId) {
            this.subjectId = subjectId;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        @NonNull
        @Override
        public String toString() {
            return name;
        }
    }
}
