package school.campusconnect.datamodel;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class PostItem {

    public String updatedAt;
    public String title;
    public String text;

    @SerializedName(value = "phone",alternate = {"senderPhone"})
    public String phone;

    public int likes;
    public boolean isLiked;
    public boolean isFavourited;
    public String id;
    public String groupId="";
    public String fileType=""; // youtube,pdf,image
    public String type; // post type , as we need to check for birthdaypost
    public String bdayUserName;
    public String bdayUserImage;

    public ArrayList<String> fileName; // Url Encoded in base 64
    public ArrayList<String> thumbnailImage; // Url Encoded in base 64

    @SerializedName(value = "createdById",alternate = {"senderId"})
    @Expose
    public String createdById;
    @SerializedName(value = "createdByImage",alternate = {"senderImage"})
    @Expose
    public String createdByImage;
    @SerializedName(value = "createdBy",alternate = {"senderName"})
    @Expose
    public String createdBy;

    public String createdAt;
    public int comments;
    public boolean canEdit;
    public String video;
    public String thumbnail; // Youtube Video thumbnail
    public String team;
    public boolean you;
    public int imageWidth;
    public int imageHeight;


    public String pdf;
    public String image;
    public String readMore;

    @SerializedName("postId")
    @Expose
    public String postIdForBookmark;


    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
