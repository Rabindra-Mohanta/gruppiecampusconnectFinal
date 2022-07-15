package school.campusconnect.datamodel.booths;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;

import java.util.List;

@Table(name = "MyTeamVotersTBL")
public class MyTeamVotersTBL extends Model {

    @Column(name = "boothId")
    public String boothId;

    @Column(name = "userId")
    public String userId;

    @Column(name = "voterId")
    public String voterId;

    @Column(name = "roleOnConstituency")
    public String roleOnConstituency;

    @Column(name = "phone")
    public String phone;

    @Column(name = "name")
    public String name;

    @Column(name = "image")
    public String image;

    @Column(name = "gender")
    public String gender;

    @Column(name = "dob")
    public String dob;

    @Column(name = "bloodGroup")
    public String bloodGroup;

    @Column(name = "allowedToAddUser")
    public boolean allowedToAddUser;

    @Column(name = "allowedToAddTeamPostComment")
    public boolean allowedToAddTeamPostComment;

    @Column(name = "allowedToAddTeamPost")
    public boolean allowedToAddTeamPost;

    @Column(name = "groupId")
    public String groupId;

    @Column(name = "address")
    public String address;

    @Column(name = "aadharNumber")
    public String aadharNumber;

    @Column(name = "now")
    public long now;

    public static List<MyTeamVotersTBL> getAll() {
        return new Select().from(MyTeamVotersTBL.class).execute();
    }

    public static List<MyTeamVotersTBL> getBoothList(String group_id,String boothId) {
        return new Select().from(MyTeamVotersTBL.class).where("groupId = ?", group_id).where("boothId = ?", boothId).execute();
    }

    public static void deleteBooth(String group_id,String boothId) {
        new Delete().from(MyTeamVotersTBL.class).where("groupId = ?", group_id).where("boothId = ?", boothId).execute();
    }

    public static void deleteAll() {
        new Delete().from(MyTeamVotersTBL.class).execute();
    }
}
