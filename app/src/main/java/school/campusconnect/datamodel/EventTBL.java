package school.campusconnect.datamodel;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;

import java.util.List;

@Table(name = "EventTBL")
public class EventTBL extends Model {
    @Column(name = "insertedId")
    public String insertedId;

    @Column(name = "groupId")
    public String groupId;

    @Column(name = "subjectId")
    public String subjectId;

    @Column(name = "teamId")
    public String teamId;

    @Column(name = "eventType")
    public String eventType;

    @Column(name = "eventName")
    public String eventName;

    @Column(name = "eventAt")
    public String eventAt;

    @Column(name = "_now")
    public long _now;

    public EventTBL() {
        super();
    }

    public static EventTBL getGroupEvent(String groupId) {
        List<EventTBL> list = new Select().from(EventTBL.class).where("eventType = ?", "1").where("groupId = ?", groupId).execute();
        if (list != null && list.size() > 0) {
            return list.get(0);
        }
        return null;
    }

    public static EventTBL getTeamEvent(String groupId, String teamId) {
        List<EventTBL> list = new Select().from(EventTBL.class).
                where("eventType = ?", "2").
                where("groupId = ?", groupId).
                where("teamId = ?", teamId).execute();
        if (list != null && list.size() > 0) {
            return list.get(0);
        }
        return null;
    }

    public static EventTBL getAssignmentEvent(String groupId, String teamId, String subjectId) {
        List<EventTBL> list = new Select().from(EventTBL.class).
                where("eventType = ?", "4").
                where("subjectId = ?", subjectId).
                where("groupId = ?", groupId).
                where("teamId = ?", teamId).execute();
        if (list != null && list.size() > 0) {
            return list.get(0);
        }
        return null;
    }

    public static EventTBL getNotesVideoEvent(String groupId, String teamId, String subjectId) {
        List<EventTBL> list = new Select().from(EventTBL.class).
                where("eventType = ?", "3").
                where("subjectId = ?", subjectId).
                where("groupId = ?", groupId).
                where("teamId = ?", teamId).execute();
        if (list != null && list.size() > 0) {
            return list.get(0);
        }
        return null;
    }

    public static void deleteAll() {
        new Delete().from(EventTBL.class).execute();
    }
}
