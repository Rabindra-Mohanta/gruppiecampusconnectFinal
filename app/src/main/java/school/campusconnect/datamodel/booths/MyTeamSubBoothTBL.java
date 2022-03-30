package school.campusconnect.datamodel.booths;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;

import java.util.List;

@Table(name = "MyTeamSubBoothTBL")
public class MyTeamSubBoothTBL extends Model {

    @Column(name = "teamId")
    public String teamID;

    @Column(name = "subBoothId")
    public String subBoothId;

    @Column(name = "userId")
    public String userId;

    @Column(name = "name")
    public String name;

    @Column(name = "members")
    public int members;

    @Column(name = "isTeamAdmin")
    public boolean isTeamAdmin;

    @Column(name = "image")
    public String image;

    @Column(name = "groupId")
    public String groupId;

    @Column(name = "category")
    public String category;

    @Column(name = "canAddUser")
    public boolean canAddUser;

    @Column(name = "allowTeamPostCommentAll")
    public boolean allowTeamPostCommentAll;

    @Column(name = "allowTeamPostAll")
    public boolean allowTeamPostAll;

    @Column(name = "_now")
    public long _now;

    public static List<MyTeamSubBoothTBL> getAll() {
        return new Select().from(MyTeamSubBoothTBL.class).execute();
    }

    public static List<MyTeamSubBoothTBL> getSubBoothList(String group_id) {
        return new Select().from(MyTeamSubBoothTBL.class).where("groupId = ?", group_id).execute();
    }

    public static void deleteSubBooth(String group_id) {
        new Delete().from(MyTeamSubBoothTBL.class).where("groupId = ?", group_id).execute();
    }

    public static void deleteAll() {
        new Delete().from(MyTeamSubBoothTBL.class).execute();
    }


}
