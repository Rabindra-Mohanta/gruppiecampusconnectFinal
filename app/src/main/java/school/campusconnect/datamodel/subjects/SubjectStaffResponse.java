package school.campusconnect.datamodel.subjects;

import androidx.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

import school.campusconnect.datamodel.BaseResponse;


public class SubjectStaffResponse extends BaseResponse {
    private ArrayList<SubjectData> data;

    public ArrayList<SubjectData> getData() {
        return data;
    }

    public void setData(ArrayList<SubjectData> data) {
        this.data = data;
    }

    public static class SubjectData {
        @SerializedName(value = "staffName")
        @Expose
        public ArrayList<SubjectStaff> staffName;
        @SerializedName("subjectId")
        @Expose
        public String subjectId;
        @SerializedName("subjectName")
        @Expose
        public String name;
        @SerializedName("canPost")
        @Expose
        public boolean canPost;

        public boolean isCanPost() {
            return canPost;
        }

        public void setCanPost(boolean canPost) {
            this.canPost = canPost;
        }

        public ArrayList<SubjectStaff> getStaffName() {
            return staffName;
        }

        public void setStaffName(ArrayList<SubjectStaff> staffName) {
            this.staffName = staffName;
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

        public String getStaffNameFormatted() {
            StringBuilder stringBuilder = new StringBuilder();
            if(staffName!=null){
                for (int i=0;i<staffName.size();i++){
                    if(i==0){
                        stringBuilder.append(staffName.get(i).staffName);
                    }else {
                        stringBuilder.append(", ").append(staffName.get(i).staffName);
                    }
                }
            }
            return stringBuilder.toString();
        }
    }

    public static class SubjectStaff {
        @SerializedName("staffId")
        @Expose
        public String staffId;
        @SerializedName("staffName")
        @Expose
        public String staffName;

    }
}
