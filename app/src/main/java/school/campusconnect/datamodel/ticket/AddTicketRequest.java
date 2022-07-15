package school.campusconnect.datamodel.ticket;

import java.io.Serializable;
import java.util.ArrayList;

public class AddTicketRequest implements Serializable {
    public String text;
    public ArrayList<String> fileName;
    public String fileType;// pdf/youtube/image

    public LocationData location;

    public static class LocationData implements Serializable
    {
        public String latitude;
        public String longitude;
        public String address;
        public String landmark;
        public String pincode;
    }

    public static class AddTicketRes implements Serializable
    {

    }
}
