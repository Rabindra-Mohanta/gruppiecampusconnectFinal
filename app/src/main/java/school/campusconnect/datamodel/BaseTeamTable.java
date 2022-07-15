package school.campusconnect.datamodel;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;

import java.util.List;

/**
 * Created by frenzin04 on 1/16/2017.
 */

@Table(name = "BaseTeamTable")

public class BaseTeamTable extends Model {

    @Column(name = "group_id")
    public String group_id;

    @Column(name = "team_id")
    public String team_id;

    @Column(name = "isClass")
    public boolean isClass;

    @Column(name = "name")
    public String name;

    @Column(name = "adminPhone")
    public String phone;

    @Column(name = "image")
    public String image;

    @Column(name = "members")
    public int members;

    @Column(name = "canAddUser")
    public boolean canAddUser;

    @Column(name = "isTeamAdmin")
    public boolean isTeamAdmin;

    @Column(name = "allowTeamPostCommentAll")
    public boolean allowTeamPostCommentAll;

    @Column(name = "allowTeamPostAll")
    public boolean allowTeamPostAll;

    @Column(name = "teamType")
    public String teamType;

    @Column(name = "enableGps")
    public boolean enableGps;

    @Column(name = "enableAttendance")
    public boolean enableAttendance;

    @Column(name = "type")
    public String type;

    @Column(name = "category")
    public String category;

    @Column(name = "postUnseenCount")
    public int postUnseenCount;

    @Column(name = "role")
    public String role;

    @Column(name = "count")
    public int count;

    @Column(name = "allowedToAddTeamPost")
    public boolean allowedToAddTeamPost;

    @Column(name = "leaveRequest")
    public boolean leaveRequest;

    @Column(name = "details")
    public String details;

    public static List<BaseTeamTable> getAll() {
        return new Select().from(BaseTeamTable.class).execute();
    }

    public static List<BaseTeamTable> getTeamList(String group_id) {
        return new Select().from(BaseTeamTable.class).where("group_id = ?", group_id).execute();
    }

    public static void deleteTeams(String group_id) {
        new Delete().from(BaseTeamTable.class).where("group_id = ?", group_id).execute();
    }

    public static void deleteAll() {
        new Delete().from(BaseTeamTable.class).execute();
    }


}
