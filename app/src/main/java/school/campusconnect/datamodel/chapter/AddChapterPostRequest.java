package school.campusconnect.datamodel.chapter;

import com.google.gson.Gson;

import java.io.Serializable;
import java.util.ArrayList;

public class AddChapterPostRequest implements Serializable {
    public String topicName;
    public String video;
    public String fileType;// pdf/youtube/image
    public ArrayList<String> fileName;
    public ArrayList<String> thumbnailImage;
    public String chapterName;


    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}



