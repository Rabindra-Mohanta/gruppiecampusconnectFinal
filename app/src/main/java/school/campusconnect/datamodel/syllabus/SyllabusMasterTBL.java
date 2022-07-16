package school.campusconnect.datamodel.syllabus;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;

import java.util.List;


@Table(name="syllabusMaster")
public class SyllabusMasterTBL extends Model {

    @Column(name = "totalTopicsCount")
    public Integer totalTopicsCount;
    @Column(name="topicsList")
    public String topicsList;
    @Column(name="chapterName")
    public String chapterName;
    @Column(name="chapterId")
    public String chapterId;
    @Column(name = "subjectID")
    public String subjectID;
    @Column(name = "teamID")
    public String teamID;



    public static List<SyllabusMasterTBL> getSyllabus(String teamID, String subjectID)
    {
        return new Select().from(SyllabusMasterTBL.class).where("teamID = ?",teamID).where("subjectID = ?",subjectID).execute();
    }
    public static List<SyllabusMasterTBL> getSyllabusChapter(String teamID, String subjectID,String chapterId)
    {
        return new Select().from(SyllabusMasterTBL.class).where("teamID = ?",teamID).where("subjectID = ?",subjectID).where("chapterId = ?",chapterId).execute();
    }

    public static void deleteAll(String teamID, String subjectID) {
        new Delete().from(SyllabusMasterTBL.class).where("teamID = ?",teamID).where("subjectID = ?",subjectID).execute();
    }
    public static void deleteAll() {


        new Delete().from(SyllabusMasterTBL.class).execute();
    }

}
