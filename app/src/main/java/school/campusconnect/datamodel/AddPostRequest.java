package school.campusconnect.datamodel;

import com.google.gson.Gson;

import java.util.ArrayList;

public class AddPostRequest {
    public String text;
    public String video;
    public String fileType;// pdf/youtube/image
    public ArrayList<String> fileName;
    public ArrayList<String> thumbnailImage;
    public String title;


    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}



