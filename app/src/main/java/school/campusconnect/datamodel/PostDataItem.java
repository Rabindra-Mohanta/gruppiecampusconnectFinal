package school.campusconnect.datamodel;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;
import com.activeandroid.query.Update;

import java.util.List;

@Table(name = "post_items")
public class PostDataItem extends Model {

    @Column(name = "updated_at")
    public String updatedAt;
    @Column(name = "title")
    public String title;
    @Column(name = "text")
    public String text;
    @Column(name = "phone")
    public String phone;
    @Column(name = "likes")
    public int likes;
    @Column(name = "isLiked")
    public boolean isLiked;
    @Column(name = "isFavourited")
    public boolean isFavourited;
    @Column(name = "post_id")
    public String id;
    @Column(name = "group_id")
    public String group_id;
    @Column(name = "fileType")
    public String fileType;
    @Column(name = "fileName")
    public String fileName;
    @Column(name = "thumbnailImage")
    public String thumbnailImage;
    @Column(name = "createdById")
    public String createdById;
    @Column(name = "createdByImage")
    public String createdByImage;
    @Column(name = "createdBy")
    public String createdBy;
    @Column(name = "createdAt")
    public String createdAt;
    @Column(name = "comments")
    public int comments;
    @Column(name = "canEdit")
    public boolean canEdit;
    @Column(name = "video")
    public String video;
    @Column(name = "thumbnail")
    public String thumbnail;
    @Column(name = "team")
    public String team;
    @Column(name = "you")
    public boolean you;
    @Column(name = "type")
    public String type;
    @Column(name = "friend_id")
    public String friend_id;


    @Column(name = "imageWidth")
    public int imageWidth;
    @Column(name = "imageHeight")
    public int imageHeight;

    @Column(name = "_now")
    public String _now;

    @Column(name = "page")
    public int page;

    public PostDataItem() {
        super();
    }

    public static List<PostDataItem> getAll() {
        return new Select().from(PostDataItem.class).execute();
    }

    public boolean isPostAvailable(String post_id) {
        List<PostDataItem> postDataItems = getAll();

        for (int i = 0; i < postDataItems.size(); i++) {
            if (post_id.equals(postDataItems.get(i).id)) {
                return true;
            }
        }
        return false;
    }

    public static List<PostDataItem> getGeneralPosts(String group_id) {
        return new Select().from(PostDataItem.class).where("type = ?", "group").where("group_id = ?", group_id).execute();
    }

    //Constituency  app (needed to pagination all genral post)
    public static List<PostDataItem> getGeneralPosts(String group_id,int page) {
        return new Select().from(PostDataItem.class).where("group_id = ?", group_id).where("page = ?", page).where("type = ? Or type = ?", "group","birthdayPost").execute();
    }

    public static List<PostDataItem> getLastGeneralPost() {
        return new Select().from(PostDataItem.class).where("type = ?", "group").orderBy("(group_id) DESC").limit(1).execute();
    }

    public static List<PostDataItem> getTeamPosts(String group_id) {
        return new Select().from(PostDataItem.class).where("type = ?", "team").where("group_id = ?", group_id).execute();
    }

    public static List<PostDataItem> getPersonalChatPosts(String group_id, String friend_id) {
        return new Select().from(PostDataItem.class).where("type = ?", "personal").where("group_id = ?", group_id).where("friend_id = ?", friend_id).execute();
    }

    public static PostDataItem getPost(String post_id) {
        return new Select().from(PostDataItem.class).where("post_id = ?", post_id).executeSingle();
    }

    public static void deleteGeneralPosts(String group_id) {
        new Delete().from(PostDataItem.class).where("type = ?", "group").where("group_id = ?", group_id).execute();
    }

    public static void deleteGeneralPostsBirthday(String group_id) {
        new Delete().from(PostDataItem.class).where("type = ?", "birthdayPost").where("group_id = ?", group_id).execute();
    }

    public static void deletePersonalChatPosts(String group_id, String friend_id) {
        new Delete().from(PostDataItem.class).where("type = ?", "personal").where("group_id = ?", group_id).where("friend_id = ?", friend_id).execute();
    }

    public static void deletePost(String post_id) {
        new Delete().from(PostDataItem.class).where("post_id = ?", post_id).execute();
    }


    public static void deleteAllPosts() {
        new Delete().from(PostDataItem.class).execute();
    }

    public static void updateLike(String post_id, int isLiked,int likes) {
        new Update(PostDataItem.class).set("isLiked = ?,likes = ?", isLiked,likes).where("post_id = ?", post_id).execute();
    }

    public static void updateFav(String post_id, int isFavourited) {
        new Update(PostDataItem.class).set("isFavourited = ?", isFavourited).where("post_id = ?", post_id).execute();
    }

}

