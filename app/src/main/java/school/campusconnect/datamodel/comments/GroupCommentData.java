package school.campusconnect.datamodel.comments;

import school.campusconnect.datamodel.BaseResponse;

/**
 * Created by frenzin04 on 1/11/2017.
 */
public class GroupCommentData extends BaseResponse {

    public String text;
    public String id;
    public int replies;
    public int likes;
    public String insertedAt;
    public String createdByPhone;
    public String createdByName;
    public String createdByImage;
    public String createdById;
    public boolean canEdit;
    public boolean isLiked;
}
