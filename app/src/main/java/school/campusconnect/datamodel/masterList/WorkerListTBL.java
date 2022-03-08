package school.campusconnect.datamodel.masterList;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;

import java.util.List;

@Table(name = "WorkerListTBL")
public class WorkerListTBL extends Model {

    @Column(name = "teamID")
    public String teamID;

    @Column(name = "now")
    public long now;

    @Column(name = "voterId")
    public String voterId;

    @Column(name = "userId")
    public String userId;

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
    public Boolean allowedToAddUser;

    @Column(name = "allowedToAddTeamPostComment")
    public Boolean allowedToAddTeamPostComment;

    @Column(name = "allowedToAddTeamPost")
    public Boolean allowedToAddTeamPost;

    @Column(name = "address")
    public String address;

    @Column(name = "aadharNumber")
    public String aadharNumber;


    public static List<WorkerListTBL> getAll()
    {
        return new Select().from(WorkerListTBL.class).execute();
    }
    public static List<WorkerListTBL> getWorkerList(String groupID)
    {
        return new Select().from(WorkerListTBL.class).where("teamID = ?",groupID).execute();
    }
    public static List<WorkerListTBL> deleteWorkerList(String groupID)
    {
        return new Delete().from(WorkerListTBL.class).where("teamID = ?",groupID).execute();
    }
    public static List<WorkerListTBL> deleteAll()
    {
        return new Delete().from(WorkerListTBL.class).execute();
    }
}
