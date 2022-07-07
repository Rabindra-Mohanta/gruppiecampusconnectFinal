package school.campusconnect.datamodel.classs;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import school.campusconnect.datamodel.BaseResponse;

public class AddCombinedClass extends BaseResponse {

    @SerializedName("name")
    @Expose
    public String combinedClass;
    @SerializedName("image")
    @Expose
    public String image;

    public AddCombinedClass(String combinedClass, String image) {
        this.combinedClass = combinedClass;
        this.image = image;
    }

    public String getCombinedClass() {
        return combinedClass;
    }

    public String getImage() {
        return image;
    }

    public void setCombinedClass(String combinedClass) {
        this.combinedClass = combinedClass;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
