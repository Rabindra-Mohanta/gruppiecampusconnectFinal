package school.campusconnect.datamodel.classs;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

import school.campusconnect.datamodel.BaseResponse;

public class TeacherClassResponse extends BaseResponse {
    private ArrayList<TeacherClassData> data;

    public ArrayList<TeacherClassData> getData() {
        return data;
    }

    public void setData(ArrayList<TeacherClassData> data) {
        this.data = data;
    }

    public static class TeacherClassData {
        @SerializedName("teacherName")
        @Expose
        public String teacherName;
        @SerializedName("countryCode")
        @Expose
        public String countryCode;
        @SerializedName("phone")
        @Expose
        public String phone;
        @SerializedName(value = "className", alternate = "name")
        @Expose
        public String className;
        @SerializedName(value = "classImage", alternate = "image")
        @Expose
        public String classImage;

        @SerializedName("teamId")
        @Expose
        private String id;

        @SerializedName("studentCount")
        @Expose
        public String members;
        @SerializedName("category")
        @Expose
        public String category;


        public String getMembers() {
            return members;
        }

        public void setMembers(String members) {
            this.members = members;
        }

        public String getTeacherName() {
            return teacherName;
        }

        public void setTeacherName(String teacherName) {
            this.teacherName = teacherName;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getName() {
            return className;
        }

        public void setName(String name) {
            this.classImage = name;
        }

        public String getImage() {
            return classImage;
        }

        public void setImage(String image) {
            this.classImage = image;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }
    }
}
