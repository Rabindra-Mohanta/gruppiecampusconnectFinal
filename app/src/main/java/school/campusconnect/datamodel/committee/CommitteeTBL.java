package school.campusconnect.datamodel.committee;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;

import java.util.List;

@Table(name = "CommitteeTBL")
public class CommitteeTBL extends Model {

    @Column(name = "defaultCommittee")
    public boolean defaultCommittee;

    @Column(name = "committeeName")
    public String committeeName;

    @Column(name = "committeeId")
    public String committeeId;

    @Column(name = "groupId")
    public String groupId;

    @Column(name = "teamId")
    public String teamId;

    @Column(name = "_now")
    public String _now;

    public static List<CommitteeTBL> getMember(String groupId, String teamId)
    {
        return new Select().from(CommitteeTBL.class).where("groupId = ?",groupId).where("teamId = ?",teamId).execute();
    }

    public static List<CommitteeTBL> deleteMember(String groupId, String teamId)
    {
        return new Delete().from(CommitteeTBL.class).where("groupId = ?",groupId).where("teamId = ?",teamId).execute();
    }


    public static List<CommitteeTBL> deleteMember()
    {
        return new Delete().from(CommitteeTBL.class).execute();
    }



}
