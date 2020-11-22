package school.campusconnect.datamodel.marksheet;

import java.util.ArrayList;

public class AddMarkSheetReq {
    public String title;
    public ArrayList<String> fileName;
    public String fileType;

    @Override
    public String toString() {
        return "AddMarkSheetReq{" +
                "title='" + title + '\'' +
                ", fileName=" + fileName +
                ", fileType='" + fileType + '\'' +
                '}';
    }
}
