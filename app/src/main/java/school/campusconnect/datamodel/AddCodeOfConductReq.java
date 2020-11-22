package school.campusconnect.datamodel;

import java.util.ArrayList;

public class AddCodeOfConductReq {
    public String fileType;// pdf/image
    public ArrayList<String> fileName;
    public ArrayList<String> thumbnailImage;
    public String title;
    public String description;
}
