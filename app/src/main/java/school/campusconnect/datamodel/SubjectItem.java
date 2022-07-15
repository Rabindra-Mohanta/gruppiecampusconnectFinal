package school.campusconnect.datamodel;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;

import java.util.List;

@Table(name = "SubjectItem")
public class SubjectItem extends Model {
    @Column(name = "staffName")
    public String staffName;

    @Column(name = "subjectId")
    public String subjectId;

    @Column(name = "subjectName")
    public String subjectName;

    @Column(name = "canPost")
    public boolean canPost;

    @Column(name = "teamId")
    public String teamId;

    @Column(name = "groupId")
    public String groupId;

    public SubjectItem(){
        super();
    }

    public static List<SubjectItem> getAll(String teamId, String groupId) {
        return new Select().from(SubjectItem.class).where("teamId = ?", teamId).where("groupId = ?", groupId).execute();
    }
    public static void deleteAll() {
        new Delete().from(SubjectItem.class).execute();
    }
    public static void deleteAll(String teamId, String groupId) {
        new Delete().from(SubjectItem.class).where("teamId = ?", teamId).where("groupId = ?", groupId).execute();
    }
}
