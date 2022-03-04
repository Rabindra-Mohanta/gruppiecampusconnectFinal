package school.campusconnect.datamodel;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;

import java.util.List;

@Table(name = "TeamCountTBL")
public class TeamCountTBL extends Model {
    @Column(name = "typeOfTeam")
    public String typeOfTeam; // LIVE,ALL,DASHBOARD

    @Column(name = "oldCount")
    public int oldCount;

    @Column(name = "count")
    public int count;

    @Column(name = "lastApiCalled")
    public long lastApiCalled;

    @Column(name = "lastApiCalledNotification")
    public long lastApiCalledNotification;

    @Column(name = "lastNotificationAt")
    public String lastNotificationAt;

    @Column(name = "lastInsertedTeamTime")
    public String lastInsertedTeamTime;

    @Column(name = "groupId")
    public String groupId;

    public TeamCountTBL() {
        super();
    }

    public static TeamCountTBL getByTypeAndGroup(String typeOfTeam, String groupId) {
        List<TeamCountTBL> list = new Select().from(TeamCountTBL.class).where("typeOfTeam = ?", typeOfTeam)
                .where("groupId = ?", groupId).execute();
        if (list != null && list.size() > 0) {
            return list.get(0);
        }
        return null;
    }

    public static void deleteAll() {
        new Delete().from(TeamCountTBL.class).execute();
    }
}
