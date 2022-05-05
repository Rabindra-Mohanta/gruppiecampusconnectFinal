package school.campusconnect.datamodel.teamdiscussion;

import java.util.ArrayList;

import school.campusconnect.datamodel.BaseResponse;

/**
 * Created by frenzin04 on 1/12/2017.
 */
public class TeamPostGetData extends BaseResponse {

    public String updatedAt;
    public String title;
    public String text;
    public String phone;
    public int likes;
    public boolean isLiked;
    public String id;
    public String groupId="";
    public String fileType=""; // youtube,pdf,image
    public ArrayList<String> fileName; // Url Encoded in base 64
    public ArrayList<String> thumbnailImage; // Url Encoded in base 64

    public String bdayUserName;
    public String bdayUserImage;

    public String createdById;
    public String createdByImage;
    public String createdBy;
    public String createdAt;
    public int comments;
    public boolean canEdit;
    public String video;
    public String thumbnail; // Youtube Video thumbnail
    public int imageWidth;
    public int imageHeight;
    public boolean isFavourited;
}
