package school.campusconnect.datamodel.syllabus;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;

import java.util.List;

import school.campusconnect.datamodel.ticket.TicketTBL;

@Table(name = "SyllabusTBL")
public class SyllabusTBL extends Model {

    @Column(name = "chapterId")
    public String chapterId;

    @Column(name = "chapterName")
    public String chapterName;

    @Column(name = "topicsList")
    public String topicsList;

    @Column(name = "teamID")
    public String teamID;

    @Column(name = "subjectID")
    public String subjectID;

    public static List<SyllabusTBL> getSyllabus(String teamID, String subjectID)
    {
        return new Select().from(SyllabusTBL.class).where("teamID = ?",teamID).where("subjectID = ?",subjectID).execute();
    }
    public static void deleteAll(String teamID, String subjectID) {
        new Delete().from(SyllabusTBL.class).where("teamID = ?",teamID).where("subjectID = ?",subjectID).execute();
    }
    public static void deleteAll() {
        new Delete().from(SyllabusTBL.class).execute();
    }
}
