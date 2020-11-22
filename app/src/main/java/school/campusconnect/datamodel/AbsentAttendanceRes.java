package school.campusconnect.datamodel;

import java.util.ArrayList;

public class AbsentAttendanceRes extends BaseResponse{
    public ArrayList<AddPostData> data;

    public class AddPostData {
        public String deviceType;
        public String deviceToken;
    }
}
