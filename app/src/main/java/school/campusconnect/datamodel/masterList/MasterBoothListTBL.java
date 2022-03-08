package school.campusconnect.datamodel.masterList;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;

import java.util.List;

import school.campusconnect.datamodel.notificationList.NotificationTable;

@Table(name = "MasterBoothListTBL")
public class MasterBoothListTBL extends Model {

    @Column(name = "teamId")
    public String teamId;

    @Column(name = "phone")
    public String phone;

    @Column(name = "name")
    public String name;

    @Column(name = "isTeamAdmin")
    public Boolean isTeamAdmin;

    @Column(name = "image")
    public String image;

    @Column(name =  "groupId")
    public String groupId;

    @Column(name =  "category")
    public String category;

    @Column(name =  "canAddUser")
    public Boolean canAddUser;

    @Column(name =  "boothId")
    public String boothId;

    @Column(name =  "boothCommittee")
    public String boothCommittee;

    @Column(name =  "adminName")
    public String adminName;

    @Column(name =  "now")
    public long now;

    public static List<MasterBoothListTBL> getAll() {
        return new Select().from(MasterBoothListTBL.class).execute();
    }

    public static List<MasterBoothListTBL> getMasterBoothList(String group_id) {
        return new Select().from(MasterBoothListTBL.class).where("groupId = ?", group_id).execute();
    }

    public static void deleteMasterBoothList(String group_id) {
        new Delete().from(MasterBoothListTBL.class).where("groupId = ?", group_id).execute();
    }

    public static void deleteAll() {
        new Delete().from(MasterBoothListTBL.class).execute();
    }
}
