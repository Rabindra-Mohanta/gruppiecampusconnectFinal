package school.campusconnect.datamodel.classs;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AddClassReq {

    @SerializedName("className")
    @Expose
    private String className;

    @SerializedName("classTypeId")
    @Expose
    private String classTypeId;

    @SerializedName("noOfSection")
    @Expose
    private int noOfSection;

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getClassTypeId() {
        return classTypeId;
    }

    public void setClassTypeId(String classTypeId) {
        this.classTypeId = classTypeId;
    }

    public int getNoOfSection() {
        return noOfSection;
    }

    public void setNoOfSection(int noOfSection) {
        this.noOfSection = noOfSection;
    }
}
