package school.campusconnect.datamodel.classs;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

import school.campusconnect.datamodel.BaseResponse;

public class ClassResponse extends BaseResponse {
    private ArrayList<ClassData> data;

    public ArrayList<ClassData> getData() {
        return data;
    }

    public void setData(ArrayList<ClassData> data) {
        this.data = data;
    }

    public static class ClassData {
        @SerializedName("teacherName")
        @Expose
        public String teacherName;
        @SerializedName("countryCode")
        @Expose
        public String countryCode;
        @SerializedName("phone")
        @Expose
        public String phone;
        @SerializedName(value = "className",alternate = "name")
        @Expose
        public String className;
        @SerializedName(value = "classImage",alternate = "image")
        @Expose
        public String classImage;

        @SerializedName("teamId")
        @Expose
        public String id;

        @SerializedName(value = "members",alternate = "studentCount")
        @Expose
        public String members;

        @SerializedName(value = "category")
        @Expose
        public String category;

        @SerializedName(value = "subjectId")
        @Expose
        public String subjectId;

        @SerializedName(value = "ebookId")
        @Expose
        public String ebookId;

        @SerializedName(value = "jitsiToken")
        @Expose
        public String jitsiToken;

        public String getJitsiToken() {
            return jitsiToken;
        }

        public void setJitsiToken(String jitsiToken) {
            this.jitsiToken = jitsiToken;
        }

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

        @NonNull
        @Override
        public String toString() {
            return new Gson().toJson(this);
        }
    }
}
