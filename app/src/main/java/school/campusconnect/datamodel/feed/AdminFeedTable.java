package school.campusconnect.datamodel.feed;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;

import java.util.List;

import school.campusconnect.datamodel.notificationList.NotificationTable;

@Table(name = "AdminFeedTable")
public class AdminFeedTable extends Model {

    @Column(name =  "groupID")
    public String groupID;

    @Column(name = "totalSubBoothsCount")
    public int totalSubBoothsCount;

    @Column(name = "totalSubBoothDiscussion")
    public int totalSubBoothDiscussion;

    @Column(name = "totalOpenIssuesCount")
    public int totalOpenIssuesCount;

    @Column(name = "totalBoothsDiscussion")
    public int totalBoothsDiscussion;

    @Column(name = "totalBoothsCount")
    public int totalBoothsCount;

    @Column(name = "totalAnnouncementCount")
    public int totalAnnouncementCount;

    public static List<AdminFeedTable> getAll() {
        return new Select().from(AdminFeedTable.class).execute();
    }

    public static List<AdminFeedTable> getAdminNotificationList(String group_id) {
        return new Select().from(AdminFeedTable.class).where("groupID = ?", group_id).execute();
    }

    public static void deleteAdminNotification(String group_id) {
        new Delete().from(AdminFeedTable.class).where("groupID = ?", group_id).execute();
    }

    public static void deleteAll() {
        new Delete().from(AdminFeedTable.class).execute();
    }

}
