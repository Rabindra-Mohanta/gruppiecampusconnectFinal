package school.campusconnect.datamodel.notifications;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;

import java.util.List;

/**
 * Created by frenzin04 on 12/30/2016.
 */

@Table(name = "noti_table")
public class NotificationModel extends Model {

    @Column(name = "noti_id")
    public String noti_id;

    @Column(name = "noti_title")
    public String noti_title;

    @Column(name = "noti_icon")
    public String noti_icon;

    @Column(name = "comment_id")
    public String comment_id;

    @Column(name = "post_id")
    public String post_id;

    @Column(name = "team_id")
    public String team_id;

    @Column(name = "group_id")
    public String group_id;

    @Column(name = "post_type")
    public int post_type;

    @Column(name = "date_time")
    public String date_time;

    @Column(name = "noti_type")
    public int noti_type;

    @Column(name = "friend_id")
    public String  friend_id;

    @Column(name = "created_by")
    public String created_by;

    @Column(name = "noti_phone")
    public String phone;

    @Column(name = "noti_seen")
    public boolean seen;

    public NotificationModel() {
        super();
    }

 /*   public static List<NotificationModel> getAll(int noti_type) {
        return new Select().from(NotificationModel.class).where("noti_type = ?", noti_type).orderBy("noti_id COLLATE NOCASE DESC").execute();
    }

    public static void deleteAll(int noti_type) {
        new Delete().from(NotificationModel.class).where("noti_type = ?", noti_type).execute();
    }

    public static void updateSeen(String noti_id) {
        new Delete().from(NotificationModel.class).where("noti_id = ?", noti_id).execute();
    }

    public static void deleteAll() {
        new Delete().from(NotificationModel.class).execute();
    }*/

    /*Group --> 8, 9

    Team --> 12, 13

    Individual --> 2, 3, 1, 6, 5, 11, 4, 7*/

}
