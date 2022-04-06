package school.campusconnect.datamodel.baseTeam;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;

import java.util.List;

import school.campusconnect.datamodel.BaseTeamTable;

@Table(name = "BaseTeamTableV2")

public class BaseTeamTableV2 extends Model {

    @Column(name = "featureIcons")
    public String featureIcons;

    @Column(name = "activity")
    public String activity;

    @Column(name = "group_id")
    public String group_id;

    @Column(name = "_now")
    public String _now;

    @Column(name = "update_team")
    public String update_team;


    public static List<BaseTeamTableV2> getAll() {
        return new Select().from(BaseTeamTableV2.class).execute();
    }

    public static List<BaseTeamTableV2> getTeamList(String group_id) {
        return new Select().from(BaseTeamTableV2.class).where("group_id = ?", group_id).execute();
    }

    public static void deleteTeams(String group_id) {
        new Delete().from(BaseTeamTableV2.class).where("group_id = ?", group_id).execute();
    }

    public static void deleteAll() {
        new Delete().from(BaseTeamTableV2.class).execute();
    }

}
