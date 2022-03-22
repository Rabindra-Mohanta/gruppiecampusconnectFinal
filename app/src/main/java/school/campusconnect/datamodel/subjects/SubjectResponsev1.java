package school.campusconnect.datamodel.subjects;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

import school.campusconnect.datamodel.BaseResponse;

public class SubjectResponsev1 extends BaseResponse{

    @SerializedName("data")
    @Expose
    public ArrayList<SubjectData> data;

    public ArrayList<SubjectData> getData() {
        return data;
    }

    public void setData(ArrayList<SubjectData> data) {
        this.data = data;
    }

    public static class SubjectData implements Serializable {

        @SerializedName("subjectName")
        @Expose
        public String subjectName;

        @SerializedName("subjectId")
        @Expose
        public String subjectId;

        @SerializedName("staffName")
        @Expose
        public ArrayList<StaffName> staffName;

        @SerializedName("canPost")
        @Expose
        public boolean canPost;

        public String getSubjectName() {
            return subjectName;
        }

        public void setSubjectName(String subjectName) {
            this.subjectName = subjectName;
        }

        public String getSubjectId() {
            return subjectId;
        }

        public void setSubjectId(String subjectId) {
            this.subjectId = subjectId;
        }

        public ArrayList<StaffName> getStaffName() {
            return staffName;
        }

        public void setStaffName(ArrayList<StaffName> staffName) {
            this.staffName = staffName;
        }

        public boolean isCanPost() {
            return canPost;
        }

        public void setCanPost(boolean canPost) {
            this.canPost = canPost;
        }
    }
    public static class StaffName implements Serializable
    {
        @SerializedName("staffId")
        @Expose
        public String staffId;

        @SerializedName("staffName")
        @Expose
        public String staffName;

        public String getStaffId() {
            return staffId;
        }

        public void setStaffId(String staffId) {
            this.staffId = staffId;
        }

        public String getStaffName() {
            return staffName;
        }

        public void setStaffName(String staffName) {
            this.staffName = staffName;
        }
    }
}
