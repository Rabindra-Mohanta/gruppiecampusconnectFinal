package school.campusconnect.datamodel.comments;

import java.util.ArrayList;

import school.campusconnect.datamodel.BaseResponse;

public class AddCommentRes extends BaseResponse {
    public ArrayList<CommentResData> data;

    public class CommentResData {
        public String userId;
        public String loginUserId;
        public String deviceType;
        public String deviceToken;
    }
}
