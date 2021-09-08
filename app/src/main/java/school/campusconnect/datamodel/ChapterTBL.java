package school.campusconnect.datamodel;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;
import com.activeandroid.query.Update;

import java.util.ArrayList;
import java.util.List;

import school.campusconnect.datamodel.chapter.ChapterRes;

@Table(name = "ChapterTBL")
public class ChapterTBL extends Model {
    @Column(name = "createdByName")
    public String createdByName;

    @Column(name = "createdById")
    public String createdById;

    @Column(name = "chapterName")
    public String chapterName;

    @Column(name = "chapterId")
    public String chapterId;

    @Column(name = "topics")
    public String topics; // json

    @Column(name = "teamId")
    public String teamId;

    @Column(name = "subjectId")
    public String subjectId;

    @Column(name = "groupId")
    public String groupId;

    public ChapterTBL(){
        super();
    }

    public static List<ChapterTBL> getAll(String subjectId, String teamId, String groupId) {
        return new Select().from(ChapterTBL.class).where("subjectId = ?", subjectId).where("teamId = ?", teamId).where("groupId = ?", groupId).execute();
    }
    public static void deleteAll() {
        new Delete().from(ChapterTBL.class).execute();
    }

    public static void deleteAll(String subjectId) {
        new Delete().from(ChapterTBL.class).where("subjectId = ?", subjectId).execute();
    }

    public static void update(String chapterId, String jsonData) {
        new Update(ChapterTBL.class).set("topics = ?", jsonData).where("chapterId = ?", chapterId).execute();
    }
}
