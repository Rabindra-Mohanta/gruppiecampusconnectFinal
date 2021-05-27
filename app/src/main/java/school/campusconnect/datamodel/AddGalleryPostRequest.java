package school.campusconnect.datamodel;

import java.io.Serializable;
import java.util.ArrayList;

public class AddGalleryPostRequest implements Serializable {
    public String video;
    public String fileType;// pdf/youtube/image
    public ArrayList<String> fileName;
    public ArrayList<String> thumbnailImage;
    public String albumName;
    public String topicName; // use for Chapter Api
}
