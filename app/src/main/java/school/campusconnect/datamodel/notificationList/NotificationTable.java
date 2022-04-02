package school.campusconnect.datamodel.notificationList;


import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;
import com.activeandroid.query.Update;

import java.util.List;

import school.campusconnect.datamodel.baseTeam.BaseTeamTableV2;

@Table(name = "NotificationTable")
public class NotificationTable extends Model {

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

    @Column(name  = "_now")
    public long _now;

    public static List<NotificationTable> getAll() {
        return new Select().from(NotificationTable.class).execute();
    }

    public static List<NotificationTable> getNotificationList(String group_id) {
        return new Select().from(NotificationTable.class).where("groupId = ?", group_id).execute();
    }

    public static List<NotificationTable> getAllNotificationList(String group_id,int page) {
        return new Select().from(NotificationTable.class).where("groupId = ?", group_id).orderBy("datetime(insertedAt) DESC").limit(20).offset((page-1)*20).execute();
    }

    public static void deleteNotification(String group_id) {
        new Delete().from(NotificationTable.class).where("groupId = ?", group_id).execute();
    }

    public static void deleteAll() {
        new Delete().from(NotificationTable.class).execute();
    }

    public static void updateNotification(String value,long id) {
        new Update(NotificationTable.class).set("readedComment = ?", value).where("Id = ?", id).execute();
    }
}

