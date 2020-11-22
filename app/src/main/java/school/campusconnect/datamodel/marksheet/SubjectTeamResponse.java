package school.campusconnect.datamodel.marksheet;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

import school.campusconnect.datamodel.BaseResponse;

public class SubjectTeamResponse extends BaseResponse {
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
        public ArrayList<String> subjects;
        @SerializedName("subjectId")
        @Expose
        public String subjectId;
        @SerializedName("teamId")
        @Expose
        public String teamId;
        @SerializedName("groupId")
        @Expose
        public String groupId;

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

        public String getTeamId() {
            return teamId;
        }

        public void setTeamId(String teamId) {
            this.teamId = teamId;
        }

        public String getGroupId() {
            return groupId;
        }

        public void setGroupId(String groupId) {
            this.groupId = groupId;
        }
    }
}
