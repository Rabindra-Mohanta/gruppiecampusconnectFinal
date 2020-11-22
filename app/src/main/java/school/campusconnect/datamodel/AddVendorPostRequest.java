package school.campusconnect.datamodel;

import java.util.ArrayList;

public class AddVendorPostRequest {
    public String fileType;// pdf/image
    public ArrayList<String> fileName;
    public ArrayList<String> thumbnailImage;
    public String vendor;
    public String description;
}
