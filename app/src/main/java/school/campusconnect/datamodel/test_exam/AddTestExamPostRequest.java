package school.campusconnect.datamodel.test_exam;

import com.google.gson.Gson;

import java.io.Serializable;
import java.util.ArrayList;

public class AddTestExamPostRequest implements Serializable {
    public String title;
    public String text;
    public String video;
    public String fileType;// pdf/youtube/image
    public ArrayList<String> fileName;
    public ArrayList<String> thumbnailImage;
    public String testDate;
    public String testStartTime;
    public String testEndTime;
    public String lastSubmissionTime;
    public boolean proctoring;
    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}



