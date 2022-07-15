package school.campusconnect.datamodel.booths;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;

import java.util.List;

@Table(name = "PublicFormBoothTBL")
public class PublicFormBoothTBL extends Model {

    @Column(name = "teamId")
    public String teamId;

    @Column(name = "postUnseenCount")
    public int postUnseenCount;

    @Column(name = "phone")
    public String phone;

    @Column(name = "name")
    public String name;

    @Column(name = "members")
    public int members;

    @Column(name = "boothNumber")
    public String boothNumber;

    @Column(name = "image")
    public String image;

    @Column(name = "groupId")
    public String groupId;

    @Column(name = "canAddUser")
    public Boolean canAddUser;

    @Column(name = "allowTeamPostCommentAll")
    public Boolean allowTeamPostCommentAll;

    @Column(name = "allowTeamPostAll")
    public Boolean allowTeamPostAll;

    @Column(name = "isTeamAdmin")
    public Boolean isTeamAdmin;

    @Column(name = "isClass")
    public Boolean isClass;

    @Column(name = "teamType")
    public String teamType;

    @Column(name = "enableGps")
    public Boolean enableGps;

    @Column(name = "enableAttendance")
    public Boolean enableAttendance;

    @Column(name = "type")
    public String type;

    @Column(name = "category")
    public String category;

    @Column(name = "role")
    public String role;

    @Column(name = "count")
    public int count;

    @Column(name = "allowedToAddTeamPost")
    public Boolean allowedToAddTeamPost;

    @Column(name = "leaveRequest")
    public Boolean leaveRequest;

    @Column(name = "TeamDetails")
    public String TeamDetails;

    @Column(name = "_now")
    public String _now;

    public static List<PublicFormBoothTBL> getAll() {
        return new Select().from(PublicFormBoothTBL.class).execute();
    }

    public static List<PublicFormBoothTBL> getBoothList(String group_id) {
        return new Select().from(PublicFormBoothTBL.class).where("groupId = ?", group_id).execute();
    }

    public static void deleteBooth(String group_id) {
        new Delete().from(PublicFormBoothTBL.class).where("groupId = ?", group_id).execute();
    }

    public static List<PublicFormBoothTBL> getLastBooth(String group_id) {
        return new Select().from(PublicFormBoothTBL.class).where("groupId = ?", group_id).orderBy("(groupId) DESC").limit(1).execute();
    }

    public static void deleteAll() {
        new Delete().from(PublicFormBoothTBL.class).execute();
    }
}
