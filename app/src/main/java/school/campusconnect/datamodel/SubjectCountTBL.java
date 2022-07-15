package school.campusconnect.datamodel;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;

import java.util.List;

@Table(name = "SubjectCountTBL")
public class SubjectCountTBL extends Model {
    @Column(name = "teamId")
    public String teamId;

    @Column(name = "groupId")
    public String groupId;

    @Column(name = "oldSubjectCount")
    public int oldSubjectCount;

    @Column(name = "subjectCount")
    public int subjectCount;

    public SubjectCountTBL(){
        super();
    }

    public static SubjectCountTBL getTeamCount(String teamId,String groupId) {
        List<SubjectCountTBL> list = new Select().from(SubjectCountTBL.class).
                where("teamId = ?", teamId).
                where("groupId = ?", groupId)
                .execute();
        if(list!=null && list.size()>0){
           return list.get(0);
        }
        return null;
    }
    public static void deleteAll() {
        new Delete().from(SubjectCountTBL.class).execute();
    }
}
