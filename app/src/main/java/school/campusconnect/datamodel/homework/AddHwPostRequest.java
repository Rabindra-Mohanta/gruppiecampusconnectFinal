package school.campusconnect.datamodel.homework;

import com.google.gson.Gson;

import java.io.Serializable;
import java.util.ArrayList;

public class AddHwPostRequest implements Serializable {
    public String title;
    public String text;
    public String video; // not used
    public String fileType;// pdf/image
    public ArrayList<String> fileName;
    public ArrayList<String> thumbnailImage;
    public String lastSubmissionDate;


    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}