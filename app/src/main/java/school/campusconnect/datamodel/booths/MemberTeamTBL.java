package school.campusconnect.datamodel.booths;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;

import java.util.List;

@Table(name = "MemberTeamTBL")
public class MemberTeamTBL extends Model {


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

    @Column(name = "userID")
    public String userID;

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

    @Column(name = "userName")
    public String userName;

    @Column(name = "userImage")
    public String userImage;

    @Column(name = "adminName")
    public String adminName;

    @Column(name = "boothId")
    public String boothId;

    public static List<MemberTeamTBL> getAll() {
        return new Select().from(MemberTeamTBL.class).execute();
    }

    public static List<MemberTeamTBL> getMemeberBoothList(String group_id,String teamId) {
        return new Select().from(MemberTeamTBL.class).where("groupId = ?", group_id).where("teamId = ?", teamId).execute();
    }

    public static List<MemberTeamTBL> getLastMemeberBoothList(String group_id,String teamId) {
        return new Select().from(MemberTeamTBL.class).where("groupId = ?", group_id).where("teamId = ?", teamId).orderBy("(teamId) DESC").limit(1).execute();
    }



    public static void deleteMemberBooth(String group_id,String teamId) {
        new Delete().from(MemberTeamTBL.class).where("groupId = ?", group_id).where("teamId = ?", teamId).execute();
    }

    public static void deleteAll() {
        new Delete().from(MemberTeamTBL.class).execute();
    }
}
