package school.campusconnect.datamodel.notificationList;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;
import com.activeandroid.query.Update;

import java.util.List;

@Table(name = "AllNotificationTable")

public class AllNotificationTable extends Model {

    @Column(name  = "groupId")
    public String groupId;

    @Column(name  = "teamId")
    public String teamId;

    @Column(name  = "userId")
    public String userId;

    @Column(name  = "type")
    public String type;

    @Column(name  = "showComment")
    public Boolean showComment;

    @Column(name  = "postId")
    public String postId;

    @Column(name  = "message")
    public String message;

    @Column(name  = "insertedAt")
    public String insertedAt;

    @Column(name  = "createdByPhone")
    public String createdByPhone;

    @Column(name  = "createdByName")
    public String createdByName;

    @Column(name  = "createdByImage")
    public String createdByImage;

    @Column(name  = "createdById")
    public String createdById;

    @Column(name  = "readedComment")
    public String readedComment;

    public static List<AllNotificationTable> getAll() {
        return new Select().from(AllNotificationTable.class).execute();
    }

    public static List<AllNotificationTable> getAllNotificationList(String group_id) {
        return new Select().from(AllNotificationTable.class).where("groupId = ?", group_id).execute();
    }

    public static void deleteAllNotification(String group_id) {
        new Delete().from(AllNotificationTable.class).where("groupId = ?", group_id).execute();
    }

    public static void deleteAll() {
        new Delete().from(AllNotificationTable.class).execute();
    }

}
