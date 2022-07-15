package school.campusconnect.datamodel.classs;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

import school.campusconnect.datamodel.BaseResponse;

public class ClassResV2 extends BaseResponse {

    @SerializedName("data")
    @Expose
    private ArrayList<Classes> data;


    public ArrayList<Classes> getData() {
        return data;
    }

    public void setData(ArrayList<Classes> data) {
        this.data = data;
    }

    public class Classes implements Serializable
    {
        @SerializedName("classes")
        @Expose
        private ArrayList<Data> classData;

        public ArrayList<Data> getClassData() {
            return classData;
        }

        public void setClassData(ArrayList<Data> classData) {
            this.classData = classData;
        }
    }
    public class Data implements Serializable
    {
        @SerializedName("type")
        @Expose
        private String type;

        @SerializedName("classTypeId")
        @Expose
        private String classTypeId;

        @SerializedName("class")
        @Expose
        private ArrayList<classData> classData;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getClassTypeId() {
            return classTypeId;
        }

        public void setClassTypeId(String classTypeId) {
            this.classTypeId = classTypeId;
        }

        public ArrayList<ClassResV2.classData> getClassData() {
            return classData;
        }

        public void setClassData(ArrayList<ClassResV2.classData> classData) {
            this.classData = classData;
        }
    }
    public class classData implements Serializable
    {
        @SerializedName("noOfSections")
        @Expose
        private int noOfSections;

        @SerializedName("className")
        @Expose
        private String className;

        public int getNoOfSections() {
            return noOfSections;
        }

        public void setNoOfSections(int noOfSections) {
            this.noOfSections = noOfSections;
        }

        public String getClassName() {
            return className;
        }

        public void setClassName(String className) {
            this.className = className;
        }
    }

}
