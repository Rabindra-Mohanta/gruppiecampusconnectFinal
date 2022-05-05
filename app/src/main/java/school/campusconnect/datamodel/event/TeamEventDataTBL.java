package school.campusconnect.datamodel.event;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;

import java.util.List;


@Table(name = "TeamEventDataTBL")
public class TeamEventDataTBL extends Model {

    @Column(name = "teamId")
    public String teamId;

    @Column(name = "members")
    public int members;

    @Column(name = "lastUserToTeamUpdatedAtEventAt")
    public String lastUserToTeamUpdatedAtEventAt;

    @Column(name = "lastCommitteeForBoothUpdatedEventAt")
    public String lastCommitteeForBoothUpdatedEventAt;

    public static List<TeamEventDataTBL> getTeamEvent(String teamId)
    {
        return new Select().from(TeamEventDataTBL.class).where("teamId = ?",teamId).execute();
    }
    public static List<TeamEventDataTBL> deleteTeamEvent(String teamId)
    {
        return new Delete().from(TeamEventDataTBL.class).where("teamId = ?",teamId).execute();
    }
    public static List<TeamEventDataTBL> deleteTeamEvent()
    {
        return new Delete().from(TeamEventDataTBL.class).execute();
    }

}
