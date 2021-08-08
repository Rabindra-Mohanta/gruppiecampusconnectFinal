package school.campusconnect.datamodel.homework;

import java.util.ArrayList;

public class ReassignReq {
    public String text;
    public ArrayList<String> fileName;
    public String fileType="image";

    public ReassignReq(String text) {
        this.text = text;
    }
}
