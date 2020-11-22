package school.campusconnect.datamodel;

import java.util.ArrayList;

public class AddPostResponse extends BaseResponse{
    public ArrayList<AddPostData> data;

    public class AddPostData {
        public String deviceType;
        public String deviceToken;
    }

}
