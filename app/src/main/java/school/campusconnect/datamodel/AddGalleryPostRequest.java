package school.campusconnect.datamodel;

import java.util.ArrayList;

public class AddGalleryPostRequest {
    public String video;
    public String fileType;// pdf/youtube/image
    public ArrayList<String> fileName;
    public ArrayList<String> thumbnailImage;
    public String albumName;
    public String topicName; // use for Chapter Api
}
