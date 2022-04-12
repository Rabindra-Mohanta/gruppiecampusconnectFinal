package school.campusconnect.datamodel.booths;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;

import java.util.List;

import school.campusconnect.datamodel.feed.AdminFeedTable;

@Table(name = "BoothsTBL")
public class BoothsTBL extends Model {

    @Column(name = "teamId")
    public String teamId;

    @Column(name = "postUnseenCount")
    public int postUnseenCount;

    @Column(name = "phone")
    public String phone;

    @Column(name = "name")
    public String name;

    @Column(name = "userName")
    public String userName;

    @Column(name = "userImage")
    public String userImage;

    @Column(name = "adminName")
    public String adminName;

    @Column(name = "boothId")
    public String boothId;


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

    @Column(name = "userId")
    public String userId;

    @Column(name = "TeamDetails")
    public String TeamDetails;

    @Column(name = "_now")
    public String _now;

    public static List<BoothsTBL> getAll() {
        return new Select().from(BoothsTBL.class).execute();
    }

    public static List<BoothsTBL> getBoothList(String group_id) {
        return new Select().from(BoothsTBL.class).where("groupId = ?", group_id).execute();
    }
    public static List<BoothsTBL> getLastBoothList(String group_id) {
        return new Select().from(BoothsTBL.class).orderBy("(groupId) DESC").limit(1).execute();
    }
    public static void deleteBooth(String group_id) {
        new Delete().from(BoothsTBL.class).where("groupId = ?", group_id).execute();
    }

    public static void deleteAll() {
        new Delete().from(BoothsTBL.class).execute();
    }


}
