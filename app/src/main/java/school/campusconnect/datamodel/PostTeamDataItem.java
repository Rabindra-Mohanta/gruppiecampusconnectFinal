package school.campusconnect.datamodel;

import school.campusconnect.utils.AppLog;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;
import com.activeandroid.query.Update;

import java.util.List;

/**
 * Created by frenzin04 on 1/16/2017.
 */

@Table(name = "post_team_items")
public class PostTeamDataItem extends Model {

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
    public String  createdById;
    @Column(name = "createdByImage")
    public String  createdByImage;
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
    @Column(name = "imageWidth")
    public int imageWidth;
    @Column(name ="imageHeight")
    public int imageHeight;
    @Column(name = "team_id")
    public String team_id;

    @Column(name = "image")
    public String image;

    @Column(name = "isFavourited")
    public boolean isFavourited;

    @Column(name = "_now")
    public long _now;

    @Column(name = "type")
    public String type;



    public PostTeamDataItem() {
        super();
    }

    public static List<PostTeamDataItem> getAll() {
        return new Select().from(PostTeamDataItem.class).execute();
    }

    public static boolean isPostAvailable(String post_id) {
        List<PostTeamDataItem> PostTeamDataItems = getAll();

        for (int i = 0; i < PostTeamDataItems.size(); i++) {
            if (post_id.equals(PostTeamDataItems.get(i).id)) {
                return true;
            }
        }
        return false;
    }

    public static List<PostTeamDataItem> getTeamPosts(String group_id, String team_id,String type) {
       AppLog.e("CHECKK", "query is " + new Select().from(PostTeamDataItem.class).where("group_id = ?", group_id).where("team_id = ?", team_id).toSql() + " " + group_id + " " + team_id);
        return new Select().from(PostTeamDataItem.class).where("type = ?", type).where("group_id = ?", group_id).where("team_id = ?", team_id).execute();
    }

    public static List<PostTeamDataItem> getLastTeamPost(String group_id, String team_id) {
        AppLog.e("CHECKK", "query is " + new Select().from(PostTeamDataItem.class).where("group_id = ?", group_id).where("team_id = ?", team_id).toSql() + " " + group_id + " " + team_id);
        return new Select().from(PostTeamDataItem.class).where("group_id = ?", group_id).where("team_id = ?", team_id).limit(1).execute();
    }

    public static PostTeamDataItem getPost(String post_id) {
        return new Select().from(PostTeamDataItem.class).where("post_id = ?", post_id).executeSingle();
    }
    public static void deleteTeamPosts(String team_id,String type) {
        new Delete().from(PostTeamDataItem.class).where("team_id = ?", team_id).where("type = ?", type).execute();
    }

    public static void deletePost(String post_id) {
        new Delete().from(PostTeamDataItem.class).where("post_id = ?", post_id).execute();
    }

    public static void deleteAllPosts() {
        new Delete().from(PostTeamDataItem.class).execute();
    }


    public static void updateLike(String post_id, int isLiked,int likes) {
        new Update(PostTeamDataItem.class).set("isLiked = ?,likes = ?", isLiked,likes).where("post_id = ?", post_id).execute();
    }

    public static void updateFav(String post_id, int isFavourited) {
        new Update(PostTeamDataItem.class).set("isFavourited = ?", isFavourited).where("post_id = ?", post_id).execute();
    }
}
